package com.example.plswork.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.plswork.viewmodel.RecipeViewModel

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

@Composable
fun MainScreen(recipeViewModel: RecipeViewModel = viewModel()) {
    var selectedTab by remember { mutableStateOf<BottomNavItem>(BottomNavItem.Home) }

    Scaffold(
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
                is BottomNavItem.Home -> HomeDiscoverScreen(recipeViewModel)
                is BottomNavItem.Search -> SearchScreen(recipeViewModel)
                is BottomNavItem.ShoppingList -> ShoppingListScreen()
                is BottomNavItem.Skills -> SkillsScreen()
                is BottomNavItem.Favorites -> FavoritesScreen(recipeViewModel)
            }
        }
    }
}
