package com.example.plswork.ui.screens



import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.plswork.viewmodel.OnboardingViewModel

/**
 * Onboarding Screen 2
 * Question: "What are your barriers?"
 * - Multi select (user can pick more than one)
 * - We store them in vm.answers.barriers (List<String>)
 */
@Composable
fun OnboardingQ2Screen(
    vm: OnboardingViewModel,
    onBack: () -> Unit,
    onContinue: () -> Unit
) {
    // Options from your UI
    val options = listOf("Time", "Budget", "Skills", "Ingredients", "Motivation")

    // Current selected barriers from ViewModel
    val selected = vm.answers.barriers

    // UI colors close to your screenshot
    val screenBg = Color.White
    val optionBg = Color(0xFFF2F2F2)
    val textGray = Color(0xFF6E6E6E)
    val black = Color(0xFF111111)

    Scaffold(
        containerColor = screenBg,

        // Bottom fixed Continue button like your screenshot
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .navigationBarsPadding()
            ) {
                Button(
                    onClick = onContinue,
                    enabled = selected.isNotEmpty(), // require at least 1 selection
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
                    text = "What are your barriers?",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold
                )

                Spacer(Modifier.height(8.dp))

                // Subtitle
                Text(
                    text = "Select all that apply",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp,
                    color = textGray
                )

                Spacer(Modifier.height(18.dp))

                // List of options with checkboxes on the right
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 90.dp)
                ) {
                    items(options) { label ->
                        val isChecked = selected.contains(label)

                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    // Toggle selection when the whole row is tapped
                                    vm.toggleBarrier(label)
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
                                Text(
                                    text = label,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.weight(1f)
                                )

                                Checkbox(
                                    checked = isChecked,
                                    onCheckedChange = { vm.toggleBarrier(label) },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = black,
                                        uncheckedColor = Color(0xFFC7C7C7),
                                        checkmarkColor = Color.White
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // "Tablet View" chip (visual only, like your screenshot)
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
