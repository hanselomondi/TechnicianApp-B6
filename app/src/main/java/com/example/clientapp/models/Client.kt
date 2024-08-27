package com.example.clientapp.models

data class Client(
    val firstName: String = "",
    val lastName: String = "",
    val phone: String = "",
    val email: String = "",
    val profilePicture: String = "",
    val bio: String = "",
    val rating: Float = 0F, // maybe do away with
    val servicesOffered: Map<String, List<String>> = emptyMap(),
    // val availabilityStatus: Boolean, // while at work, maybe add a "working/busy" status
    val workingHours: Map<String, String> = emptyMap(),
    // val appointments: List<String>
)
