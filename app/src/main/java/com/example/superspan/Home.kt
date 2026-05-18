package com.example.superspan

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
}