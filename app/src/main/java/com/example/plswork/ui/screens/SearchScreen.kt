package com.example.plswork.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.plswork.viewmodel.RecipeViewModel
import com.example.plswork.viewmodel.RecipeUiState
import com.example.plswork.viewmodel.RecipeDetailUiState
import com.example.plswork.network.Recipe

@Composable
fun SearchScreen(viewModel: RecipeViewModel) {
    var showResults by remember { mutableStateOf(false) }
    var selectedIngredients by remember { mutableStateOf(listOf<Ingredient>()) }
    val recipeState by viewModel.recipeState.collectAsState()

    val availableIngredients = remember {
        listOf(
            Ingredient("Tomato", "🍅"),
            Ingredient("Egg", "🥚"),
            Ingredient("Rice", "🍚"),
            Ingredient("Chicken", "🍗"),
            Ingredient("Onion", "🧅"),
            Ingredient("Garlic", "🧄"),
            Ingredient("Pepper", "🌶️"),
            Ingredient("Salt", "🧂"),
            Ingredient("Potato", "🥔"),
            Ingredient("Carrot", "🥕")
        )
    }

    LaunchedEffect(recipeState) {
        if (recipeState is RecipeUiState.Success) {
            showResults = true
        }
    }

    if (!showResults) {
        IngredientSelectionScreen(
            availableIngredients = availableIngredients,
            selectedIngredients = selectedIngredients,
            onIngredientSelected = { ingredient ->
                if (!selectedIngredients.contains(ingredient)) {
                    selectedIngredients = selectedIngredients + ingredient
                }
            },
            onIngredientRemoved = { ingredient ->
                selectedIngredients = selectedIngredients.filter { it != ingredient }
            },
            onFindRecipes = {
                if (selectedIngredients.isNotEmpty()) {
                    viewModel.searchRecipes(selectedIngredients.map { it.name })
                }
            },
            isLoading = recipeState is RecipeUiState.Loading,
            errorMessage = if (recipeState is RecipeUiState.Error) {
                (recipeState as RecipeUiState.Error).message
            } else null
        )
    } else {
        when (val state = recipeState) {
            is RecipeUiState.Success -> {
                SearchResultsWithDetail(
                    recipes = state.recipes,
                    selectedIngredients = selectedIngredients,
                    viewModel = viewModel,
                    onBack = {
                        showResults = false
                        selectedIngredients = emptyList()
                        viewModel.resetRecipeState()
                    }
                )
            }
            else -> {
                showResults = false
            }
        }
    }
}

@Composable
private fun SearchResultsWithDetail(
    recipes: List<Recipe>,
    selectedIngredients: List<Ingredient>,
    viewModel: RecipeViewModel,
    onBack: () -> Unit
) {
    var showDetailScreen by remember { mutableStateOf(false) }
    var selectedRecipeId by remember { mutableStateOf<Int?>(null) }
    val recipeDetailState by viewModel.recipeDetailState.collectAsState()

    if (showDetailScreen && selectedRecipeId != null) {
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
            is RecipeDetailUiState.Error -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(PinkBackground)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        "Failed to load recipe details",
                        color = Color.Red,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            showDetailScreen = false
                            selectedRecipeId = null
                            viewModel.resetDetailState()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = DarkPink)
                    ) {
                        Text("Go Back")
                    }
                }
            }
            else -> {}
        }
    } else {
        RecipeResultsScreen(
            selectedIngredients = selectedIngredients,
            recipes = recipes,
            onBack = onBack,
            onRecipeClick = { recipeId ->
                selectedRecipeId = recipeId
                showDetailScreen = true
                viewModel.getRecipeDetails(recipeId)
            },
            viewModel = viewModel  // PASSES VIEWMODEL FOR FAVORITES
        )
    }
}
