package com.example.plswork.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun ProfileIconButton(
    navController: NavController,
    size: Int = 32,
    tint: Color = Color.Black
) {
    Icon(
        imageVector = Icons.Default.AccountCircle,
        contentDescription = "Profile",
        modifier = Modifier
            .size(size.dp)
            .clickable {
                navController.navigate("profile") {
                    launchSingleTop = true
                }
            },
        tint = tint
    )
}
