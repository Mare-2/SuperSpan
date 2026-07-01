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
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults


@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
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
            // --- HERO: etichetta + titolo + città (allineati a sinistra, look "da annuncio") ---
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                Spacer(Modifier.height(72.dp)) // spazio per i pulsanti flottanti (indietro / modifica)

                Text(
                    text = "OFFERTA DI LAVORO",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.5.sp,
                    color = com.example.superspan.ui.theme.LogoLeft
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = offer?.titolo ?: "",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF1A1A1A),
                    lineHeight = 34.sp
                )
                Spacer(Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, null, tint = com.example.superspan.ui.theme.LogoLeft, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = offer?.supermarket?.citta ?: "-",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.DarkGray
                    )
                }

                Spacer(Modifier.height(16.dp))

                // Riepilogo rapido a "pillole"
                androidx.compose.foundation.layout.FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    offer?.let {
                        InfoChip(Icons.Default.WorkOutline, it.ruoloEnum.nome)
                        InfoChip(Icons.Default.WorkOutline, it.tipoContratto.nome)
                        InfoChip(Icons.Default.Schedule, it.orario.nome)
                        InfoChip(Icons.Default.LocationOn, "${it.distanzaKm} km")
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // --- CONTENUTO (card informative) ---
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                androidx.compose.material3.Card(
                    colors = androidx.compose.material3.CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    elevation = androidx.compose.material3.CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("Informazioni Generali", Modifier.fillMaxWidth().padding(bottom = 16.dp), fontWeight = FontWeight.Bold, color = com.example.superspan.ui.theme.LogoLeft, fontSize = 17.sp)
                        InfoRowItem(Icons.Default.Storefront, "Sede:", offer?.supermarket?.nome ?: "-")
                        Spacer(Modifier.height(8.dp))
                        InfoRowItem(Icons.Default.LocationCity, "Città:", offer?.supermarket?.citta ?: "-")
                        Spacer(Modifier.height(8.dp))
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

        // Floating Edit Button (solo admin)
        if (actualUser.admin && offer != null) {
            IconButton(
                onClick = { navController?.navigate("${Destination.EDIT_WORK_OFFER.route}/${offer.id}") },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 16.dp + paddingValues.calculateTopPadding(), end = 16.dp)
                    .background(Color.White.copy(alpha = 0.7f), CircleShape)
                    .size(48.dp)
            ) {
                Icon(Icons.Default.Edit, "Modifica", tint = com.example.superspan.ui.theme.LogoLeft)
            }
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