package com.example.plswork.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.ui.draw.clip
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.plswork.data.RecipeDetail
import com.example.plswork.data.Step


@Composable
fun RecipeDetailScreen(
    recipeDetail: RecipeDetail?,
    isLoading: Boolean,
    onBack: () -> Unit,
    onOpenWebsite: () -> Unit,
    onOpenYouTube: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PinkBackground)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(PinkHeader)
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = Color.Black,
                modifier = Modifier
                    .size(28.dp)
                    .clickable { onBack() }
            )

            Text(
                text = "Recipe Details",
                fontSize = 20.sp,
                color = Color.Black
            )

            Spacer(modifier = Modifier.width(28.dp))
        }

        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = DarkPink)
                }
            }

            recipeDetail == null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Failed to load recipe", color = Color.Red)
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        AsyncImage(
                            model = recipeDetail.image,
                            contentDescription = recipeDetail.title,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }

                    item {
                        Text(
                            text = recipeDetail.title,
                            fontSize = 24.sp,
                            color = Color.Black
                        )
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            InfoChip("⏱️ ${recipeDetail.readyInMinutes} mins")
                            InfoChip("👤 ${recipeDetail.servings} servings")
                        }
                    }

                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = onOpenWebsite,
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = DarkPink
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("🌐 Open Full Recipe Website", fontSize = 16.sp)
                            }

                            Button(
                                onClick = onOpenYouTube,
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFFF0000)
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("📺 Watch on YouTube", fontSize = 16.sp)
                            }
                        }
                    }

                    item {
                        Text(
                            text = "Instructions:",
                            fontSize = 20.sp,
                            color = Color.Black
                        )
                    }

                    if (recipeDetail.analyzedInstructions.isNotEmpty()) {
                        val steps = recipeDetail.analyzedInstructions.first().steps
                        items(steps) { step ->
                            StepCard(step)
                        }
                    } else {
                        item {
                            Text(
                                text = "No detailed instructions available. Tap the website button above to see the full recipe.",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoChip(text: String) {
    Box(
        modifier = Modifier
            .background(Color.White, RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(text = text, fontSize = 14.sp)
    }
}

@Composable
private fun StepCard(step: Step) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(DarkPink, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${step.number}",
                    color = Color.White
                )
            }

            Text(
                text = step.step,
                fontSize = 14.sp,
                color = Color.Black
            )
        }
    }
}
