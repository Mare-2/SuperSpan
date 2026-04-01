package com.example.superspan

import android.graphics.drawable.Icon
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.NavigationBarItemDefaults

enum class Destination (
    val route: String,
    val label: String,
    val icon: ImageVector?
) {
    HOME("home", "Home", Icons.Default.Home),
    COUPON("coupon", "Coupon", Icons.Default.ConfirmationNumber),
    OFFERTE("discount", "Offerte", Icons.Default.LocalOffer),
    LAVORO("work", "Lavoro", Icons.Default.Work),
    PROFILO("profile", "Profilo", Icons.Default.AccountCircle),
    LOGIN("login", "Login", null),
    REGISTER("register", "Registrazione", null)
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Navigation(navController: NavHostController, startDestination: Destination, paddingValues: PaddingValues) {
    NavHost(navController = navController, startDestination = startDestination.route) {
        Destination.entries.forEach { destination ->
            composable(destination.route) {
                when(destination) {
                    Destination.LOGIN -> Login(paddingValues, navController)
                    Destination.REGISTER -> Register(paddingValues, navController)
                    else -> {}
                }
            }
        }
    }
}

//La Navbar è composta solo da placeholder
@Composable
@Preview(showBackground = true)
fun MainNavigation() {
    var changeRoute = Destination.REGISTER.route
    val navController = rememberNavController()
    val startDestination: Destination = Destination.HOME
    val navBarStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBarStackEntry?.destination?.route ?: Destination.LOGIN.route
    val showBar: Boolean = currentRoute != Destination.LOGIN.route &&
                            currentRoute != Destination.REGISTER.route
    Scaffold(
        bottomBar = {
            if(showBar) {
                BottomAppBar(containerColor = Color.Unspecified) {
                    Destination.entries.forEach { destination ->
                        when(destination) {
                            Destination.LOGIN -> {}
                            Destination.REGISTER -> {}
                            else -> {
                                NavigationBarItem(
                                    selected = currentRoute==destination.route,
                                    onClick = {navController.navigate(destination.route)},
                                    icon = {
                                        androidx.compose.material3.Icon(
                                            imageVector = destination.icon?: Icons.Default.Face,
                                            contentDescription = destination.label,
                                            tint = if (currentRoute == destination.route) Color.Blue else Color.Green,
                                            modifier = Modifier.size(40.dp)
                                        )
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        indicatorColor = Color.Transparent, // Rende il contorno invisibile
                                        selectedIconColor = Color.Blue, // Colore dell'icona quando è selezionata
                                        unselectedIconColor = Color.Gray // Colore dell'icona quando NON è selezionata
                                    )

                                )
                            }
                        }
                    }
                }
            } /*else {
                BottomAppBar(containerColor = Color.Unspecified, modifier = Modifier.padding(top = 0.dp, bottom = 0.dp)) {
                    if (currentRoute==Destination.LOGIN.route) {
                        changeRoute = Destination.REGISTER.route
                    }
                    else {
                        changeRoute = Destination.LOGIN.route
                    }
                    Button({navController.navigate(changeRoute)}) { Text("Cambia")}
                }
            }*/
        }
    ) {
        contentPadding -> Navigation(navController, startDestination, contentPadding)
    }
}
