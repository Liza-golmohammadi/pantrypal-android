package com.example.plswork.ui.screens



import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
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
 * Onboarding Screen 4: "Any dietary requirements?"
 *
 * What this screen does:
 * - Shows 4 options in a 2x2 grid (single choice)
 * - Saves the selected option into the shared OnboardingViewModel
 * - Continue button only enables after user picks one option
 */

// Small model for each diet tile (label + drawable file name)
private data class DietItem(
    val label: String,
    val drawableName: String
)

@Composable
fun OnboardingQ4Screen(
    vm: OnboardingViewModel,
    onBack: () -> Unit,
    onContinue: () -> Unit
) {
    // NOTE: These drawable names must match files in res/drawable (no spaces, lowercase)
    val diets = listOf(
        DietItem("None", "diet_none"),
        DietItem("Veggie", "diet_veggie"),
        DietItem("Vegan", "diet_vegan"),
        DietItem("Pescatarian", "diet_pescatarian")
    )

    // Split into rows of 2 (simple grid layout using LazyColumn + Row)
    val rows = diets.chunked(2)

    // Read the selected value from the ViewModel
    val selected = vm.answers.dietaryRequirement  // <-- we add this field in the VM section below

    // Colours to match your mock
    val screenBg = Color.White
    val subtitleGray = Color(0xFF6E6E6E)
    val black = Color(0xFF111111)
    val tileBg = Color(0xFFF2F2F2)
    val accentOrange = Color(0xFFFF7A1A) // orange border + tick circle

    // Used to lookup drawable IDs by name (so you can swap images without changing code)
    val context = LocalContext.current
    fun drawableId(name: String): Int =
        context.resources.getIdentifier(name, "drawable", context.packageName)

    Scaffold(
        containerColor = screenBg,

        // Bottom Continue button (fixed like your design)
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .navigationBarsPadding()
            ) {
                Button(
                    onClick = onContinue,
                    enabled = !selected.isNullOrBlank(), // only enable when user selected a diet
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

                // Back arrow (top-left)
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }

                Spacer(Modifier.height(6.dp))

                // Title
                Text(
                    text = "Any dietary\nrequirements?",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold,
                    lineHeight = 30.sp
                )

                Spacer(Modifier.height(8.dp))

                // Subtitle
                Text(
                    text = "We'll only show recipes that match your\npreferences.",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp,
                    color = subtitleGray
                )

                Spacer(Modifier.height(18.dp))

                // 2x2 grid
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

                            rowItems.forEach { item ->
                                DietTile(
                                    modifier = Modifier.weight(1f),
                                    item = item,
                                    isSelected = (selected == item.label),
                                    onSelect = {
                                        // Save selection to ViewModel (single select)
                                        vm.setDietaryRequirement(item.label)
                                    },
                                    tileBg = tileBg,
                                    accentOrange = accentOrange,
                                    drawableId = { drawableId(item.drawableName) }
                                )
                            }

                            // Keep last row balanced if it ever has 1 item (safe layout habit)
                            if (rowItems.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }

            // “Tablet View” chip (visual only)
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
 * Single diet option tile:
 * - Shows image with label underneath
 * - Selected state: orange border + orange check circle (top-right)
 */
@Composable
private fun DietTile(
    modifier: Modifier,
    item: DietItem,
    isSelected: Boolean,
    onSelect: () -> Unit,
    tileBg: Color,
    accentOrange: Color,
    drawableId: () -> Int
) {
    Column(
        modifier = modifier.clickable { onSelect() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Border only appears when selected (matches mock)
        val borderModifier = if (isSelected) {
            Modifier.border(2.dp, accentOrange, RoundedCornerShape(18.dp))
        } else {
            Modifier
        }

        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = tileBg),
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.15f)
                .then(borderModifier)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {

                val resId = drawableId()

                if (resId != 0) {
                    Image(
                        painter = painterResource(id = resId),
                        contentDescription = item.label,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    // Placeholder if image isn't added yet (NO emoji)
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

                // Selected tick (orange circle with white tick)
                if (isSelected) {
                    Surface(
                        color = accentOrange,
                        shape = CircleShape,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(10.dp)
                            .size(26.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Selected",
                                tint = Color.White
                            )
                        }
                    }
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
