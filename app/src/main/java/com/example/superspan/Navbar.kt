package com.example.superspan

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHost
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

enum class Destination (
    val route: String,
    val label: String
) {
    LOGIN("login", "Login"),
    REGISTER("register", "Registrazione"),
    HOME("home", "Home"),
    COUPON("coupon", "Coupon"),
    OFFERTE("discount", "Offerte"),
    LAVORO("work", "Lavoro"),
    PROFILO("profile", "Profilo")
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Navigation() {
    val navController = rememberNavController()
    val startDestination: Destination = Destination.LOGIN
    NavHost(navController = navController, startDestination = startDestination.route) {
        Destination.entries.forEach { destination ->
            composable(destination.route) {
                when(destination) {
                    Destination.LOGIN -> Login()
                    else -> {}
                }
            }
        }
    }
}
