package com.example.superspan

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import androidx.navigation.NavDirections

@Composable
fun ProductPage(product: Product?, navController: NavController?, paddingValues: PaddingValues) {
    var showNutritionalInfo by remember { mutableStateOf(false) }

    Box(
        Modifier
            .padding(paddingValues)
            .fillMaxSize()
            .background(Color(0xFFF9F9F9)) // Sfondo super chiaro per il contrasto
    ) {
        Column(Modifier.fillMaxSize()) {
            // Area Immagine Superiore
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(4.5f)
                    .shadow(12.dp, RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                    .background(Color.White, RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
            ) {
                if (product?.image != null) {
                    val needsShrink = product.nome == "Pane Fresco" || product.nome == "Parmigiano Reggiano 200g" || product.nome == "Detersivo Piatti"
                    val horizontalPadding = if (needsShrink) 80.dp else 40.dp
                    val topPadding = if (needsShrink) 90.dp else 70.dp
                    Image(
                        painter = androidx.compose.ui.res.painterResource(id = product.image!!),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = topPadding, bottom = 30.dp, start = horizontalPadding, end = horizontalPadding),
                        contentScale = androidx.compose.ui.layout.ContentScale.Fit
                    )
                } else {
                    Image(
                        imageVector = Icons.Default.Image,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 70.dp, bottom = 30.dp),
                        colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(Color(0xFFE0E0E0))
                    )
                }

                // Pulsante Indietro sovrapposto
                IconButton(
                    onClick = { navController?.popBackStack() },
                    modifier = Modifier
                        .padding(top = 16.dp, start = 16.dp)
                        .background(Color.White.copy(alpha = 0.8f), androidx.compose.foundation.shape.CircleShape)
                        .size(48.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.Black)
                }
            }

            // Area Dettagli Inferiore
            Column(
                modifier = Modifier
                    .weight(5.5f)
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
            ) {
                Spacer(Modifier.height(24.dp))
                
                // Chip Categoria
                Box(
                    modifier = Modifier
                        .background(Color(0xFFE3F2FD), RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = product?.categoria?.nome?.uppercase() ?: "",
                        color = Color(0xFF1565C0),
                        fontSize = 11.sp,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )
                }

                Spacer(Modifier.height(12.dp))

                // Nome e Prezzo
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = product?.nome ?: "",
                        fontSize = 28.sp,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.ExtraBold,
                        color = Color.Black,
                        modifier = Modifier.weight(1f)
                    )
                    val bestDiscount = product?.let { p -> ListOfCoupon.filter { it.products.contains(p) }.maxOfOrNull { it.discount } }
                    if (bestDiscount != null && bestDiscount > 0) {
                        val discountedPrice = (product?.prezzo ?: 0f) * (1 - bestDiscount / 100)
                        Column(horizontalAlignment = Alignment.End, modifier = Modifier.padding(start = 16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    color = Color.Red,
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.padding(end = 8.dp)
                                ) {
                                    Text("-${bestDiscount.toInt()}%", color = Color.White, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                }
                                Text(
                                    text = "€ ${"%.2f".format(product?.prezzo ?: 0.0f)}",
                                    fontSize = 16.sp,
                                    color = Color.Gray,
                                    textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough,
                                    fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
                                )
                            }
                            Text(
                                text = "€ ${"%.2f".format(discountedPrice)}",
                                fontSize = 28.sp,
                                color = Color(0xFFD32F2F),
                                fontWeight = androidx.compose.ui.text.font.FontWeight.ExtraBold
                            )
                        }
                    } else {
                        Text(
                            text = "€ ${"%.2f".format(product?.prezzo ?: 0.0f)}",
                            fontSize = 26.sp,
                            color = Color(0xFF2E7D32),
                            fontWeight = androidx.compose.ui.text.font.FontWeight.ExtraBold,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))

                // Area Scorrevole con Info
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Dettagli Prodotto",
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color.Black
                        )
                        if (product?.categoria != Category.PULIZIA_CASA && product?.categoria != Category.IGIENE_PERSONALE) {
                            IconButton(onClick = { showNutritionalInfo = true }) {
                                Icon(Icons.Default.Info, contentDescription = "Valori Nutrizionali", tint = Color(0xFF1565C0))
                            }
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = product?.descrizione ?: "",
                        textAlign = androidx.compose.ui.text.style.TextAlign.Start,
                        color = Color(0xFF616161),
                        fontSize = 15.sp,
                        lineHeight = 22.sp
                    )
                    
                    if (product?.ingredienti != null) {
                        Spacer(Modifier.height(24.dp))
                        Text(
                            text = "Ingredienti",
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color.Black
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = product.ingredienti!!,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Start,
                            color = Color(0xFF757575),
                            fontSize = 14.sp,
                            lineHeight = 20.sp
                        )
                    }
                    Spacer(Modifier.height(24.dp)) // Padding per scrollare fino in fondo senza coprire il pulsante
                }
            }
        }
    }

    if (showNutritionalInfo) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showNutritionalInfo = false },
            title = { Text("Valori Nutrizionali", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold) },
            text = {
                Column {
                    Text("Valori medi per 100g:")
                    Spacer(Modifier.height(12.dp))
                    Text("Energia: 250 kcal")
                    Text("Grassi: 5 g")
                    Text("Carboidrati: 40 g")
                    Text("Proteine: 10 g")
                    Text("Sale: 0.5 g")
                    Spacer(Modifier.height(16.dp))
                    Text("I valori mostrati sono puramente indicativi a scopo dimostrativo.", fontSize = 12.sp, color = Color.Gray)
                }
            },
            confirmButton = {
                androidx.compose.material3.TextButton(onClick = { showNutritionalInfo = false }) {
                    Text("Chiudi")
                }
            }
        )
    }
}

@Composable
@Preview(showBackground = true)
fun ProductPreview() {
    ProductPage(ListOfProduct[0], null, PaddingValues(0.dp))
}

//TODO: da aggiustare visto che cambiando topOvalShape è morto