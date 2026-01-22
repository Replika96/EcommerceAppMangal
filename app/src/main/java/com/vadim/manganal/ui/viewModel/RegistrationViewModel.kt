package com.vadim.manganal.ui.viewModel

import androidx.annotation.OptIn
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.vadim.manganal.data.repository.RegistrationRepositoryImpl
import com.vadim.manganal.domain.Repository.AuthResult
import com.vadim.manganal.domain.Repository.RegistrationRepository
import com.vadim.manganal.domain.entity.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException


sealed interface AuthUiState {
    object Idle : AuthUiState
    object Loading : AuthUiState
    object Success : AuthUiState
    data class Error(val message: String) : AuthUiState
}

sealed interface NavEvent {
    object ToLogin : NavEvent
    object ToHome  : NavEvent
    object ToAdmin  : NavEvent
}

@HiltViewModel
class RegistrationViewModel @OptIn(UnstableApi::class)
@Inject constructor(
    private val repo: RegistrationRepositoryImpl,
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val _navEvent = MutableSharedFlow<NavEvent>(extraBufferCapacity = 1)
    val navEvent: SharedFlow<NavEvent> = _navEvent.asSharedFlow()

    private val _isAnonymous = MutableStateFlow<Boolean?>(null)
    val isAnonymous: StateFlow<Boolean?> = _isAnonymous.asStateFlow()

    init {
        viewModelScope.launch {
            repo.authState.collect { user ->
                Log.d("Auth", "User: ${user?.uid}, isAnonymous: ${user?.isAnonymous}")
                _isAnonymous.value = user?.isAnonymous
            }
        }
    }

    fun initAuth() = viewModelScope.launch {
        val user = repo.authState.first() // проверяем текущего пользователя
        if (user == null) {
            signInAnonymously() // если пользователя нет, выполняем анонимный вход
        } else {
            _uiState.value = AuthUiState.Success // если пользователь уже есть, устанавливаем Success
        }
    }

    private fun signInAnonymously() = viewModelScope.launch {
        _uiState.value = AuthUiState.Loading
        try {
            repo.signInAnonymously() // выполняем анонимный вход
            _uiState.value = AuthUiState.Success // успех, если ошибок нет
        } catch (e: Exception) {
            _uiState.value = AuthUiState.Error("Ошибка анонимного входа: ${e.message}")
        }
    }


    // РЕГИСТРАЦИЯ!!
    fun registerUser(name: String, email: String, password: String) = viewModelScope.launch {
        _uiState.value = AuthUiState.Loading
        try {
            withTimeout(10000) { // таймаут 10 секунд
                val result = if (auth.currentUser?.isAnonymous == true)
                    repo.linkAnonymousUserWithEmail(email, password)
                else
                    repo.createUser(email, password)

                when (result) {
                    is AuthResult.Success -> {
                        val uid = result.user.uid
                        saveProfile(uid, name, email)
                        _isAnonymous.value = false // <- добавлено
                        _uiState.value = AuthUiState.Success
                        _navEvent.tryEmit(NavEvent.ToHome)
                    }
                    is AuthResult.Error -> {
                        _uiState.value = AuthUiState.Error(result.message)
                    }
                }

            }
        } catch (e: TimeoutCancellationException) {
            _uiState.value = AuthUiState.Error("Регистрация: превышено время ожидания")
        } catch (e: Exception) {
            handleException(e, "Регистрация")
        }
    }


    // ВХОД

    fun loginUser(email: String, password: String) = viewModelScope.launch {
        if (email.isBlank() || password.isBlank()) {
            _uiState.value = AuthUiState.Error("Поля не могут быть пустыми")
            return@launch
        }

        _uiState.value = AuthUiState.Loading
        try {
            when (val result = repo.signIn(email, password)) {
                is AuthResult.Success -> {
                    _isAnonymous.value = false // <- добавлено
                    _uiState.value = AuthUiState.Success
                    _navEvent.tryEmit(NavEvent.ToHome)
                }
                is AuthResult.Error -> {
                    _uiState.value = AuthUiState.Error(result.message)
                }
            }
        } catch (e: Exception) {
            handleException(e, "Вход")
        }
    }


    // ВЫХОД

    fun signOut() = viewModelScope.launch {
        auth.signOut()
        _uiState.value = AuthUiState.Idle
        _navEvent.tryEmit(NavEvent.ToLogin)
    }

    // ВСПОМОГАТЕЛЬНОЕ

    val isSignedIn: Boolean
        get() = repo.isSignedIn()

    private suspend fun saveProfile(uid: String, name: String, email: String) {
        val userDoc = User(uid = uid, name = name, email = email)
        db.collection("users").document(uid).set(userDoc).await()
    }

    private fun handleException(e: Exception, src: String) {
        if (e is CancellationException) throw e                 // не гасим корутин-отмену
        _uiState.value = AuthUiState.Error("$src: ${e.message ?: "неизвестная ошибка"}")
    }




    // PROFILE DATA

    private val _userData = MutableStateFlow<User?>(null)
    val userData: StateFlow<User?> = _userData.asStateFlow()

    @OptIn(UnstableApi::class)
    fun loadUserData() = viewModelScope.launch {
        val uid = auth.currentUser?.uid ?: return@launch
        try {
            val document = db.collection("users").document(uid).get().await()
            if (document.exists()) {
                val user = document.toObject<User>()
                _userData.value = user
            } else {
                _userData.value = null
            }
        } catch (e: Exception) {
            Log.e("RegistrationViewModel", "Ошибка загрузки данных: ${e.message}")
        }
    }

    // cохранение обновленных данных
    @OptIn(UnstableApi::class)
    fun updateUserData(updatedUser: User) = viewModelScope.launch {
        val uid = auth.currentUser?.uid ?: return@launch
        _uiState.value = AuthUiState.Loading
        try {
            db.collection("users").document(uid).set(updatedUser).await()
            _userData.value = updatedUser
            _uiState.value = AuthUiState.Success
        } catch (e: Exception) {
            Log.e("RegistrationViewModel", "Ошибка сохранения данных: ${e.message}")
            _uiState.value = AuthUiState.Error(e.message ?: "Неизвестная ошибка")
        }
    }
    fun resetUiState() {
        _uiState.value = AuthUiState.Idle
    }

}




