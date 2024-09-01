package com.example.clientapp.firebase_functions

import android.util.Log
import com.example.clientapp.models.Client
import com.example.clientapp.models.Technician
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.toObject
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
        // Suspend until the set operation is complete for the client collection
        clientDocRef.set(clientMessageData, SetOptions.merge()).await()
        Log.d("Firestore", "Client Message added to Client Collection successfully.")

        // Suspend until the set operation is complete for the tech collection
        techDocRef.set(clientMessageData, SetOptions.merge()).await()
        Log.d("Firestore", "Client Message added to Tech Collection successfully.")

        Result.success("Client Message added to Firestore successfully.")
    } catch (exception: Exception) {
        Log.e("Firestore", "Error adding Client message: $exception")

        Result.failure(Exception("Error adding Client message: $exception"))
    }
}


suspend fun getChatList(): Result<List<DocumentSnapshot>> {
    val db = FirebaseFirestore.getInstance()
    val uid = FirebaseAuth.getInstance().currentUser!!.uid

    val chatCollection = db.collection("Clients").document(uid).collection("Chats")

    return try {
        val result = withContext(Dispatchers.IO) {
            chatCollection.get().await()
        }

        if (!result.isEmpty) {
            Log.d("Firestore", "Chat list fetched successfully.\n${result.documents}")

            Result.success(result.documents)
        } else {
            Log.e("Firestore", "No chats found.")

            Result.failure(Exception("No chats found"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
}

suspend fun recommendTechnicians(inferredServices: List<String>): List<Technician> {
    val db = FirebaseFirestore.getInstance()

    // Fetch all technician documents from the "Technicians" collection and convert them to Technician objects
    val technicians = db.collection("Technicians")
        .get().await().documents
        .mapNotNull { it.toObject<Technician>() } // Convert each document to a Technician object

    // Calculate the number of matching services for each technician
    val technicianScores = technicians.map { technician ->
        val matchedServices = inferredServices.count { service ->
            technician.servicesOffered.values.flatten().contains(service)
        }
        technician to matchedServices
    }

    // Group technicians by the number of services they offer and sort within each group by rating
    return technicianScores
        .filter { it.second > 0 } // Only include technicians that offer at least one of the requested services
        .sortedWith(compareByDescending<Pair<Technician, Int>> { it.second } // Sort by number of matched services
            .thenByDescending { it.first.rating }) // Then sort by rating within each group
        .map { it.first } // Return only the technicians
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