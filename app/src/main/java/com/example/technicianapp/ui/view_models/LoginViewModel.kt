package com.example.technicianapp.ui.view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.technicianapp.firebase_functions.authenticateUser
import com.example.technicianapp.firebase_functions.createUser
import com.example.technicianapp.results.AuthResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password

    private val _authResult = MutableStateFlow<AuthResult>(AuthResult.Failure(""))
    val authResult = _authResult.asStateFlow()

    fun onEmailChange(newEmail: String) {
        _email.value = newEmail
    }

    fun onPasswordChange(newPassword: String) {
        _password.value = newPassword
    }

    fun login() {
        viewModelScope.launch {
            val result = authenticateUser(_email.value, _password.value)

            _authResult.value = result
        }
    }
}