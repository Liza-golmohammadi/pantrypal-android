package com.example.plswork.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.plswork.viewmodel.OnboardingViewModel

/**
 * Onboarding Screen 3: "What do you like?"
 *
 * What this screen does:
 * - Shows preference tiles in a 2-column layout (like the design mock-up)
 * - User can select multiple tiles
 * - Selections are saved into vm.answers.likes (so we can use them later)
 */

// Simple model for each tile (label shown to user + drawable file name)
private data class PrefItem(
    val label: String,
    val drawableName: String
)

@Composable
fun OnboardingQ3Screen(
    vm: OnboardingViewModel,
    onBack: () -> Unit,
    onContinue: () -> Unit
) {
    // List of preferences shown on this screen
    // NOTE: "Balanced Bowls" uses the comfort image (pref_comfort_soups) as requested
    val prefs = listOf(
        PrefItem("Packed Lunches", "pref_packed_lunches"),
        PrefItem("High Protein", "pref_high_protein"),
        PrefItem("Veggie Recipes", "pref_veggie_recipes"),
        PrefItem("Family-Friendly\nRecipes", "pref_family_friendly"),
        PrefItem("Balanced Bowls", "pref_comfort_soups"),
        PrefItem("Quick Dinners", "pref_quick_meals")
    )

    // Turn the list into rows of 2 items to create a simple “grid” layout
    val rows = prefs.chunked(2)

    // Selected items come from the shared onboarding ViewModel
    val selected = vm.answers.likes

    // Basic styling colours (kept simple)
    val screenBg = Color.White
    val subtitleGray = Color(0xFF6E6E6E)
    val black = Color(0xFF111111)
    val tileBg = Color(0xFFF2F2F2)

    // We use this to find a drawable by its resource name (string)
    val context = LocalContext.current

    // Helper: convert a drawable name into an Android resource ID
    // If the file does not exist, Android returns 0
    fun drawableId(name: String): Int {
        return context.resources.getIdentifier(name, "drawable", context.packageName)
    }

    Scaffold(
        containerColor = screenBg,

        // Bottom button (fixed at the bottom like your design)
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .navigationBarsPadding()
            ) {
                Button(
                    onClick = onContinue,

                    // Only allow continue if user selected at least 1 preference
                    enabled = selected.isNotEmpty(),

                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = black)
                ) {
                    Text("Continue", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 18.dp)
                    .padding(top = 10.dp)
            ) {

                // Back button (top left)
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }

                Spacer(Modifier.height(6.dp))

                // Main title
                Text(
                    text = "What do you like?",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold
                )

                Spacer(Modifier.height(8.dp))

                // Subtitle
                Text(
                    text = "Let us know to personalise your experience.",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp,
                    color = subtitleGray
                )

                Spacer(Modifier.height(18.dp))

                /**
                 * Grid layout:
                 * We use LazyColumn + Row because it works reliably on all Compose versions
                 * and is easy to control spacing.
                 */
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = PaddingValues(bottom = 90.dp)
                ) {
                    items(rows) { rowItems ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {

                            // Add each tile in this row
                            rowItems.forEach { item ->
                                PreferenceTile(
                                    modifier = Modifier.weight(1f), // weight used inside Row
                                    item = item,
                                    isSelected = selected.contains(item.label),
                                    onToggle = { vm.toggleLike(item.label) },
                                    tileBg = tileBg,
                                    drawableId = { drawableId(item.drawableName) }
                                )
                            }

                            // If last row has only 1 item, add spacer to keep columns aligned
                            if (rowItems.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }

            // “Tablet View” chip (just visual, like your mock-up)
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 22.dp, bottom = 92.dp),
                color = black,
                shape = RoundedCornerShape(50.dp)
            ) {
                Text(
                    text = "Tablet View",
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

/**
 * Single preference tile:
 * - Shows image (from drawable)
 * - Shows label under it
 * - Shows tick icon if selected
 */
@Composable
private fun PreferenceTile(
    modifier: Modifier = Modifier,
    item: PrefItem,
    isSelected: Boolean,
    onToggle: () -> Unit,
    tileBg: Color,
    drawableId: () -> Int
) {
    Column(
        modifier = modifier.clickable { onToggle() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = tileBg),
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.15f)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {

                val resId = drawableId()

                if (resId != 0) {
                    // If the drawable exists, display it
                    Image(
                        painter = painterResource(id = resId),
                        contentDescription = item.label,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    // If image is missing, show a simple placeholder (NO emoji)
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFFEAEAEA)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Image missing",
                            fontSize = 12.sp,
                            color = Color(0xFF666666)
                        )
                    }
                }

                // Selection overlay + tick
                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.12f))
                    )
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Selected",
                        tint = Color.White,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(10.dp)
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        Text(
            text = item.label,
            textAlign = TextAlign.Center,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
