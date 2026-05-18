package com.example.superspan

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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.ExperimentalComposeUiApi

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

        OutlinedTextField(
            value = expirationDate,
            onValueChange = { expirationDate = it },
            label = { Text("Scadenza (yyyy-MM-dd)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
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
