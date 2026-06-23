package com.example.superspan

import android.widget.Toast
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.AlertDialog
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

@Composable
fun AddCoupon(paddingValues: PaddingValues, navController: NavController?) {
    var selected by remember { mutableIntStateOf(0) }
    var isSelectionOpen by remember { mutableStateOf(false) }
    var pendingProductSelection by remember { mutableStateOf<((Product) -> Unit)?>(null) }
    var pendingCouponSave by remember { mutableStateOf<Coupon?>(null) }
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // --- HEADER ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .clip(BottomOvalShape(25.dp))
                    .background(Color.Gray)
            ) {
                IconButton(onClick = { navController?.popBackStack() }, modifier = Modifier.padding(8.dp)) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Indietro", tint = Color.White)
                }
                Column(Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Aggiungi Promo/Coupon", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    Text("I campi verdi sono completati", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                }
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
                    onSelectProductRequest = { callback ->
                        pendingProductSelection = callback
                        isSelectionOpen = true
                    },
                    onSave = { newCoupon ->
                        pendingCouponSave = newCoupon
                    }
                )
            } else {
                PromoForm(
                    onSelectProductRequest = { callback ->
                        pendingProductSelection = callback
                        isSelectionOpen = true
                    },
                    onSave = { newPromo ->
                        pendingCouponSave = newPromo
                    }
                )
            }
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

        if (pendingCouponSave != null) {
            val isCouponSave = pendingCouponSave!!.products.size == 3
            AlertDialog(
                onDismissRequest = { pendingCouponSave = null },
                title = { Text("Conferma Creazione") },
                text = { Text(if (isCouponSave) "Vuoi salvare il nuovo coupon?" else "Vuoi salvare la nuova offerta?") },
                confirmButton = {
                    androidx.compose.material3.TextButton(onClick = {
                        ListOfCoupon.add(pendingCouponSave!!)
                        pendingCouponSave = null
                        Toast.makeText(context, "Salvato con successo", Toast.LENGTH_SHORT).show()
                        navController?.popBackStack()
                    }) {
                        Text("Salva", color = Color(0xFF388E3C))
                    }
                },
                dismissButton = {
                    androidx.compose.material3.TextButton(onClick = { pendingCouponSave = null }) { Text("Annulla", color = Color.Gray) }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CouponForm(
    onSelectProductRequest: ((Product) -> Unit) -> Unit,
    onSave: (Coupon) -> Unit
) {
    var code by rememberSaveable { mutableStateOf("") }
    var discount by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var expirationDate by rememberSaveable { mutableStateOf("") }
    val selectedProducts = remember { mutableStateListOf<Product>() }

    val discountValue = discount.toFloatOrNull()
    val isFormValid =
        code.isNotBlank() &&
        description.isNotBlank() &&
        expirationDate.isNotBlank() &&
        selectedProducts.size == 3 &&
        discountValue != null &&
        discountValue in 0f..100f

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        CouponTextField(
            value = code,
            onValueChange = { code = it },
            label = "Codice coupon",
            keyboardType = if ("code" == "discount") KeyboardType.Number else KeyboardType.Text,
            minLines = if ("code" == "description") 2 else 1,
            singleLine = "code" != "description"
        )

        CouponTextField(
            value = discount,
            onValueChange = { discount = it },
            label = "Sconto %",
            keyboardType = if ("discount" == "discount") KeyboardType.Number else KeyboardType.Text,
            minLines = if ("discount" == "description") 2 else 1,
            singleLine = "discount" != "description"
        )

        CouponTextField(
            value = description,
            onValueChange = { description = it },
            label = "Descrizione (es. Kit Pranzo Veloce)",
            keyboardType = if ("description" == "discount") KeyboardType.Number else KeyboardType.Text,
            minLines = if ("description" == "description") 2 else 1,
            singleLine = "description" != "description"
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

        CouponTextField(
            value = expirationDate,
            onValueChange = { },
            label = "Scadenza",
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { showDatePicker = true }
        ) {
                    Icon(androidx.compose.material.icons.Icons.Default.DateRange, contentDescription = "Seleziona Data")
                }
            }
        )

        Text("Prodotti nel Coupon (Selezionati ${selectedProducts.size}/3)", fontWeight = FontWeight.Bold)

        selectedProducts.forEach { p ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Row(Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.ShoppingCart, null, tint = Color(0xFF388E3C), modifier = Modifier.size(32.dp))
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(p.nome, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF1B5E20))
                        Text("Prezzo originario: €${p.prezzo}", fontSize = 14.sp, color = Color(0xFF2E7D32))
                    }
                    IconButton(onClick = { selectedProducts.remove(p) }) {
                        Icon(Icons.Default.Close, "Rimuovi", tint = Color.Red)
                    }
                }
            }
        }

        if (selectedProducts.size < 3) {
            OutlinedButton(
                onClick = {
                    onSelectProductRequest { product ->
                        if (!selectedProducts.contains(product) && selectedProducts.size < 3) {
                            selectedProducts.add(product)
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.ShoppingCart, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Aggiungi Prodotto")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

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
            modifier = Modifier.height(55.dp).width(220.dp).align(Alignment.CenterHorizontally),
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF388E3C))
        ) {
            Text("Salva coupon")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PromoForm(
    onSelectProductRequest: ((Product) -> Unit) -> Unit,
    onSave: (Coupon) -> Unit
) {
    var code by rememberSaveable { mutableStateOf("") }
    var discount by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var expirationDate by rememberSaveable { mutableStateOf("") }
    var selectedProduct by remember { mutableStateOf<Product?>(null) }

    val discountValue = discount.toFloatOrNull()
    val isFormValid =
        code.isNotBlank() &&
        description.isNotBlank() &&
        expirationDate.isNotBlank() &&
        selectedProduct != null &&
        discountValue != null &&
        discountValue in 0f..100f

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        CouponTextField(
            value = code,
            onValueChange = { code = it },
            label = "Codice offerta (interno)",
            keyboardType = if ("code" == "discount") KeyboardType.Number else KeyboardType.Text,
            minLines = if ("code" == "description") 2 else 1,
            singleLine = "code" != "description"
        )

        CouponTextField(
            value = discount,
            onValueChange = { discount = it },
            label = "Sconto %",
            keyboardType = if ("discount" == "discount") KeyboardType.Number else KeyboardType.Text,
            minLines = if ("discount" == "description") 2 else 1,
            singleLine = "discount" != "description"
        )

        CouponTextField(
            value = description,
            onValueChange = { description = it },
            label = "Descrizione",
            keyboardType = if ("description" == "discount") KeyboardType.Number else KeyboardType.Text,
            minLines = if ("description" == "description") 2 else 1,
            singleLine = "description" != "description"
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

        CouponTextField(
            value = expirationDate,
            onValueChange = { },
            label = "Scadenza",
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { showDatePicker = true }
        ) {
                    Icon(androidx.compose.material.icons.Icons.Default.DateRange, contentDescription = "Seleziona Data")
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
                shape = RoundedCornerShape(12.dp)
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
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Row(Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.ShoppingCart, null, tint = Color(0xFF388E3C), modifier = Modifier.size(32.dp))
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

        Spacer(modifier = Modifier.height(16.dp))

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
            modifier = Modifier.height(55.dp).width(220.dp).align(Alignment.CenterHorizontally),
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF388E3C))
        ) {
            Text("Salva offerta")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CouponTextField(
    label: String,
    value: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    isError: Boolean = false,
    errorMessage: String = "",
    readOnly: Boolean = false,
    minLines: Int = 1,
    singleLine: Boolean = true,
    trailingIcon: @Composable (() -> Unit)? = null,
    onValueChange: (String) -> Unit
) {
    val containerColor = when {
        isError -> Color(0xFFFDECEA)
        value.isEmpty() -> Color(0xFFFFF3E0)
        else -> Color(0xFFE8F5E9)
    }

    val borderColor = when {
        isError -> Color.Red
        value.isEmpty() -> Color(0xFFFFB74D)
        else -> Color(0xFF81C784)
    }

    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            modifier = Modifier.fillMaxWidth().background(containerColor, RoundedCornerShape(20.dp)),
            shape = RoundedCornerShape(20.dp),
            singleLine = singleLine,
            minLines = minLines,
            readOnly = readOnly,
            isError = isError,
            trailingIcon = trailingIcon,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = borderColor,
                focusedBorderColor = borderColor,
                errorBorderColor = Color.Red
            )
        )
        if (isError) {
            Text(errorMessage, color = Color.Red, fontSize = 12.sp, modifier = Modifier.padding(start = 12.dp))
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Seleziona Prodotto", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Indietro")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF8F9FA)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
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

            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
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
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(Color(0xFFFFF3E0), shape = RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = Color(0xFFE65100))
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
        }
    }
}