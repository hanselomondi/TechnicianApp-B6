package com.example.clientapp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.clientapp.ui.screens.AssistantScreen
import com.example.clientapp.ui.screens.ChatListScreen
import com.example.clientapp.ui.screens.HomeScreen
import com.example.clientapp.ui.screens.LoginScreen

import com.example.clientapp.ui.screens.SignUpScreen
import com.example.clientapp.ui.screens.SpecificChatScreen
import com.example.clientapp.ui.view_models.HomeViewModel

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
    }

    NavHost(
        navController,
        startDestination = if (currentUser != null) NavDestinations.HOME.name else NavDestinations.LOGIN.name
    ) {
        composable(NavDestinations.LOGIN.name) {
            LoginScreen(navController = navController)
        }
        composable(NavDestinations.SIGN_UP.name) {
            SignUpScreen(navController)
        }
        composable(NavDestinations.HOME.name) { //backStackEntry ->
            //val techID = backStackEntry.arguments?.getString("techID") ?: currentUser!!.uid
            HomeScreen(
                navController = navController,
                viewModel = HomeViewModel(),
            )
        }
        composable(NavDestinations.PROMPT_SCREEN.name) {
            AssistantScreen(navController = navController)
        }
        // For first time login
        /*composable("${NavDestinations.HOME.name}/{techID}") { backStackEntry ->
            val techID = backStackEntry.arguments?.getString("techID") ?: currentUser!!.uid
            HomeScreen(
                navController = navController,
                viewModel = HomeViewModel(),
                techID = techID
            )
        }*/
        composable(NavDestinations.CHAT_LIST.name) {
            ChatListScreen(navController = navController)
        }
        composable(NavDestinations.SPECIFIC_CHAT.name + "/{techID}") { backStackEntry ->
            val techID = backStackEntry.arguments?.getString("techID")
            SpecificChatScreen(
                clientID = currentUser!!.uid,
                techID = techID
            )
        }
        /*composable(NavDestinations.EDIT_PROFILE.name) {
            EditProfileScreen()
        }*/
    }
}

// screens
enum class NavDestinations {
    LOGIN,
    SIGN_UP,
    HOME,
    CHAT_LIST,
    SPECIFIC_CHAT,
    PROMPT_SCREEN,
    EDIT_PROFILE
}

