package com.example.clientapp.models

enum class Status(val displayName: String) {
    PENDING("Pending"),
    CONFIRMED("Confirmed"),
    CANCELLED("Cancelled"),
    IN_PROGRESS("In Progress"),
    DONE("Done")
}
