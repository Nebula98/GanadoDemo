package com.luisramos.ganadodemo.repository

import com.luisramos.ganadodemo.models.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirebaseRepository {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    // Autenticación
    suspend fun login(email: String, password: String): Result<String> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            Result.success(result.user?.uid ?: "")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(email: String, password: String, nombre: String): Result<String> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val userId = result.user?.uid ?: ""

            // Crear documento de usuario
            val user = User(
                id = userId,
                nombre = nombre,
                email = email,
                rol = "usuario"
            )
            db.collection("users").document(userId).set(user).await()

            Result.success(userId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout() {
        auth.signOut()
    }

    fun getCurrentUserId(): String? = auth.currentUser?.uid

    suspend fun getUserData(userId: String): Result<User> {
        return try {
            val doc = db.collection("users").document(userId).get().await()
            val user = doc.toObject(User::class.java)
            if (user != null) {
                Result.success(user)
            } else {
                Result.failure(Exception("Usuario no encontrado"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Animales
    suspend fun addAnimal(animal: Animal): Result<String> {
        return try {
            val docRef = db.collection("animales").document()
            val animalWithId = animal.copy(id = docRef.id)
            docRef.set(animalWithId).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getAnimales(): Flow<List<Animal>> = callbackFlow {
        val listener = db.collection("animales")
            .orderBy("fechaRegistro", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val animales = snapshot?.documents?.mapNotNull {
                    it.toObject(Animal::class.java)
                } ?: emptyList()

                trySend(animales)
            }

        awaitClose { listener.remove() }
    }

    suspend fun updateAnimal(animal: Animal): Result<Unit> {
        return try {
            db.collection("animales").document(animal.id).set(animal).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteAnimal(animalId: String): Result<Unit> {
        return try {
            db.collection("animales").document(animalId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Insumos
    suspend fun addInsumo(insumo: Insumo): Result<String> {
        return try {
            val docRef = db.collection("insumos").document()
            val insumoWithId = insumo.copy(id = docRef.id)
            docRef.set(insumoWithId).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getInsumos(): Flow<List<Insumo>> = callbackFlow {
        val listener = db.collection("insumos")
            .orderBy("fechaRegistro", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val insumos = snapshot?.documents?.mapNotNull {
                    it.toObject(Insumo::class.java)
                } ?: emptyList()

                trySend(insumos)
            }

        awaitClose { listener.remove() }
    }

    suspend fun updateInsumo(insumo: Insumo): Result<Unit> {
        return try {
            db.collection("insumos").document(insumo.id).set(insumo).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteInsumo(insumoId: String): Result<Unit> {
        return try {
            db.collection("insumos").document(insumoId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Producción
    suspend fun addProduccion(produccion: Produccion): Result<String> {
        return try {
            val docRef = db.collection("produccion").document()
            val produccionWithId = produccion.copy(id = docRef.id)
            docRef.set(produccionWithId).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getProduccion(): Flow<List<Produccion>> = callbackFlow {
        val listener = db.collection("produccion")
            .orderBy("fechaRegistro", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val produccion = snapshot?.documents?.mapNotNull {
                    it.toObject(Produccion::class.java)
                } ?: emptyList()

                trySend(produccion)
            }

        awaitClose { listener.remove() }
    }
}
