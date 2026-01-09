package com.example.plswork.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import com.google.firebase.auth.EmailAuthProvider

class AuthManager {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    // Get current user
    fun getCurrentUser(): FirebaseUser? = auth.currentUser

    // Check if user is logged in
    fun isUserLoggedIn(): Boolean = getCurrentUser() != null

    // Register new user
    suspend fun registerUser(
        email: String,
        password: String,
        fullName: String
    ): kotlin.Result<FirebaseUser> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user

            if (user != null) {
                // Save user data to Firestore
                val userData = hashMapOf(
                    "uid" to user.uid,
                    "email" to email,
                    "fullName" to fullName,
                    "createdAt" to System.currentTimeMillis()
                )

                firestore.collection("users")
                    .document(user.uid)
                    .set(userData)
                    .await()

                kotlin.Result.success(user)
            } else {
                kotlin.Result.failure(Exception("User creation failed"))
            }
        } catch (e: Exception) {
            kotlin.Result.failure(e)
        }
    }

    // Login user
    suspend fun loginUser(
        email: String,
        password: String
    ): kotlin.Result<FirebaseUser> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val user = result.user

            if (user != null) {
                kotlin.Result.success(user)
            } else {
                kotlin.Result.failure(Exception("Login failed"))
            }
        } catch (e: Exception) {
            kotlin.Result.failure(e)
        }
    }

    // Logout user
    fun logoutUser() {
        auth.signOut()
    }

    // Get user email
    fun getUserEmail(): String? = getCurrentUser()?.email

    // Get user ID
    fun getUserId(): String? = getCurrentUser()?.uid

    // Re-authenticate user
    private suspend fun reauthenticate(currentPassword: String): kotlin.Result<Unit> {
        return try {
            val user = getCurrentUser() ?: return kotlin.Result.failure(Exception("No user logged in"))
            val email = user.email ?: return kotlin.Result.failure(Exception("No email found"))

            val credential = EmailAuthProvider.getCredential(email, currentPassword)
            user.reauthenticate(credential).await()

            kotlin.Result.success(Unit)
        } catch (e: Exception) {
            kotlin.Result.failure(e)
        }
    }

    // Change email
    suspend fun changeEmail(currentPassword: String, newEmail: String): kotlin.Result<Unit> {
        return try {
            val user = getCurrentUser() ?: return kotlin.Result.failure(Exception("No user logged in"))

            val reauth = reauthenticate(currentPassword)
            if (reauth.isFailure) return kotlin.Result.failure(reauth.exceptionOrNull()!!)

            user.updateEmail(newEmail).await()

            kotlin.Result.success(Unit)
        } catch (e: Exception) {
            kotlin.Result.failure(e)
        }
    }

    // Change password
    suspend fun changePassword(currentPassword: String, newPassword: String): kotlin.Result<Unit> {
        return try {
            val user = getCurrentUser() ?: return kotlin.Result.failure(Exception("No user logged in"))

            val reauth = reauthenticate(currentPassword)
            if (reauth.isFailure) return kotlin.Result.failure(reauth.exceptionOrNull()!!)

            user.updatePassword(newPassword).await()

            kotlin.Result.success(Unit)
        } catch (e: Exception) {
            kotlin.Result.failure(e)
        }
    }

}
