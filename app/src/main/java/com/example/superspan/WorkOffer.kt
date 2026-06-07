package com.example.superspan

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.WorkOutline
import androidx.compose.material3.AlertDialog


@Composable
fun WorkOfferPage(offer: WorkOffer?, navController: NavController?, paddingValues: PaddingValues) {
    val scrollState = rememberScrollState()
    var showSaveDialog by remember { mutableStateOf(false) }
    
    // Stato per la bozza candidatura
    var draftWorkName by remember { mutableStateOf("") }
    var draftWorkCognome by remember { mutableStateOf("") }
    var draftWorkEmail by remember { mutableStateOf("") }
    var draftWorkTelefono by remember { mutableStateOf("") }
    var draftWorkCvFileName by remember { mutableStateOf<String?>(null) }

    // Carica la bozza precedente se esiste
    LaunchedEffect(offer?.id) {
        if (offer != null) {
            val savedDraft = getDraftWorkForOffer(actualUser, offer.id)
            if (savedDraft != null) {
                draftWorkName = savedDraft.nome
                draftWorkCognome = savedDraft.cognome
                draftWorkEmail = savedDraft.email
                draftWorkTelefono = savedDraft.telefono
                draftWorkCvFileName = savedDraft.cvFileName
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5)) // Un grigio chiarissimo di sfondo
            .padding(paddingValues)
    ) {
        // --- CONTENUTO ---
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 60.dp) // Spazio per la freccia in alto
                // Usiamo angoli arrotondati invece della parabola
                .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                .background(Color.White) // Sfondo bianco per massima leggibilità
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // Titolo e Luogo
            Text(
                text = offer?.ruolo ?: "",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF1A1A1A)
            )
            Text(
                text = offer?.citta ?: "",
                fontSize = 18.sp,
                color = Color.Gray,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Box Grigio con info rapide (Indirizzo, Contratto, ecc.)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF9F9F9), RoundedCornerShape(16.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                InfoRowItem(Icons.Default.LocationOn, "Indirizzo:", offer?.indirizzo ?: "-")
                InfoRowItem(Icons.Default.Schedule, "Orario:", offer?.orario?.nome ?: "-")
                InfoRowItem(Icons.Default.WorkOutline, "Contratto:", offer?.tipoContratto?.nome ?: "-")
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Descrizione
            Text("Descrizione incarico", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF1A1A1A))
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = offer?.descrizioneEstesa ?: "",
                fontSize = 16.sp,
                color = Color(0xFF444444),
                lineHeight = 24.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Requisiti
            Text("Requisiti", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF1A1A1A))
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = offer?.requisiti ?: "",
                fontSize = 16.sp,
                color = Color(0xFF444444),
                lineHeight = 24.sp
            )

            Spacer(modifier = Modifier.height(120.dp)) // Spazio per il tasto
        }

        // --- TASTO INDIETRO ---
        IconButton(
            onClick = { showSaveDialog = true },
            modifier = Modifier.padding(12.dp).size(48.dp)
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.Black)
        }

        // --- DIALOGO SALVA BOZZA ---
        if (showSaveDialog) {
            AlertDialog(
                onDismissRequest = { showSaveDialog = false },
                title = { Text("Salva candidatura?", fontWeight = FontWeight.Bold) },
                text = { Text("Vuoi salvare questa candidatura come bozza prima di uscire?") },
                confirmButton = {
                    Button(
                        onClick = {
                            // Salva la bozza
                            val draft = DraftWork(
                                nome = draftWorkName,
                                cognome = draftWorkCognome,
                                email = draftWorkEmail,
                                telefono = draftWorkTelefono,
                                cvFileName = draftWorkCvFileName
                            )
                            saveDraftWorkForOffer(actualUser, offer?.id ?: 0, draft)
                            showSaveDialog = false
                            navController?.popBackStack()
                        }
                    ) {
                        Text("Salva")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = {
                            // Non salva, semplicemente torna indietro
                            showSaveDialog = false
                            navController?.popBackStack()
                        }
                    ) {
                        Text("Scarta")
                    }
                }
            )
        }

        // --- TASTO CANDIDATI (Fisso con ombra) ---
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Color.White.copy(alpha = 0.9f)) // Effetto sfumato dietro il tasto
                .padding(vertical = 20.dp),
            contentAlignment = Alignment.Center
        ) {
            Button(
                onClick = { currentOfferIdApplying = offer?.id ?: 0 // Salviamo l'ID
                    navController?.navigate(Destination.APPLY_STEP_1.route)},
                modifier = Modifier
                    .align(Alignment.BottomCenter) // Resta centrato in basso
                    .padding(bottom = 24.dp)      // Distanza fissa dalla navbar
                    .height(56.dp),               // Altezza fissa per un buon touch-target
                shape = CircleShape,               // Lo rende perfettamente stondato (pillola)
                contentPadding = PaddingValues(horizontal = 32.dp) // Aggiunge spazio a destra e sinistra del testo
            ) {
                Text(
                    text = "Invia Candidatura",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
@Composable
fun InfoRowItem(icon: ImageVector, label: String, value: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically, // Allinea icona e testo al centro
        modifier = Modifier.padding(vertical = 2.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(18.dp), // Dimensione piccola per l'icona
            tint = Color.Gray // Colore dell'icona
        )

        Spacer(Modifier.width(8.dp)) // Spazio tra icona e testo

        Text(
            text = "$label ",
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = Color.Black
        )

        Text(
            text = value,
            fontSize = 15.sp,
            color = Color.DarkGray
        )
    }
}