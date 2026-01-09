package com.example.plswork.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.plswork.ui.screens.LoginScreen
import com.example.plswork.ui.screens.RegisterScreen
import com.example.plswork.ui.screens.MainScreen
import com.example.plswork.viewmodel.RecipeViewModel
import com.example.plswork.auth.AuthManager

@Composable
fun AppNavGraph(navController: NavHostController) {
    // Create a shared ViewModel at this level so it persists across bottom nav tabs
    val recipeViewModel: RecipeViewModel = viewModel()
    val authManager = AuthManager()

    // Check if user is already logged in on app start
    val startDestination = if (authManager.isUserLoggedIn()) {
        "main"
    } else {
        "login"
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Login screen
        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    // Navigate to main app after successful login
                    navController.navigate("main") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onRegisterClick = {
                    navController.navigate("register")
                }
            )
        }

        // Register screen
        composable("register") {
            RegisterScreen(
                onRegisterSuccess = {
                    // Navigate to main app after successful registration
                    navController.navigate("main") {
                        popUpTo("register") { inclusive = true }
                    }
                },
                onLoginClick = {
                    navController.navigate("login") {
                        popUpTo("register") { inclusive = true }
                    }
                }
            )
        }

        // Main app with bottom navigation
        // Main app with bottom navigation
        composable("main") {
            MainScreen(
                recipeViewModel = recipeViewModel,
                onLogout = {
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

    } }
