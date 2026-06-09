package com.example.superspan

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.ui.Modifier
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.text.font.FontWeight

@Composable
fun AddCoupon(paddingValues: PaddingValues, navController: NavController?) {
    var selected by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController?.popBackStack() }) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Indietro"
                )
            }
            Text("Aggiungi promo", style = MaterialTheme.typography.titleLarge)
        }

        Spacer(modifier = Modifier.height(12.dp))

        SingleChoiceSegmentedButtonRow(
            modifier = Modifier.fillMaxWidth()
        ) {
            SegmentedButton(
                selected = selected == 0,
                onClick = { selected = 0 },
                shape = SegmentedButtonDefaults.itemShape(0, 2)
            ) {
                Text("Aggiungi Coupon")
            }
            SegmentedButton(
                selected = selected == 1,
                onClick = { selected = 1 },
                shape = SegmentedButtonDefaults.itemShape(1, 2)
            ) {
                Text("Aggiungi Offerta")
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        if (selected == 0) {
            CouponForm(
                onSave = { newCoupon ->
                    ListOfCoupon.add(newCoupon)
                    navController?.popBackStack()
                }
            )
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Form offerta non ancora completato")
                    Text("Le offerte potranno contenere 2 o 3 prodotti.")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CouponForm(
    onSave: (Coupon) -> Unit
) {
    var code by rememberSaveable { mutableStateOf("") }
    var discount by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var expirationDate by rememberSaveable { mutableStateOf("") }
    var selectedProductName by rememberSaveable { mutableStateOf("") }
    var query by rememberSaveable { mutableStateOf("") }
    val searchResults = ListOfProduct.map { it.nome }

    val selectedProduct = ListOfProduct.find { it.nome == selectedProductName }
    val discountValue = discount.toFloatOrNull()
    val isFormValid =
        code.isNotBlank() &&
        description.isNotBlank() &&
        expirationDate.isNotBlank() &&
        selectedProduct != null &&
        discountValue != null &&
        discountValue in 0f..100f

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        OutlinedTextField(
            value = code,
            onValueChange = { code = it },
            label = { Text("Codice coupon") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = discount,
            onValueChange = { discount = it },
            label = { Text("Sconto %") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Descrizione") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2
        )

        var showDatePicker by remember { mutableStateOf(false) }
        val datePickerState = androidx.compose.material3.rememberDatePickerState()

        if (showDatePicker) {
            androidx.compose.material3.DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    androidx.compose.material3.TextButton(onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val formatter = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                            formatter.timeZone = java.util.TimeZone.getTimeZone("UTC")
                            expirationDate = formatter.format(java.util.Date(millis))
                        }
                        showDatePicker = false
                    }) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    androidx.compose.material3.TextButton(onClick = { showDatePicker = false }) {
                        Text("Annulla")
                    }
                }
            ) {
                androidx.compose.material3.DatePicker(state = datePickerState)
            }
        }

        OutlinedTextField(
            value = expirationDate,
            onValueChange = { },
            label = { Text("Scadenza") },
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { showDatePicker = true }) {
                    Icon(androidx.compose.material.icons.Icons.Default.DateRange, contentDescription = "Seleziona Data")
                }
            }
        )

        SimpleSearchBar(
            query = query,
            onQueryChange = { query = it },
            searchResults = searchResults,
            onResultSelected = { selectedProductName = it }
        )

        Text(
            "I coupon possono avere un solo prodotto.",
            style = MaterialTheme.typography.bodyMedium
        )

        Button(
            onClick = {
                val product = selectedProduct ?: return@Button
                val discountFloat = discount.toFloatOrNull() ?: return@Button
                onSave(
                    Coupon(
                        code.trim(),
                        discountFloat,
                        description.trim(),
                        expirationDate.trim(),
                        product
                    )
                )
            },
            enabled = isFormValid,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Salva coupon")
        }
    }
}

@Composable
@Preview(showBackground = true)
fun AddCouponPreview() {
    AddCoupon(PaddingValues(0.dp), null)
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun SimpleSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    searchResults: List<String>,
    onResultSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // fallback implementation using OutlinedTextField + DropdownMenu to avoid SearchBar crashes
    var expanded by rememberSaveable { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    // When dropdown opens, ensure TextField has focus and keyboard is visible
    LaunchedEffect(expanded) {
        if (expanded) {
            focusRequester.requestFocus()
            keyboardController?.show()
        }
    }

    Box(
        modifier
            .fillMaxWidth()
            .semantics { isTraversalGroup = true }
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = {
                onQueryChange(it)
                expanded = it.isNotBlank()
                // keep focus and keyboard visible when typing
                focusRequester.requestFocus()
                keyboardController?.show()
            },
            label = { Text("Cerca prodotto") },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester)
                .onFocusChanged { state -> if (state.isFocused) expanded = query.isNotBlank() },
            singleLine = true
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            val filtered = if (query.isBlank()) searchResults else searchResults.filter { it.contains(query, ignoreCase = true) }
            if (filtered.isEmpty()) {
                DropdownMenuItem(text = { Text("Nessun risultato") }, onClick = { expanded = false })
            } else {
                filtered.forEach { result ->
                    DropdownMenuItem(
                        text = { Text(result) },
                        onClick = {
                            onResultSelected(result)
                            onQueryChange(result)
                            // keep keyboard visible after selecting a suggestion
                            focusRequester.requestFocus()
                            keyboardController?.show()
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

/*package com.example.superspan

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCoupon(paddingValues: PaddingValues, navController: NavController?) {
    var selectedType by remember { mutableIntStateOf(0) } // 0 = Singolo, 1 = Bundle

    Column(Modifier.padding(paddingValues).fillMaxSize().background(Color.White)) {
        // Header
        Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { navController?.popBackStack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) }
            Text("Crea Nuova Promo", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }

        SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
            SegmentedButton(selected = selectedType == 0, onClick = { selectedType = 0 }, shape = SegmentedButtonDefaults.itemShape(0, 2)) {
                Text("Coupon Singolo")
            }
            SegmentedButton(selected = selectedType == 1, onClick = { selectedType = 1 }, shape = SegmentedButtonDefaults.itemShape(1, 2)) {
                Text("Offerta Bundle")
            }
        }

        Column(Modifier.fillMaxSize().padding(24.dp).verticalScroll(rememberScrollState())) {
            if (selectedType == 0) {
                CouponSingleForm { new -> ListOfCoupon.add(new); navController?.popBackStack() }
            } else {
                OfferBundleForm { new -> ListOfCoupon.add(new); navController?.popBackStack() }
            }
        }
    }
}

@Composable
fun CouponSingleForm(onSave: (Coupon) -> Unit) {
    var code by rememberSaveable { mutableStateOf("") }
    var discount by rememberSaveable { mutableStateOf("") }
    var desc by rememberSaveable { mutableStateOf("") }
    var expiry by rememberSaveable { mutableStateOf("2026-06-30") }
    var selectedProduct by remember { mutableStateOf<Product?>(null) }
    var query by remember { mutableStateOf("") }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        OutlinedTextField(value = code, onValueChange = { code = it }, label = { Text("Codice Coupon") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = discount, onValueChange = { discount = it }, label = { Text("Sconto %") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Descrizione breve") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = expiry, onValueChange = { expiry = it }, label = { Text("Scadenza (yyyy-MM-dd)") }, modifier = Modifier.fillMaxWidth())

        Text("Seleziona Prodotto", fontWeight = FontWeight.Bold)
        SimpleSearchBar(query, { query = it }, ListOfProduct.map { it.nome }, { name ->
            selectedProduct = ListOfProduct.find { it.nome == name }
            query = name
        })

        selectedProduct?.let {
            Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))) {
                Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Add, null, tint = Color(0xFF388E3C))
                    Text(" Selezionato: ${it.nome}", fontWeight = FontWeight.Medium)
                }
            }
        }

        Button(
            onClick = { onSave(Coupon(code.trim(), discount.toFloatOrNull() ?: 0f, desc, expiry, selectedProduct!!)) },
            enabled = code.isNotBlank() && selectedProduct != null,
            modifier = Modifier.fillMaxWidth().height(56.dp), shape = CircleShape
        ) { Text("Salva Coupon") }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OfferBundleForm(onSave: (Coupon) -> Unit) {
    var code by rememberSaveable { mutableStateOf("") }
    var discount by rememberSaveable { mutableStateOf("") }
    var desc by rememberSaveable { mutableStateOf("") }
    var expiry by rememberSaveable { mutableStateOf("2026-06-30") }
    val selectedProducts = remember { mutableStateListOf<Product>() }
    var query by remember { mutableStateOf("") }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        OutlinedTextField(code, { code = it }, label = { Text("Codice Promo") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(discount, { discount = it }, label = { Text("Sconto % (per tutti)") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(desc, { desc = it }, label = { Text("Titolo Offerta") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(expiry, { expiry = it }, label = { Text("Scadenza") }, modifier = Modifier.fillMaxWidth())

        Text("Prodotti nel Bundle (${selectedProducts.size})", fontWeight = FontWeight.Bold)
        SimpleSearchBar(query, { query = it }, ListOfProduct.map { it.nome }, { name ->
            val p = ListOfProduct.find { it.nome == name }
            if (p != null && !selectedProducts.contains(p)) {
                selectedProducts.add(p)
                query = ""
            }
        })

        // Lista prodotti selezionati con possibilità di rimuoverli (per Daniela)
        selectedProducts.forEach { p ->
            Surface(Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp), color = Color(0xFFF1F1F1)) {
                Row(Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(p.nome, Modifier.weight(1f), fontSize = 14.sp)
                    IconButton(onClick = { selectedProducts.remove(p) }, Modifier.size(24.dp)) {
                        Icon(Icons.Default.Close, null, tint = Color.Red)
                    }
                }
            }
        }

        Button(
            onClick = { onSave(Coupon(code.trim(), discount.toFloatOrNull() ?: 0f, desc, expiry, *selectedProducts.toTypedArray())) },
            enabled = code.isNotBlank() && selectedProducts.size >= 2,
            modifier = Modifier.fillMaxWidth().height(56.dp), shape = CircleShape
        ) { Text("Pubblica Offerta Bundle") }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    searchResults: List<String>,
    onResultSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = query,
            onValueChange = {
                onQueryChange(it)
                expanded = it.isNotBlank() // Mostra il menu solo se c'è testo
            },
            label = { Text("Cerca prodotto per nome") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.Search, null) },
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        // Menu a tendina con i suggerimenti
        DropdownMenu(
            expanded = expanded && query.isNotBlank(),
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth(0.8f), // Leggermente più stretto del campo
            properties = androidx.compose.ui.window.DropdownMenuProperties(focusable = false)
        ) {
            val filteredResults = searchResults.filter {
                it.contains(query, ignoreCase = true)
            }

            if (filteredResults.isEmpty()) {
                DropdownMenuItem(
                    text = { Text("Nessun prodotto trovato") },
                    onClick = { expanded = false }
                )
            } else {
                filteredResults.take(5).forEach { result -> // Mostriamo i primi 5 risultati
                    DropdownMenuItem(
                        text = { Text(result) },
                        onClick = {
                            onResultSelected(result)
                            onQueryChange("") // Puliamo la barra dopo la selezione
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}*/