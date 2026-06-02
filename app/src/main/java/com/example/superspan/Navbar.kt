package com.example.superspan

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.navigation.NavType
import androidx.navigation.navArgument
//p.cortellesi@gmail.com  d.tinti@superspan.it
var actualUser by mutableStateOf(MapOfUser["p.cortellesi@gmail.com"]!!)
//var actualUser: User? = MapOfUser.getValue("p.cortellesi@gmail.com")

enum class Destination (
    val route: String,
    val label: String,
    val icon: ImageVector?
) {
    HOME("home", "Home", Icons.Default.Home),
    SEARCH("search", "Search", Icons.Default.Search),
    OFFERTE("discount", "Offerte", Icons.Default.LocalOffer),
    LAVORO("work", "Lavoro", Icons.Default.Work),
    LOGIN("login", "Login", null),
    REGISTER("register", "Registrazione", null),
    PROFILO("profile", "Profilo", Icons.Default.AccountCircle),
    PRODOTTO("product", "prodotto", null),
    ADD_COUPON("add_coupon", "Aggiungi Offerta", null),
    PERSONAL_DATA_SUMMARY("data_summary", "Riepilogo Dati", null),
    PERSONAL_DATA_EDIT("data_edit", "Modifica Dati", null)
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
                    Destination.SEARCH -> SearchPageComplete(paddingValues, navController)
                    Destination.HOME -> Home(paddingValues, navController)
                    Destination.OFFERTE -> CouponPageComplete(paddingValues, navController)
                    Destination.ADD_COUPON -> AddCoupon(paddingValues, navController)
                    Destination.LAVORO -> WorkSearchPageComplete(paddingValues, navController)
                    Destination.PROFILO -> ProfilePage(user = actualUser, navController = navController, paddingValues = paddingValues)
                    Destination.PERSONAL_DATA_SUMMARY -> PersonalDataSummaryPage(navController = navController, padding = paddingValues)
                    Destination.PERSONAL_DATA_EDIT -> PersonalDataEditPage(navController = navController, padding = paddingValues)
                    else -> {}
                }
            }
        }
        composable(Destination.PRODOTTO.route+"/{prod}", arguments = listOf(
            navArgument("prod") {
                type = NavType.StringType
                defaultValue = ""
                nullable = false
            })
        ) { backStackEntry ->
                val nameProduct = backStackEntry.arguments?.getString("prod")
                val product = ListOfProduct.find {product -> product.nome == nameProduct}
                ProductPage(product, navController, paddingValues)
        }

        composable(
            // Questa rotta deve corrispondere esattamente a quella usata nel navigate
            route = "dettaglio_offerta/{offerId}",
            arguments = listOf(
                navArgument("offerId") {
                    type = NavType.IntType // Usiamo Int perché l'ID dell'offerta è un numero
                }
            )
        ) { backStackEntry ->
            // 1. Recuperiamo l'ID passato durante la navigazione
            val idOfferta = backStackEntry.arguments?.getInt("offerId")

            // 2. Cerchiamo l'offerta corrispondente nella lista WorkOfferSearchList
            val offerta = WorkOfferSearchList.find { it.id == idOfferta }

            // 3. Richiamiamo la pagina WorkOfferPage che abbiamo creato
            WorkOfferPage(offerta, navController, paddingValues)
        }
    }
}

//La Navbar è composta solo da placeholder
@Composable
@Preview(showBackground = true)
fun MainNavigation() {
    val navController = rememberNavController()
    val startDestination: Destination = Destination.OFFERTE
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
                            Destination.PRODOTTO -> {}
                            Destination.ADD_COUPON -> {}
                            Destination.PERSONAL_DATA_SUMMARY -> {}
                            Destination.PERSONAL_DATA_EDIT -> {}
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
            }
        }
    ) {
        contentPadding -> Navigation(navController, startDestination, contentPadding)
    }
}
