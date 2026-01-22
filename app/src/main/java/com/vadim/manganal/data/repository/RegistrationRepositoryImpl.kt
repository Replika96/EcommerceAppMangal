package com.vadim.manganal.data.repository

import android.util.Log
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.vadim.manganal.domain.Repository.AuthResult
import com.vadim.manganal.domain.Repository.RegistrationRepository
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

@ViewModelScoped
class RegistrationRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth
): RegistrationRepository {

    override fun getCurrentUserId(): String? = auth.currentUser?.uid
    override fun isSignedIn(): Boolean      = auth.currentUser != null

    override val authState: Flow<FirebaseUser?> = callbackFlow {
        val l = FirebaseAuth.AuthStateListener { trySend(it.currentUser).isSuccess }
        auth.addAuthStateListener(l)
        awaitClose { auth.removeAuthStateListener(l) }
    }

    override fun signOut() = auth.signOut()


    override suspend fun signInAnonymously(): AuthResult = authOp("Анонимный вход") {
        auth.signInAnonymously().await().user
    }

    override suspend fun createUser(email: String, pwd: String): AuthResult = authOp("Регистрация") {
        auth.createUserWithEmailAndPassword(email, pwd).await().user
    }

    override suspend fun linkAnonymousUserWithEmail(email: String, pwd: String): AuthResult {
        val anon = auth.currentUser ?: return AuthResult.Error("Нет анонимного пользователя")
        return authOp("Связывание") {
            val cred = EmailAuthProvider.getCredential(email, pwd)
            anon.linkWithCredential(cred).await().user
        }
    }

    override suspend fun signIn(email: String, pwd: String): AuthResult = authOp("Вход") {
        auth.signInWithEmailAndPassword(email, pwd).await().user
    }


    override suspend fun authOp(tag: String, block: suspend () -> FirebaseUser?): AuthResult = try {
        val user = block()
        if (user != null) AuthResult.Success(user)
        else AuthResult.Error("$tag: user == null")
    } catch (e: Exception) {
        if (e is CancellationException) throw e
        Log.e("RegistrationRepository", "$tag: ${e.message}")
        AuthResult.Error("$tag: ${e.message ?: "неизвестная ошибка"}")
    }
}