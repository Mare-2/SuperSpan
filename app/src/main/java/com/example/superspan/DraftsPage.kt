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
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.ExperimentalFoundationApi

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DraftsPage(navController: NavController?, padding: PaddingValues) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    
    // Lista di coppie (offerId -> DraftWork)
    val draftsList by remember { derivedStateOf { actualUser.candidacyDraftsByOfferId.entries.toList() } }
    
    // Lista di candidature inviate
    val submittedList by remember { derivedStateOf { AllCandidacies.filter { it.userEmail == actualUser.email } } }

    var showDeleteConfirmFor by remember { mutableStateOf<Int?>(null) }

    Box(Modifier.fillMaxSize().padding(padding)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 120.dp)
        ) {
            item {
                // Header
                Column {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        IconButton(
                            onClick = { navController?.popBackStack() },
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .background(Color.White.copy(alpha = 0.7f), CircleShape)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Indietro", tint = com.example.superspan.ui.theme.LogoLeft)
                        }
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Le tue Candidature",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF1A1A1A)
                        )
                        Text(
                            text = "Monitora e gestisci le tue opportunità",
                            fontSize = 16.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
            
            stickyHeader {
                // Tabs
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.Transparent
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 12.dp)
                            .shadow(4.dp, CircleShape)
                            .background(Color(0xFFEDF7E7), CircleShape)
                            .padding(4.dp)
                    ) {
                        TabButton("Bozze", selectedTabIndex == 0, Modifier.weight(1f)) { selectedTabIndex = 0 }
                        TabButton("Inviate", selectedTabIndex == 1, Modifier.weight(1f)) { selectedTabIndex = 1 }
                    }
                }
            }

            if (selectedTabIndex == 0) {
                // --- BOZZE ---
                if (draftsList.isEmpty()) {
                    item {
                        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                            Spacer(Modifier.height(40.dp))
                            Text("Nessuna bozza salvata.", color = Color.Gray)
                        }
                    }
                } else {
                    items(draftsList) { entry ->
                        val offerId = entry.key
                        val draft = entry.value
                        val offer = WorkOfferSearchList.find { it.id == offerId }

                        Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                shape = RoundedCornerShape(12.dp),
                                color = Color.White,
                                shadowElevation = 2.dp
                            ) {
                                Column(Modifier.padding(12.dp)) {
                                    Text(offer?.titolo ?: "Offerta #$offerId", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                    Text(offer?.supermarket?.citta ?: "-", color = Color.Gray, fontSize = 13.sp)
                                    Spacer(Modifier.height(8.dp))
                                    Text("Nome: ${draft.nome} ${draft.cognome}", fontSize = 14.sp)
                                    Text("Email: ${draft.emailLavoro}", fontSize = 14.sp)
                                    Text("Telefono: ${draft.telefono}", fontSize = 14.sp)

                                    Spacer(Modifier.height(12.dp))
                                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                                        TextButton(onClick = {
                                            currentDraft = draft.copy()
                                            currentOfferIdApplying = offerId
                                            candidacySourceRoute = Destination.DRAFTS.route
                                            navController?.navigate(draft.lastStepRoute)
                                        }) {
                                            Icon(Icons.Default.Edit, null)
                                            Spacer(Modifier.width(6.dp))
                                            Text("Continua")
                                        }
                                        Spacer(Modifier.width(8.dp))
                                        OutlinedButton(onClick = { showDeleteConfirmFor = offerId }, colors = ButtonDefaults.outlinedButtonColors(contentColor = com.example.superspan.ui.theme.AppError)) {
                                            Icon(Icons.Default.Delete, null, tint = com.example.superspan.ui.theme.AppError)
                                            Spacer(Modifier.width(6.dp))
                                            Text("Elimina", color = com.example.superspan.ui.theme.AppError)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // --- INVIATE ---
                if (submittedList.isEmpty()) {
                    item {
                        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                            Spacer(Modifier.height(40.dp))
                            Text("Nessuna candidatura inviata.", color = Color.Gray)
                        }
                    }
                } else {
                    items(submittedList) { candidacy ->
                        Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                            SubmittedCandidacyCard(candidacy)
                        }
                        Spacer(Modifier.height(12.dp))
                    }
                }
            }
        }

        if (showDeleteConfirmFor != null) {
            val idToDelete = showDeleteConfirmFor!!
            ModernAlertDialog(
                onDismissRequest = { showDeleteConfirmFor = null },
                title = "Elimina bozza",
                text = "Sei sicuro di eliminare la bozza per l'offerta #$idToDelete ?",
                icon = Icons.Default.Delete,
                isDestructive = true,
                confirmText = "Elimina",
                onConfirm = {
                    clearCandidacyDraftForOffer(actualUser, idToDelete)
                    showDeleteConfirmFor = null
                },
                dismissText = "Annulla",
                onDismiss = { showDeleteConfirmFor = null }
            )
        }
    }
}

@Composable
fun SubmittedCandidacyCard(candidacy: Candidacy) {
    val offer = WorkOfferSearchList.find { it.id == candidacy.offerId }
    
    // Determinazione del badge di stato
    val (statusText, statusColor, bgColor) = when (candidacy.stato) {
        "Inviata" -> Triple("Non ancora visualizzata", Color(0xFFE65100), Color(0xFFFFF3E0)) // Orange
        "Scartata" -> Triple("Rifiutata", Color(0xFFD32F2F), Color(0xFFFFEBEE)) // Red
        "Inoltrata a HR" -> Triple("In valutazione", Color(0xFF2E7D32), Color(0xFFE8F5E9)) // Green
        "Inoltrata al Responsabile" -> Triple("In valutazione", Color(0xFF2E7D32), Color(0xFFE8F5E9)) // Green
        else -> Triple(candidacy.stato, Color.Gray, Color(0xFFEEEEEE))
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = offer?.titolo ?: "Posizione non disponibile",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color(0xFF1565C0)
                    )
                    Spacer(Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, null, tint = Color.Gray, modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(offer?.supermarket?.nome ?: "-", fontSize = 13.sp, color = Color.Gray)
                    }
                    Spacer(Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.DateRange, null, tint = Color.Gray, modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Inviata il: ${candidacy.dataInvio}", fontSize = 13.sp, color = Color.Gray)
                    }
                }
            }
            
            Spacer(Modifier.height(16.dp))
            Divider(color = Color(0xFFEEEEEE))
            Spacer(Modifier.height(12.dp))
            
            // Badge di stato
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(bgColor)
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = statusText,
                    color = statusColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
