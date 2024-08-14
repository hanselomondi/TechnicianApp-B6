package com.example.technicianapp.ui.view_models

import androidx.lifecycle.ViewModel
import com.example.technicianapp.firebase_functions.createUser
import com.example.technicianapp.firebase_functions.saveUserProfileToFirestore
import com.example.technicianapp.models.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

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

    suspend fun signUp() {
        try {
            val authResult = createUser(_email.value, _password.value)

            authResult.onSuccess { uid ->
                val profileResult = saveUserProfileToFirestore(
                    uid = uid,
                    user = User(
                        _firstName.value,
                        _lastName.value,
                        _phone.value,
                        _email.value
                    )
                )

                _authResult.value = profileResult.map { "Success document" }
            }
        } catch (e: Exception) {
            _authResult.value = Result.failure(e)
        }
    }
}