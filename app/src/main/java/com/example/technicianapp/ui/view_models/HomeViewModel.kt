package com.example.technicianapp.ui.view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// TODO probably take outFirebase logic from View Model

class HomeViewModel : ViewModel() {
    private val _signedOut = MutableStateFlow(false)
    val signedOut = _signedOut.asStateFlow()

    fun logout() {
        viewModelScope.launch {
            FirebaseAuth.getInstance().signOut()

            _signedOut.value = true
        }
    }

    fun getAccountDetails(): String {
        return FirebaseAuth.getInstance().currentUser?.email ?: "No email"
    }
}