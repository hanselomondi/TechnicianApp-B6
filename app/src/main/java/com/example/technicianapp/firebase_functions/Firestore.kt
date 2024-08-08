package com.example.technicianapp.firebase_functions

import com.example.technicianapp.models.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

suspend fun saveUserProfileToFirestore(uid: String, user: User): Result<Unit> { // TODO change return T
    val db = FirebaseFirestore.getInstance()

    return try {
        // Save the user profile to Firestore with the document ID set to the User's UID
        db.collection("Clients").document(uid).set(user).await()

        Result.success(Unit) // Return success if the operation completes without exceptions
    } catch (e: Exception) {
        // Return failure with an error message if an exception occurs
        Result.failure(e)
    }
}
