package com.example.technicianapp.ui.view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    fun logout() {
        viewModelScope.launch {
            // Handle logout logic here
            FirebaseAuth.getInstance().signOut()

        }
    }

    fun getAccountDetails(): String {
        return FirebaseAuth.getInstance().currentUser?.email ?: "No email"
    }
}