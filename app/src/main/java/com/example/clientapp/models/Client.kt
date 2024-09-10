package com.example.clientapp.models

data class Client(
    val firstName: String = "",
    val lastName: String = "",
    val phone: String = "",
    val email: String = "",
    val profilePicture: String = "",
    val bio: String = "",
    val location: String = "",
    val rating: Float = 0F, // maybe do away with
)