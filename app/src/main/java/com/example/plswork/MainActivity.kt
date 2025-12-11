package com.example.plswork

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.navigation.compose.rememberNavController
import com.example.plswork.navigation.AppNavGraph
import com.example.plswork.ui.theme.PlsworkTheme

// ------------------ MAIN ACTIVITY ------------------ //

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            PlsworkTheme {
                Surface {
                    val navController = rememberNavController()
                    AppNavGraph(navController)   // ← This handles Login → Register → MainApp
                }
            }
        }
    }
}


