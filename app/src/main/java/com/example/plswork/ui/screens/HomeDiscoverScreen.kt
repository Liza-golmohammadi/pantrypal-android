package com.example.plswork.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.plswork.ui.theme.DarkPink
import com.example.plswork.ui.theme.PinkBackground
import com.example.plswork.ui.theme.PinkHeader
import com.example.plswork.viewmodel.RecipeDetailUiState
import com.example.plswork.viewmodel.RecipeUiState
import com.example.plswork.viewmodel.RecipeViewModel
import androidx.navigation.NavController
import com.example.plswork.ui.components.ProfileIconButton
data class MealCategory(
    val name: String,
    val emoji: String,
    val query: String,
    val diet: String? = null
)

@Composable
fun HomeDiscoverScreen(viewModel: RecipeViewModel, navController: NavController) {
    val categories = listOf(
        MealCategory("Low Carb", "🥗", "dinner", "low carb"),
        MealCategory("+45g Protein Dinners", "🍗", "dinner", "high protein"),
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

    LaunchedEffect(recipeState) {
        if (recipeState is RecipeUiState.Success) {
            showRecipeList = true
        }
    }

    when {
        showRecipeDetail && selectedRecipeId != null -> {
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
                            showRecipeDetail = false
                            selectedRecipeId = null
                            viewModel.resetDetailState()
                        },
                        onOpenWebsite = {},
                        onOpenYouTube = {}
                    )
                }

                else -> Unit
            }
        }

        showRecipeList -> {
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
                        viewModel = viewModel
                    )
                }

                else -> Unit
            }
        }

        else -> {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(PinkBackground)
                    .padding(horizontal = 16.dp, vertical = 24.dp)
            ) {
                // Top bar
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ProfileIconButton(
                            navController = navController,
                            size = 28,
                            tint = Color.Black
                        )
                    }
                }

                // Title
                item {
                    Text(
                        text = "Plan your week",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                }

                // 3×2 grid categories
                item {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        categories.chunked(2).forEach { rowCategories ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                rowCategories.forEach { category ->
                                    CategoryCard(
                                        category = category,
                                        onClick = {
                                            viewModel.searchByQuery(
                                                category.query,
                                                category.diet
                                            )
                                        },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                                if (rowCategories.size == 1) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                }

                // Personalised picks title
                item {
                    Text(
                        text = "Personalised picks for you",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Horizontal big cards
                item {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            PersonalizedPickCard(
                                title = "Protein-Packed Breakfasts",
                                description = "Energising breakfasts to fuel your day.",
                                imageEmoji = "🥞",
                                onClick = {
                                    viewModel.searchByQuery("breakfast", "high protein")
                                },
                                modifier = Modifier.width(260.dp)
                            )
                        }
                        item {
                            PersonalizedPickCard(
                                title = "Quick 15-Minute Dinners",
                                description = "Fast meals for busy weeknights.",
                                imageEmoji = "⚡",
                                onClick = {
                                    viewModel.searchByQuery("quick dinner", null)
                                },
                                modifier = Modifier.width(260.dp)
                            )
                        }
                        item {
                            PersonalizedPickCard(
                                title = "Vegetarian Delights",
                                description = "Delicious plant-based recipes.",
                                imageEmoji = "🥬",
                                onClick = {
                                    viewModel.searchByQuery("vegetarian", "vegetarian")
                                },
                                modifier = Modifier.width(260.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
fun CategoryCard(
    category: MealCategory,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(80.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(0.dp),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = category.name,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
            Text(
                text = category.emoji,
                fontSize = 28.sp
            )
        }
    }
}

@Composable
fun PersonalizedPickCard(
    title: String,
    description: String,
    imageEmoji: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(220.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = PinkHeader),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp)
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
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
        }
    }
}
