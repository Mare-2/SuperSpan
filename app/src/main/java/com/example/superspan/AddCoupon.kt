package com.example.superspan

import android.widget.Toast
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.ui.graphics.Brush
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.LaunchedEffect

@Composable
fun AddCoupon(paddingValues: PaddingValues, navController: NavController?) {
    val defaultTab = navController?.previousBackStackEntry?.savedStateHandle?.get<Int>("add_type") ?: 0
    var isSelectionOpen by remember { mutableStateOf(false) }
    var pendingProductSelection by remember { mutableStateOf<((Product) -> Unit)?>(null) }
    
    var isMultiSelectionOpen by remember { mutableStateOf(false) }
    var pendingMultiProductSelection by remember { mutableStateOf<((List<Product>) -> Unit)?>(null) }
    var initialSelectionForMulti by remember { mutableStateOf<List<Product>>(emptyList()) }
    var maxSelectionForMulti by remember { mutableIntStateOf(3) }
    
    var pendingCouponSave by remember { mutableStateOf<Coupon?>(null) }
    var hasUnsavedChanges by remember { mutableStateOf(false) }
    var showBackConfirm by remember { mutableStateOf(false) }
    val context = LocalContext.current

    androidx.activity.compose.BackHandler(enabled = hasUnsavedChanges) {
        showBackConfirm = true
    }

    AuraBackground(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding())
                .verticalScroll(rememberScrollState())
        ) {
            // --- HEADER ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 80.dp, bottom = 16.dp)
            ) {
                Column(Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (defaultTab == 0) "Aggiungi Coupon" else "Aggiungi Offerta",
                        color = Color.Black,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(8.dp))

            if (defaultTab == 0) {
                CouponForm(
                    onMultiSelectProductRequest = { initial, max, callback ->
                        initialSelectionForMulti = initial
                        maxSelectionForMulti = max
                        pendingMultiProductSelection = callback
                        isMultiSelectionOpen = true
                    },
                    onSave = { newCoupon ->
                        pendingCouponSave = newCoupon
                    },
                    onFormDirty = { dirty -> hasUnsavedChanges = dirty }
                )
            } else {
                PromoForm(
                    onSelectProductRequest = { callback ->
                        pendingProductSelection = callback
                        isSelectionOpen = true
                    },
                    onSave = { newPromo ->
                        pendingCouponSave = newPromo
                    },
                    onFormDirty = { dirty -> hasUnsavedChanges = dirty }
                )
            }
            Spacer(modifier = Modifier.height(paddingValues.calculateBottomPadding() + 24.dp))
        }
        }

        // Floating Back Button
        IconButton(
            onClick = { 
                if (hasUnsavedChanges) {
                    showBackConfirm = true
                } else {
                    navController?.popBackStack() 
                }
            },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 16.dp + paddingValues.calculateTopPadding(), start = 16.dp)
                .background(Color.White.copy(alpha = 0.7f), CircleShape)
                .size(48.dp)
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Indietro", tint = com.example.superspan.ui.theme.LogoLeft)
        }

        if (isSelectionOpen) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {} // Intercept clicks so they don't fall through to the form below
                    )
            ) {
                ProductSelectionScreen(
                    onBack = { isSelectionOpen = false },
                    onSelected = { product ->
                        pendingProductSelection?.invoke(product)
                        isSelectionOpen = false
                    }
                )
            }
        }

        if (isMultiSelectionOpen) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {} // Intercept clicks so they don't fall through to the form below
                    )
            ) {
                MultiProductSelectionScreen(
                    initialSelection = initialSelectionForMulti,
                    maxSelection = maxSelectionForMulti,
                    onBack = { isMultiSelectionOpen = false },
                    onConfirm = { products ->
                        pendingMultiProductSelection?.invoke(products)
                        isMultiSelectionOpen = false
                    }
                )
            }
        }

        if (pendingCouponSave != null) {
            val isCouponSave = pendingCouponSave!!.products.size == 3
            ModernAlertDialog(
                onDismissRequest = { pendingCouponSave = null },
                title = "Conferma Creazione",
                text = if (isCouponSave) "Vuoi salvare il nuovo coupon?" else "Vuoi salvare la nuova offerta?",
                icon = Icons.Default.Save,
                confirmText = "Salva",
                onConfirm = {
                    ListOfCoupon.add(pendingCouponSave!!)
                    val code = pendingCouponSave!!.code
                    pendingCouponSave = null
                    Toast.makeText(context, "Salvato con successo", Toast.LENGTH_SHORT).show()
                    navController?.previousBackStackEntry?.savedStateHandle?.set("added_coupon_code", code)
                    navController?.popBackStack()
                },
                dismissText = "Annulla",
                onDismiss = { pendingCouponSave = null }
            )
        }

        if (showBackConfirm) {
            ModernAlertDialog(
                onDismissRequest = { showBackConfirm = false },
                title = "Attenzione",
                text = "Hai delle modifiche non salvate. Vuoi uscire comunque senza salvare?",
                icon = Icons.Default.Close,
                isDestructive = true,
                confirmText = "Esci",
                onConfirm = {
                    showBackConfirm = false
                    navController?.popBackStack()
                },
                dismissText = "Annulla",
                onDismiss = { showBackConfirm = false }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CouponForm(
    onMultiSelectProductRequest: (List<Product>, Int, (List<Product>) -> Unit) -> Unit,
    onSave: (Coupon) -> Unit,
    onFormDirty: (Boolean) -> Unit
) {
    var code by rememberSaveable { mutableStateOf("") }
    var discount by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var expirationDate by rememberSaveable { mutableStateOf("") }
    val selectedProducts = remember { mutableStateListOf<Product>() }

    val isDirty = code.isNotEmpty() || discount.isNotEmpty() || description.isNotEmpty() || expirationDate.isNotEmpty() || selectedProducts.isNotEmpty()
    LaunchedEffect(isDirty) {
        onFormDirty(isDirty)
    }

    val discountValue = discount.toFloatOrNull()
    
    val isCodeError = code.isNotEmpty() && code.length < 3
    val isDiscountError = discount.isNotEmpty() && (discountValue == null || discountValue !in 0f..100f)
    val isDescriptionError = description.isNotEmpty() && description.length < 5
    
    val isExpirationValid = try {
        val d = java.time.LocalDate.parse(expirationDate)
        !d.isBefore(java.time.LocalDate.now())
    } catch (e: Exception) { false }
    val isExpirationError = expirationDate.isNotEmpty() && !isExpirationValid

    val isFormValid =
        code.length >= 3 &&
        description.length >= 5 &&
        isExpirationValid &&
        selectedProducts.size == 3 &&
        discountValue != null &&
        discountValue in 0f..100f

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
        EditTextField(
            value = code,
            onValueChange = { code = it },
            label = "Codice coupon",
            keyboardType = KeyboardType.Text,
            isError = isCodeError,
            errorMessage = "Il codice deve avere almeno 3 caratteri"
        )

        EditTextField(
            value = discount,
            onValueChange = { discount = it },
            label = "Sconto %",
            keyboardType = KeyboardType.Number,
            isError = isDiscountError,
            errorMessage = "Inserisci un numero tra 0 e 100"
        )

        EditTextField(
            value = description,
            onValueChange = { description = it },
            label = "Descrizione (es. Kit Pranzo Veloce)",
            keyboardType = KeyboardType.Text,
            minLines = 2,
            singleLine = false,
            isError = isDescriptionError,
            errorMessage = "La descrizione deve avere almeno 5 caratteri"
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

        EditTextField(
            value = expirationDate,
            onValueChange = { expirationDate = it },
            label = "Scadenza (yyyy-MM-dd)",
            keyboardType = KeyboardType.Text,
            isError = isExpirationError,
            errorMessage = "Il coupon deve avere una scadenza successiva all'odierna.",
            readOnly = true,
            onClick = { showDatePicker = true },
            trailingIcon = {
                IconButton(onClick = { showDatePicker = true }) {
                    Icon(Icons.Default.DateRange, contentDescription = "Seleziona Data")
                }
            }
        )

        Text("Prodotti nel Coupon (Selezionati ${selectedProducts.size}/3)", fontWeight = FontWeight.Bold)

        selectedProducts.forEach { p ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF81C784)),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Row(Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    if (p.image != null) {
                        androidx.compose.foundation.Image(
                            painter = androidx.compose.ui.res.painterResource(id = p.image!!),
                            contentDescription = p.nome,
                            modifier = Modifier.size(32.dp).clip(RoundedCornerShape(8.dp)),
                            contentScale = androidx.compose.ui.layout.ContentScale.Crop
                        )
                    } else {
                        Icon(Icons.Default.ShoppingCart, null, tint = Color(0xFF388E3C), modifier = Modifier.size(32.dp))
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(p.nome, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF1B5E20))
                        Text("Prezzo originario: €${p.prezzo}", fontSize = 14.sp, color = Color(0xFF2E7D32))
                    }
                    IconButton(onClick = { selectedProducts.remove(p) }) {
                        Icon(Icons.Default.Close, "Rimuovi", tint = com.example.superspan.ui.theme.AppError)
                    }
                }
            }
        }

        OutlinedButton(
            onClick = {
                onMultiSelectProductRequest(selectedProducts.toList(), 3) { newSelection ->
                    selectedProducts.clear()
                    selectedProducts.addAll(newSelection)
                }
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = CircleShape,
            border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF81C784)),
            colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(containerColor = Color.White)
        ) {
            Icon(Icons.Default.ShoppingCart, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text(if (selectedProducts.isEmpty()) "Seleziona Prodotti" else "Modifica Selezione")
        }

            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = {
                val discountFloat = discount.toFloatOrNull() ?: return@Button
                onSave(
                    Coupon(
                        code.trim(),
                        discountFloat,
                        description.trim(),
                        expirationDate.trim(),
                        *selectedProducts.toTypedArray()
                    )
                )
            },
            enabled = isFormValid,
            modifier = Modifier.height(55.dp),
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(containerColor = com.example.superspan.ui.theme.LogoLeft)
        ) {
            Text("Salva coupon", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PromoForm(
    onSelectProductRequest: ((Product) -> Unit) -> Unit,
    onSave: (Coupon) -> Unit,
    onFormDirty: (Boolean) -> Unit
) {
    var code by rememberSaveable { mutableStateOf("") }
    var discount by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var expirationDate by rememberSaveable { mutableStateOf("") }
    var selectedProduct by remember { mutableStateOf<Product?>(null) }

    val isDirty = code.isNotEmpty() || discount.isNotEmpty() || description.isNotEmpty() || expirationDate.isNotEmpty() || selectedProduct != null
    LaunchedEffect(isDirty) {
        onFormDirty(isDirty)
    }

    val discountValue = discount.toFloatOrNull()
    
    val isCodeError = code.isNotEmpty() && code.length < 3
    val isDiscountError = discount.isNotEmpty() && (discountValue == null || discountValue !in 0f..100f)
    val isDescriptionError = description.isNotEmpty() && description.length < 5

    val isExpirationValid = try {
        val d = java.time.LocalDate.parse(expirationDate)
        !d.isBefore(java.time.LocalDate.now())
    } catch (e: Exception) { false }
    val isExpirationError = expirationDate.isNotEmpty() && !isExpirationValid

    val isFormValid =
        code.length >= 3 &&
        description.length >= 5 &&
        isExpirationValid &&
        selectedProduct != null &&
        discountValue != null &&
        discountValue in 0f..100f

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
        EditTextField(
            value = code,
            onValueChange = { code = it },
            label = "Codice offerta (interno)",
            keyboardType = KeyboardType.Text,
            isError = isCodeError,
            errorMessage = "Il codice deve avere almeno 3 caratteri"
        )

        EditTextField(
            value = discount,
            onValueChange = { discount = it },
            label = "Sconto %",
            keyboardType = KeyboardType.Number,
            isError = isDiscountError,
            errorMessage = "Inserisci un numero tra 0 e 100"
        )

        EditTextField(
            value = description,
            onValueChange = { description = it },
            label = "Descrizione",
            keyboardType = KeyboardType.Text,
            minLines = 2,
            singleLine = false,
            isError = isDescriptionError,
            errorMessage = "La descrizione deve avere almeno 5 caratteri"
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

        EditTextField(
            value = expirationDate,
            onValueChange = { expirationDate = it },
            label = "Scadenza (yyyy-MM-dd)",
            keyboardType = KeyboardType.Text,
            isError = isExpirationError,
            errorMessage = "Formato non valido, usa AAAA-MM-GG",
            readOnly = true,
            onClick = { showDatePicker = true },
            trailingIcon = {
                IconButton(onClick = { showDatePicker = true }) {
                    Icon(Icons.Default.DateRange, contentDescription = "Seleziona Data")
                }
            }
        )

        Text("Prodotto in Promozione", fontWeight = FontWeight.Bold)

        if (selectedProduct == null) {
            OutlinedButton(
                onClick = {
                    onSelectProductRequest { product ->
                        selectedProduct = product
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = CircleShape,
                border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF81C784)),
                colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(containerColor = Color.White)
            ) {
                Icon(Icons.Default.ShoppingCart, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Seleziona Prodotto")
            }
        } else {
            selectedProduct?.let { p ->
                Card(
                    modifier = Modifier.fillMaxWidth().clickable {
                        onSelectProductRequest { product ->
                            selectedProduct = product
                        }
                    },
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF81C784)),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Row(Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        if (p.image != null) {
                        androidx.compose.foundation.Image(
                            painter = androidx.compose.ui.res.painterResource(id = p.image!!),
                            contentDescription = p.nome,
                            modifier = Modifier.size(32.dp).clip(RoundedCornerShape(8.dp)),
                            contentScale = androidx.compose.ui.layout.ContentScale.Crop
                        )
                    } else {
                        Icon(Icons.Default.ShoppingCart, null, tint = Color(0xFF388E3C), modifier = Modifier.size(32.dp))
                    }
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(p.nome, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF1B5E20))
                            Text("Prezzo originario: €${p.prezzo}", fontSize = 14.sp, color = Color(0xFF2E7D32))
                        }
                        Text("Modifica", color = Color(0xFF388E3C), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }

            }
        }

        Spacer(modifier = Modifier.height(30.dp))

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
            modifier = Modifier.height(55.dp),
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(containerColor = com.example.superspan.ui.theme.LogoLeft)
        ) {
            Text("Salva offerta", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MultiProductSelectionScreen(
    initialSelection: List<Product>,
    maxSelection: Int,
    onBack: () -> Unit,
    onConfirm: (List<Product>) -> Unit
) {
    var query by remember { mutableStateOf("") }
    val currentSelection = remember { mutableStateListOf<Product>().apply { addAll(initialSelection) } }

    val filteredList = remember(query) {
        if (query.isBlank()) ListOfProduct
        else ListOfProduct.filter {
            it.nome.contains(query, ignoreCase = true)
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 220.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
                items(filteredList) { p ->
                    val isSelected = currentSelection.contains(p)
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                if (isSelected) {
                                    currentSelection.remove(p)
                                } else if (currentSelection.size < maxSelection) {
                                    currentSelection.add(p)
                                } else {
                                    // optional toast, doing nothing is fine
                                }
                            },
                        colors = CardDefaults.cardColors(containerColor = if (isSelected) Color(0xFFE8F5E9) else Color.White),
                        border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF388E3C)) else null,
                        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 4.dp else 2.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (p.image != null) {
                                androidx.compose.foundation.Image(
                                    painter = androidx.compose.ui.res.painterResource(id = p.image!!),
                                    contentDescription = p.nome,
                                    modifier = Modifier.size(48.dp).clip(RoundedCornerShape(12.dp)),
                                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .background(com.example.superspan.ui.theme.LogoLeft.copy(alpha = 0.1f), shape = RoundedCornerShape(12.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = com.example.superspan.ui.theme.LogoLeft)
                                }
                            }
                            Spacer(Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(p.nome, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF1A1A1A))
                                Spacer(Modifier.height(4.dp))
                                Text("€${p.prezzo} • ${p.categoria.nome}", fontSize = 14.sp, color = Color.Gray)
                            }
                        }
                    }
                }
            }

        // --- TRANSLUCENT HEADER ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .align(Alignment.TopCenter)
        ) {
            Spacer(modifier = Modifier.height(64.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Seleziona Prodotti",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF1A1A1A)
                )
                Text(
                    text = "Selezionati ${currentSelection.size} su $maxSelection",
                    fontSize = 16.sp,
                    color = Color.Gray
                )
            }
            TextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Cerca prodotto per nome...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = { query = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Cancella", tint = Color.Gray)
                        }
                    }
                },
                shape = RoundedCornerShape(24.dp),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                ),
                singleLine = true
            )
        }

        // Floating Back Button
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 16.dp, start = 16.dp)
                .background(Color.White.copy(alpha = 0.7f), CircleShape)
                .size(48.dp)
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Indietro", tint = com.example.superspan.ui.theme.LogoLeft)
        }

        // Floating Bottom Confirm Button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 100.dp, start = 16.dp, end = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Button(
                onClick = { onConfirm(currentSelection.toList()) },
                enabled = currentSelection.size == maxSelection,
                modifier = Modifier.wrapContentWidth().height(55.dp),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(containerColor = com.example.superspan.ui.theme.LogoLeft)
            ) {
                Text("Conferma Selezione", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductSelectionScreen(
    onBack: () -> Unit,
    onSelected: (Product) -> Unit
) {
    var query by remember { mutableStateOf("") }

    val filteredList = remember(query) {
        if (query.isBlank()) ListOfProduct
        else ListOfProduct.filter {
            it.nome.contains(query, ignoreCase = true)
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 220.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
                items(filteredList) { p ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelected(p) },
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (p.image != null) {
                                androidx.compose.foundation.Image(
                                    painter = androidx.compose.ui.res.painterResource(id = p.image!!),
                                    contentDescription = p.nome,
                                    modifier = Modifier.size(48.dp).clip(RoundedCornerShape(12.dp)),
                                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .background(com.example.superspan.ui.theme.LogoLeft.copy(alpha = 0.1f), shape = RoundedCornerShape(12.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = com.example.superspan.ui.theme.LogoLeft)
                                }
                            }
                            Spacer(Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(p.nome, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF1A1A1A))
                                Spacer(Modifier.height(4.dp))
                                Text("€${p.prezzo} • ${p.categoria.nome}", fontSize = 14.sp, color = Color.Gray)
                            }
                        }
                    }
                }
            }

        // --- TRANSLUCENT HEADER ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .align(Alignment.TopCenter)
        ) {
            Spacer(modifier = Modifier.height(64.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Seleziona Prodotto",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF1A1A1A)
                )
            }
            TextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Cerca prodotto per nome...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = { query = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Cancella", tint = Color.Gray)
                        }
                    }
                },
                shape = RoundedCornerShape(24.dp),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                ),
                singleLine = true
            )
        }

        // Floating Back Button
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 16.dp, start = 16.dp)
                .background(Color.White.copy(alpha = 0.7f), CircleShape)
                .size(48.dp)
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Indietro", tint = com.example.superspan.ui.theme.LogoLeft)
        }
    }
}