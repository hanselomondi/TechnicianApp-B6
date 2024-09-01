package com.example.clientapp.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference

// TODO add the extra stuff eg primary & secondary communication for both client and tech
data class Appointment(
    val clientID: DocumentReference,
    val techID: DocumentReference,

    val scheduledTime: Timestamp,
    val startTime: Timestamp,
    val endTime: Timestamp,

    val services: Map<String, List<String>>,
    val clientConfirmed: Boolean = false,
    val technicianConfirmed: Boolean = false,
    val status: String = if(clientConfirmed and(technicianConfirmed)) Status.CONFIRMED.displayName else Status.PENDING.displayName,
    val price: Map<String, Any> = hashMapOf(
        "Currency" to 0,
        Price.DAILY.displayName to Price.DAILY.value,
        Price.HOURLY.displayName to Price.HOURLY.value,
        Price.ONE_TIME.displayName to Price.ONE_TIME.value
    ),
    val finalPrice: Int = 0,
    val notes: String = "",
    //val rating: Float,
)


/*
price
(map)

currency
"KES"
(string)

daily
3400
(number)

hourly
20
(number)

one-time
""*/
