package com.example.technicianapp.firebase_functions

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun authenticateUser(email: String, password: String): Result<String> {
    val auth = FirebaseAuth.getInstance()

    return try {
        // Attempt to sign in with email and password
        val signInResult = withContext(Dispatchers.IO) {
            auth.signInWithEmailAndPassword(email, password).await()
        }

        // Check if the sign-in was successful
        if (signInResult?.user != null) {
            Log.d("Authentication", "User signed in with UID: ${signInResult.user!!.uid}")

            Result.success(signInResult.user!!.uid)
        } else {
            Log.e("Authentication", "Authentication failed. Please try again.")

            Result.failure(Exception("Authentication failed. Please try again."))
        }
    } catch (e: Exception) {
        // TODO Handle errors and return a failure result
        Result.failure(e)
    }
}

// create a user with email & password and return the uid on success
suspend fun createUser(email: String, password: String): Result<String> {
    val auth = FirebaseAuth.getInstance()

    return try {
        val createResult = withContext(Dispatchers.IO) {
            auth.createUserWithEmailAndPassword(email, password).await()
        }

        val uid = createResult.user?.uid

        if (uid != null) {
            Result.success(uid)
        } else {
            Result.failure(NullPointerException("User UID is null"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
}
