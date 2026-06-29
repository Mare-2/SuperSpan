/*package com.example.superspan

import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.NewReleases
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import androidx.navigation.NavController


enum class CardSize { LARGE, MEDIUM, SMALL }

data class HomeCardData(
    val id: String,
    val title: String,
    val description: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val accent: Color,
    val background: Color,
    val size: CardSize = CardSize.MEDIUM,
    val tag: String? = null,
    val actionLabel: String = "Apri"
)

@Composable
private fun TagChip(text: String, accent: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(accent.copy(alpha = 0.14f))
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Text(
            text = text,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = accent
        )
    }
}

@Composable
fun HomeFeatureCard(card: HomeCardData, navController: NavController?) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(190.dp)
            .clip(RoundedCornerShape(22.dp))
            .clickable { navController?.let { } },
        colors = CardDefaults.elevatedCardColors(containerColor = card.background)
    ) {
        Row(Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .width(10.dp)
                    .fillMaxHeight()
                    .background(card.accent)
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(18.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    card.tag?.let {
                        TagChip(it, card.accent)
                        Spacer(Modifier.size(10.dp))
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            card.icon,
                            contentDescription = card.title,
                            tint = card.accent,
                            modifier = Modifier.size(30.dp)
                        )
                        Spacer(Modifier.size(10.dp))
                        Text(
                            card.title,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(Modifier.size(10.dp))
                    Text(
                        card.description,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "In primo piano",
                        color = card.accent,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Button(onClick = { navController?.let { } }) {
                        Text(card.actionLabel)
                    }
                }
            }
        }
    }
}

@Composable
fun HomeMediumCard(card: HomeCardData, navController: NavController?) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(132.dp)
            .clip(RoundedCornerShape(18.dp))
            .clickable { navController?.let { } },
        colors = CardDefaults.elevatedCardColors(containerColor = card.background)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(card.accent.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        card.icon,
                        contentDescription = card.title,
                        tint = card.accent,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Spacer(Modifier.size(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            card.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Spacer(Modifier.size(6.dp))
                    Text(
                        card.description,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    card.tag?.let {
                        Spacer(Modifier.size(8.dp))
                        TagChip(it, card.accent)
                    }
                }
            }

            Button(onClick = { navController?.let { } }) {
                Text(card.actionLabel)
            }
        }
    }
}

@Composable
fun HomeSmallCard(card: HomeCardData, navController: NavController?) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(92.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable { navController?.let { } },
        colors = CardDefaults.elevatedCardColors(containerColor = card.background)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(card.accent.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        card.icon,
                        contentDescription = card.title,
                        tint = card.accent,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(Modifier.size(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        card.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.size(3.dp))
                    Text(
                        card.description,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            card.tag?.let {
                TagChip(it, card.accent)
            }
        }
    }
}

@Composable
fun Home(paddingValues: PaddingValues, navController: NavController?) {
    // Card fittizie per notizie, offerte e posizioni aperte
    val cards = listOf(
        HomeCardData(
            id = "work_open_day",
            title = "Lavora con noi",
            description = "Abbiamo aperto nuove selezioni per cassieri, scaffalisti e addetti reparto in più punti vendita.",
            icon = Icons.Default.Work,
            accent = Color(0xFF2E7D32),
            background = Color(0xFFEAF5EC),
            size = CardSize.LARGE,
            tag = "Nuove posizioni",
            actionLabel = "Candidati"
        ),
        HomeCardData(
            id = "news_roma",
            title = "Apertura nuovo punto vendita",
            description = "Sta per aprire un nuovo supermercato a Roma: cerchiamo personale locale e collaboratori per il lancio.",
            icon = Icons.Default.Info,
            accent = Color(0xFF1565C0),
            background = Color(0xFFE8F1FB),
            size = CardSize.MEDIUM,
            tag = "Nuova apertura",
            actionLabel = "Dettagli"
        ),
        HomeCardData(
            id = "news_closure",
            title = "Avviso: chiusura anticipata",
            description = "Per imprevisti tecnici, il punto vendita di Bologna Centro chiuderà oggi alle 19:00.",
            icon = Icons.Default.Info,
            accent = Color(0xFFD32F2F),
            background = Color(0xFFFFEBEE),
            size = CardSize.MEDIUM,
            tag = "Importante",
            actionLabel = "Leggi"
        ),
        HomeCardData(
            id = "news_anniversary",
            title = "Anniversario SuperSpan",
            description = "Festeggiamo 10 anni del punto vendita di Verona con eventi, gadget e iniziative per i clienti.",
            icon = Icons.Default.NewReleases,
            accent = Color(0xFF6A1B9A),
            background = Color(0xFFF3E5F5),
            size = CardSize.SMALL,
            tag = "Celebration"
        ),
        HomeCardData(
            id = "coupon_teaser",
            title = "Offerte e coupon",
            description = "La sezione dedicata alle promozioni è separata: qui trovi solo un richiamo rapido alle offerte più importanti.",
            icon = Icons.Default.LocalOffer,
            accent = Color(0xFFF57C00),
            background = Color(0xFFFFF4E6),
            size = CardSize.MEDIUM,
            tag = "Sezione dedicata",
            actionLabel = "Vai"
        ),
        HomeCardData(
            id = "work_torino",
            title = "Posizione aperta: Scaffalista",
            description = "Sede: Torino - Lingotto. Contratto a tempo determinato, inizio immediato.",
            icon = Icons.Default.Work,
            accent = Color(0xFF455A64),
            background = Color(0xFFF1F5F8),
            size = CardSize.MEDIUM,
            tag = "Tempo determinato",
            actionLabel = "Candidati"
        ),
        HomeCardData(
            id = "season_fruit",
            title = "Novità di stagione",
            description = "Sono arrivati mango, avocado e frutta esotica fresca in reparto ortofrutta.",
            icon = Icons.Default.NewReleases,
            accent = Color(0xFF7B1FA2),
            background = Color(0xFFF5ECF8),
            size = CardSize.SMALL,
            tag = "Fresh"
        ),
        HomeCardData(
            id = "fidelity",
            title = "Programma fedeltà",
            description = "Iscriviti al programma punti e ricevi vantaggi esclusivi e buoni personalizzati.",
            icon = Icons.Default.AccountCircle,
            accent = Color(0xFF00897B),
            background = Color(0xFFE8F7F4),
            size = CardSize.SMALL,
            tag = "Bonus"
        ),
        HomeCardData(
            id = "event_day",
            title = "Open day selezioni",
            description = "Una giornata dedicata ai colloqui rapidi nei negozi della tua zona.",
            icon = Icons.Default.Work,
            accent = Color(0xFF6D4C41),
            background = Color(0xFFF6EEE9),
            size = CardSize.SMALL
        )
    )

    Column(Modifier.padding(paddingValues)) {
        // Manteniamo lo stesso peso usato nelle altre schermate
        Header(Modifier.weight(1f))

        Column(
            modifier = Modifier
                .weight(4f)
                .padding(horizontal = 12.dp)
        ) {
            Text(
                text = "Ultime dal mondo SuperSpan",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp, bottom = 10.dp)
            )
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 8.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(cards) { card ->
                    when (card.size) {
                        CardSize.LARGE -> HomeFeatureCard(card, navController)
                        CardSize.MEDIUM -> HomeMediumCard(card, navController)
                        CardSize.SMALL -> HomeSmallCard(card, navController)
                    }
                }
            }
        }
    }

}

@Composable
@Preview(showBackground = true)
fun HomePreview() {
    Home(PaddingValues(0.dp), null)
}*/

package com.example.superspan

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = paddingValues.calculateBottomPadding())
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
                modifier = Modifier.clickable { navController?.navigateTopLevel(Destination.OFFERTE.route) }
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
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFD32F2F), modifier = Modifier.size(32.dp))
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text("Attenzione Richiesta", color = Color(0xFFD32F2F), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("Hai $pendingCandidacies candidature in sospeso da visionare.", color = Color(0xFFB71C1C), fontSize = 14.sp)
                    }
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
                    navController?.navigateTopLevel(Destination.ADD_COUPON.route)
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