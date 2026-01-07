package com.example.plswork.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.plswork.ui.screens.LoginScreen
import com.example.plswork.ui.screens.RegisterScreen
import com.example.plswork.ui.screens.MainScreen
import com.example.plswork.viewmodel.RecipeViewModel

@Composable
fun AppNavGraph(navController: NavHostController) {
    // Create a shared ViewModel at this level so it persists across bottom nav tabs
    val recipeViewModel: RecipeViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        // Login screen
        composable("login") {
            LoginScreen(
                onLoginClick = { email, password ->
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
            RegisterScreen { username, email, password ->
                // Navigate back to login after successful registration
                navController.navigate("login") {
                    popUpTo("register") { inclusive = true }
                }
            }
        }

        // Main app with bottom navigation
        composable("main") {
            MainScreen(recipeViewModel = recipeViewModel)
        }
    }
}
