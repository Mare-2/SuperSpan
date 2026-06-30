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

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
        ) {
            // --- HEADER (Senza rettangolo bianco, testo scuro) ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 16.dp)
            ) {
                Column(Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Dettaglio Offerta", color = Color.Black, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    Text(offer?.supermarket?.citta ?: "", color = Color.DarkGray, fontSize = 12.sp)
                }

                if (actualUser.admin && offer != null) {
                    IconButton(
                        onClick = { navController?.navigate("${Destination.EDIT_WORK_OFFER.route}/${offer.id}") },
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = 8.dp)
                            .background(Color.White.copy(alpha = 0.7f), CircleShape)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Modifica", tint = Color.Black)
                    }
                }
            }

            // Tutto il contenuto centrato
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = offer?.titolo ?: "",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.Black,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(24.dp))

                androidx.compose.material3.Card(
                    colors = androidx.compose.material3.CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    elevation = androidx.compose.material3.CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("Informazioni Generali", Modifier.fillMaxWidth().padding(bottom = 16.dp), fontWeight = FontWeight.Bold, color = com.example.superspan.ui.theme.LogoLeft, fontSize = 17.sp)
                        InfoRowItem(Icons.Default.LocationOn, "Indirizzo:", offer?.supermarket?.indirizzo ?: "-")
                        Spacer(Modifier.height(8.dp))
                        InfoRowItem(Icons.Default.Schedule, "Orario:", offer?.orario?.nome ?: "-")
                        Spacer(Modifier.height(8.dp))
                        InfoRowItem(Icons.Default.WorkOutline, "Contratto:", offer?.tipoContratto?.nome ?: "-")
                    }
                }

                Spacer(Modifier.height(24.dp))

                androidx.compose.material3.Card(
                    colors = androidx.compose.material3.CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    elevation = androidx.compose.material3.CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("Descrizione incarico", Modifier.fillMaxWidth().padding(bottom = 16.dp), fontWeight = FontWeight.Bold, color = com.example.superspan.ui.theme.LogoLeft, fontSize = 17.sp)
                        Text(
                            text = offer?.descrizioneEstesa ?: "",
                            fontSize = 15.sp,
                            color = Color.DarkGray,
                            lineHeight = 22.sp
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))

                androidx.compose.material3.Card(
                    colors = androidx.compose.material3.CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    elevation = androidx.compose.material3.CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("Requisiti", Modifier.fillMaxWidth().padding(bottom = 16.dp), fontWeight = FontWeight.Bold, color = com.example.superspan.ui.theme.LogoLeft, fontSize = 17.sp)
                        Text(
                            text = offer?.requisiti ?: "",
                            fontSize = 15.sp,
                            color = Color.DarkGray,
                            lineHeight = 22.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(100.dp))
            }
        }



        // --- TASTO CANDIDATI (Fisso in basso) ---
        if (!actualUser.admin) {
            val hasCandidacy = AllCandidacies.any { it.userEmail == actualUser.email && it.offerId == offer?.id }
            val draft = offer?.let { getCandidacyDraftForOffer(actualUser, it.id) }
            
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(start = 24.dp, end = 24.dp, bottom = 24.dp + paddingValues.calculateBottomPadding()),
                contentAlignment = Alignment.Center
            ) {
                if (hasCandidacy) {
                    Button(
                        onClick = { },
                        modifier = Modifier.height(55.dp),
                        shape = CircleShape,
                        contentPadding = PaddingValues(horizontal = 32.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Gray,
                            disabledContainerColor = Color.Gray,
                            disabledContentColor = Color.White
                        ),
                        enabled = false
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
                        modifier = Modifier.height(55.dp),
                        shape = CircleShape,
                        contentPadding = PaddingValues(horizontal = 32.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = com.example.superspan.ui.theme.LogoCenter)
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
                            // Partiamo sempre da una bozza pulita per evitare che dati o video
                            // di una candidatura precedente (scartata) vengano trascinati qui.
                            currentDraft = CandidacyDraft()
                            navController?.navigate(Destination.APPLY_STEP_1.route)
                        },
                        modifier = Modifier.height(55.dp),
                        shape = CircleShape,
                        contentPadding = PaddingValues(horizontal = 32.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = com.example.superspan.ui.theme.LogoLeft)
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

        // Floating Back Button
        IconButton(
            onClick = { navController?.popBackStack() },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 16.dp + paddingValues.calculateTopPadding(), start = 16.dp)
                .background(Color.White.copy(alpha = 0.7f), CircleShape)
                .size(48.dp)
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Indietro", tint = com.example.superspan.ui.theme.LogoLeft)
        }
    } // Chiude Box root
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