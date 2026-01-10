@file:OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)

package com.example.plswork.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.plswork.R
import kotlinx.coroutines.launch

data class OnboardingPage(
    val titleTop: String,
    val titleHighlight: String,
    val titleBottom: String,
    val description: String
)

@Composable
fun OnboardingRoute(
    onGetStarted: () -> Unit,
    onSignIn: () -> Unit
) {
    val pages = listOf(
        OnboardingPage(
            titleTop = "End the",
            titleHighlight = "\"nothing to eat\"",
            titleBottom = "era",
            description = "Discover delicious recipes tailored to\nthe ingredients you already have."
        ),


    )

    OnboardingScreen(
        pages = pages,
        heroRes = R.drawable.onboarding_hero_1, //  only image you have
        onGetStarted = onGetStarted,
        onSignIn = onSignIn
    )
}

@Composable
fun OnboardingScreen(
    pages: List<OnboardingPage>,
    heroRes: Int,
    onGetStarted: () -> Unit,
    onSignIn: () -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()

    val bg = Color(0xFFF7F4EF)
    val titleDark = Color(0xFF2A2A2A)
    val accentOrange = Color(0xFFC46A3A)
    val bodyGray = Color(0xFF5C5C5C)
    val green = Color(0xFF2F4B2D)
    val dotActive = Color(0xFF4E6B3E)
    val dotInactive = Color(0xFFD9D9D9)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bg)
    ) {
        Box(
            modifier = Modifier
                .offset(x = (-120).dp, y = 140.dp)
                .size(320.dp)
                .clip(CircleShape)
                .background(Color(0xFFDDE8D8).copy(alpha = 0.55f))
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = 120.dp, y = 120.dp)
                .size(380.dp)
                .clip(CircleShape)
                .background(Color(0xFFDDE8D8).copy(alpha = 0.55f))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .padding(WindowInsets.safeDrawing.asPaddingValues())
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(18.dp))

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxWidth()
            ) { page ->
                val p = pages[page]

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = p.titleTop,
                        textAlign = TextAlign.Center,
                        fontFamily = FontFamily.Serif,
                        style = MaterialTheme.typography.headlineLarge,
                        color = titleDark
                    )
                    Text(
                        text = p.titleHighlight,
                        textAlign = TextAlign.Center,
                        fontFamily = FontFamily.Serif,
                        style = MaterialTheme.typography.headlineLarge,
                        color = accentOrange
                    )
                    Text(
                        text = p.titleBottom,
                        textAlign = TextAlign.Center,
                        fontFamily = FontFamily.Serif,
                        style = MaterialTheme.typography.headlineLarge,
                        color = titleDark
                    )

                    Spacer(Modifier.height(10.dp))

                    Text(
                        text = p.description,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyLarge,
                        color = bodyGray,
                        lineHeight = 22.sp
                    )

                    Spacer(Modifier.height(22.dp))

                    Card(
                        shape = RoundedCornerShape(28.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1.18f)
                    ) {
                        Image(
                            painter = painterResource(id = heroRes),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(pages.size) { i ->
                    val isActive = i == pagerState.currentPage
                    Box(
                        modifier = Modifier
                            .size(if (isActive) 10.dp else 9.dp)
                            .clip(CircleShape)
                            .background(if (isActive) dotActive else dotInactive)
                    )
                }
            }

            Spacer(Modifier.height(18.dp))

            Button(
                onClick = {
                    if (pagerState.currentPage < pages.lastIndex) {
                        scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                    } else {
                        onGetStarted()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = green)
            ) {
                Text(
                    text = if (pagerState.currentPage < pages.lastIndex) "Next" else "Get Started",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(Modifier.height(10.dp))

            TextButton(onClick = onSignIn) {
                Text(
                    text = "Sign in",
                    color = dotActive,
                    style = MaterialTheme.typography.titleSmall
                )
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun OnboardingPreview() {
    MaterialTheme {
        OnboardingRoute(onGetStarted = {}, onSignIn = {})
    }
}
