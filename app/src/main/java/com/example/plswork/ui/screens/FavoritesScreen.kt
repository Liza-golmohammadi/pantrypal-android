package com.example.plswork.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.plswork.viewmodel.RecipeViewModel
import com.example.plswork.viewmodel.RecipeDetailUiState
import com.example.plswork.network.Recipe
import kotlinx.coroutines.launch

@Composable
fun FavoritesScreen(viewModel: RecipeViewModel) {
    val favorites by viewModel.favorites.collectAsState()
    var favoriteRecipes by remember { mutableStateOf<List<Recipe>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var showDetailScreen by remember { mutableStateOf(false) }
    var selectedRecipeId by remember { mutableStateOf<Int?>(null) }
    val recipeDetailState by viewModel.recipeDetailState.collectAsState()

    val scope = rememberCoroutineScope()

    // Load favorite recipes whenever favorites list changes
    LaunchedEffect(favorites) {
        if (favorites.isNotEmpty()) {
            isLoading = true
            scope.launch {
                try {
                    val recipes = favorites.mapNotNull { recipeId ->
                        try {
                            val detail = com.example.plswork.network.ApiClient.api.getRecipeDetails(
                                recipeId = recipeId,
                                apiKey = com.example.plswork.Constants.SPOONACULAR_API_KEY,
                                includeNutrition = false
                            )
                            // Convert RecipeDetail to Recipe for display
                            Recipe(
                                id = detail.id,
                                title = detail.title,
                                image = detail.image,
                                usedIngredientCount = 0,
                                missedIngredientCount = 0
                            )
                        } catch (e: Exception) {
                            null
                        }
                    }
                    favoriteRecipes = recipes
                } finally {
                    isLoading = false
                }
            }
        } else {
            favoriteRecipes = emptyList()
        }
    }

    if (showDetailScreen && selectedRecipeId != null) {
        // Show recipe detail
        when (val state = recipeDetailState) {
            is RecipeDetailUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = DarkPink)
                }
            }
            is RecipeDetailUiState.Success -> {
                RecipeDetailScreen(
                    recipeDetail = state.recipeDetail,
                    isLoading = false,
                    onBack = {
                        showDetailScreen = false
                        selectedRecipeId = null
                        viewModel.resetDetailState()
                    },
                    onOpenWebsite = {},
                    onOpenYouTube = {}
                )
            }
            else -> {}
        }
    } else {
        // Show favorites list
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(PinkBackground)
                .padding(16.dp)
        ) {
            Text(
                text = "Favorites",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(24.dp))

            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = DarkPink)
                    }
                }
                favorites.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text("❤️", fontSize = 64.sp)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "No favorites yet!",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Start exploring recipes and tap the heart to save them",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
                else -> {
                    Text(
                        "You have ${favorites.size} favorite recipe${if (favorites.size != 1) "s" else ""}",
                        fontSize = 16.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(favoriteRecipes) { recipe ->
                            RecipeCard(
                                recipe = recipe,
                                onClick = {
                                    selectedRecipeId = recipe.id
                                    showDetailScreen = true
                                    viewModel.getRecipeDetails(recipe.id)
                                },
                                viewModel = viewModel
                            )
                        }
                    }
                }
            }
        }
    }
}
