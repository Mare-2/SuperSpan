package com.example.superspan

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import kotlin.math.*


@Composable
fun SearchPageComplete(
    padding: PaddingValues,
    navController: NavController?
) {
    var enabled: Boolean by remember { mutableStateOf(false) }
    val filterData by remember { mutableStateOf(FilterData()) }
    Column(
        Modifier.padding(padding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if(!enabled) {
            Header(Modifier.weight(1f))
            SearchPage(Modifier.weight(4f), navController, filterData) {
                enabled = true
            }
        }
        else FilterPage(Modifier.fillMaxSize(), filterData) {enabled = false}
    }
}

@Composable
fun ProductCompose(product: Product, navController: NavController?) {
    Box(
        Modifier
            .fillMaxSize()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(20.dp))
            .background(Color.LightGray)
            .clickable(onClick = { navController?.navigate(Destination.PRODOTTO.route + "/${product.nome}") }),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                Icons.Default.Photo,
                null,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
            )
            Column (
                Modifier
                    .fillMaxWidth()
                    .weight(0.6f)
            ) {
                TestoAdattabile(product.nome, Modifier.padding(end = 25.dp, start = 10.dp), 15.sp)
                Spacer(Modifier.weight(1f))
                TestoAdattabile("${product.prezzo} $", Modifier.padding(start = 25.dp, end = 10.dp, bottom = 10.dp),
                    15.sp,
                    TextAlign.End
                )
            }
        }
    }
}

@Composable
fun CategoryItem(category: Category, isSelected: Boolean, onclick: ()->Unit) {
    var color by remember { mutableStateOf(Color.LightGray) }
    if (isSelected) color = Color.LightGray
    else color = Color.Unspecified
    Column(
        Modifier
            .background(color),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            Modifier
                .clip(RoundedCornerShape(10.dp))
                .border(5.dp, Color.Gray)
                .aspectRatio(1f)
                .weight(0.7f)
                .clickable(onClick = {
                    onclick()
                }),
            contentAlignment = Alignment.TopCenter
        ) {
            Image(category.icon, null, modifier = Modifier.fillMaxSize())
        }
        Text(category.nome, modifier = Modifier.weight(0.3f), fontSize = 12.sp)
    }
}

@Composable
fun SearchPage(
    modifier: Modifier,
    navController: NavController?,
    filterData: FilterData,
    onclick: () -> Unit
) {
    val productSearchList: List<Product> by remember {
        derivedStateOf {
            searchProduct(filterData)
        }
    }
    var showSortMenu by remember { mutableStateOf(false) }
    
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        OutlinedTextField(
            filterData.nome,
            {
                filterData.nome = it
            },
            label = { Text("Cerca prodotto") },
            modifier = Modifier
                .padding(top = 15.dp, bottom = 10.dp)
                .weight(0.8f),
            shape = RoundedCornerShape(30.dp)
        )
        Spacer(Modifier.height(5.dp))
        Row(
            Modifier
                .weight(0.5f)
                .fillMaxWidth()
                .padding(bottom = 8.dp, end = 10.dp, start = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box {
                Button({
                    showSortMenu = true
                }) {
                    Text("Ordina per: ${filterData.ordinamento} ${if(filterData.ordinamentoCrescente) "▲" else "▼"}")
                }
                DropdownMenu(
                    expanded = showSortMenu,
                    onDismissRequest = { showSortMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Nome ▲") },
                        onClick = {
                            filterData.ordinamento = "Nome"
                            filterData.ordinamentoCrescente = true
                            showSortMenu = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Nome ▼") },
                        onClick = {
                            filterData.ordinamento = "Nome"
                            filterData.ordinamentoCrescente = false
                            showSortMenu = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Prezzo ▲") },
                        onClick = {
                            filterData.ordinamento = "Prezzo"
                            filterData.ordinamentoCrescente = true
                            showSortMenu = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Prezzo ▼") },
                        onClick = {
                            filterData.ordinamento = "Prezzo"
                            filterData.ordinamentoCrescente = false
                            showSortMenu = false
                        }
                    )
                }
            }
            Button({onclick()}) {
                Text("Filtri")
            }
        }
        Spacer(Modifier.height(5.dp))
        LazyHorizontalGrid(
            rows = GridCells.Adaptive(200.dp),
            modifier = Modifier
                .weight(0.8f)
                .padding(bottom = 0.dp, start = 10.dp, end = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(30.dp)
        ) {
            items(Category.entries.toList(), key = null) { category ->
                CategoryItem(category, filterData.categorie.contains(category)) {
                    if (filterData.categorie.contains(category)) filterData.categorie.remove(category)
                    else filterData.categorie.add(category)
                }
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Adaptive(120.dp),
            modifier = Modifier
                .weight(2.9f)
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(productSearchList, key = { product -> product.nome }) { product ->
                ProductCompose(product, navController)
            }
        }
    }
}

@Composable
fun FilterPage(modifier: Modifier, filterData: FilterData, onDismiss: ()-> Unit) {
    var sliderPosition by remember { mutableStateOf(filterData.minPrice.toFloat()..filterData.maxPrice.toFloat()) }
    var minPrice by remember { mutableStateOf(sliderPosition.start.toString()) }
    var maxPrice by remember { mutableStateOf(sliderPosition.endInclusive.toString()) }
    filterData.minPrice = sliderPosition.start.toDouble()
    filterData.maxPrice = sliderPosition.endInclusive.toDouble()
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Bottom
    ) {
        Text("Filtri", fontSize = 30.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(20.dp, 16.dp, 20.dp, 8.dp))
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Categorie", fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
            Button(onClick = {
                filterData.categorie.clear()
                minPrice = "0.00"
                maxPrice = filterData.maxPossiblePrice().toString()
                sliderPosition = 0.0f..filterData.maxPossiblePrice()
                filterData.ordinamento = "Nome"
                filterData.ordinamentoCrescente = true
            }) {
                Text("Reset filtri")
            }
        }
        Spacer(Modifier.height(8.dp))
        LazyVerticalGrid(
            columns = GridCells.Adaptive(120.dp),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 160.dp, max = 420.dp)
                .padding(top = 8.dp, bottom = 8.dp, start = 10.dp, end = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(30.dp)
        ) {
            items(Category.entries.toList(), key = null) { category ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(filterData.categorie.contains(category), {
                        if (filterData.categorie.contains(category)) filterData.categorie.remove(category)
                        else filterData.categorie.add(category) }
                    )
                    Text(category.nome, modifier = Modifier.padding(start = 8.dp))
                }

            }
        }
        Spacer(Modifier.height(28.dp))
        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 14.dp)) {
            Text(text = "Seleziona intervallo: ${"%.2f".format(sliderPosition.start)} - ${"%.2f".format(sliderPosition.endInclusive)}")
            Spacer(Modifier.height(10.dp))

            RangeSlider(
                value = sliderPosition,
                onValueChange = {
                    sliderPosition = it
                    minPrice = "%.2f".format(sliderPosition.start)
                    maxPrice = "%.2f".format(sliderPosition.endInclusive)
                },
                valueRange = 0.0.toFloat()..filterData.maxPossiblePrice(), // Intervallo totale
                steps = 0 // Opzionale: aggiunge tacche
            )
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = minPrice,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            // quando l'utente preme "Done" sulla tastiera, rimuovi il focus
                            if(minPrice.isEmpty() || minPrice.toFloat()<0) {
                                minPrice = "0.00"
                                sliderPosition = 0.0f..sliderPosition.endInclusive
                            }
                        },
                        onSend = {
                            if(minPrice.isEmpty() || minPrice.toFloat()<0) {
                                minPrice = "0.00"
                                sliderPosition = 0.0f..sliderPosition.endInclusive
                            }
                        }),
                    onValueChange = {
                        minPrice = it
                        if (minPrice.toFloatOrNull()==null) {
                            minPrice = ""
                            sliderPosition = sliderPosition.start..filterData.maxPossiblePrice()
                        }
                        if (minPrice.isEmpty() || minPrice.toFloat()<0){
                            sliderPosition = 0.0f..sliderPosition.endInclusive
                        } else if(minPrice.toFloat()<=sliderPosition.endInclusive) {
                            sliderPosition = minPrice.toFloat()..sliderPosition.endInclusive
                        } else {
                            minPrice = sliderPosition.endInclusive.toString()
                            sliderPosition = minPrice.toFloat()..sliderPosition.endInclusive
                        }
                    },
                    label = { Text("Prezzo Minimo") },
                    modifier = Modifier
                        .padding(top = 0.dp)
                        .weight(1f)
                        .padding(end = 5.dp)
                        .onFocusChanged { focusState ->
                            // quando perde il focus e il campo è vuoto, impostalo a "0.00"
                            if (!focusState.isFocused && (minPrice.isEmpty() || minPrice.toFloat()<0)) {
                                minPrice = "0.00"
                                // aggiorna anche lo slider di conseguenza
                                sliderPosition = 0.0f..sliderPosition.endInclusive
                            }
                        }
                )
                OutlinedTextField(
                    value = maxPrice,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            // quando l'utente preme "Done" sulla tastiera, rimuovi il focus
                            if(maxPrice.isEmpty() || maxPrice.toFloat()<0) {
                                maxPrice = filterData.maxPossiblePrice().toString()
                                sliderPosition = sliderPosition.start..filterData.maxPossiblePrice()
                            } else {
                                maxPrice = sliderPosition.endInclusive.toString()
                            }
                        },
                        onSend = {
                            if(maxPrice.isEmpty() || maxPrice.toFloat()<0) {
                                maxPrice = filterData.maxPossiblePrice().toString()
                                sliderPosition = sliderPosition.start..filterData.maxPossiblePrice()
                            } else {
                                maxPrice = sliderPosition.endInclusive.toString()
                            }
                        }),
                    onValueChange = {
                        maxPrice = it
                        if (maxPrice.toFloatOrNull()==null) {
                            maxPrice = ""
                            sliderPosition = sliderPosition.start..filterData.maxPossiblePrice()
                        }
                        if (maxPrice.isEmpty() || maxPrice.toFloat()<0){
                            sliderPosition = sliderPosition.start..filterData.maxPossiblePrice()
                        } else if(maxPrice.toFloat()>filterData.maxPossiblePrice()) {
                            maxPrice = filterData.maxPossiblePrice().toString()
                            sliderPosition = sliderPosition.start..maxPrice.toFloat()
                        } else if(maxPrice.toFloat()>=sliderPosition.start) {
                            sliderPosition = sliderPosition.start..maxPrice.toFloat()
                        } else {
                            maxPrice = sliderPosition.start.toString()
                            sliderPosition = sliderPosition.start..maxPrice.toFloat()
                        }

                    },
                    label = { Text("Prezzo Massimo") },
                    modifier = Modifier
                        .padding(top = 0.dp)
                        .weight(1f)
                        .padding(end = 5.dp)
                        .onFocusChanged { focusState ->
                            // quando perde il focus e il campo è vuoto, impostalo a "0.00"
                            if (!focusState.isFocused && (maxPrice.isEmpty() || maxPrice.toFloat()<0)) {
                                maxPrice = filterData.maxPrice.toString()
                                // aggiorna anche lo slider di conseguenza
                                sliderPosition = sliderPosition.start..filterData.maxPossiblePrice()
                            }
                        }
                )

            }
        }
        Spacer(Modifier.height(14.dp))
        Row (horizontalArrangement = Arrangement.End, modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 14.dp)) {
            Button(onClick = {onDismiss()}) { Text("Applica") }
        }
        Spacer(Modifier.height(5.dp))
    }
}


@Preview(showBackground = true)
@Composable
fun SearchPreview() {
    SearchPageComplete(PaddingValues(0.dp), null)
}

