package com.example.plswork.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * SkillsScreen (Student version)
 * - Shows large skill cards (image + title + level)
 * - Image is loaded from LOCAL drawable (res/drawable) using the drawable name
 * - When user taps a card, it opens YouTube (search link) using an Intent
 */
private data class SkillLesson(
    val title: String,
    val level: String,
    val drawableName: String, // <-- local drawable name (no file extension)
    val youtubeUrl: String
)

@Composable
fun SkillsScreen() {
    val context = LocalContext.current

    // Student note:
    // These drawable names MUST match the files inside res/drawable
    // Example files:
    // - skill_knife_skills.jpg
    // - skill_perfect_rice.jpg
    val lessons = remember {
        listOf(
            SkillLesson(
                title = "Basic Knife Skills",
                level = "Beginner",
                drawableName = "skill_knife",
                youtubeUrl = "https://www.youtube.com/results?search_query=basic+knife+skills+for+beginners"
            ),
            SkillLesson(
                title = "Perfect Rice Every Time",
                level = "Essential",
                drawableName = "skill_rice",
                youtubeUrl = "https://www.youtube.com/results?search_query=how+to+cook+rice+perfectly"
            )
        )
    }

    val screenBg = Color.White
    val subtitleGray = Color(0xFF6E6E6E)
    val black = Color(0xFF111111)

    // Student helper:
    // getIdentifier lets us load drawables by name (no hardcoded R.drawable.xxx needed)
    fun drawableId(name: String): Int {
        return context.resources.getIdentifier(name, "drawable", context.packageName)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(screenBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .navigationBarsPadding() // leave space for bottom nav
        ) {
            Spacer(Modifier.height(10.dp))

            Text(
                text = "Skills",
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = "Learn essential cooking techniques",
                fontSize = 14.sp,
                color = subtitleGray
            )

            Spacer(Modifier.height(16.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                items(lessons) { lesson ->
                    SkillCard(
                        title = lesson.title,
                        level = lesson.level,
                        imageResId = drawableId(lesson.drawableName),
                        onClick = {
                            // Student note:
                            // Opens YouTube app if installed, otherwise opens browser.
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(lesson.youtubeUrl))
                            context.startActivity(intent)
                        }
                    )
                }
            }
        }

        // Tablet View chip (visual only)
        Surface(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 18.dp, bottom = 92.dp),
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

@Composable
private fun SkillCard(
    title: String,
    level: String,
    imageResId: Int,  // local drawable resource id
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {

            // Student note:
            // If imageResId == 0, it means the drawable name was not found.
            if (imageResId != 0) {
                Image(
                    painter = painterResource(id = imageResId),
                    contentDescription = title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(190.dp)
                )
            } else {
                // Placeholder so the screen still works if the image is missing
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(190.dp)
                        .background(Color(0xFFEAEAEA)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Missing image", fontSize = 12.sp, color = Color(0xFF666666))
                }
            }

            Spacer(Modifier.height(10.dp))

            Text(
                text = title,
                modifier = Modifier.padding(horizontal = 14.dp),
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(Modifier.height(8.dp))

            AssistChip(
                onClick = { /* label only */ },
                label = { Text(text = level, style = MaterialTheme.typography.labelMedium) },
                modifier = Modifier.padding(start = 14.dp, bottom = 14.dp)
            )
        }
    }
}
