package com.example.clientapp.models

enum class Price(val displayName: String, val value: Int) {
    DAILY("Daily", 0),
    HOURLY("Hourly", 0),
    ONE_TIME("One-time", 0)
}