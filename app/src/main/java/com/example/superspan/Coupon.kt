package com.example.superspan

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@Suppress("UNUSED_PARAMETER")
@Composable
fun CouponPageComplete(paddingValues: PaddingValues, navController: NavController?) {
    var selectedOffer: Coupon? by remember { mutableStateOf(null) }
    Box(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()
    ) {
        Column(Modifier.fillMaxSize()) {
            Header(Modifier.weight(1f))
            if(selectedOffer == null) {
                CouponListPage(Modifier.weight(4f)) { coupon ->
                    selectedOffer = coupon
                }
            } else {
                OfferPage(Modifier.weight(4f), selectedOffer!!) {
                    selectedOffer = null
                }
            }
        }
        if(actualUser?.admin ?: false) {
            FloatingActionButton(
                onClick = { navController?.navigate(Destination.ADD_COUPON.route) },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Aggiungi coupon")
            }
        }
    }
}

@Composable
fun CouponListPage(modifier: Modifier, onClick: (Coupon)->Unit) {
    LazyColumn(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        items(ListOfCoupon) { coupon ->
            if(coupon.products.size==1) CouponCard(coupon)
            else OfferCard(coupon) {
                onClick(coupon)
            }
        }
    }
}

@Composable
fun CouponCard(coupon: Coupon) {
    val (bgColor, alpha) = expirationColorAndAlpha(coupon.dateOfExpiration)

    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor.copy(alpha = alpha))
    ) {
        Column(modifier = Modifier
            .padding(12.dp)
            .fillMaxWidth()) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    // nome prodotto (primo prodotto) - centrato
                    val productName = coupon.products.firstOrNull()?.nome ?: "Prodotto"
                    Text(productName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(coupon.description, style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Scadenza: ${coupon.dateOfExpiration}", style = MaterialTheme.typography.bodySmall)
                }

                Spacer(modifier = Modifier.width(12.dp))

                // sconto a destra
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("-${coupon.discount.toInt()}%", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Codice coupon in grassetto e grande in basso
            Text(coupon.code, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold)
        }
    }
}

@Composable
fun OfferCard(coupon: Coupon, onClick: (Coupon) -> Unit) {
    val (bgColor, alpha) = expirationColorAndAlpha(coupon.dateOfExpiration)

    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable(onClick = { onClick(coupon) }),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor.copy(alpha = alpha))
    ) {
        Column(modifier = Modifier
            .padding(12.dp)
            .fillMaxWidth()) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    // non mostriamo i nomi dei prodotti, solo il numero
                    Text("Offerta: ${coupon.products.size} prodotti", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(coupon.description, style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Scadenza: ${coupon.dateOfExpiration}", style = MaterialTheme.typography.bodySmall)
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("-${coupon.discount.toInt()}%", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Codice coupon in grassetto e grande in basso
            Text(coupon.code, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold)
        }
    }
}

// Restituisce colore base e alpha in base alla scadenza
private fun expirationColorAndAlpha(dateString: String): Pair<Color, Float> {
    return try {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val expiry = LocalDate.parse(dateString, formatter)
        val today = LocalDate.now()
        val daysBetween = ChronoUnit.DAYS.between(today, expiry)

        when {
            daysBetween < 0 -> Pair(Color.Gray, 0.6f) // scaduto -> semi trasparente
            daysBetween == 0L -> Pair(Color(0xFFFFCDD2), 1.0f) // scade oggi -> rosso chiaro
            else -> Pair(Color(0xFFE3F2FD), 1.0f) // neutro (azzurro tenue)
        }
    } catch (_: Exception) {
        Pair(Color.LightGray, 1.0f)
    }
}

@Composable
fun OfferPage(modifier: Modifier, coupon: Coupon, onDismiss: () -> Unit) {
    val (bgColor, alpha) = expirationColorAndAlpha(coupon.dateOfExpiration)

    Column(
        modifier
            .background(bgColor.copy(alpha = alpha), TopOvalShape(50.dp))
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 20.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            coupon.description,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(18.dp))

        coupon.products.forEach { product ->
            ProductDiscountRow(product = product, coupon = coupon)
            Spacer(modifier = Modifier.height(12.dp))
        }

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.65f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    coupon.code,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "-${coupon.discount.toInt()}%",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onDismiss) {
            Text("Torna alla lista")
        }
    }
}

@Composable
private fun ProductDiscountRow(product: Product, coupon: Coupon) {
    val discountedPrice = coupon.calculateDiscountedPrice(product)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.82f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    product.nome,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        formatPrice(product.prezzo),
                        style = MaterialTheme.typography.bodyLarge,
                        textDecoration = TextDecoration.LineThrough,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        formatPrice(discountedPrice),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF1B5E20)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "-${coupon.discount.toInt()}%",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFFD32F2F)
                )
            }
        }
    }
}

private fun formatPrice(price: Float): String = "€%.2f".format(price)

@Composable
@Preview(showBackground = true)
fun CouponPreview() {
    CouponPageComplete(PaddingValues(0.dp), null)
}