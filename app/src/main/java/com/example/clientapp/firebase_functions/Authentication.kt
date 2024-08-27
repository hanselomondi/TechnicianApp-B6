package com.example.clientapp.firebase_functions

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
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
            Result.failure(Exception("Failed to create user. Please try again."))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
}


/*
suspend fun updateAuthEmail(newEmail: String): Result<String> {
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser
        ?: return Result.failure(IllegalStateException("No user is currently signed in"))

    return try {
        currentUser.updateEmail(newEmail).await()
        Result.success("Email updated successfully").also {
            Log.d("Authentication", "Email updated successfully")
        }
    } catch (e: FirebaseAuthException) {

        Result.failure<String>(e).also {
            Log.e("Authentication", "Failed to update email: ${e.message}")
        }
    } catch (e: Exception) {

        Result.failure<String>(e).also {
            Log.e("Authentication", "Failed to update email: ${e.message}")
        }
    }
}*/
