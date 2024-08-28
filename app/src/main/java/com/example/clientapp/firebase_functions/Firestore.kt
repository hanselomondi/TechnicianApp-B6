package com.example.clientapp.firebase_functions

import android.util.Log
import com.example.clientapp.models.Client
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


suspend fun saveClientToFirestore(
    uid: String,
    client: Client
): Result<String> { // TODO change return T
    val db = FirebaseFirestore.getInstance()

    return try {
        // Save the client profile to Firestore with the document ID set to the Client's UID
        db.collection("Clients").document(uid).set(client).await()

        Result.success("Account created successfully") // Return success if the operation completes without exceptions
    } catch (e: Exception) {
        // Return failure with an error message if an exception occurs
        Result.failure(e)
    }
}


suspend fun getTechProfileFromFirestore(): Result<Client> {
    val db = FirebaseFirestore.getInstance()
    val uid = FirebaseAuth.getInstance().currentUser!!.uid

    return try {
        // Get the Tech profile from Firestore using the User's UID
        val tech =
            db.collection("Technicians").document(uid).get().await()
                .toObject(Client::class.java)

        if (tech != null) {
            Log.d("Firestore", "Tech profile fetched successfully.")

            Result.success(tech) // Return success if the operation completes without exceptions
        } else {
            Log.e("Firestore", "Tech profile is null.")

            db.collection("Technicians").document(uid).set(Client()).await()

            Result.success(Client()).also {
                Log.d("Firestore", "Tech profile created successfully.")
            }
        }
    } catch (e: Exception) {
        Log.e("Firestore", "Error fetching Tech profile: $e")

        Result.failure(e)
    }
}

// maybe take a Technician object as a parameter instead?
suspend fun editTechProfile(fields: Map<String, Any>): Result<String> {
    val db = FirebaseFirestore.getInstance()
    val uid = FirebaseAuth.getInstance().currentUser!!.uid

    return try {
        val docRef = db.collection("Technicians").document(uid)
        docRef.update(fields).await()

        Result.success("Profile updated successfully").also {
            Log.d("Firestore", "Profile updated successfully.")
        }
    } catch (e: Exception) {

        Result.failure<String>(e).also {
            Log.e("Firestore", "Error updating profile: $e")
        }
    }
}

suspend fun fetchServices(): Result<Map<String, List<String>>> {

    return try {
        val firestore = FirebaseFirestore.getInstance()
        val doc = firestore.collection("Categories").document("Services").get().await()

        if (doc.exists()) {
            val data = doc.data ?: emptyMap<String, Any>()

            val servicesMap = data.mapValues { (_, value) ->
                (value as? List<*>)?.map { it.toString() } ?: emptyList()
            }

            Log.d(
                "Firestore",
                "fetchServices: Services fetched successfully. ${servicesMap.toSortedMap()}"
            )

            Result.success(servicesMap.toSortedMap())
        } else {
            Log.d("Firestore", "fetchServices: Document does not exist.")

            Result.failure(Exception("Document does not exist"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
}

// TODO handle updates
suspend fun addServicesToTechnicianFirestore(selectedSkills: Map<String, List<String>>): Result<String> {

    return try {
        val db = FirebaseFirestore.getInstance()
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: "Control_T1"
        val docRef = db.collection("Technicians").document(uid)

        docRef.set(mapOf("servicesOffered" to selectedSkills), SetOptions.merge())
            .await()

        Log.d(
            "Firestore",
            "addServicesToTechnicianFirestore: Skills successfully saved to Firestore for user $uid"
        )

        Result.success("Services successfully saved to Firestore for user $uid")
    } catch (e: Exception) {
        Log.e(
            "Firestore",
            "addServicesToTechnicianFirestore: Error saving Services to Firestore",
            e
        )

        Result.failure(e)
    }
}

suspend fun techCreateOrSendMessage(
    clientId: String,
    techId: String,
    message: String
): Result<String> {
    val db = FirebaseFirestore.getInstance()

    val techDocRef =
        db.collection("Technicians").document(techId).collection("Chats").document(clientId)
    val clientDocRef =
        db.collection("Clients").document(clientId).collection("Chats").document(techId)

    val randomTail = System.currentTimeMillis().toString()
    val techUniqueKey = "Tech_$randomTail"

    val techMessageData = mapOf(
        techUniqueKey to mapOf(
            "message" to message,
            "time" to Timestamp.now()
        )
    )

    return try {
        // Suspend until the set operation is complete for the tech collection
        techDocRef.set(techMessageData, SetOptions.merge()).await()
        Log.d("Firestore", "Tech Message added to Tech Collection successfully.")

        // Suspend until the set operation is complete for the client collection
        clientDocRef.set(techMessageData, SetOptions.merge()).await()

        Log.d("Firestore", "Tech Message added to Client Collection successfully.")

        Result.success("Tech Message added to Firestore successfully.")
    } catch (exception: Exception) {
        Log.e("Firestore", "Error adding tech message: $exception")

        Result.failure(Exception("Error adding tech message: $exception"))
    }
}

suspend fun clientCreateOrSendMessage(
    clientId: String,
    techId: String,
    message: String
): Result<String> {
    val db = FirebaseFirestore.getInstance()

    val techDocRef =
        db.collection("Technicians").document(techId).collection("Chats").document(clientId)
    val clientDocRef =
        db.collection("Clients").document(clientId).collection("Chats").document(techId)

    val randomTail = System.currentTimeMillis().toString()
    val clientUniqueKey = "Client_$randomTail"

    val clientMessageData = mapOf(
        clientUniqueKey to mapOf(
            "message" to message,
            "time" to Timestamp.now()
        )
    )

    return try {
        // Suspend until the set operation is complete for the tech collection
        techDocRef.set(clientMessageData, SetOptions.merge()).await()
        Log.d("Firestore", "Client Message added to Tech Collection successfully.")

        // Suspend until the set operation is complete for the client collection
        clientDocRef.set(clientMessageData, SetOptions.merge()).await()

        Log.d("Firestore", "Client Message added to Client Collection successfully.")

        Result.success("Client Message added to Firestore successfully.")
    } catch (exception: Exception) {
        Log.e("Firestore", "Error adding Client message: $exception")

        Result.failure(Exception("Error adding Client message: $exception"))
    }
}


suspend fun getClientsChatList(techId: String): Result<List<DocumentSnapshot>> {
    val db = FirebaseFirestore.getInstance()

    val clientsChatCollection = db.collection("Technicians").document(techId).collection("Chats")

    return try {
        val result = withContext(Dispatchers.IO) {
            clientsChatCollection.get().await()
        }

        if (!result.isEmpty) {
            Log.d("Firestore", "Clients chat list fetched successfully.\n${result.documents}")

            Result.success(result.documents)
        } else {
            Log.e("Firestore", "No clients found.")

            Result.failure(Exception("No clients found"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
}
