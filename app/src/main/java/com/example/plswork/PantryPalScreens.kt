package com.example.plswork

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
import kotlinx.coroutines.launch

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

// ------------------ MAIN APP UI ------------------ //

@Composable
fun PantryPalApp() {
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

    var selectedIngredients by remember { mutableStateOf(listOf<Ingredient>()) }
    var recipes by remember { mutableStateOf<List<Recipe>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showResults by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

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
                    isLoading = true
                    errorMessage = null

                    scope.launch {
                        try {
                            val ingredientNames = selectedIngredients.map { it.name.lowercase() }
                            val ingredientsList = ingredientNames.joinToString(",")

                            // API CALL
                            val result = ApiClient.api.searchRecipes(
                                apiKey = "YOUR_API_KEY",
                                ingredients = ingredientsList,
                                number = 10
                            )

                            recipes = result
                            isLoading = false
                            showResults = true

                        } catch (e: Exception) {
                            errorMessage = "Error: ${e.message}"
                            isLoading = false
                        }
                    }
                }
            },
            isLoading = isLoading,
            errorMessage = errorMessage
        )
    } else {
        RecipeResultsScreen(
            selectedIngredients = selectedIngredients,
            recipes = recipes,
            onBack = {
                showResults = false
                recipes = emptyList()
            }
        )
    }
}



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



// ------------------ RESULTS SCREEN + RECIPE CARD ------------------ //

@Composable
fun RecipeResultsScreen(
    selectedIngredients: List<Ingredient>,
    recipes: List<Recipe>,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PinkBackground)
    ) {

        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "[← Back]",
                modifier = Modifier.clickable { onBack() }
            )
            Text(
                "PANTRY PAL",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                fontStyle = FontStyle.Italic
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(PinkHeader)
                .padding(14.dp)
        ) {
            Text("Recipe Results:", fontSize = 20.sp)
        }

        Text(
            "Selected Ingredients:",
            modifier = Modifier.padding(16.dp)
        )

        Row(modifier = Modifier.padding(start = 16.dp)) {
            selectedIngredients.forEach { ing -> Text(ing.emoji, fontSize = 32.sp) }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(PinkHeader)
                .padding(14.dp)
        ) {
            Text("${recipes.size} Recipes Found")
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(recipes) { recipe ->
                RecipeCard(recipe)
            }
        }
    }
}


@Composable
fun RecipeCard(recipe: Recipe) {
    val isExactMatch = recipe.missedIngredientCount == 0

    val matchPercentage =
        if (recipe.usedIngredientCount + recipe.missedIngredientCount > 0)
            (recipe.usedIngredientCount.toFloat() /
                    (recipe.usedIngredientCount +
                            recipe.missedIngredientCount) * 100).toInt()
        else 100

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp)) {

            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(PinkHeader, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("🍳", fontSize = 40.sp)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(recipe.title, fontSize = 16.sp, fontWeight = FontWeight.Bold)

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
                        color = Color.White
                    )
                }

                if (isExactMatch) {
                    Text("✓ All ingredients available", fontSize = 12.sp)
                } else {
                    Text("+ Need ${recipe.missedIngredientCount} more", fontSize = 12.sp)
                }

                Text("⏱️ 15-30 mins  👤 2-4 servings", fontSize = 11.sp)
            }
        }
    }
}
