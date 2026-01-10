package com.example.plswork.auth

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class AuthManager {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    fun isUserLoggedIn(): Boolean = auth.currentUser != null

    fun getUserEmail(): String? = auth.currentUser?.email

    fun logoutUser() {
        auth.signOut()
    }

    suspend fun loginUser(email: String, password: String): Result<Unit> = runCatching {
        auth.signInWithEmailAndPassword(email, password).awaitUnit()
    }

    suspend fun registerUser(email: String, password: String, fullName: String): Result<Unit> = runCatching {
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        val uid = result.user?.uid ?: throw IllegalStateException("User ID missing after registration")

        // Optional: store basic user profile in Firestore
        val userData = hashMapOf(
            "fullName" to fullName,
            "email" to email
        )
        db.collection("users").document(uid).set(userData).awaitUnit()
    }

    suspend fun changeEmail(currentPassword: String, newEmail: String): Result<Unit> = runCatching {
        val user = auth.currentUser ?: throw IllegalStateException("No user logged in")
        val currentEmail = user.email ?: throw IllegalStateException("No email found for current user")

        val credential = EmailAuthProvider.getCredential(currentEmail, currentPassword)
        user.reauthenticate(credential).awaitUnit()
        user.updateEmail(newEmail).awaitUnit()
    }

    suspend fun changePassword(currentPassword: String, newPassword: String): Result<Unit> = runCatching {
        val user = auth.currentUser ?: throw IllegalStateException("No user logged in")
        val currentEmail = user.email ?: throw IllegalStateException("No email found for current user")

        val credential = EmailAuthProvider.getCredential(currentEmail, currentPassword)
        user.reauthenticate(credential).awaitUnit()
        user.updatePassword(newPassword).awaitUnit()
    }

    // ----------------- Task helpers (no extra dependency needed) -----------------

    private suspend fun <T> Task<T>.await(): T =
        suspendCancellableCoroutine { cont ->
            addOnSuccessListener { result -> cont.resume(result) }
            addOnFailureListener { e -> cont.resumeWithException(e) }
        }

    private suspend fun Task<*>.awaitUnit(): Unit =
        suspendCancellableCoroutine { cont ->
            addOnSuccessListener { cont.resume(Unit) }
            addOnFailureListener { e -> cont.resumeWithException(e) }
        }
}
