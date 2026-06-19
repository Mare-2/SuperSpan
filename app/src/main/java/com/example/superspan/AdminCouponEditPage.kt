package com.example.superspan

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextFieldDefaults
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
    val context = LocalContext.current
    var isSelectionOpen by remember { mutableStateOf(false) }
    var pendingProductSelection by remember { mutableStateOf<((Product) -> Unit)?>(null) }

    if (isSelectionOpen) {
        ProductSelectionScreen(
            onBack = { isSelectionOpen = false },
            onSelected = { product ->
                pendingProductSelection?.invoke(product)
                isSelectionOpen = false
            }
        )
    } else {
        var code by remember { mutableStateOf(existingCoupon?.code ?: "") }
        var discount by remember { mutableStateOf(existingCoupon?.discount?.toInt()?.toString() ?: "") }
        var description by remember { mutableStateOf(existingCoupon?.description ?: "") }
        var dateOfExpiration by remember { mutableStateOf(existingCoupon?.dateOfExpiration ?: "") }
        
        val selectedProducts = remember { 
            mutableStateListOf<Product>().apply { 
                existingCoupon?.products?.let { addAll(it) } 
            } 
        }

        val isCoupon = existingCoupon?.products?.size == 3
        val requiredProductsCount = if (isCoupon) 3 else 1
        val discountVal = discount.toFloatOrNull()
        
        val isFormValid =
            code.isNotBlank() &&
            description.isNotBlank() &&
            dateOfExpiration.isNotBlank() &&
            selectedProducts.size == requiredProductsCount &&
            discountVal != null &&
            discountVal in 0f..100f

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
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
                if (existingCoupon != null) {
                    IconButton(
                        onClick = {
                            ListOfCoupon.remove(existingCoupon)
                            Toast.makeText(context, "Eliminato con successo", Toast.LENGTH_SHORT).show()
                            navController?.popBackStack()
                        },
                        modifier = Modifier.align(Alignment.TopEnd).padding(8.dp)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Elimina", tint = Color(0xFFEF5350))
                    }
                }
                Column(Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(if (isCoupon) "Modifica Coupon" else "Modifica Offerta", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    Text("I campi verdi sono completati", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 25.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(20.dp))
                CouponTextField(
                    value = code,
                    onValueChange = { code = it },
                    label = if (isCoupon) "Codice Coupon" else "Codice Offerta",
                    keyboardType = if ("code" == "discount") KeyboardType.Number else KeyboardType.Text,
                    readOnly = existingCoupon != null && "code" == "code",
                    minLines = if ("code" == "description") 2 else 1,
                    singleLine = "code" != "description"
                )

                CouponTextField(
                    value = discount,
                    onValueChange = { discount = it },
                    label = "Sconto (%)",
                    keyboardType = if ("discount" == "discount") KeyboardType.Number else KeyboardType.Text,
                    readOnly = existingCoupon != null && "discount" == "code",
                    minLines = if ("discount" == "description") 2 else 1,
                    singleLine = "discount" != "description"
                )

                CouponTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = "Descrizione",
                    keyboardType = if ("description" == "discount") KeyboardType.Number else KeyboardType.Text,
                    readOnly = existingCoupon != null && "description" == "code",
                    minLines = if ("description" == "description") 2 else 1,
                    singleLine = "description" != "description"
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

                CouponTextField(
                    value = dateOfExpiration,
                    onValueChange = { },
                    label = "Data Scadenza",
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { showDatePicker = true }
                ) {
                            Icon(Icons.Default.DateRange, contentDescription = "Seleziona Data")
                        }
                    }
                )

                Text("Prodotti (${selectedProducts.size}/$requiredProductsCount)", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                
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

                if (selectedProducts.size < requiredProductsCount) {
                    OutlinedButton(
                        onClick = {
                            pendingProductSelection = { product ->
                                if (!selectedProducts.contains(product) && selectedProducts.size < requiredProductsCount) {
                                    selectedProducts.add(product)
                                }
                            }
                            isSelectionOpen = true
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text(if (isCoupon) "Aggiungi Prodotto" else "Seleziona Prodotto")
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        val finalDiscount = discountVal ?: 0f
                        val newCoupon = Coupon(
                            _code = code,
                            _discount = finalDiscount,
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
                        Toast.makeText(context, "Modifica salvata con successo", Toast.LENGTH_SHORT).show()
                        navController?.popBackStack()
                    },
                    enabled = isFormValid,
                    modifier = Modifier.height(55.dp).width(220.dp),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF388E3C))
                ) {
                    Text("Salva Modifiche", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
