package com.example.technicianapp

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.technicianapp.ui.screens.HomeScreen
import com.example.technicianapp.ui.screens.LoginScreen
import com.example.technicianapp.ui.screens.SignUpScreen
import com.example.technicianapp.ui.screens.SkillsSelectionScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController, startDestination = "skills_section") {
        composable("login") { LoginScreen(navController) }
        composable("sign_up") { SignUpScreen(navController) }
        composable("home") { HomeScreen(navController) }
        composable("skills_section") { SkillsSelectionScreen(navController) }
    }
}