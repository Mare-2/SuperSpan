package com.example.superspan

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminCouponEditPage(
    navController: NavController?,
    paddingValues: PaddingValues,
    existingCoupon: Coupon? = null
) {
    var code by remember { mutableStateOf(existingCoupon?.code ?: "") }
    var discount by remember { mutableStateOf(existingCoupon?.discount?.toInt()?.toString() ?: "") }
    var description by remember { mutableStateOf(existingCoupon?.description ?: "") }
    var dateOfExpiration by remember { mutableStateOf(existingCoupon?.dateOfExpiration ?: "") }
    
    // Per semplicità in questa versione, usiamo una selezione multipla dei prodotti esistenti
    val selectedProducts = remember { 
        mutableStateListOf<Product>().apply { 
            existingCoupon?.products?.let { addAll(it) } 
        } 
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (existingCoupon == null) "Aggiungi Coupon" else "Modifica Coupon") },
                navigationIcon = {
                    IconButton(onClick = { navController?.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Indietro")
                    }
                },
                actions = {
                    if (existingCoupon != null) {
                        IconButton(onClick = {
                            ListOfCoupon.remove(existingCoupon)
                            navController?.popBackStack()
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Elimina", tint = Color.Red)
                        }
                    }
                }
            )
        },
        modifier = Modifier.padding(paddingValues)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = code,
                onValueChange = { code = it },
                label = { Text("Codice Coupon") },
                modifier = Modifier.fillMaxWidth(),
                enabled = existingCoupon == null // Non permettiamo di cambiare il codice se stiamo modificando
            )

            OutlinedTextField(
                value = discount,
                onValueChange = { discount = it },
                label = { Text("Sconto (%)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descrizione") },
                modifier = Modifier.fillMaxWidth()
            )

            var showDatePicker by remember { mutableStateOf(false) }
            val datePickerState = rememberDatePickerState()

            if (showDatePicker) {
                DatePickerDialog(
                    onDismissRequest = { showDatePicker = false },
                    confirmButton = {
                        TextButton(onClick = {
                            datePickerState.selectedDateMillis?.let { millis ->
                                val formatter = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                                formatter.timeZone = java.util.TimeZone.getTimeZone("UTC")
                                dateOfExpiration = formatter.format(java.util.Date(millis))
                            }
                            showDatePicker = false
                        }) {
                            Text("OK")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDatePicker = false }) {
                            Text("Annulla")
                        }
                    }
                ) {
                    DatePicker(state = datePickerState)
                }
            }

            OutlinedTextField(
                value = dateOfExpiration,
                onValueChange = { },
                label = { Text("Data Scadenza") },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(androidx.compose.material.icons.Icons.Default.DateRange, contentDescription = "Seleziona Data")
                    }
                }
            )

            Text("Seleziona Prodotti", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            
            // Lista dei prodotti con checkbox
            ListOfProduct.forEach { product ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = selectedProducts.contains(product),
                        onCheckedChange = { isChecked ->
                            if (isChecked) selectedProducts.add(product)
                            else selectedProducts.remove(product)
                        }
                    )
                    Text(product.nome)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val discountVal = discount.toFloatOrNull() ?: 0f
                    val newCoupon = Coupon(
                        _code = code,
                        _discount = discountVal,
                        _description = description,
                        _dateOfExpiration = dateOfExpiration,
                        *selectedProducts.toTypedArray()
                    )

                    if (existingCoupon != null) {
                        val index = ListOfCoupon.indexOf(existingCoupon)
                        if (index != -1) {
                            ListOfCoupon[index] = newCoupon
                        }
                    } else {
                        ListOfCoupon.add(newCoupon)
                    }
                    navController?.popBackStack()
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Save, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Salva Modifiche")
            }
        }
    }
}
