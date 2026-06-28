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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.WorkOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults


@Composable
fun WorkOfferPage(offer: WorkOffer?, navController: NavController?, paddingValues: PaddingValues) {
    val scrollState = rememberScrollState()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(com.example.superspan.ui.theme.AppBackgroundBrush) // Sfondo basato sul logo
            .padding(paddingValues)
    ) {
        // --- CONTENUTO ---
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 60.dp) // Spazio per la freccia in alto
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // Titolo e Luogo
            Text(
                text = offer?.titolo ?: "",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF1A1A1A)
            )
            Text(
                text = offer?.supermarket?.citta ?: "",
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
                InfoRowItem(Icons.Default.LocationOn, "Indirizzo:", offer?.supermarket?.indirizzo ?: "-")
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

        // --- TASTO INDIETRO E MODIFICA ---
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { navController?.popBackStack() },
                modifier = Modifier.size(48.dp).background(Color.White.copy(alpha = 0.7f), CircleShape)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.Black)
            }

            if (actualUser.admin && offer != null) {
                IconButton(
                    onClick = { navController?.navigate("${Destination.EDIT_WORK_OFFER.route}/${offer.id}") },
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = "Modifica", tint = Color.Black)
                }
            }
        }


        // --- TASTO CANDIDATI (Fisso con ombra) - NASCOSTO PER GLI ADMIN ---
        if (!actualUser.admin) {
            val hasCandidacy = AllCandidacies.any { it.userEmail == actualUser.email && it.offerId == offer?.id }
            val draft = offer?.let { getCandidacyDraftForOffer(actualUser, it.id) }
            
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(vertical = 20.dp),
                contentAlignment = Alignment.Center
            ) {
                if (hasCandidacy) {
                    Button(
                        onClick = { },
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 24.dp)
                            .height(56.dp),
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Gray,
                            disabledContainerColor = Color.Gray,
                            disabledContentColor = Color.White
                        ),
                        enabled = false,
                        contentPadding = PaddingValues(horizontal = 32.dp)
                    ) {
                        Text(
                            text = "Candidatura Inviata",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else if (draft != null) {
                    Button(
                        onClick = { 
                            currentOfferIdApplying = offer?.id ?: 0
                            currentDraft = draft.copy()
                            navController?.navigate(draft.lastStepRoute) 
                        },
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 24.dp)
                            .height(56.dp),
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(containerColor = androidx.compose.material3.MaterialTheme.colorScheme.secondary),
                        contentPadding = PaddingValues(horizontal = 32.dp)
                    ) {
                        Text(
                            text = "Continua la compilazione",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                } else {
                    Button(
                        onClick = { 
                            currentOfferIdApplying = offer?.id ?: 0
                            candidacySourceRoute = Destination.LAVORO.route
                            navController?.navigate(Destination.APPLY_STEP_1.route)
                        },
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