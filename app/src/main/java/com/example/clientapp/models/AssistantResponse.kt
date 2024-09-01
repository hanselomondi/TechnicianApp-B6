package com.example.clientapp.models

data class AssistantResponse(
    val response: String = "",
    val inferredServices: List<String> = emptyList()
)
