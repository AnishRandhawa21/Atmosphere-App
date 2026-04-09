package com.example.atmosphere.data

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import com.example.atmosphere.ui.navigation.AppNavigation
import com.example.atmosphere.ui.theme.AtmosphereTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {

            val systemUiController = rememberSystemUiController()

            // 🔥 THIS IS THE REAL FIX
            SideEffect {
                systemUiController.setStatusBarColor(
                    color = Color.Transparent,
                    darkIcons = true   // 🔥 BLACK ICONS
                )
            }

            AtmosphereTheme {
                AppNavigation()
            }
        }
    }
}