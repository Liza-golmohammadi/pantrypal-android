package com.example.plswork.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
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

data class MealCategory(
    val name: String,
    val emoji: String,
    val query: String,
    val diet: String? = null
)

@Composable
fun HomeDiscoverScreen(viewModel: RecipeViewModel) {
    val categories = listOf(
        MealCategory("Low Carb", "🥗", "dinner", "low carb"),
        MealCategory("+45g Protein", "🍗", "dinner", "high protein"),
        MealCategory("Meal Prep", "🍱", "meal prep", null),
        MealCategory("Freezer Friendly", "🧊", "freezer meals", null),
        MealCategory("High Protein, Low Cal", "💪", "chicken", "high protein"),
        MealCategory("Batch Cook", "🍲", "one pot meals", null)
    )

    val recipeState by viewModel.recipeState.collectAsState()
    val recipeDetailState by viewModel.recipeDetailState.collectAsState()
    var showRecipeList by remember { mutableStateOf(false) }
    var showRecipeDetail by remember { mutableStateOf(false) }
    var selectedRecipeId by remember { mutableStateOf<Int?>(null) }

    // Watch for recipe state changes
    LaunchedEffect(recipeState) {
        if (recipeState is RecipeUiState.Success) {
            showRecipeList = true
        }
    }

    when {
        showRecipeDetail && selectedRecipeId != null -> {
            // Show recipe detail
            when (val state = recipeDetailState) {
                is RecipeDetailUiState.Loading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = DarkPink)
                    }
                }
                is RecipeDetailUiState.Success -> {
                    RecipeDetailScreen(
                        recipeDetail = state.recipeDetail,
                        isLoading = false,
                        onBack = {
                            showRecipeDetail = false
                            selectedRecipeId = null
                            viewModel.resetDetailState()
                        },
                        onOpenWebsite = {},
                        onOpenYouTube = {}
                    )
                }
                else -> {}
            }
        }
        showRecipeList -> {
            // Show recipe list
            when (val state = recipeState) {
                is RecipeUiState.Success -> {
                    RecipeResultsScreen(
                        selectedIngredients = emptyList(),
                        recipes = state.recipes,
                        onBack = {
                            showRecipeList = false
                            viewModel.resetRecipeState()
                        },
                        onRecipeClick = { recipeId ->
                            selectedRecipeId = recipeId
                            showRecipeDetail = true
                            viewModel.getRecipeDetails(recipeId)
                        },
                        viewModel = viewModel  // PASSES VIEWMODEL FOR FAVORITES
                    )
                }
                else -> {}
            }
        }
        else -> {
            // Show home screen
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(PinkBackground)
                    .padding(16.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Profile",
                            modifier = Modifier.size(40.dp),
                            tint = Color.Black
                        )
                    }
                }

                item {
                    Text(
                        text = "Plan your week",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }

                item {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(categories.chunked(2)) { rowCategories ->
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                rowCategories.forEach { category ->
                                    CategoryCard(category) {
                                        viewModel.searchByQuery(category.query, category.diet)
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                }

                item {
                    Text(
                        text = "Personalised picks for you",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                item {
                    PersonalizedPickCard(
                        title = "Protein-Packed Breakfasts",
                        description = "Energising breakfasts to fuel your day.",
                        imageEmoji = "🥞",
                        onClick = { viewModel.searchByQuery("breakfast", "high protein") }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                item {
                    PersonalizedPickCard(
                        title = "Quick 15-Minute Dinners",
                        description = "Fast meals for busy weeknights.",
                        imageEmoji = "⚡",
                        onClick = { viewModel.searchByQuery("quick dinner", null) }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                item {
                    PersonalizedPickCard(
                        title = "Vegetarian Delights",
                        description = "Delicious plant-based recipes.",
                        imageEmoji = "🥬",
                        onClick = { viewModel.searchByQuery("vegetarian", "vegetarian") }
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryCard(category: MealCategory, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(160.dp)
            .height(80.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Text(text = category.emoji, fontSize = 32.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = category.name,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
        }
    }
}

@Composable
fun PersonalizedPickCard(
    title: String,
    description: String,
    imageEmoji: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = PinkHeader),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(24.dp)
            ) {
                Text(text = imageEmoji, fontSize = 72.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    fontSize = 14.sp,
                    color = Color.DarkGray
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onClick,
                    colors = ButtonDefaults.buttonColors(containerColor = DarkPink),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("View recipes", fontSize = 14.sp)
                }
            }
        }
    }
}
