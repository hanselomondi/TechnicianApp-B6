package com.example.technicianapp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.technicianapp.ui.screens.ClientChatScreen
import com.example.technicianapp.ui.screens.EditTechProfileScreen
import com.example.technicianapp.ui.screens.HomeScreen
import com.example.technicianapp.ui.screens.LoginScreen
import com.example.technicianapp.ui.screens.SignUpScreen
import com.example.technicianapp.ui.screens.ServicesSelectionScreen
import com.example.technicianapp.ui.screens.TechnicianChatListScreen
import com.example.technicianapp.ui.view_models.HomeViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val auth = FirebaseAuth.getInstance()
    val currentUser by remember { mutableStateOf(auth.currentUser) }

    // check if a user is logged in or not and navigate to the appropriate screen
    LaunchedEffect(currentUser) {
        if (currentUser != null) {
            navController.navigate(NavDestinations.HOME.name) {
                popUpTo(NavDestinations.LOGIN.name) { inclusive = true }
            }
        } else {
            navController.navigate(NavDestinations.LOGIN.name) {
                popUpTo(NavDestinations.HOME.name) {
                    inclusive = true
                } // TODO if location of logout changes, change from HOME
            }

        }

        /*navController.navigate(NavDestinations.TECHNICIAN_CHAT_LIST.name) {
            popUpTo(0) { inclusive = true }
        }*/
    }

    NavHost(
        navController,
        startDestination = if (currentUser != null) NavDestinations.HOME.name else NavDestinations.SIGN_UP.name
    ) {
        composable(NavDestinations.LOGIN.name) { LoginScreen(navController) }
        composable(NavDestinations.SIGN_UP.name) { SignUpScreen(navController) }
        composable(NavDestinations.HOME.name) { //backStackEntry ->
            //val techID = backStackEntry.arguments?.getString("techID") ?: currentUser!!.uid
            HomeScreen(
                navController = navController,
                viewModel = HomeViewModel(),
                techID = currentUser!!.uid
            )
        }
        // For first time login
        composable("${NavDestinations.HOME.name}/{techID}") { backStackEntry ->
            val techID = backStackEntry.arguments?.getString("techID") ?: currentUser!!.uid
            HomeScreen(
                navController = navController,
                viewModel = HomeViewModel(),
                techID = techID
            )
        }
        composable(NavDestinations.SERVICES_SELECTION.name) { ServicesSelectionScreen(navController) }
        composable(NavDestinations.TECHNICIAN_CHAT_LIST.name) {
            TechnicianChatListScreen(navController = navController, techId = currentUser!!.uid)
        }
        composable("chat_screen/{clientId}") { backStackEntry ->
            val clientId = backStackEntry.arguments?.getString("clientId")
            ClientChatScreen(
                clientId = clientId,
                navController = navController,
                techID = currentUser!!.uid
            )
        }
        composable(NavDestinations.EDIT_TECH_PROFILE.name) {
            EditTechProfileScreen()
        }
    }
}

// screens
enum class NavDestinations {
    LOGIN,
    SIGN_UP,
    HOME,
    SERVICES_SELECTION,
    TECHNICIAN_CHAT_LIST,
    EDIT_TECH_PROFILE
}