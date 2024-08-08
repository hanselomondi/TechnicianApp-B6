package com.example.technicianapp.firebase_functions

import com.example.technicianapp.results.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun authenticateUser(email: String, password: String): AuthResult {
    val auth = FirebaseAuth.getInstance()

    return try {
        // Attempt to sign in with email and password
        val authResult = withContext(Dispatchers.IO) {
            auth.signInWithEmailAndPassword(email, password).await()
        }

        // Check if the sign-in was successful
        if (authResult?.user != null) {
            AuthResult.Success(authResult.user!!.uid ?: "")
        } else {
            AuthResult.Failure("Authentication failed. Please try again.")
        }
    } catch (e: Exception) {
        // Handle errors and return a failure result
        AuthResult.Failure(e.message ?: "An unknown error occurred.")
    }
}

suspend fun createUser(email: String, password: String): Result<String> {
    val auth = FirebaseAuth.getInstance()

    return try {
        val authResult = withContext(Dispatchers.IO) {
            auth.createUserWithEmailAndPassword(email, password).await()
        }

        val uid = authResult.user?.uid

        if (uid != null) {
            Result.success(uid)
        } else {
            Result.failure(NullPointerException("User UID is null"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
}
