package com.example.superspan

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.Abc
import androidx.compose.material.icons.filled.SortByAlpha
import androidx.compose.material.icons.filled.Payments
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import coil.compose.AsyncImage
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.collectLatest


// Contiene solo i dati finali, già digeriti e pronti per essere stampati
data class ProductUiState(
    val product: Product,
    val bestDiscount: Int?,
    val needsShrink: Boolean,
    val discountedPrice: Float?
)

class SearchViewModel : ViewModel() {

    val filterData = FilterData()

    // Attenzione: ora la lista contiene ProductUiState, non più Product grezzi!
    private val _productList = MutableStateFlow<List<ProductUiState>>(emptyList())
    val productList: StateFlow<List<ProductUiState>> = _productList.asStateFlow()

    init {
        // Popoliamo subito la lista, così alla prima apertura non compare un flash di "lista vuota"
        eseguiRicerca()
        viewModelScope.launch {
            snapshotFlow {
                // Leggiamo tutti gli stati rilevanti per innescare la ricerca
                listOf(
                    filterData.nome,
                    filterData.minPrice,
                    filterData.maxPrice,
                    filterData.ordinamentoNomeCrescente,
                    filterData.ordinamentoPrezzoCrescente,
                    filterData.categorie.toList()
                )
            }.collectLatest {
                delay(300) // Debounce per evitare ricalcoli eccessivi
                eseguiRicerca()
            }
        }
    }

    fun onSearchQueryChanged(newQuery: String) {
        filterData.nome = newQuery // questo innescherà lo snapshotFlow!
    }

    private fun eseguiRicerca() {
        viewModelScope.launch {
            // Spostiamo il calcolo pesante sul thread Default
            val risultatiPronti = withContext(Dispatchers.Default) {
                
                // 1. Otteniamo i prodotti dal finto database usando i filtri
                val rawProducts = searchProduct(filterData)
                
                // 2. MAPPATURA: Trasformiamo ogni Product in un ProductUiState
                rawProducts.map { product ->
                    // Calcolo sconto
                    val sconto: Int? = ListOfCoupon
                        .filter { it.products.contains(product) }
                        .maxOfOrNull { it.discount }
                        ?.toInt()
                    
                    // Calcolo logica visiva
                    val daRimpicciolire = product.nome == "Pane Fresco" || 
                                          product.nome == "Parmigiano Reggiano 200g" || 
                                          product.nome == "Detersivo Piatti"
                                          
                    val prezzoScontato = if (sconto != null && sconto > 0) {
                        product.prezzo * (1 - sconto.toFloat() / 100)
                    } else null

                    // Creiamo e restituiamo il pacchetto finito
                    ProductUiState(
                        product = product,
                        bestDiscount = sconto,
                        needsShrink = daRimpicciolire,
                        discountedPrice = prezzoScontato
                    )
                }
            }
            // 3. Inviamo la lista finita alla UI
            _productList.value = risultatiPronti
        }
    }
}

// ---------------------------------------------------------------------------
// PRODUCT CARD
// ---------------------------------------------------------------------------



@Composable
fun ProductCompose(uiState: ProductUiState, navController: NavController?) {

    // Estraiamo il prodotto per comodità di lettura
    val product = uiState.product

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(215.dp)
            .clickable { navController?.navigate(Destination.PRODOTTO.route + "/${product.nome}") },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // --- IMMAGINE ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(115.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFF8F9FA)),
                contentAlignment = Alignment.Center
            ) {
                if (product.image != null) {
                    // Leggiamo la variabile booleana già calcolata!
                    val imagePadding = if (uiState.needsShrink) 16.dp else 0.dp

                    // Usiamo Coil per caricare asincronamente
                    AsyncImage(
                        model = product.image,
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

                // --- BADGE SCONTO (in alto a sinistra) ---
                if (uiState.bestDiscount != null && uiState.bestDiscount > 0) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .background(
                                Brush.horizontalGradient(listOf(Color(0xFF4CAF50), Color(0xFF2E7D32))),
                                RoundedCornerShape(topStart = 16.dp, bottomEnd = 16.dp)
                            )
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text("-${uiState.bestDiscount}%", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // --- NOME PRODOTTO ---
            Text(
                text = product.nome,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                minLines = 2,
                lineHeight = 16.sp,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            // --- PREZZI (in basso) ---
            if (uiState.bestDiscount != null && uiState.discountedPrice != null) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "€ ${"%.2f".format(product.prezzo)}",
                        fontSize = 11.sp,
                        color = Color.Gray,
                        textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough
                    )
                    Surface(color = Color(0xFF2E7D32).copy(alpha = 0.12f), shape = CircleShape) {
                        Text(
                            text = "€ ${"%.2f".format(uiState.discountedPrice)}",
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp),
                            fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32)
                        )
                    }
                }
            } else {
                Surface(
                    color = androidx.compose.material3.MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "€ ${"%.2f".format(product.prezzo)}",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = androidx.compose.material3.MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// CATEGORY CHIP (usata nella search page)
// ---------------------------------------------------------------------------

@Composable
fun CategoryChip(category: Category, isSelected: Boolean, onClick: () -> Unit) {
    val bgColor = if (isSelected) androidx.compose.material3.MaterialTheme.colorScheme.primary else Color(0xFFEEEEEE)
    val textColor = if (isSelected) Color.White else Color.DarkGray

    Surface(
        modifier = Modifier.clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = bgColor,
        shadowElevation = if (isSelected) 4.dp else 1.dp
    ) {
        Text(
            text = category.nome,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            fontSize = 14.sp,
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
    padding: PaddingValues,
    onOpenFilters: () -> Unit,
    viewModel: SearchViewModel = viewModel() // Iniettiamo il ViewModel
) {
    // Osserviamo gli stati dal ViewModel in modo reattivo
    val filterData = viewModel.filterData
    val searchQuery = filterData.nome
    val productSearchList by viewModel.productList.collectAsState()

    val listState = rememberLazyGridState()

    Column(modifier = Modifier.fillMaxSize()) {
    PrimaryHeader("I nostri prodotti", "Trova quello che cerchi")
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        state = listState,
        modifier = Modifier.weight(1f).fillMaxWidth(),
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            top = 16.dp,
            bottom = padding.calculateBottomPadding() + 100.dp
        ),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // 1. BARRA DI RICERCA
        item(span = { GridItemSpan(maxLineSpan) }) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    modifier = Modifier.weight(1f).height(50.dp),
                    shape = RoundedCornerShape(25.dp),
                    shadowElevation = 6.dp,
                    color = Color.White
                ) {
                    TextField(
                        value = searchQuery, // Leggiamo lo stato dal ViewModel
                        onValueChange = { viewModel.onSearchQueryChanged(it) }, // Inviamo l'evento al ViewModel
                        placeholder = { Text("Cerca prodotto...", color = Color.Gray) },
                        modifier = Modifier.fillMaxSize(),
                        leadingIcon = { Icon(Icons.Default.Search, null, tint = Color.Gray) },
                        trailingIcon = {
                            IconButton(onClick = onOpenFilters) {
                                Icon(Icons.Default.Tune, contentDescription = "Filtri", tint = com.example.superspan.ui.theme.LogoLeft)
                            }
                        },
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            cursorColor = com.example.superspan.ui.theme.LogoLeft
                        )
                    )
                }
            }
        }

        val maxPossiblePrice = filterData.maxPossiblePrice()
        val hasActiveFilters = filterData.categorie.isNotEmpty() || filterData.minPrice > 0.0 || filterData.maxPrice < maxPossiblePrice

        if (hasActiveFilters) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 0.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (filterData.minPrice > 0.0 || filterData.maxPrice < maxPossiblePrice) {
                        item {
                            FilterChipCustom(
                                text = "€ ${"%.2f".format(filterData.minPrice)} - € ${"%.2f".format(filterData.maxPrice)}",
                                onRemove = { 
                                    filterData.minPrice = 0.0
                                    filterData.maxPrice = maxPossiblePrice.toDouble()
                                }
                            )
                        }
                    }
                    items(filterData.categorie.toList()) { cat ->
                        FilterChipCustom(
                            text = cat.nome,
                            onRemove = { filterData.categorie.remove(cat) }
                        )
                    }
                }
            }
        }

        // 2. ORDINAMENTO: Alfabetico (sinistra) | Prezzo (destra)
        item(span = { GridItemSpan(maxLineSpan) }) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // --- TASTO ALFABETICO (A-Z -> Z-A -> OFF) ---
                val nomeAttivo = filterData.ordinamentoNomeCrescente != null

                Button(
                    onClick = {
                        if (filterData.ordinamentoNomeCrescente == null) {
                            filterData.ordinamentoNomeCrescente = true
                        } else if (filterData.ordinamentoNomeCrescente == true) {
                            filterData.ordinamentoNomeCrescente = false
                        } else {
                            filterData.ordinamentoNomeCrescente = null
                        }
                    },
                    modifier = Modifier.height(44.dp).wrapContentWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (nomeAttivo) com.example.superspan.ui.theme.LogoLeft else Color.White,
                        contentColor = if (nomeAttivo) Color.White else Color.Gray
                    ),
                    border = null,
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp, pressedElevation = 2.dp),
                    shape = CircleShape,
                    contentPadding = PaddingValues(horizontal = 20.dp)
                ) {
                    Text(
                        text = if (nomeAttivo && filterData.ordinamentoNomeCrescente == false) "Z-A" else "A-Z",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(Modifier.width(12.dp))

                // --- TASTO PREZZO (Crescente -> Decrescente -> OFF) ---
                val prezzoAttivo = filterData.ordinamentoPrezzoCrescente != null

                Button(
                    onClick = {
                        if (filterData.ordinamentoPrezzoCrescente == null) {
                            filterData.ordinamentoPrezzoCrescente = true
                        } else if (filterData.ordinamentoPrezzoCrescente == true) {
                            filterData.ordinamentoPrezzoCrescente = false
                        } else {
                            filterData.ordinamentoPrezzoCrescente = null
                        }
                    },
                    modifier = Modifier.height(44.dp).wrapContentWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (prezzoAttivo) com.example.superspan.ui.theme.LogoLeft else Color.White,
                        contentColor = if (prezzoAttivo) Color.White else Color.Gray
                    ),
                    border = null,
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp, pressedElevation = 2.dp),
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
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "Prezzo",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.width(6.dp))
                        Icon(
                            imageVector = if (filterData.ordinamentoPrezzoCrescente == true) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
        }

        if (productSearchList.isEmpty()) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                EmptyState(
                    icon = Icons.Default.SearchOff,
                    title = "Nessun prodotto trovato",
                    subtitle = "Prova un altro termine di ricerca o cambia i filtri."
                )
            }
        }

        // 3. GRIGLIA PRODOTTI
        items(
            items = productSearchList,
            key = { uiState -> uiState.product.nome } // La key deve essere un tipo salvabile nel Bundle, come una String
        ) { product ->
            ProductCompose(uiState = product, navController = navController)
        }
    }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterPage(modifier: Modifier, filterData: FilterData, padding: PaddingValues, onDismiss: () -> Unit) {
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

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(com.example.superspan.ui.theme.LogoRight.copy(alpha = 0.03f))
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            FilterTitle(
                title = "Filtri Prodotti",
                paddingValues = padding
            )

            Column(Modifier.padding(horizontal = 24.dp)) {
                // --- 2. SEZIONE PREZZO (CARD MODERNA) ---
            Text("Fascia di prezzo", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.4f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(Modifier.padding(20.dp)) {
                    Text(
                        text = "Da €${"%.2f".format(sliderPosition.start)} a €${"%.2f".format(sliderPosition.endInclusive)}",
                        color = com.example.superspan.ui.theme.LogoLeft,
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
                                    .background(com.example.superspan.ui.theme.LogoLeft, CircleShape)
                                    .border(2.dp, Color.White, CircleShape)
                            )
                        },
                        endThumb = {
                            Box(
                                modifier = Modifier
                                    .size(22.dp)
                                    .background(com.example.superspan.ui.theme.LogoLeft, CircleShape)
                                    .border(2.dp, Color.White, CircleShape)
                            )
                        },
                        // Personalizzazione Barra (Track)
                        track = { rangeSliderState ->
                            SliderDefaults.Track(
                                modifier = Modifier.height(4.dp),
                                rangeSliderState = rangeSliderState,
                                colors = SliderDefaults.colors(
                                    activeTrackColor = com.example.superspan.ui.theme.LogoLeft,
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
                            onValueChange = { newText ->
                                minPriceText = newText
                                // Aggiorniamo lo slider (e quindi il filtro) solo se il testo è un numero valido
                                newText.replace(',', '.').toFloatOrNull()?.let { parsed ->
                                    val newMin = parsed.coerceIn(0f, sliderPosition.endInclusive)
                                    sliderPosition = newMin..sliderPosition.endInclusive
                                }
                            },
                            label = { Text("Min") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                        )
                        OutlinedTextField(
                            value = maxPriceText,
                            onValueChange = { newText ->
                                maxPriceText = newText
                                newText.replace(',', '.').toFloatOrNull()?.let { parsed ->
                                    val newMax = parsed.coerceIn(sliderPosition.start, absoluteMax)
                                    sliderPosition = sliderPosition.start..newMax
                                }
                            },
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

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.4f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(Modifier.padding(20.dp)) {
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
                }
            }

            Spacer(Modifier.height(100.dp + padding.calculateBottomPadding()))
            }
        }

        Button(
            onClick = onDismiss,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = padding.calculateBottomPadding() + 16.dp),
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(containerColor = com.example.superspan.ui.theme.LogoLeft)
        ) {
            Text(
                text = "Applica filtri",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
            )
        }

        FloatingFilterActions(
            onDismiss = onDismiss,
            onReset = {
                filterData.categorie.clear()
                sliderPosition = 0f..absoluteMax
                minPriceText = "0.00"
                maxPriceText = "%.2f".format(absoluteMax)
            },
            paddingValues = padding
        )
    }
}


@Composable
fun SearchPageComplete(navController: NavController?, padding: PaddingValues) {
    var showFilters by remember { mutableStateOf(false) }
    val viewModel: SearchViewModel = viewModel()

    if (showFilters) {
        FilterPage(
            modifier = Modifier.fillMaxSize(),
            filterData = viewModel.filterData,
            padding = padding,
            onDismiss = { showFilters = false }
        )
    } else {
        SearchPage(
            navController = navController,
            padding = padding,
            onOpenFilters = { showFilters = true },
            viewModel = viewModel
        )
    }
}

