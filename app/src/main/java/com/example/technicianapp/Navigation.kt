package com.example.technicianapp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.technicianapp.ui.screens.HomeScreen
import com.example.technicianapp.ui.screens.LoginScreen
import com.example.technicianapp.ui.screens.SignUpScreen
import com.example.technicianapp.ui.screens.ServicesSelectionScreen
import com.google.firebase.auth.FirebaseAuth

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    val auth = FirebaseAuth.getInstance()
    val currentUser by remember { mutableStateOf(auth.currentUser) }

    LaunchedEffect(currentUser) {
        if (currentUser != null) {
            navController.navigate(NavDestinations.HOME.name) {
                popUpTo(NavDestinations.LOGIN.name) { inclusive = true }
            }
        } else {
            navController.navigate(NavDestinations.LOGIN.name) {
                popUpTo(NavDestinations.HOME.name) { inclusive = true } // TODO if location of logout changes, change from HOME
            }
        }
    }

    NavHost(
        navController,
        startDestination = if (currentUser != null) NavDestinations.HOME.name else NavDestinations.LOGIN.name
    ) {
        composable(NavDestinations.LOGIN.name) { LoginScreen(navController) }
        composable(NavDestinations.SIGN_UP.name) { SignUpScreen(navController) }
        composable(NavDestinations.HOME.name) { HomeScreen(navController) }
        composable(NavDestinations.SERVICES_SELECTION.name) { ServicesSelectionScreen(navController) }
    }
}

enum class NavDestinations {
    LOGIN,
    SIGN_UP,
    HOME,
    SERVICES_SELECTION
}