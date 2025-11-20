package com.luisramos.ganadodemo.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luisramos.ganadodemo.models.Animal
import com.luisramos.ganadodemo.models.Insumo
import com.luisramos.ganadodemo.models.Produccion
import com.luisramos.ganadodemo.repository.FirebaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DashboardViewModel(private val repository: FirebaseRepository = FirebaseRepository()) : ViewModel() {
    private val _animales = MutableStateFlow<List<Animal>>(emptyList())
    val animales: StateFlow<List<Animal>> = _animales

    private val _insumos = MutableStateFlow<List<Insumo>>(emptyList())
    val insumos: StateFlow<List<Insumo>> = _insumos

    private val _produccion = MutableStateFlow<List<Produccion>>(emptyList())
    val produccion: StateFlow<List<Produccion>> = _produccion

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            repository.getAnimales().collect { _animales.value = it }
        }
        viewModelScope.launch {
            repository.getInsumos().collect { _insumos.value = it }
        }
        viewModelScope.launch {
            repository.getProduccion().collect { _produccion.value = it }
        }
    }

    fun addAnimal(animal: Animal) {
        viewModelScope.launch {
            repository.addAnimal(animal)
        }
    }

    fun updateAnimal(animal: Animal) {
        viewModelScope.launch {
            repository.updateAnimal(animal)
        }
    }

    fun deleteAnimal(animalId: String) {
        viewModelScope.launch {
            repository.deleteAnimal(animalId)
        }
    }

    fun addInsumo(insumo: Insumo) {
        viewModelScope.launch {
            repository.addInsumo(insumo)
        }
    }

    fun updateInsumo(insumo: Insumo) {
        viewModelScope.launch {
            repository.updateInsumo(insumo)
        }
    }

    fun deleteInsumo(insumoId: String) {
        viewModelScope.launch {
            repository.deleteInsumo(insumoId)
        }
    }

    fun addProduccion(produccion: Produccion) {
        viewModelScope.launch {
            repository.addProduccion(produccion)
        }
    }
}
