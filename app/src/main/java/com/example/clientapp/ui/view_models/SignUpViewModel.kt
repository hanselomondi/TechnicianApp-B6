package com.example.clientapp.ui.view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clientapp.firebase_functions.createUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SignUpViewModel() : ViewModel() {
    private val _firstName = MutableStateFlow("")
    val firstName = _firstName.asStateFlow()

    private val _lastName = MutableStateFlow("")
    val lastName = _lastName.asStateFlow()

    private val _phone = MutableStateFlow("")
    val phone = _phone.asStateFlow()

    private val _email = MutableStateFlow("")
    val email = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()

    private val _confirmPassword = MutableStateFlow("")
    val confirmPassword = _confirmPassword.asStateFlow()

    private val _authResult = MutableStateFlow<Result<String>>(Result.failure(Exception("")))
    val authResult = _authResult.asStateFlow()


    fun onFirstNameChange(newFirstName: String) {
        _firstName.value = newFirstName
    }

    fun onLastNameChange(newLastName: String) {
        _lastName.value = newLastName
    }

    fun onPhoneChange(newPhone: String) {
        _phone.value = newPhone
    }

    fun onEmailChange(newEmail: String) {
        _email.value = newEmail
    }

    fun onPasswordChange(newPassword: String) {
        _password.value = newPassword
    }

    fun onConfirmPasswordChange(newConfirmPassword: String) {
        _confirmPassword.value = newConfirmPassword
    }

    fun signUp() {
        try {
            viewModelScope.launch {
                val authResult = createUser(_email.value, _password.value)

                authResult.onSuccess { uid ->
                    // TODO change to client
                    /*val profileResult = saveTechnicianToFirestore(
                        uid = uid,
                        tech = Technician(
                            firstName = _firstName.value,
                            lastName = _lastName.value,
                            phone = _phone.value,
                            email = _email.value,
                            profilePicture = "",
                            bio = "",
                            rating = 0f,
                            servicesOffered = mapOf(),
                            workingHours = mapOf(),
                        )
                    )

                    _authResult.value = profileResult*/
                }.onFailure {
                    _authResult.value = Result.failure(it)
                }
            }
        } catch (e: Exception) {
            _authResult.value = Result.failure(e)
        }
    }
}