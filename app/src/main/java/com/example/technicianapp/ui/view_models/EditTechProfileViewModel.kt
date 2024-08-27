package com.example.technicianapp.ui.view_models

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.technicianapp.firebase_functions.editTechProfile
import com.example.technicianapp.firebase_functions.getTechProfileFromFirestore
import com.example.technicianapp.firebase_functions.updateAuthEmail
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EditTechProfileViewModel : ViewModel() {
    private var _buttonEnabled = MutableStateFlow(false)
    var buttonEnabled = _buttonEnabled.asStateFlow()

    private var _firstName = MutableStateFlow("")
    var firstName = _firstName.asStateFlow()

    private val _lastName = MutableStateFlow("")
    val lastName = _lastName.asStateFlow()

    private val _phone = MutableStateFlow("")
    val phone = _phone.asStateFlow()

    private val _email = MutableStateFlow("")
    val email = _email.asStateFlow()

    private val _profilePicture = MutableStateFlow("")
    val profilePicture = _profilePicture.asStateFlow()

    private val _bio = MutableStateFlow("")
    val bio = _bio.asStateFlow()

    private val _servicesOffered = MutableStateFlow<Map<String, List<String>>>(emptyMap())
    val servicesOffered = _servicesOffered.asStateFlow()

    private val _workingHours = MutableStateFlow<Map<String, String>>(emptyMap())
    val workingHours = _workingHours.asStateFlow()

    private val _updateResult = MutableStateFlow<Result<String>>(Result.failure(Exception("")))
    val updateResult = _updateResult.asStateFlow()

    private var _emailBefore = ""


    init {
        viewModelScope.launch {
            val tech = getTechProfileFromFirestore()

            tech.onSuccess { result ->
                _firstName.value = result.firstName
                _lastName.value = result.lastName
                _phone.value = result.phone
                _email.value = result.email.also {
                    _emailBefore = it
                }
                _profilePicture.value = result.profilePicture
                _bio.value = result.bio
                _servicesOffered.value = result.servicesOffered
                _workingHours.value = result.workingHours

            }.onFailure { error ->
                // TODO Handle error
            }
        }
    }


    fun onFirstNameChange(newFirstName: String) {
        _firstName.value = newFirstName

        _buttonEnabled.value = true
    }

    fun onLastNameChange(newLastName: String) {
        _lastName.value = newLastName

        _buttonEnabled.value = true
    }

    fun onPhoneChange(newPhone: String) {
        _phone.value = newPhone

        _buttonEnabled.value = true
    }

    fun onEmailChange(newEmail: String) {
        _email.value = newEmail

        _buttonEnabled.value = true
    }

    fun onProfilePictureChange(newProfilePicture: String) {
        _profilePicture.value = newProfilePicture

        _buttonEnabled.value = true
    }

    fun onBioChange(newBio: String) {
        _bio.value = newBio

        _buttonEnabled.value = true
    }

    fun onServicesOfferedChange(newServicesOffered: Map<String, List<String>>) {
        _servicesOffered.value = newServicesOffered

        _buttonEnabled.value = true
    }

    fun onWorkingHoursChange(newWorkingHours: Map<String, String>) {
        _workingHours.value = newWorkingHours

        _buttonEnabled.value = true
    }


    // TODO check for email change and call appropriate auth function
    fun updateProfile() {
        val fields = mapOf(
            "firstName" to _firstName.value,
            "lastName" to _lastName.value,
            "phone" to _phone.value,
            //"email" to _email.value,
            "profilePicture" to profilePicture.value,
            "bio" to _bio.value,
            "servicesOffered" to _servicesOffered.value,
            "workingHours" to _workingHours.value
        ).filterValues { it is String && it.isNotEmpty() || it is Map<*, *> && it.isNotEmpty() }

        // TODO get non-empty fields only
        viewModelScope.launch {
            // if the email has been changed, update the auth email first
            // TODO updating email requires confirmation link being sent. Disabled for now. Implement later
            /*if (_emailBefore != _email.value) {
                updateAuthEmail(_email.value).onSuccess {
                    val result = editTechProfile(fields)

                    _updateResult.value = result

                    _buttonEnabled.value = result.isSuccess.not()

                    return@launch
                }.onFailure {
                    _updateResult.value = Result.failure(it)

                    return@launch
                }
            }*/

            val result = editTechProfile(fields)

            _updateResult.value = result

            _buttonEnabled.value = result.isSuccess.not()
        }
    }
}
