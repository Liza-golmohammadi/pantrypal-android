package com.example.plswork.ui.screens
import com.example.plswork.viewmodel.OnboardingViewModel

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp




/**
 * Screen 1 of onboarding:
 * "What are you trying to achieve?"
 *
 * - User picks ONE option
 * - We save it into the shared OnboardingViewModel (vm.setGoal)
 * - Continue goes to next screen (handled by nav in AppNavGraph)
 */
@Composable
fun OnboardingQ1Screen(
    vm: OnboardingViewModel,
    onBack: () -> Unit,
    onContinue: () -> Unit
) {

    // List options (emoji + label) like your design
    val options = listOf(
        "💪" to "Hit macro goals",
        "🗓️" to "Meal prep for the week",
        "🧑‍🍳" to "Find super simple recipes",
        "💡" to "Last minute dinner inspiration",
        "🏠" to "Feed my family",
        "👶" to "Feed my toddler",
        "🥗" to "Live well for longer"
    )

    // Current selected answer from ViewModel
    val selected = vm.answers.goal

    // UI colors (close to your screenshot)
    val screenBg = Color.White
    val optionBg = Color(0xFFF2F2F2)
    val textGray = Color(0xFF6E6E6E)
    val black = Color(0xFF111111)

    Scaffold(
        containerColor = screenBg,

        // Bottom fixed area: "Continue" button like your screenshot
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .navigationBarsPadding()
            ) {
                Button(
                    onClick = onContinue,
                    enabled = selected != null, // disabled until user selects
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = black)
                ) {
                    Text(
                        text = "Continue",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            // Main content column
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 18.dp)
                    .padding(top = 10.dp)
            ) {

                // Back arrow at top-left
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back"
                    )
                }

                Spacer(Modifier.height(6.dp))

                // Title (centered)
                Text(
                    text = "What are you trying to\nachieve?",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold,
                    lineHeight = 28.sp
                )

                Spacer(Modifier.height(8.dp))

                // Subtitle
                Text(
                    text = "Pick one of the choices below",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp,
                    color = textGray
                )

                Spacer(Modifier.height(18.dp))

                // Options list
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 90.dp) // space for bottom button
                ) {
                    items(options) { (emoji, label) ->
                        val isSelected = label == selected

                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    // Save to ViewModel when user taps
                                    vm.setGoal(label)
                                },
                            color = optionBg,
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = emoji, fontSize = 18.sp)
                                Spacer(Modifier.width(12.dp))

                                Text(
                                    text = label,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.weight(1f)
                                )

                                // Show check icon if selected
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected"
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // "Tablet View" small chip (bottom-right above Continue)
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

@Preview(showBackground = true)
@Composable
private fun OnboardingQ1Preview() {
    // Preview needs a ViewModel normally; keep empty preview minimal
}
