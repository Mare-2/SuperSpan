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
// Import per i componenti della barra Material 3
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text

// Import per la gestione dei testi e pesi (Bold/Normal)
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalConfiguration

// Import per i colori e modificatori
import androidx.compose.foundation.layout.size

import androidx.compose.material.icons.filled.Face
import androidx.compose.ui.draw.clip
import androidx.compose.ui.Alignment
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.navigation.NavGraph.Companion.findStartDestination



enum class Destination (
    val route: String,
    val label: String,
    val icon: ImageVector?
) {
    HOME("home", "Home", Icons.Default.Home),
    SEARCH("search", "Ricerca", Icons.Default.Search),
    OFFERTE("discount", "Offerte", Icons.Default.LocalOffer),
    LAVORO("work", "Lavoro", Icons.Default.Work),
    LOGIN("login", "Login", null),
    REGISTER("register", "Registrazione", null),
    PROFILO("profile", "Profilo", Icons.Default.AccountCircle),
    PRODOTTO("product", "prodotto", null),
    ADD_COUPON("add_coupon", "Aggiungi Offerta", null),
    PERSONAL_DATA_SUMMARY("data_summary", "Riepilogo Dati", null),
    PERSONAL_DATA_EDIT("data_edit", "Modifica Dati", null),
    DRAFTS("drafts", "Bozze", Icons.Default.Description),
    APPLY_STEP_1("apply_1", "Dati e CV", null),
    APPLY_STEP_2_INTRO("apply_2_intro", "Istruzioni Video", null),
    APPLY_STEP_2_RECORD("apply_2_record", "Registra Video", null),
    APPLY_STEP_2_REVIEW("apply_2_review", "Rivedi Video", null),
    APPLY_STEP_3("apply_3", "Riepilogo Finale", null),
    EDIT_COUPON("edit_coupon", "Modifica Offerta", null),
    ADD_WORK_OFFER("add_work_offer", "Aggiungi Lavoro", null),
    EDIT_WORK_OFFER("edit_work_offer", "Modifica Lavoro", null),
    ACCOUNT_SUMMARY("account_summary", "Il mio account", null),
    ACCOUNT_EDIT("account_edit", "Modifica Account", null),
    ADMIN_CANDIDACIES("admin_candidacies", "Revisione Candidature", Icons.Default.Badge)
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
                    Destination.LAVORO -> {
                        if (actualUser.admin) {
                            AdminWorkMainPage(paddingValues, navController)
                        } else {
                            WorkSearchPageComplete(paddingValues, navController)
                        }
                    }
                    Destination.PROFILO -> ProfilePage(user = actualUser, navController = navController, paddingValues = paddingValues)
                    Destination.PERSONAL_DATA_SUMMARY -> PersonalDataSummaryPage(navController = navController, padding = paddingValues)
                    Destination.PERSONAL_DATA_EDIT -> PersonalDataEditPage(navController = navController, padding = paddingValues)
                    Destination.DRAFTS -> DraftsPage(navController = navController, padding = paddingValues)
                    Destination.APPLY_STEP_1 -> ApplyStep1(navController, paddingValues)
                    Destination.APPLY_STEP_2_INTRO -> ApplyStep2Intro(navController, paddingValues)
                    Destination.APPLY_STEP_2_RECORD -> ApplyStep2Record(navController, paddingValues)
                    Destination.APPLY_STEP_2_REVIEW -> ApplyStep2Review(navController, paddingValues)
                    Destination.APPLY_STEP_3 -> ApplyStep3(navController, paddingValues)
                    Destination.EDIT_COUPON -> AdminCouponEditPage(navController, paddingValues)
                    Destination.ADD_WORK_OFFER -> AdminWorkOfferEditPage(null, navController, paddingValues)
                    Destination.EDIT_WORK_OFFER -> AdminWorkOfferEditPage(null, navController, paddingValues) // Will be handled by ID route
                    Destination.ACCOUNT_SUMMARY -> AccountSummaryPage(actualUser, navController, paddingValues)
                    Destination.ACCOUNT_EDIT -> AccountSettingsPage(actualUser, navController, paddingValues)
                    Destination.ADMIN_CANDIDACIES -> AdminWorkMainPage(paddingValues, navController, initialTab = 1)
                    else -> {}
                }
            }
        }
        composable(Destination.EDIT_COUPON.route+"/{couponCode}", arguments = listOf(
            navArgument("couponCode") { type = NavType.StringType }
        )) { backStackEntry ->
            val code = backStackEntry.arguments?.getString("couponCode")
            val coupon = ListOfCoupon.find { it.code == code }
            AdminCouponEditPage(navController, paddingValues, coupon)
        }

        composable(Destination.EDIT_WORK_OFFER.route+"/{offerId}", arguments = listOf(
            navArgument("offerId") { type = NavType.IntType }
        )) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("offerId")
            val offer = WorkOfferSearchList.find { it.id == id }
            AdminWorkOfferEditPage(offer, navController, paddingValues)
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

/*//La Navbar è composta solo da placeholder
@Composable
fun MainNavigation() {
    val navController = rememberNavController()
    val startDestination: Destination = Destination.HOME
    val navBarStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBarStackEntry?.destination?.route ?: Destination.LOGIN.route

    val showBar: Boolean = currentRoute != Destination.LOGIN.route &&
            currentRoute != Destination.REGISTER.route &&
            !currentRoute.startsWith("apply")

    Scaffold(
        bottomBar = {
            if (showBar) {
                NavigationBar(
                    containerColor = Color.White,
                    tonalElevation = 8.dp
                ) {
                    Destination.entries.forEach { destination ->
                        // Qui rimettiamo TUTTE le tue eccezioni in modo esplicito
                        when (destination) {
                            Destination.LOGIN,
                            Destination.REGISTER,
                            Destination.PRODOTTO,
                            Destination.ADD_COUPON,
                            Destination.PERSONAL_DATA_SUMMARY,
                            Destination.PERSONAL_DATA_EDIT,
                            Destination.APPLY_STEP_1,
                            Destination.APPLY_STEP_2_INTRO,
                            Destination.APPLY_STEP_2_RECORD,
                            Destination.APPLY_STEP_2_REVIEW,
                            Destination.APPLY_STEP_3,
                            Destination.ADMIN_CANDIDACIES -> {
                                // Non facciamo nulla: queste pagine NON appaiono nella Navbar
                            }
                            else -> {
                                // Tutte le altre (Home, Search, Offerte, Lavoro, Profilo) appaiono
                                val isSelected = currentRoute == destination.route

                                NavigationBarItem(
                                    selected = isSelected,
                                    onClick = {
                                        if (currentRoute != destination.route) {
                                            navController.navigate(destination.route) {
                                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        }
                                    },
                                    label = {
                                        Text(
                                            text = destination.label,
                                            fontSize = 10.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    },
                                    icon = {
                                        Icon(
                                            imageVector = destination.icon ?: Icons.Default.Face,
                                            contentDescription = destination.label,
                                            modifier = Modifier.size(26.dp)
                                        )
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        indicatorColor = Color(0xFFE8F5E9), // Sfondo pillola verde chiaro
                                        selectedIconColor = Color(0xFF388E3C), // Icona verde scuro
                                        selectedTextColor = Color(0xFF388E3C),
                                        unselectedIconColor = Color.Gray,
                                        unselectedTextColor = Color.Gray
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    ) { contentPadding ->
        Navigation(navController, startDestination, contentPadding)
    }
}
*/

@Composable
fun MainNavigation() {
    val navController = rememberNavController()
    val navBarStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBarStackEntry?.destination?.route ?: Destination.LOGIN.route

    val showBar = currentRoute != Destination.LOGIN.route &&
            currentRoute != Destination.REGISTER.route &&
            !currentRoute.startsWith("apply")

    Scaffold(
        bottomBar = {
            if (showBar) {
                CustomAnimatedBottomBar(currentRoute) { route ->
                    // --- NAVIGAZIONE CORRETTA ---
                    navController.navigate(route) {
                        // Pulisce tutto lo stack fino alla destinazione iniziale (Login)
                        // Questo evita l'accumulo di pagine e risolve il tuo bug
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = false // NON salviamo lo stato "sporco"
                        }
                        // Evita di creare più copie della stessa pagina
                        launchSingleTop = true
                        // NON ripristiniamo il vecchio stato (così torniamo puliti)
                        restoreState = false
                    }
                }
            }
        }
    ) { contentPadding ->
        Navigation(navController, Destination.HOME, contentPadding)
    }
}

@Composable
fun CustomAnimatedBottomBar(currentRoute: String, onNavigate: (String) -> Unit) {
    val items = listOf(
        Destination.HOME,
        Destination.SEARCH,
        Destination.OFFERTE,
        Destination.LAVORO,
        Destination.PROFILO
    )

    // Logica per mantenere la bolla verde sull'icona giusta anche nelle sottopagine
    val selectedIndex = items.indexOfFirst { it.route == currentRoute }.let { index ->
        if (index == -1) {
            // Se siamo in una sottopagina, cerchiamo di capire a quale macro-area appartiene
            when {
                currentRoute.contains("apply") || currentRoute.contains("offerta") || currentRoute == Destination.ADMIN_CANDIDACIES.route -> 3 // Icona Lavoro
                currentRoute.contains("product") -> 1 // Icona Ricerca
                currentRoute.contains("data") -> 4 // Icona Profilo
                else -> 0 // Torna a Home se non sa dove andare
            }
        } else index
    }

    val configuration = LocalConfiguration.current
    val horizontalPadding = 20.dp
    val screenWidth = configuration.screenWidthDp.dp
    val barWidth = screenWidth - (horizontalPadding * 2)
    val tabWidth = barWidth / items.size

    val animatedOffsetX by animateDpAsState(
        targetValue = tabWidth * selectedIndex,
        animationSpec = spring(dampingRatio = 0.8f, stiffness = 400f),
        label = "bubbleMove"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp)
            .padding(horizontal = horizontalPadding),
        contentAlignment = Alignment.BottomCenter
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth().height(70.dp),
            color = Color.White,
            shape = RoundedCornerShape(35.dp),
            shadowElevation = 10.dp
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                // BOLLA ANIMATA
                Box(
                    modifier = Modifier
                        .offset(x = animatedOffsetX)
                        .width(tabWidth)
                        .fillMaxHeight()
                        .padding(5.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(30.dp))
                            .background(Color(0xFF388E3C).copy(alpha = 0.12f))
                    )
                }

                // ICONE
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    items.forEachIndexed { index, destination ->
                        val isSelected = selectedIndex == index

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clickable(
                                    // --- FIX RETTANGOLO GRIGIO ---
                                    interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                                    indication = null // Rimuove il rettangolo di sfondo al clic
                                ) { onNavigate(destination.route) },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = destination.icon ?: Icons.Default.Face,
                                    contentDescription = destination.label,
                                    tint = if (isSelected) Color(0xFF388E3C) else Color.Gray,
                                    modifier = Modifier.size(26.dp)
                                )
                                if (isSelected) {
                                    Text(
                                        text = destination.label,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color(0xFF388E3C),
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}