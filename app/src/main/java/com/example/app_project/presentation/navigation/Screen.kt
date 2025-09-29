package com.example.app_project.presentation.navigation

sealed class Screen(val route: String) {
    object Onboarding1 : Screen("onboarding1")
    object Onboarding2 : Screen("onboarding2")
    object Onboarding3 : Screen("onboarding3")
    object Login : Screen("login")
    object Home : Screen("home")
    object Camera : Screen("camera")
    object History : Screen("history")
}
