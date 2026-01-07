package com.example.plswork.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.plswork.network.Recipe
import com.example.plswork.Constants
import com.example.plswork.viewmodel.RecipeViewModel

// YOUR COLORS
val PinkHeader = Color(0xFFFFB6C1)
val PinkBackground = Color(0xFFFFF0F5)
val DarkPink = Color(0xFFFF69B4)
val RedTitle = Color(0xFFCC0000)
val GreenMatch = Color(0xFF4CAF50)
val YellowMatch = Color(0xFFFFC107)

// YOUR DATA CLASS
data class Ingredient(
    val name: String,
    val emoji: String
)

// ------------------ INGREDIENT SELECTION ------------------ //

@Composable
fun IngredientSelectionScreen(
    availableIngredients: List<Ingredient>,
    selectedIngredients: List<Ingredient>,
    onIngredientSelected: (Ingredient) -> Unit,
    onIngredientRemoved: (Ingredient) -> Unit,
    onFindRecipes: () -> Unit,
    isLoading: Boolean,
    errorMessage: String?
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PinkBackground)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Profile",
                modifier = Modifier.size(32.dp),
                tint = Color.Black
            )
            Text(text = "9:41 PM", fontSize = 14.sp)
        }

        Text(
            text = "PANTRY\nPAL",
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold,
            fontStyle = FontStyle.Italic,
            color = RedTitle,
            textAlign = TextAlign.Center,
            lineHeight = 45.sp,
            modifier = Modifier.fillMaxWidth()
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(PinkHeader)
                .padding(14.dp)
        ) {
            Text("Search Ingredients:", fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
        }

        if (selectedIngredients.isNotEmpty()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                selectedIngredients.forEach { ingredient ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White, RoundedCornerShape(8.dp))
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(ingredient.emoji, fontSize = 36.sp)
                            Text(ingredient.name, fontSize = 20.sp, fontStyle = FontStyle.Italic)
                        }
                        Text(
                            text = "[X]",
                            color = Color.Red,
                            modifier = Modifier.clickable { onIngredientRemoved(ingredient) }
                        )
                    }
                }
            }
        } else {
            Text(
                "Available Ingredients:",
                fontSize = 16.sp,
                modifier = Modifier.padding(16.dp)
            )
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(availableIngredients.filter { it !in selectedIngredients }) { ingredient ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(8.dp))
                        .clickable { onIngredientSelected(ingredient) }
                        .padding(12.dp),
                ) {
                    Text(ingredient.emoji, fontSize = 32.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(ingredient.name, fontSize = 18.sp)
                }
            }
        }

        if (errorMessage != null) {
            Text(errorMessage, color = Color.Red, modifier = Modifier.padding(16.dp))
        }

        if (isLoading) {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = DarkPink)
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(PinkHeader)
                .clickable(enabled = selectedIngredients.isNotEmpty() && !isLoading) {
                    onFindRecipes()
                }
                .padding(16.dp)
        ) {
            Text("Find Recipes:", fontSize = 20.sp, modifier = Modifier.align(Alignment.Center))
        }
    }
}

// ------------------ RESULTS SCREEN ------------------ //

@Composable
fun RecipeResultsScreen(
    selectedIngredients: List<Ingredient>,
    recipes: List<Recipe>,
    onBack: () -> Unit,
    onRecipeClick: (Int) -> Unit = {},
    viewModel: RecipeViewModel? = null
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PinkBackground)
    ) {
        // Header with back button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(PinkHeader)
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "[← Back]",
                modifier = Modifier.clickable { onBack() },
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Text(
                text = "PANTRY PAL",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                fontStyle = FontStyle.Italic,
                color = RedTitle
            )
            Spacer(modifier = Modifier.width(50.dp))
        }

        // Recipe Results header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(PinkHeader)
                .padding(14.dp)
        ) {
            Text("Recipe Results:", fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
        }

        // Selected ingredients display
        if (selectedIngredients.isNotEmpty()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "Selected Ingredients:",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    selectedIngredients.take(5).forEach { ing ->
                        Text(ing.emoji, fontSize = 32.sp)
                    }
                }
            }
        }

        // Recipe count
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(PinkHeader)
                .padding(14.dp)
        ) {
            Text(
                "${recipes.size} Recipes Found",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        // Recipe cards list
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(recipes) { recipe ->
                RecipeCard(
                    recipe = recipe,
                    onClick = {
                        onRecipeClick(recipe.id)
                    },
                    viewModel = viewModel
                )
            }
        }
    }
}

// ------------------ RECIPE CARD ------------------ //

@Composable
fun RecipeCard(
    recipe: Recipe,
    onClick: () -> Unit,
    viewModel: RecipeViewModel? = null
) {
    val isExactMatch = recipe.missedIngredientCount == 0

    val matchPercentage =
        if (recipe.usedIngredientCount + recipe.missedIngredientCount > 0)
            (recipe.usedIngredientCount.toFloat() /
                    (recipe.usedIngredientCount + recipe.missedIngredientCount) * 100).toInt()
        else 100

    // Check if this recipe is favorited
    // Check if this recipe is favorited - observe favorites state
    val favorites by viewModel?.favorites?.collectAsState() ?: remember { mutableStateOf(emptySet()) }
    val isFavorite = favorites.contains(recipe.id)


    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp)) {
            // Recipe image placeholder
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(PinkHeader, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("🍳", fontSize = 40.sp)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                // Title and heart button row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        recipe.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )

                    // FAVORITE HEART BUTTON
                    if (viewModel != null) {
                        IconButton(
                            onClick = { viewModel.toggleFavorite(recipe.id) },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Text(
                                text = if (isFavorite) "❤️" else "🤍",
                                fontSize = 20.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Match percentage badge
                Box(
                    modifier = Modifier
                        .background(
                            if (isExactMatch) GreenMatch else YellowMatch,
                            RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (isExactMatch) "⭐ EXACT MATCH"
                        else "🟡 $matchPercentage% MATCH",
                        fontSize = 11.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Ingredient info
                if (isExactMatch) {
                    Text("✓ All ingredients available", fontSize = 12.sp, color = GreenMatch)
                } else {
                    Text("+ Need ${recipe.missedIngredientCount} more", fontSize = 12.sp, color = Color.Gray)
                }

                Text("⏱️ 15-30 mins  👤 2-4 servings", fontSize = 11.sp, color = Color.Gray)

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "👉 Tap card for full recipe",
                    fontSize = 10.sp,
                    fontStyle = FontStyle.Italic,
                    color = DarkPink
                )
            }
        }
    }
}
