package com.example.atmosphere.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.*
import androidx.compose.ui.platform.LocalContext

import com.example.atmosphere.utils.TokenManager
import com.example.atmosphere.ui.screens.auth.LoginScreen
import com.example.atmosphere.ui.screens.auth.SignupScreen

@Composable
fun AppNavigation() {

    val context = LocalContext.current
    val navController = rememberNavController()
    val tokenManager = TokenManager(context)

    val startDestination =
        if (tokenManager.getToken() != null)
            Routes.Main.route
        else
            Routes.Login.route

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {

        composable(Routes.Login.route) {
            LoginScreen(navController)
        }

        composable(Routes.Signup.route) {
            SignupScreen(navController)
        }

        composable(Routes.Main.route) {
            MainScreen(navController)   // ✅ pass root controller
        }
    }
}