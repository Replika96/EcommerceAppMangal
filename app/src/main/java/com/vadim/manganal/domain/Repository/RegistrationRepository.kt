package com.vadim.manganal.domain.Repository


import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow


interface RegistrationRepository{
    fun getCurrentUserId(): String?
    fun isSignedIn(): Boolean

    val authState: Flow<FirebaseUser?>
    fun signOut()
    suspend fun signInAnonymously(): AuthResult
    suspend fun createUser(email: String, pwd: String): AuthResult
    suspend fun linkAnonymousUserWithEmail(email: String, pwd: String): AuthResult
    suspend fun signIn(email: String, pwd: String): AuthResult

    suspend fun authOp(tag: String, block: suspend () -> FirebaseUser?): AuthResult

}
sealed class AuthResult {
    data class Success(val user: FirebaseUser) : AuthResult()
    data class Error(val message: String)      : AuthResult()
}




