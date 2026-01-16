package com.vadim.manganal.domain.Repository

import android.util.Log
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

sealed class AuthResult {
    data class Success(val user: FirebaseUser) : AuthResult()
    data class Error(val message: String)      : AuthResult()
}

@ViewModelScoped
class RegistrationRepository @Inject constructor(
    private val auth: FirebaseAuth
) {

    fun getCurrentUserId(): String? = auth.currentUser?.uid
    fun isSignedIn(): Boolean      = auth.currentUser != null

    val authState: Flow<FirebaseUser?> = callbackFlow {
        val l = FirebaseAuth.AuthStateListener { trySend(it.currentUser).isSuccess }
        auth.addAuthStateListener(l)
        awaitClose { auth.removeAuthStateListener(l) }
    }

    fun signOut() = auth.signOut()


    suspend fun signInAnonymously(): AuthResult = authOp("Анонимный вход") {
        auth.signInAnonymously().await().user
    }

    suspend fun createUser(email: String, pwd: String): AuthResult = authOp("Регистрация") {
        auth.createUserWithEmailAndPassword(email, pwd).await().user
    }

    suspend fun linkAnonymousUserWithEmail(email: String, pwd: String): AuthResult {
        val anon = auth.currentUser ?: return AuthResult.Error("Нет анонимного пользователя")
        return authOp("Связывание") {
            val cred = EmailAuthProvider.getCredential(email, pwd)
            anon.linkWithCredential(cred).await().user
        }
    }

    suspend fun signIn(email: String, pwd: String): AuthResult = authOp("Вход") {
        auth.signInWithEmailAndPassword(email, pwd).await().user
    }


    private inline fun authOp(tag: String, block: () -> FirebaseUser?): AuthResult = try {
        val user = block()
        if (user != null) AuthResult.Success(user)
        else AuthResult.Error("$tag: user == null")
    } catch (e: Exception) {
        if (e is CancellationException) throw e
        Log.e("RegistrationRepository", "$tag: ${e.message}")
        AuthResult.Error("$tag: ${e.message ?: "неизвестная ошибка"}")
    }
}


