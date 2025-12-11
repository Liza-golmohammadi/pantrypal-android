package com.example.plswork.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.plswork.ui.screens.LoginScreen
import com.example.plswork.ui.screens.RegisterScreen
import com.example.plswork.ui.screens.PantryPalApp

@Composable
fun AppNavGraph(navController: NavHostController) {

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {

        composable("login") {
            LoginScreen(
                onLoginClick = { email, password ->
                    navController.navigate("main") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onRegisterClick = {
                    navController.navigate("register")
                }
            )
        }

        composable("register") {
            RegisterScreen { username, email, password ->
                navController.navigate("login") {
                    popUpTo("register") { inclusive = true }
                }
            }
        }

        composable("main") {
            PantryPalApp()
        }
    }
}
