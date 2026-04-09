package com.example.atmosphere.ui.navigation

sealed class Routes(val route: String) {

    // 🔐 Auth
    object Login : Routes("login")
    object Signup : Routes("signup")

    // 🏠 Root
    object Main : Routes("main")

    // 🎵 Main Screens
    object Home : Routes("home")
    object Mood : Routes("mood")
    object Discovery : Routes("discovery")
    object AI : Routes("ai")
    object Player : Routes("player")
}