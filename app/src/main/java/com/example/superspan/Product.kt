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
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
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
            .fillMaxSize()
            .background(com.example.superspan.ui.theme.LogoRight.copy(alpha = 0.03f))
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Area Immagine
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(350.dp)
                    .background(
                        androidx.compose.ui.graphics.Brush.verticalGradient(
                            colors = listOf(
                                Color.White,
                                Color.White,
                                com.example.superspan.ui.theme.LogoRight.copy(alpha = 0.03f)
                            )
                        )
                    )
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
                            .padding(top = topPadding + paddingValues.calculateTopPadding(), bottom = 30.dp, start = horizontalPadding, end = horizontalPadding),
                        contentScale = androidx.compose.ui.layout.ContentScale.Fit
                    )
                } else {
                    Image(
                        imageVector = Icons.Default.Image,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 70.dp + paddingValues.calculateTopPadding(), bottom = 30.dp),
                        colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(Color(0xFFE0E0E0))
                    )
                }
            }

            // Area Dettagli
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                Spacer(Modifier.height(24.dp))
                
                // Chip Categoria
                Box(
                    modifier = Modifier
                        .background(com.example.superspan.ui.theme.LogoLeft.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = product?.categoria?.nome?.uppercase() ?: "",
                        color = com.example.superspan.ui.theme.LogoLeft,
                        fontSize = 11.sp,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )
                }

                Spacer(Modifier.height(4.dp))

                // Nome e Prezzo (Compatto)
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
                        modifier = Modifier.weight(1f).padding(end = 16.dp),
                        lineHeight = 34.sp
                    )

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color.White,
                        shadowElevation = 2.dp
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalAlignment = Alignment.End
                        ) {
                            val bestDiscount = product?.let { p -> ListOfCoupon.filter { it.products.contains(p) }.maxOfOrNull { it.discount } }
                            if (bestDiscount != null && bestDiscount > 0) {
                                val discountedPrice = (product?.prezzo ?: 0f) * (1 - bestDiscount / 100)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        color = Color(0xFF2E7D32),
                                        shape = RoundedCornerShape(6.dp),
                                        modifier = Modifier.padding(end = 6.dp)
                                    ) {
                                        Text("-${bestDiscount.toInt()}%", color = Color.White, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                                    }
                                    Text(
                                        text = "€ ${"%.2f".format(product?.prezzo ?: 0.0f)}",
                                        fontSize = 13.sp,
                                        color = Color.Gray,
                                        textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough,
                                        fontWeight = androidx.compose.ui.text.font.FontWeight.Medium
                                    )
                                }
                                Text(
                                    text = "€ ${"%.2f".format(discountedPrice)}",
                                    fontSize = 24.sp,
                                    color = Color(0xFF2E7D32),
                                    fontWeight = androidx.compose.ui.text.font.FontWeight.ExtraBold
                                )
                            } else {
                                Text(
                                    text = "€ ${"%.2f".format(product?.prezzo ?: 0.0f)}",
                                    fontSize = 24.sp,
                                    color = Color.Black,
                                    fontWeight = androidx.compose.ui.text.font.FontWeight.ExtraBold
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))

                    // Card Descrizione
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = Color.White,
                        shadowElevation = 2.dp
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(
                                text = "Dettagli Prodotto",
                                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                                fontSize = 18.sp,
                                color = Color.Black
                            )
                            Spacer(Modifier.height(12.dp))
                            Text(
                                text = product?.descrizione ?: "",
                                textAlign = androidx.compose.ui.text.style.TextAlign.Start,
                                color = Color(0xFF616161),
                                fontSize = 15.sp,
                                lineHeight = 22.sp
                            )
                        }
                    }
                    
                    if (product?.ingredienti != null) {
                        Spacer(Modifier.height(16.dp))
                        // Card Ingredienti
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            color = Color.White,
                            shadowElevation = 2.dp
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Text(
                                    text = "Ingredienti",
                                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = Color.Black
                                )
                                Spacer(Modifier.height(12.dp))
                                Text(
                                    text = product.ingredienti!!,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Start,
                                    color = Color(0xFF757575),
                                    fontSize = 15.sp,
                                    lineHeight = 22.sp
                                )
                            }
                        }
                    }

                    if (product?.categoria != Category.PULIZIA_CASA && product?.categoria != Category.IGIENE_PERSONALE) {
                        Spacer(Modifier.height(16.dp))
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .clickable { showNutritionalInfo = true },
                            shape = RoundedCornerShape(16.dp),
                            color = Color.White,
                            shadowElevation = 2.dp
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .background(com.example.superspan.ui.theme.LogoLeft.copy(alpha = 0.1f), androidx.compose.foundation.shape.CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Info, null, modifier = Modifier.size(24.dp), tint = com.example.superspan.ui.theme.LogoLeft)
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Valori Nutrizionali", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, fontSize = 16.sp)
                                    Text("Scopri calorie e macronutrienti", fontSize = 13.sp, color = Color.Gray)
                                }
                                Icon(Icons.Default.ChevronRight, null, tint = Color.LightGray)
                            }
                        }
                    }
                    Spacer(Modifier.height(24.dp + paddingValues.calculateBottomPadding()))
            }
        }

        // Pulsante Indietro Fisso Sovrapposto
        IconButton(
            onClick = { navController?.popBackStack() },
            modifier = Modifier
                .padding(top = 16.dp + paddingValues.calculateTopPadding(), start = 16.dp)
                .background(Color.White.copy(alpha = 0.8f), androidx.compose.foundation.shape.CircleShape)
                .size(48.dp)
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.Black)
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