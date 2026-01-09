package com.example.plswork.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

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
}
