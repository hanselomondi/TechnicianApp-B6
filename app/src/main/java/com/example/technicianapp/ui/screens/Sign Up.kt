package com.example.technicianapp.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.technicianapp.NavDestinations
import com.example.technicianapp.ui.view_models.SignUpViewModel
import kotlinx.coroutines.launch

//@Preview
@Composable
fun SignUpScreen(navController: NavController, signUpViewModel: SignUpViewModel = viewModel()) {
    val context = LocalContext.current

    val firstName by signUpViewModel.firstName.collectAsState()
    val lastName by signUpViewModel.lastName.collectAsState()
    val phone by signUpViewModel.phone.collectAsState()
    val email by signUpViewModel.email.collectAsState()
    val password by signUpViewModel.password.collectAsState()
    val confirmPassword by signUpViewModel.confirmPassword.collectAsState()
    val authResult by signUpViewModel.authResult.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp) // Space between items
    ) {
        item {
            TextField(
                value = firstName,
                onValueChange = { signUpViewModel.onFirstNameChange(it) },
                label = { Text("First Name") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            TextField(
                value = lastName,
                onValueChange = { signUpViewModel.onLastNameChange(it) },
                label = { Text("Last Name") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            TextField(
                value = phone,
                onValueChange = { signUpViewModel.onPhoneChange(it) },
                label = { Text("Phone Number") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            TextField(
                value = email,
                onValueChange = { signUpViewModel.onEmailChange(it) },
                label = { Text("Email") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            TextField(
                value = password,
                onValueChange = { signUpViewModel.onPasswordChange(it) },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation()
            )
        }

        item {
            TextField(
                value = confirmPassword,
                onValueChange = { signUpViewModel.onConfirmPasswordChange(it) },
                label = { Text("Confirm Password") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation()
            )
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            Button(
                onClick = {
                    signUpViewModel.signUp()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Sign Up")
            }
        }
    }


    authResult.onSuccess {
        Text("Signup successful. UID: $it")

        navController.navigate(NavDestinations.HOME.name) {
            // Clear the backstack and set Home as the only page
            popUpTo(navController.graph.startDestinationId) {
                inclusive = true
            }
            launchSingleTop = true
        }
    }.onFailure { failure ->
        if (failure.message?.isNotBlank() == true)
            Toast.makeText(
                context,
                failure.message,
                Toast.LENGTH_LONG
            ).show()
    }
}


