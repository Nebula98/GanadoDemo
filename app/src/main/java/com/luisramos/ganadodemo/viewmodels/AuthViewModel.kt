package com.luisramos.ganadodemo.viewmodels


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luisramos.ganadodemo.models.User
import com.luisramos.ganadodemo.repository.FirebaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: FirebaseRepository = FirebaseRepository()) : ViewModel() {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Initial)
    val authState: StateFlow<AuthState> = _authState

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    init {
        checkCurrentUser()
    }

    private fun checkCurrentUser() {
        viewModelScope.launch {
            val userId = repository.getCurrentUserId()
            if (userId != null) {
                repository.getUserData(userId).onSuccess { user ->
                    _currentUser.value = user
                    _authState.value = AuthState.Authenticated
                }
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            repository.login(email, password)
                .onSuccess { userId ->
                    repository.getUserData(userId).onSuccess { user ->
                        _currentUser.value = user
                        _authState.value = AuthState.Authenticated
                    }
                }
                .onFailure { error ->
                    _authState.value = AuthState.Error(error.message ?: "Error desconocido")
                }
        }
    }

    fun register(email: String, password: String, nombre: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            repository.register(email, password, nombre)
                .onSuccess { userId ->
                    repository.getUserData(userId).onSuccess { user ->
                        _currentUser.value = user
                        _authState.value = AuthState.Authenticated
                    }
                }
                .onFailure { error ->
                    _authState.value = AuthState.Error(error.message ?: "Error desconocido")
                }
        }
    }

    fun logout() {
        repository.logout()
        _currentUser.value = null
        _authState.value = AuthState.Initial
    }
}

sealed class AuthState {
    object Initial : AuthState()
    object Loading : AuthState()
    object Authenticated : AuthState()
    data class Error(val message: String) : AuthState()
}