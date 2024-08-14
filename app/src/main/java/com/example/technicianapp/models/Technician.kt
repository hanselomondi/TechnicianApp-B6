package com.example.technicianapp.models

data class Technician (
    val firstName: String,
    val lastName: String,
    val phone: String,
    val email: String,
    val profilePicture: String,
    val bio: String,
    val rating: String,
    // val userDetails: Map<String, String>, // fnm, lnm, phn, eml, pfp, bio, rtng
    val skills: Map<String, List<String>>,
    val availabilityStatus: Boolean,
    val workingHours: Map<String, String>,
    val appointments: List<String>
)
