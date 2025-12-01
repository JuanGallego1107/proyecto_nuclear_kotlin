package com.apps_moviles.proyecto_nuclear_kotlin.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.apps_moviles.proyecto_nuclear_kotlin.data.UserPreferences
import com.apps_moviles.proyecto_nuclear_kotlin.data.UserRepository
import com.apps_moviles.proyecto_nuclear_kotlin.model.User
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(val user: User) : LoginState()
    data class Error(val message: String) : LoginState()
}


class UserViewModel(private val repository: UserRepository, private val prefs: UserPreferences) : ViewModel() {

    val loggedUserId = prefs.userId.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        null
    )

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> get() = _loginState

    val users: StateFlow<List<User>> = repository.allUsers
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    private val _loginResult = MutableStateFlow<User?>(null)
    val loginResult: StateFlow<User?> = _loginResult.asStateFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading

            val user = repository.login(email, password)

            _loginState.value = if (user != null) {
                prefs.saveUserId(user.id)
                LoginState.Success(user)
            } else {
                LoginState.Error("Credenciales incorrectas")
            }
        }
    }

    fun insert(email: String, password: String, fullName: String) {
        viewModelScope.launch {
            repository.insert(User(email = email, password = password, fullName = fullName))
        }
    }

    fun delete(user: User) {
        viewModelScope.launch {
            repository.delete(user)
        }
    }

    fun clearLoggedUser() {
        viewModelScope.launch {
            prefs.clearUserId()
            _loginState.value = LoginState.Idle
            _loginResult.value = null
        }
    }
}

class UserViewModelFactory(private val repository: UserRepository, private val prefs: UserPreferences) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UserViewModel(repository, prefs) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
