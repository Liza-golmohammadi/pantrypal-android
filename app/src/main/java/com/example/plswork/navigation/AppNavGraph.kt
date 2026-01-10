package com.example.plswork.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.plswork.auth.AuthManager
import com.example.plswork.ui.screens.LoginScreen
import com.example.plswork.ui.screens.MainScreen
import com.example.plswork.ui.screens.OnboardingRoute      // ✅ FIXED (this is your intro screen)
import com.example.plswork.ui.screens.OnboardingQ1Screen
import com.example.plswork.ui.screens.OnboardingQ2Screen
import com.example.plswork.ui.screens.OnboardingQ3Screen
import com.example.plswork.ui.screens.ProfileScreen
import com.example.plswork.ui.screens.RegisterScreen
import com.example.plswork.viewmodel.OnboardingViewModel
import com.example.plswork.viewmodel.RecipeViewModel
import com.example.plswork.ui.screens.OnboardingQ4Screen

/**
 * AppNavGraph:
 * - Controls all navigation routes in the app
 * - If user is logged in -> go to Main
 * - If not logged in -> start onboarding intro screen
 * - Shares ViewModels so selections persist across screens
 */
@Composable
fun AppNavGraph(navController: NavHostController) {

    // ViewModel shared across main app screens
    val recipeViewModel: RecipeViewModel = viewModel()

    // ViewModel shared across onboarding screens (Q1/Q2/Q3...)
    val onboardingVm: OnboardingViewModel = viewModel()

    // Used to check login state
    val authManager = AuthManager()

    // Start screen depends on login state
    val startDestination = if (authManager.isUserLoggedIn()) "main" else "onboarding"

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {

        // ---------------- Intro Onboarding screen (End the "nothing to eat" era) ----------------
        composable("onboarding") {
            OnboardingRoute(
                onGetStarted = {
                    // Go to Q1 and remove intro from back stack
                    navController.navigate("onboarding_q1") {
                        popUpTo("onboarding") { inclusive = true }
                    }
                },
                onSignIn = {
                    // Go to Login and remove intro from back stack
                    navController.navigate("login") {
                        popUpTo("onboarding") { inclusive = true }
                    }
                }
            )
        }

        // ---------------- Onboarding Q1 ----------------
        composable("onboarding_q1") {
            OnboardingQ1Screen(
                vm = onboardingVm,
                onBack = { navController.popBackStack() },
                onContinue = { navController.navigate("onboarding_q2") }
            )
        }

        // ---------------- Onboarding Q2 ----------------
        composable("onboarding_q2") {
            OnboardingQ2Screen(
                vm = onboardingVm,
                onBack = { navController.popBackStack() },
                onContinue = { navController.navigate("onboarding_q3") }
            )
        }
// Q3
        composable("onboarding_q3") {
            OnboardingQ3Screen(
                vm = onboardingVm,
                onBack = { navController.popBackStack() },
                onContinue = { navController.navigate("onboarding_q4") }
            )
        }

// Q4
        composable("onboarding_q4") {
            OnboardingQ4Screen(
                vm = onboardingVm,
                onBack = { navController.popBackStack() },
                onContinue = { navController.navigate("register") }
            )
        }


        // ---------------- Login ----------------
        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("main") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onRegisterClick = { navController.navigate("register") }
            )
        }

        // ---------------- Register ----------------
        composable("register") {
            RegisterScreen(
                onRegisterSuccess = {
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

        // ---------------- Main ----------------
        composable("main") {
            MainScreen(
                recipeViewModel = recipeViewModel,
                navController = navController,
                onLogout = {
                    authManager.logoutUser()
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // ---------------- Profile ----------------
        composable("profile") {
            ProfileScreen(navController = navController)
        }
    }
}
