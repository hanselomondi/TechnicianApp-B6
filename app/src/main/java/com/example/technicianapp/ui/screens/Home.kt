package com.example.technicianapp.ui.screens
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.technicianapp.ui.view_models.HomeViewModel

@Composable
fun HomeScreen(navController: NavController, homeViewModel: HomeViewModel = viewModel()) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Welcome to the Home Screen!")

        Text(homeViewModel.getAccountDetails())

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                homeViewModel.logout()

                navController.navigate("login") {
                    popUpTo("login") { inclusive = true }
                }
            }
        ) {
            Text("Logout")
        }
    }
}