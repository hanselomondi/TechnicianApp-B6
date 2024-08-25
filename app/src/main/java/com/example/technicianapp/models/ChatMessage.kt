package com.example.technicianapp.models

import com.google.firebase.Timestamp

data class ChatMessage(
    val content: String,
    val timestamp: Timestamp,
    val isFromTech: Boolean
)