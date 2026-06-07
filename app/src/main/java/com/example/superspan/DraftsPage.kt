package com.example.superspan

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun DraftsPage(navController: NavController?, padding: PaddingValues) {
    val scrollState = rememberScrollState()

    // Lista di coppie (offerId -> DraftWork)
    val draftsList by remember { derivedStateOf { actualUser.draftWorksByOfferId.entries.toList() } }

    var showDeleteConfirmFor by remember { mutableStateOf<Int?>(null) }

    Box(Modifier.fillMaxSize().background(Color.White).padding(padding)) {
        Column(Modifier.fillMaxSize()) {
            // Header
            Row(Modifier.fillMaxWidth().padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { navController?.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Indietro")
                }
                Spacer(Modifier.width(8.dp))
                Text("Bozze candidature", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
            }

            // Parabola / area contenuto
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(top = 8.dp)
                    .background(Color(0xFFF5F5F5), TopOvalShape(20.dp))
                    .verticalScroll(scrollState)
                    .padding(16.dp)
            ) {
                if (draftsList.isEmpty()) {
                    Spacer(Modifier.height(40.dp))
                    Text("Nessuna bozza salvata.", color = Color.Gray, modifier = Modifier.align(Alignment.CenterHorizontally))
                } else {
                    draftsList.forEach { entry ->
                        val offerId = entry.key
                        val draft = entry.value
                        val offer = WorkOfferSearchList.find { it.id == offerId }

                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            shape = RoundedCornerShape(12.dp),
                            color = Color.White,
                            shadowElevation = 2.dp
                        ) {
                            Column(Modifier.padding(12.dp)) {
                                Text(offer?.ruolo ?: "Offerta #$offerId", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text(offer?.citta ?: "-", color = Color.Gray, fontSize = 13.sp)
                                Spacer(Modifier.height(8.dp))
                                Text("Nome: ${draft.nome} ${draft.cognome}", fontSize = 14.sp)
                                Text("Email: ${draft.email}", fontSize = 14.sp)
                                Text("Telefono: ${draft.telefono}", fontSize = 14.sp)

                                Spacer(Modifier.height(12.dp))
                                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                                    TextButton(onClick = {
                                        // Continua: popola currentDraft e vai allo step 1
                                        currentDraft = currentDraft.copy(
                                            nome = draft.nome,
                                            cognome = draft.cognome,
                                            emailLavoro = draft.email,
                                            telefono = draft.telefono,
                                            cvFileName = draft.cvFileName ?: ""
                                        )
                                        currentOfferIdApplying = offerId
                                        navController?.navigate(Destination.APPLY_STEP_1.route)
                                    }) {
                                        Icon(Icons.Default.Edit, null)
                                        Spacer(Modifier.width(6.dp))
                                        Text("Continua")
                                    }
                                    Spacer(Modifier.width(8.dp))
                                    OutlinedButton(onClick = { showDeleteConfirmFor = offerId }, colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red)) {
                                        Icon(Icons.Default.Delete, null, tint = Color.Red)
                                        Spacer(Modifier.width(6.dp))
                                        Text("Elimina", color = Color.Red)
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(120.dp))
            }
        }

        if (showDeleteConfirmFor != null) {
            val idToDelete = showDeleteConfirmFor!!
            AlertDialog(
                onDismissRequest = { showDeleteConfirmFor = null },
                title = { Text("Elimina bozza") },
                text = { Text("Sei sicuro di eliminare la bozza per l'offerta #$idToDelete ?") },
                confirmButton = {
                    TextButton(onClick = {
                        clearDraftWorkForOffer(actualUser, idToDelete)
                        showDeleteConfirmFor = null
                    }) { Text("Elimina", color = Color.Red) }
                },
                dismissButton = { TextButton(onClick = { showDeleteConfirmFor = null }) { Text("Annulla") } }
            )
        }
    }
}

