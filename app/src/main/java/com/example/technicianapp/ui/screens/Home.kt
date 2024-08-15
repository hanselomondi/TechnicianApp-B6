package com.example.technicianapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.technicianapp.NavDestinations
import com.example.technicianapp.ui.view_models.HomeViewModel
import kotlin.math.sign

@Composable
fun HomeScreen(navController: NavController, viewModel: HomeViewModel = viewModel()) {
    val signedOut by viewModel.signedOut.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Welcome to the Home Screen!")

        Text(viewModel.getAccountDetails())

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                navController.navigate(NavDestinations.SERVICES_SELECTION.name)
            }
        ) {
            Text("Add Services")
        }

        Button(
            onClick = {
                viewModel.logout()
            }
        ) {
            Text("Logout")
        }
    }

    signedOut.onSuccess {
        navController.navigate(NavDestinations.LOGIN.name) {
            // Clear the back stack to only have LOGIN as the top destination
            popUpTo(NavDestinations.LOGIN.name) { inclusive = true }
            launchSingleTop = true
        }
    }.onFailure { }

}
