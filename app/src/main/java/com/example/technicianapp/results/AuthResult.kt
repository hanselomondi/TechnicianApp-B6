package com.example.technicianapp.results

sealed class AuthResult {
    data class Success(val uid: String): AuthResult()

    data class Failure(val errorMessage: String) : AuthResult()
}