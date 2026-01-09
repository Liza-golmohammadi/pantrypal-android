package com.example.plswork.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.plswork.viewmodel.RecipeViewModel
import com.example.plswork.auth.AuthManager
import com.example.plswork.ui.components.ProfileIconButton
import androidx.navigation.NavHostController

sealed class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val label: String
) {
    object Home : BottomNavItem("home", Icons.Default.Home, "Home")
    object Search : BottomNavItem("search", Icons.Default.Search, "Search")
    object ShoppingList : BottomNavItem("shopping", Icons.Default.ShoppingCart, "Shopping list")
    object Skills : BottomNavItem("skills", Icons.Default.Star, "Skills")
    object Favorites : BottomNavItem("favorites", Icons.Default.Favorite, "Favorites")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    recipeViewModel: RecipeViewModel = viewModel(),
    navController: NavHostController,
    onLogout: (() -> Unit)? = null
) {
    var selectedTab by remember { mutableStateOf<BottomNavItem>(BottomNavItem.Home) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    val authManager = remember { AuthManager() }

    // Logout confirmation dialog
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Logout") },
            text = { Text("Are you sure you want to logout?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        authManager.logoutUser()
                        showLogoutDialog = false
                        onLogout?.invoke()
                    }
                ) {
                    Text("Logout", color = Color(0xFFFF69B4))
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Pantry Pal")
                        authManager.getUserEmail()?.let {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFFF69B4),
                    titleContentColor = Color.White
                ),
                actions = {
                    IconButton(onClick = { showLogoutDialog = true }) {
                        Icon(
                            Icons.Default.ExitToApp,
                            contentDescription = "Logout",
                            tint = Color.White
                        )
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color.White
            ) {
                val items = listOf(
                    BottomNavItem.Home,
                    BottomNavItem.Search,
                    BottomNavItem.ShoppingList,
                    BottomNavItem.Skills,
                    BottomNavItem.Favorites
                )
                items.forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) },
                        selected = selectedTab == item,
                        onClick = { selectedTab = item },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = DarkPink,
                            selectedTextColor = DarkPink,
                            indicatorColor = PinkHeader
                        )
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when (selectedTab) {
                is BottomNavItem.Home -> HomeDiscoverScreen(recipeViewModel, navController = navController)  // Add the parameter back!
                is BottomNavItem.Search -> SearchScreen(recipeViewModel)
                is BottomNavItem.ShoppingList -> ShoppingListScreen()
                is BottomNavItem.Skills -> SkillsScreen()
                is BottomNavItem.Favorites -> FavoritesScreen(recipeViewModel)
            }
        }
    }
}
