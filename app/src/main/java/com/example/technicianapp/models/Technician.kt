package com.example.technicianapp.models

data class Technician (
    val firstName: String,
    val lastName: String,
    val phone: String,
    val email: String,
    val profilePicture: String,
    val bio: String,
    val rating: Float, // maybe do away with
    // val userDetails: Map<String, String>, // fnm, lnm, phn, eml, pfp, bio, rtng
    val skills: Map<String, List<String>>,
    val availabilityStatus: Boolean, // while at work, maybe add a "working/busy" status
    val workingHours: Map<String, String>,
    val appointments: List<String>
)
