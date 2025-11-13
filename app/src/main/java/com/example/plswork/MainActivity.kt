package com.example.plswork

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import com.example.plswork.ui.theme.PlsworkTheme
import kotlinx.coroutines.launch
import com.example.plswork.ui.LoginScreen
import com.example.plswork.ui.RegisterScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PlsworkTheme {
//                Surface(modifier = Modifier.fillMaxSize()) {
//                    PantryPalApp()
//                }
                LoginScreen { email, password ->
                    println("Email: $email, Password: $password")
                }


//                RegisterScreen { name, email, password ->
//                    println("Name: $name, Email: $email, Password: $password")
//                }

            }
        }
    }
}

// Pink color scheme matching Figma
val PinkHeader = Color(0xFFFFB6C1)
val PinkBackground = Color(0xFFFFF0F5)
val DarkPink = Color(0xFFFF69B4)
val RedTitle = Color(0xFFCC0000)
val GreenMatch = Color(0xFF4CAF50)
val YellowMatch = Color(0xFFFFC107)

data class Ingredient(
    val name: String,
    val emoji: String
)

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
        // Ingredient Selection Screen
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

                            // ⚠️ PUT YOUR API KEY HERE ⚠️
                            val result = ApiClient.api.searchRecipes(
                                apiKey = "f0e38403bacf47e6a632779e1ffcd34c",
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
        // Results Screen
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
        // Header with icon and time
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
            Text(
                text = "9:41 PM",
                fontSize = 14.sp,
                color = Color.Black
            )
        }

        // App Title
        Text(
            text = "PANTRY\nPAL",
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold,
            fontStyle = FontStyle.Italic,
            color = RedTitle,
            textAlign = TextAlign.Center,
            lineHeight = 45.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp)
        )

        // "Search Ingredients:" Section
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(PinkHeader)
                .padding(vertical = 14.dp, horizontal = 16.dp)
        ) {
            Text(
                text = "Search Ingredients:",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )
        }

        // Selected Ingredients
        if (selectedIngredients.isNotEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                selectedIngredients.forEach { ingredient ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White, RoundedCornerShape(8.dp))
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = ingredient.emoji,
                                fontSize = 36.sp
                            )
                            Text(
                                text = ingredient.name,
                                fontSize = 20.sp,
                                fontStyle = FontStyle.Italic,
                                color = Color.Black
                            )
                        }
                        Text(
                            text = "[X]",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Red,
                            modifier = Modifier
                                .clickable { onIngredientRemoved(ingredient) }
                                .padding(8.dp)
                        )
                    }
                }
            }
        } else {
            // Available Ingredients label
            Text(
                text = "Available Ingredients:",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                modifier = Modifier.padding(16.dp)
            )
        }

        // Available ingredients list
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
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = ingredient.emoji,
                        fontSize = 32.sp
                    )
                    Text(
                        text = ingredient.name,
                        fontSize = 18.sp,
                        color = Color.Black
                    )
                }
            }
        }

        // Error message
        if (errorMessage != null) {
            Text(
                text = errorMessage,
                color = Color.Red,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        // Loading indicator
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = DarkPink)
            }
        }

        // "Find Recipes:" Button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(PinkHeader)
                .clickable(
                    enabled = selectedIngredients.isNotEmpty() && !isLoading
                ) {
                    onFindRecipes()
                }
                .padding(vertical = 16.dp)
        ) {
            Text(
                text = "Find Recipes:",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

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
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "[← Back]",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier
                    .clickable { onBack() }
                    .padding(8.dp)
            )
            Text(
                text = "PANTRY PAL",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                fontStyle = FontStyle.Italic,
                color = RedTitle
            )
            Spacer(modifier = Modifier.width(60.dp))
        }

        // Recipe Results header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(PinkHeader)
                .padding(14.dp)
        ) {
            Text(
                text = "Recipe Results:",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )
        }

        // Selected ingredients display
        Text(
            text = "Selected Ingredients List:",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(16.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            selectedIngredients.forEach { ingredient ->
                Text(text = ingredient.emoji, fontSize = 32.sp)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Results count
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(PinkHeader)
                .padding(14.dp)
        ) {
            Text(
                text = "${recipes.size} Recipes Found",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        // Recipe cards
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(recipes) { recipe ->
                RecipeCard(recipe = recipe)
            }
        }
    }
}

@Composable
fun RecipeCard(recipe: Recipe) {
    val isExactMatch = recipe.missedIngredientCount == 0
    val matchPercentage = if (recipe.usedIngredientCount + recipe.missedIngredientCount > 0) {
        (recipe.usedIngredientCount.toFloat() /
                (recipe.usedIngredientCount + recipe.missedIngredientCount) * 100).toInt()
    } else 100

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Pink placeholder for image
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(PinkHeader, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("🍳", fontSize = 40.sp)
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Recipe info
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Title
                Text(
                    text = recipe.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    maxLines = 2
                )

                // Match badge
                Box(
                    modifier = Modifier
                        .background(
                            color = if (isExactMatch) GreenMatch else YellowMatch,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (isExactMatch) "⭐ EXACT MATCH" else "🟡 $matchPercentage% MATCH",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Details
                if (isExactMatch) {
                    Text(
                        text = "✓ All ingredients available",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                } else {
                    Text(
                        text = "+ Need ${recipe.missedIngredientCount} more",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                Text(
                    text = "⏱️ 15-30 mins  👤 2-4 servings",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }
        }
    }
}