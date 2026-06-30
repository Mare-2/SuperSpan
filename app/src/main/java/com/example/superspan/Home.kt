package com.example.superspan

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.superspan.ui.theme.LogoLeft
import com.example.superspan.ui.theme.LogoCenter
import com.example.superspan.ui.theme.LogoRight

@Composable
fun Home(paddingValues: PaddingValues, navController: NavController?) {
    AuraBackground(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // 1. HEADER
            Header()

            // 2. SEZIONE AZIONI RAPIDE
            QuickActionsSection(actualUser.admin, navController)

            // 3. DASHBOARD DINAMICA
            if (actualUser.admin) {
                AdminDashboard(navController)
            } else {
                UserDashboard(navController)
            }
            
            Spacer(Modifier.height(paddingValues.calculateBottomPadding() + 16.dp))
        }
    }
}

@Composable
fun UserDashboard(navController: NavController?) {
    Column(Modifier.padding(vertical = 16.dp)) {
        // Ultima Candidatura
        val myCandidacies = AllCandidacies.filter { it.userEmail == actualUser.email }
        val lastCandidacy = myCandidacies.lastOrNull()
        if (lastCandidacy != null) {
            Text(
                text = "La tua ultima candidatura",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.padding(start = 20.dp, bottom = 12.dp)
            )
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .clickable { navController?.navigateTopLevel(Destination.DRAFTS.route) },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.AssignmentTurnedIn, contentDescription = null, tint = LogoCenter, modifier = Modifier.size(32.dp))
                    Spacer(Modifier.width(16.dp))
                    Column {
                        val offer = WorkOfferSearchList.find { it.id == lastCandidacy.offerId }
                        Text(offer?.titolo ?: "Candidatura", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("Stato: ${lastCandidacy.stato}", color = Color.Gray, fontSize = 14.sp)
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
        }

        // Offerte Reali in Vetrina
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 20.dp, bottom = 12.dp, end = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Offerte in vetrina",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = "Vedi tutte",
                color = LogoLeft,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                modifier = Modifier.clickable { navController?.navigateTopLevel(Destination.PROMOZIONI.route) }
            )
        }
        val topOffers = ListOfCoupon.filter { it.products.size == 1 }.sortedByDescending { it.discount }.take(5)
        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(topOffers) { coupon ->
                Card(
                    modifier = Modifier
                        .width(220.dp)
                        .height(140.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(3.dp)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        TagChip("Sconto ${coupon.discount.toInt()}%", LogoRight)
                        Spacer(Modifier.height(8.dp))
                        Text(coupon.products.first().nome, fontWeight = FontWeight.Bold, fontSize = 16.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
                        Spacer(Modifier.height(4.dp))
                        Text(coupon.description, color = Color.Gray, fontSize = 12.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
                    }
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        Text(
            text = "Ultime dal mondo SuperSpan",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.padding(start = 20.dp, bottom = 12.dp)
        )

        val newsList = listOf(
            Triple("Nuova Apertura Roma", "SuperSpan arriva a Roma Viale Marconi! Cerchiamo 15 nuovi collaboratori.", Icons.Default.NewReleases),
            Triple("Eco-Sostenibilità", "SuperSpan riduce la plastica: scopri i nuovi sacchetti bio nel tuo negozio.", Icons.Default.Eco),
            Triple("Prodotti a Km 0", "Sosteniamo l'agricoltura locale: nuova sezione dedicata ai produttori sardi.", Icons.Default.ShoppingCart),
            Triple("SuperSpan App", "Abbiamo rinnovato l'app! Nuova grafica e navigazione più semplice per i tuoi acquisti.", Icons.Default.PhoneAndroid),
            Triple("Raccolta Punti 2026", "Al via la nuova raccolta punti! Scopri il catalogo premi dedicato alla casa e al tempo libero.", Icons.Default.Star)
        )

        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(newsList) { news ->
                Card(
                    modifier = Modifier
                        .width(260.dp)
                        .height(140.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(3.dp)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(news.third, null, tint = LogoCenter, modifier = Modifier.size(24.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(news.first, fontWeight = FontWeight.Bold, fontSize = 16.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                        Spacer(Modifier.height(12.dp))
                        Text(news.second, color = Color.Gray, fontSize = 13.sp, maxLines = 3, overflow = TextOverflow.Ellipsis)
                    }
                }
            }
        }
    }
}

@Composable
fun AdminDashboard(navController: NavController?) {
    Column(Modifier.padding(vertical = 16.dp)) {
        val pendingCandidacies = AllCandidacies.count { it.stato == "Inviata" || it.stato == "In Valutazione" }
        val activeCoupons = ListOfCoupon.size
        val activeJobs = WorkOfferSearchList.size
        
        if (pendingCandidacies > 0) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
                    .clickable { navController?.navigateTopLevel(Destination.ADMIN_CANDIDACIES.route) },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(LogoRight.copy(alpha = 0.15f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = LogoRight, modifier = Modifier.size(24.dp))
                    }
                    Spacer(Modifier.width(16.dp))
                    Column(Modifier.weight(1f)) {
                        Text("Nuove Candidature", color = Color.Black, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                        Text("$pendingCandidacies candidature in attesa di revisione.", color = Color.Gray, fontSize = 14.sp)
                    }
                    Icon(Icons.Default.KeyboardArrowRight, contentDescription = null, tint = Color.LightGray)
                }
            }
            Spacer(Modifier.height(16.dp))
        }

        Text(
            text = "Riepilogo Negozio",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.padding(start = 20.dp, bottom = 12.dp)
        )
        Row(Modifier.fillMaxWidth().padding(horizontal = 20.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            AdminStatCard(Modifier.weight(1f), activeJobs.toString(), "Annunci Lavoro", Icons.Default.Work, LogoLeft) {
                navController?.navigateTopLevel(Destination.LAVORO.route)
            }
            AdminStatCard(Modifier.weight(1f), activeCoupons.toString(), "Promo Attive", Icons.Default.LocalOffer, LogoRight) {
                navController?.navigateTopLevel(Destination.OFFERTE.route)
            }
        }
    }
}

@Composable
fun AdminStatCard(modifier: Modifier, value: String, label: String, icon: ImageVector, color: Color, onClick: () -> Unit) {
    Card(
        modifier = modifier.height(100.dp).clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, null, tint = color, modifier = Modifier.size(24.dp))
                Spacer(Modifier.width(8.dp))
                Text(value, fontWeight = FontWeight.ExtraBold, fontSize = 24.sp, color = color)
            }
            Spacer(Modifier.height(4.dp))
            Text(label, fontSize = 12.sp, color = Color.Gray, textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun QuickActionsSection(isAdmin: Boolean, navController: NavController?) {
    Column(Modifier.padding(horizontal = 20.dp, vertical = 10.dp)) {
        Text(
            text = if (isAdmin) "Dashboard Amministratore" else "Suggeriti per te",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Gray
        )
        Spacer(Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (isAdmin) {
                QuickActionItem(Modifier.weight(1f), "Nuova Promo", Icons.Default.AddCard, LogoLeft) {
                    navController?.currentBackStackEntry?.savedStateHandle?.set("add_type", 1)
                    navController?.currentBackStackEntry?.savedStateHandle?.set("source", "home")
                    navController?.navigate(Destination.ADD_COUPON.route)
                }
                QuickActionItem(Modifier.weight(1f), "Candidature", Icons.Default.Badge, LogoCenter) {
                    navController?.navigateTopLevel(Destination.ADMIN_CANDIDACIES.route)
                }
            } else {
                QuickActionItem(Modifier.weight(1f), "Lavora con noi", Icons.Default.Work, LogoLeft) {
                    navController?.navigateTopLevel(Destination.LAVORO.route)
                }
                QuickActionItem(Modifier.weight(1f), "I miei Coupon", Icons.Default.LocalOffer, LogoRight) {
                    navController?.navigateTopLevel(Destination.OFFERTE.route)
                }
            }
        }
    }
}

@Composable
fun QuickActionItem(modifier: Modifier, label: String, icon: ImageVector, color: Color, onClick: () -> Unit) {
    Surface(
        modifier = modifier.height(90.dp),
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(30.dp))
            Spacer(Modifier.height(8.dp))
            Text(label, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun TagChip(text: String, accent: Color) {
    Surface(color = accent.copy(alpha = 0.15f), shape = CircleShape) {
        Text(text, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp), color = accent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}