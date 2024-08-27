package com.example.technicianapp.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.technicianapp.ui.view_models.EditTechProfileViewModel

@Composable
fun EditTechProfileScreen(viewModel: EditTechProfileViewModel = viewModel()) {
    val context = LocalContext.current

    val firstName by viewModel.firstName.collectAsState()
    val lastName by viewModel.lastName.collectAsState()
    val phone by viewModel.phone.collectAsState()
    val email by viewModel.email.collectAsState()
    val profilePicture by viewModel.profilePicture.collectAsState()
    val bio by viewModel.bio.collectAsState()
    val updateResult by viewModel.updateResult.collectAsState()
    val buttonEnabled by viewModel.buttonEnabled.collectAsState()
    val servicesOffered by viewModel.servicesOffered.collectAsState()
    val workingHours by viewModel.workingHours.collectAsState()

    LazyColumn(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            TextField(
                value = firstName,
                onValueChange = { viewModel.onFirstNameChange(it) },
                label = { Text("First Name") }
            )
        }
        item {
            TextField(
                value = lastName,
                onValueChange = { viewModel.onLastNameChange(it) },
                label = { Text("Last Name") }
            )
        }
        item {
            TextField(
                value = phone,
                onValueChange = { viewModel.onPhoneChange(it) },
                label = { Text("Phone") }
            )
        }
        /*item {
            TextField(
                value = email,
                onValueChange = { viewModel.onEmailChange(it) },
                label = { Text("Email") }
            )
        }*/
        item {
            TextField(
                value = profilePicture,
                onValueChange = { viewModel.onProfilePictureChange(it) },
                label = { Text("Profile Picture URL") }
            )
        }
        item {
            TextField(
                value = bio,
                onValueChange = { viewModel.onBioChange(it) },
                label = { Text("Bio") }
            )
        }

        // Add more items for servicesOffered and workingHours as required

        item {
            Button(
                enabled = buttonEnabled,
                onClick = {
                    viewModel.updateProfile()
                }
            ) {
                Text("Save Changes")
            }
        }
    }

    updateResult.onSuccess { result ->
        Toast.makeText(context, result, Toast.LENGTH_LONG).show()
    }.onFailure { failure ->
        if (failure.message?.isNotBlank() == true) Toast.makeText(context, failure.message, Toast.LENGTH_LONG).show()
    }
}
