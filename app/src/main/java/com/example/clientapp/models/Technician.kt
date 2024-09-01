package com.example.clientapp.models

data class Technician(
    val firstName: String = "",
    val lastName: String = "",
    val phone: String = "",
    val email: String = "",
    val profilePicture: String = "",
    val bio: String = "",
    val location: String = "",
    val rating: Float = 0F,
    val servicesOffered: Map<String, List<String>> = emptyMap(),
)