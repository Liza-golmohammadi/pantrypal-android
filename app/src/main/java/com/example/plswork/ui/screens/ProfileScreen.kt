package com.example.plswork.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.plswork.auth.AuthManager
import com.example.plswork.ui.theme.PinkBackground
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(navController: NavController) {
    val authManager = remember { AuthManager() }

    // UI state
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var showChangeEmailDialog by remember { mutableStateOf(false) }
    var showChangePasswordDialog by remember { mutableStateOf(false) }

    var currentPassword by remember { mutableStateOf("") }
    var newEmail by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }

    var isLoading by remember { mutableStateOf(false) }

    // Current user email (recompose-safe; will update on recomposition)
    val currentEmail = authManager.getUserEmail() ?: "No email found"

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = PinkBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(PinkBackground)
                .padding(padding)
                .padding(16.dp)
        ) {

            // Top bar
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    modifier = Modifier
                        .size(28.dp)
                        .clickable { navController.popBackStack() },
                    tint = Color.Black
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = "Profile",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Avatar
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Profile Icon",
                modifier = Modifier
                    .size(120.dp)
                    .align(Alignment.CenterHorizontally),
                tint = Color.DarkGray
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Current email output
            Text(
                text = currentEmail,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Buttons
            ProfileRow(text = "Change password", enabled = !isLoading) {
                // reset fields
                currentPassword = ""
                newPassword = ""
                showChangePasswordDialog = true
            }

            ProfileRow(text = "Change email", enabled = !isLoading) {
                // reset fields
                currentPassword = ""
                newEmail = ""
                showChangeEmailDialog = true
            }

            ProfileRow(
                text = "Log out",
                textColor = Color(0xFFB00020),
                enabled = !isLoading
            ) {
                authManager.logoutUser()
                navController.navigate("login") {
                    popUpTo(0) { inclusive = true }
                }
            }

            if (isLoading) {
                Spacer(modifier = Modifier.height(16.dp))
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
        }
    }

    // ---- Change Email Dialog ----
    if (showChangeEmailDialog) {
        AlertDialog(
            onDismissRequest = { if (!isLoading) showChangeEmailDialog = false },
            title = { Text("Change email") },
            text = {
                Column {
                    Text(
                        text = "Enter your current password and your new email.",
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = newEmail,
                        onValueChange = { newEmail = it },
                        label = { Text("New email") },
                        singleLine = true,
                        enabled = !isLoading,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = currentPassword,
                        onValueChange = { currentPassword = it },
                        label = { Text("Current password") },
                        singleLine = true,
                        enabled = !isLoading,
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    enabled = !isLoading && newEmail.isNotBlank() && currentPassword.isNotBlank(),
                    onClick = {
                        scope.launch {
                            isLoading = true
                            val result = authManager.changeEmail(
                                currentPassword = currentPassword.trim(),
                                newEmail = newEmail.trim()
                            )
                            isLoading = false

                            if (result.isSuccess) {
                                showChangeEmailDialog = false
                                snackbarHostState.showSnackbar("Email updated successfully.")
                            } else {
                                snackbarHostState.showSnackbar(
                                    result.exceptionOrNull()?.localizedMessage ?: "Failed to update email."
                                )
                            }
                        }
                    }
                ) { Text("Save") }
            },
            dismissButton = {
                TextButton(
                    enabled = !isLoading,
                    onClick = { showChangeEmailDialog = false }
                ) { Text("Cancel") }
            }
        )
    }

    // ---- Change Password Dialog ----
    if (showChangePasswordDialog) {
        AlertDialog(
            onDismissRequest = { if (!isLoading) showChangePasswordDialog = false },
            title = { Text("Change password") },
            text = {
                Column {
                    Text(
                        text = "Enter your current password and your new password.",
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = newPassword,
                        onValueChange = { newPassword = it },
                        label = { Text("New password") },
                        singleLine = true,
                        enabled = !isLoading,
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = currentPassword,
                        onValueChange = { currentPassword = it },
                        label = { Text("Current password") },
                        singleLine = true,
                        enabled = !isLoading,
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    enabled = !isLoading && newPassword.isNotBlank() && currentPassword.isNotBlank(),
                    onClick = {
                        scope.launch {
                            isLoading = true
                            val result = authManager.changePassword(
                                currentPassword = currentPassword.trim(),
                                newPassword = newPassword.trim()
                            )
                            isLoading = false

                            if (result.isSuccess) {
                                showChangePasswordDialog = false
                                snackbarHostState.showSnackbar("Password updated successfully.")
                            } else {
                                snackbarHostState.showSnackbar(
                                    result.exceptionOrNull()?.localizedMessage ?: "Failed to update password."
                                )
                            }
                        }
                    }
                ) { Text("Save") }
            },
            dismissButton = {
                TextButton(
                    enabled = !isLoading,
                    onClick = { showChangePasswordDialog = false }
                ) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun ProfileRow(
    text: String,
    textColor: Color = Color.Black,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = enabled) { onClick() }
            .padding(vertical = 14.dp)
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = if (enabled) textColor else textColor.copy(alpha = 0.4f)
        )
    }
}
