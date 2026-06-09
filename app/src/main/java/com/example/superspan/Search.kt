package com.example.superspan

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.Abc
import androidx.compose.material.icons.filled.SortByAlpha
import androidx.compose.material.icons.filled.Payments


// ---------------------------------------------------------------------------
// ENTRY POINT
// ---------------------------------------------------------------------------

@Composable
fun SearchPageComplete(
    padding: PaddingValues,
    navController: NavController?
) {
    var showFilters by remember { mutableStateOf(false) }
    val filterData by remember { mutableStateOf(FilterData()) }

    Box(modifier = Modifier.padding(padding)) {
        if (!showFilters) {
            SearchPage(navController, filterData) { showFilters = true }
        } else {
            FilterPage(Modifier.fillMaxSize(), filterData) { showFilters = false }
        }
    }
}

// ---------------------------------------------------------------------------
// PRODUCT CARD
// ---------------------------------------------------------------------------

@Composable
fun ProductCompose(product: Product, navController: NavController?) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clickable { navController?.navigate(Destination.PRODOTTO.route + "/${product.nome}") },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F1F1))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Immagine prodotto — altezza fissa
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                if (product.image != null) {
                    val needsShrink = product.nome == "Pane Fresco" || product.nome == "Parmigiano Reggiano 200g" || product.nome == "Detersivo Piatti"
                    val imagePadding = if (needsShrink) 16.dp else 0.dp
                    Image(
                        painter = androidx.compose.ui.res.painterResource(id = product.image!!),
                        contentDescription = product.nome,
                        modifier = Modifier.fillMaxSize().padding(imagePadding),
                        contentScale = androidx.compose.ui.layout.ContentScale.Fit
                    )
                } else {
                    Icon(
                        Icons.Default.Photo,
                        contentDescription = product.nome,
                        modifier = Modifier.size(48.dp),
                        tint = Color.LightGray
                    )
                }
            }
            // Nome prodotto — 1 riga, tronca con "..."
            Text(
                text = product.nome,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            // Badge prezzo — sempre in fondo
            Surface(
                color = Color(0xFF388E3C).copy(alpha = 0.12f),
                shape = CircleShape
            ) {
                Text(
                    text = "€ ${"%.2f".format(product.prezzo)}",
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E7D32)
                )
            }
        }
    }
}

// ---------------------------------------------------------------------------
// CATEGORY CHIP (usata nella search page)
// ---------------------------------------------------------------------------

@Composable
fun CategoryChip(category: Category, isSelected: Boolean, onClick: () -> Unit) {
    val bgColor = if (isSelected) Color(0xFF388E3C) else Color(0xFFEEEEEE)
    val textColor = if (isSelected) Color.White else Color.DarkGray

    Surface(
        modifier = Modifier.clickable { onClick() },
        shape = RoundedCornerShape(999.dp),
        color = bgColor
    ) {
        Text(
            text = category.nome,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = textColor
        )
    }
}

// ---------------------------------------------------------------------------
// SEARCH PAGE (lista prodotti + barra ricerca)
// ---------------------------------------------------------------------------

@Composable
fun SearchPage(
    navController: NavController?,
    filterData: FilterData,
    onOpenFilters: () -> Unit
) {
    val productSearchList by remember {
        derivedStateOf { searchProduct(filterData) }
    }

    // Direzioni indipendenti per ciascun ordinamento
    var nomeAscendente by remember { mutableStateOf(true) }
    var prezzoAscendente by remember { mutableStateOf(true) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 20.dp)
    ) {
        // 0. HEADER TITOLO (scorre con la pagina)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "I nostri prodotti",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "Trova quello che cerchi",
                    fontSize = 16.sp,
                    color = Color.Gray
                )
            }
        }

        // 1. BARRA DI RICERCA + FILTRI
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = filterData.nome,
                    onValueChange = { filterData.nome = it },
                    placeholder = { Text("Cerca prodotto...") },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    leadingIcon = { Icon(Icons.Default.Search, null) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFF7F7F7),
                        unfocusedContainerColor = Color(0xFFF7F7F7)
                    )
                )

                Spacer(Modifier.width(8.dp))

                FilledIconButton(
                    onClick = onOpenFilters,
                    modifier = Modifier.size(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = Color(0xFF388E3C)
                    )
                ) {
                    Icon(Icons.Default.Tune, contentDescription = "Filtri", tint = Color.White)
                }
            }
        }

        /*// 2. ORDINAMENTO: Nome (sinistra) | Prezzo (destra)
        item {
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // ── Pulsante NOME (sinistra) ──
                val nomeAttivo = filterData.ordinamento == "Nome"
                Button(
                    onClick = {
                        if (nomeAttivo) {
                            // Inverte solo la direzione del nome
                            nomeAscendente = !nomeAscendente
                            filterData.ordinamentoCrescente = nomeAscendente
                        } else {
                            // Attiva ordinamento nome, ripristina la sua direzione salvata
                            filterData.ordinamento = "Nome"
                            filterData.ordinamentoCrescente = nomeAscendente
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (nomeAttivo) Color(0xFF388E3C) else Color(0xFFA5D6A7)
                    )
                ) {
                    Text(
                        text = if (nomeAscendente) "A - Z" else "Z - A",
                        fontSize = 13.sp,
                        color = if (nomeAttivo) Color.White else Color(0xFF1B5E20)
                    )
                }

                // ── Pulsante PREZZO (destra) ──
                val prezzoAttivo = filterData.ordinamento == "Prezzo"
                Button(
                    onClick = {
                        if (prezzoAttivo) {
                            // Inverte solo la direzione del prezzo
                            prezzoAscendente = !prezzoAscendente
                            filterData.ordinamentoCrescente = prezzoAscendente
                        } else {
                            // Attiva ordinamento prezzo, ripristina la sua direzione salvata
                            filterData.ordinamento = "Prezzo"
                            filterData.ordinamentoCrescente = prezzoAscendente
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (prezzoAttivo) Color(0xFF388E3C) else Color(0xFFA5D6A7)
                    )
                ) {
                    Text(
                        text = "Prezzo ${if (prezzoAscendente) "▲" else "▼"}",
                        fontSize = 13.sp,
                        color = if (prezzoAttivo) Color.White else Color(0xFF1B5E20)
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
        }*/

        // 2. ORDINAMENTO: Alfabetico (sinistra) | Prezzo (destra)
        item {
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // --- TASTO ALFABETICO (A-Z -> Z-A -> OFF) ---
                val nomeAttivo = filterData.ordinamento == "Nome"

                Button(
                    onClick = {
                        if (!nomeAttivo) {
                            filterData.ordinamento = "Nome"
                            filterData.ordinamentoCrescente = true
                        } else if (filterData.ordinamentoCrescente) {
                            filterData.ordinamentoCrescente = false
                        } else {
                            filterData.ordinamento = ""
                        }
                    },
                    modifier = Modifier
                        .height(44.dp) // Leggermente più grande (da 40 a 44)
                        .wrapContentWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (nomeAttivo) Color(0xFF388E3C) else Color.Transparent,
                        contentColor = if (nomeAttivo) Color.White else Color.DarkGray
                    ),
                    border = if (!nomeAttivo) BorderStroke(1.dp, Color.LightGray) else null,
                    shape = CircleShape,
                    // Aumentiamo il padding orizzontale per rendere il tasto più "importante"
                    contentPadding = PaddingValues(horizontal = 20.dp)
                ) {
                    // Rimosso Row e Spacer: il testo ora si centra automaticamente
                    Text(
                        text = if (nomeAttivo && !filterData.ordinamentoCrescente) "Z-A" else "A-Z",
                        fontSize = 13.sp, // Leggermente più grande per Paolo
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(Modifier.width(12.dp))

                // --- TASTO PREZZO (Crescente -> Decrescente -> OFF) ---
                val prezzoAttivo = filterData.ordinamento == "Prezzo"

                Button(
                    onClick = {
                        if (!prezzoAttivo) {
                            filterData.ordinamento = "Prezzo"
                            filterData.ordinamentoCrescente = true
                        } else if (filterData.ordinamentoCrescente) {
                            filterData.ordinamentoCrescente = false
                        } else {
                            filterData.ordinamento = ""
                        }
                    },
                    modifier = Modifier
                        .height(44.dp) // Stessa altezza dell'altro
                        .wrapContentWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (prezzoAttivo) Color(0xFF388E3C) else Color.Transparent,
                        contentColor = if (prezzoAttivo) Color.White else Color.DarkGray
                    ),
                    border = if (!prezzoAttivo) BorderStroke(1.dp, Color.LightGray) else null,
                    shape = CircleShape,
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Payments,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp) // Icona leggermente più grande
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "Prezzo",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        if (prezzoAttivo) {
                            Spacer(Modifier.width(6.dp))
                            Icon(
                                imageVector = if (filterData.ordinamentoCrescente) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
        }

        // 3. GRIGLIA PRODOTTI (2 colonne fisse per look coerente)
        item {
            val columns = 2
            val chunked = productSearchList.chunked(columns)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                chunked.forEach { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        row.forEach { product ->
                            Box(Modifier.weight(1f)) {
                                ProductCompose(product, navController)
                            }
                        }
                        // Riempi riga incompleta
                        if (row.size < columns) {
                            repeat(columns - row.size) {
                                Spacer(Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// FILTER PAGE (stile WorkFilterPage)
// ---------------------------------------------------------------------------

/*@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterPage(modifier: Modifier, filterData: FilterData, onDismiss: () -> Unit) {
    val scrollState = rememberScrollState()

    val absoluteMax = filterData.maxPossiblePrice()

    var sliderPosition by remember {
        mutableStateOf(
            filterData.minPrice.toFloat()..filterData.maxPrice.toFloat()
        )
    }
    var minPriceText by remember { mutableStateOf("%.2f".format(sliderPosition.start)) }
    var maxPriceText by remember { mutableStateOf("%.2f".format(sliderPosition.endInclusive)) }

    // Sincronizza filterData con lo slider
    filterData.minPrice = sliderPosition.start.toDouble()
    filterData.maxPrice = sliderPosition.endInclusive.toDouble()

    Column(
        modifier = modifier
            .background(Color(0xFFF8F9FA))
            .verticalScroll(scrollState)
    ) {
        // ── HEADER ──────────────────────────────────────────────────────────
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.White,
            shadowElevation = 2.dp
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onDismiss) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Indietro")
                }
                Text(
                    "Filtri prodotti",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 8.dp)
                )
                Spacer(Modifier.weight(1f))
                TextButton(onClick = {
                    filterData.categorie.clear()
                    sliderPosition = 0f..absoluteMax
                    minPriceText = "0.00"
                    maxPriceText = "%.2f".format(absoluteMax)
                    filterData.ordinamento = "Nome"
                    filterData.ordinamentoCrescente = true
                }) {
                    Text("Reset", color = Color.Red, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        Column(Modifier.padding(24.dp)) {

            // ── 1. SEZIONE PREZZO ───────────────────────────────────────────
            Text("Fascia di prezzo", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    Color.LightGray.copy(alpha = 0.4f)
                )
            ) {
                Column(Modifier.padding(20.dp)) {
                    Text(
                        text = "Da €${"%.2f".format(sliderPosition.start)} a €${"%.2f".format(sliderPosition.endInclusive)}",
                        color = Color(0xFF388E3C),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp
                    )

                    Spacer(Modifier.height(12.dp))

                    // RANGE SLIDER MODERNO
                    RangeSlider(
                        value = sliderPosition,
                        onValueChange = { newRange ->
                            sliderPosition = newRange
                            minPriceText = "%.2f".format(newRange.start)
                            maxPriceText = "%.2f".format(newRange.endInclusive)
                        },
                        valueRange = 0f..absoluteMax,
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFF388E3C),
                            activeTrackColor = Color(0xFF388E3C),
                            inactiveTrackColor = Color.LightGray.copy(alpha = 0.3f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("€ 0,00", fontSize = 12.sp, color = Color.Gray)
                        Text("€ ${"%.2f".format(absoluteMax)}", fontSize = 12.sp, color = Color.Gray)
                    }

                    Spacer(Modifier.height(16.dp))
                    HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray.copy(alpha = 0.5f))
                    Spacer(Modifier.height(16.dp))

                    // CAMPI TESTO PREZZO MIN / MAX
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = minPriceText,
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            keyboardActions = KeyboardActions(onDone = {
                                val v = minPriceText.toFloatOrNull() ?: 0f
                                val clamped = v.coerceIn(0f, sliderPosition.endInclusive)
                                sliderPosition = clamped..sliderPosition.endInclusive
                                minPriceText = "%.2f".format(clamped)
                            }),
                            onValueChange = { txt ->
                                minPriceText = txt
                                val v = txt.toFloatOrNull()
                                if (v != null && v >= 0f && v <= sliderPosition.endInclusive) {
                                    sliderPosition = v..sliderPosition.endInclusive
                                }
                            },
                            label = { Text("Prezzo min") },
                            modifier = Modifier
                                .weight(1f)
                                .onFocusChanged { fs ->
                                    if (!fs.isFocused) {
                                        val v = minPriceText.toFloatOrNull() ?: 0f
                                        val clamped = v.coerceIn(0f, sliderPosition.endInclusive)
                                        sliderPosition = clamped..sliderPosition.endInclusive
                                        minPriceText = "%.2f".format(clamped)
                                    }
                                },
                            shape = RoundedCornerShape(12.dp)
                        )
                        OutlinedTextField(
                            value = maxPriceText,
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            keyboardActions = KeyboardActions(onDone = {
                                val v = maxPriceText.toFloatOrNull() ?: absoluteMax
                                val clamped = v.coerceIn(sliderPosition.start, absoluteMax)
                                sliderPosition = sliderPosition.start..clamped
                                maxPriceText = "%.2f".format(clamped)
                            }),
                            onValueChange = { txt ->
                                maxPriceText = txt
                                val v = txt.toFloatOrNull()
                                if (v != null && v <= absoluteMax && v >= sliderPosition.start) {
                                    sliderPosition = sliderPosition.start..v
                                }
                            },
                            label = { Text("Prezzo max") },
                            modifier = Modifier
                                .weight(1f)
                                .onFocusChanged { fs ->
                                    if (!fs.isFocused) {
                                        val v = maxPriceText.toFloatOrNull() ?: absoluteMax
                                        val clamped = v.coerceIn(sliderPosition.start, absoluteMax)
                                        sliderPosition = sliderPosition.start..clamped
                                        maxPriceText = "%.2f".format(clamped)
                                    }
                                },
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(32.dp))

            // ── 2. SEZIONE CATEGORIE ────────────────────────────────────────
            Text("Categorie", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))

            Category.entries.chunked(2).forEach { pair ->
                Row(Modifier.fillMaxWidth()) {
                    pair.forEach { category ->
                        Row(
                            Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            FilterCheckboxRow(
                                label = category.nome,
                                isChecked = filterData.categorie.contains(category),
                                onCheckedChange = { isChecked ->
                                    if (isChecked) filterData.categorie.add(category)
                                    else filterData.categorie.remove(category)
                                }
                            )
                        }
                    }
                    if (pair.size == 1) Spacer(Modifier.weight(1f))
                }
            }

            Spacer(Modifier.height(48.dp))

            // ── 3. TASTO APPLICA ────────────────────────────────────────────
            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF388E3C))
            ) {
                Text("Applica filtri", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(32.dp))
        }
    }
}*/

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterPage(modifier: Modifier, filterData: FilterData, onDismiss: () -> Unit) {
    val scrollState = rememberScrollState()
    val absoluteMax = filterData.maxPossiblePrice()

    // Stati locali per lo slider e i testi
    var sliderPosition by remember {
        mutableStateOf(filterData.minPrice.toFloat()..filterData.maxPrice.toFloat())
    }
    var minPriceText by remember { mutableStateOf("%.2f".format(sliderPosition.start)) }
    var maxPriceText by remember { mutableStateOf("%.2f".format(sliderPosition.endInclusive)) }

    // Sincronizzazione dati
    filterData.minPrice = sliderPosition.start.toDouble()
    filterData.maxPrice = sliderPosition.endInclusive.toDouble()

    Column(
        modifier = modifier
            .background(Color(0xFFF8F9FA)) // Sfondo grigio chiarissimo
            .verticalScroll(scrollState)
    ) {
        // --- 1. HEADER FISSO CON OMBRA ---
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.White,
            shadowElevation = 2.dp
        ) {
            Row(
                Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onDismiss) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Indietro")
                }
                Text(
                    "Filtri prodotti",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 8.dp)
                )
                Spacer(Modifier.weight(1f))
                TextButton(onClick = {
                    filterData.categorie.clear()
                    sliderPosition = 0f..absoluteMax
                    minPriceText = "0.00"
                    maxPriceText = "%.2f".format(absoluteMax)
                }) {
                    Text("Reset", color = Color.Red, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        Column(Modifier.padding(24.dp)) {

            // --- 2. SEZIONE PREZZO (CARD MODERNA) ---
            Text("Fascia di prezzo", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.4f))
            ) {
                Column(Modifier.padding(20.dp)) {
                    Text(
                        text = "Da €${"%.2f".format(sliderPosition.start)} a €${"%.2f".format(sliderPosition.endInclusive)}",
                        color = Color(0xFF388E3C),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp
                    )

                    Spacer(Modifier.height(12.dp))

                    // RANGE SLIDER MODERNO (Due pallini, barra sottile)
                    RangeSlider(
                        value = sliderPosition,
                        onValueChange = { newRange ->
                            sliderPosition = newRange
                            minPriceText = "%.2f".format(newRange.start)
                            maxPriceText = "%.2f".format(newRange.endInclusive)
                        },
                        valueRange = 0f..absoluteMax,
                        // Personalizzazione Pallini (Thumb)
                        startThumb = {
                            Box(
                                modifier = Modifier
                                    .size(22.dp)
                                    .background(Color(0xFF388E3C), CircleShape)
                                    .border(2.dp, Color.White, CircleShape)
                            )
                        },
                        endThumb = {
                            Box(
                                modifier = Modifier
                                    .size(22.dp)
                                    .background(Color(0xFF388E3C), CircleShape)
                                    .border(2.dp, Color.White, CircleShape)
                            )
                        },
                        // Personalizzazione Barra (Track)
                        track = { rangeSliderState ->
                            SliderDefaults.Track(
                                modifier = Modifier.height(4.dp),
                                rangeSliderState = rangeSliderState,
                                colors = SliderDefaults.colors(
                                    activeTrackColor = Color(0xFF388E3C),
                                    inactiveTrackColor = Color.LightGray.copy(alpha = 0.3f)
                                )
                            )
                        }
                    )

                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("€ 0,00", fontSize = 12.sp, color = Color.Gray)
                        Text("€ ${"%.2f".format(absoluteMax)}", fontSize = 12.sp, color = Color.Gray)
                    }

                    Spacer(Modifier.height(16.dp))
                    HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray.copy(alpha = 0.5f))
                    Spacer(Modifier.height(16.dp))

                    // Campi di testo per inserimento manuale
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = minPriceText,
                            onValueChange = { minPriceText = it },
                            label = { Text("Min") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                        )
                        OutlinedTextField(
                            value = maxPriceText,
                            onValueChange = { maxPriceText = it },
                            label = { Text("Max") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                        )
                    }
                }
            }

            Spacer(Modifier.height(32.dp))

            // --- 3. SEZIONE CATEGORIE ---
            Text("Categorie prodotti", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(16.dp))

            Category.entries.chunked(2).forEach { pair ->
                Row(Modifier.fillMaxWidth()) {
                    pair.forEach { category ->
                        Row(Modifier.weight(1f)) {
                            FilterCheckboxRow(
                                label = category.nome,
                                isChecked = filterData.categorie.contains(category),
                                onCheckedChange = { isChecked ->
                                    if (isChecked) filterData.categorie.add(category)
                                    else filterData.categorie.remove(category)
                                }
                            )
                        }
                    }
                    if (pair.size == 1) Spacer(Modifier.weight(1f))
                }
            }

            Spacer(Modifier.height(48.dp))

            // --- 4. TASTO APPLICA (STILE PILLOLA VERDE) ---
            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF388E3C))
            ) {
                Text("Applica filtri", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(32.dp))
        }
    }
}


@Preview(showBackground = true)
@Composable
fun SearchPreview() {
    SearchPageComplete(PaddingValues(0.dp), null)
}

