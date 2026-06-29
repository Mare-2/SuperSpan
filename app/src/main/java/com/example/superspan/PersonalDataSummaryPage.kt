package com.example.superspan

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun PersonalDataSummaryPage(navController: NavController?, padding: PaddingValues) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(80.dp))

            // TITOLI CENTRATI
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "I tuoi dati",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF1A1A1A)
                )
                Text(
                    text = "Velocizza le tue candidature",
                    fontSize = 16.sp,
                    color = Color.Gray
                )
            }

            // --- CONTENUTO ---
            Column(
                modifier = Modifier
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Sezione Informazioni
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        SummarySectionTitle("Informazioni Personali")
                        SummaryDataRow(Icons.Default.Person, "Nome", actualUser.nome)
                        SummaryDataRow(Icons.Default.Badge, "Cognome", actualUser.cognome)
                        SummaryDataRow(Icons.Default.Email, "Email (può essere diversa da quella del profilo)", actualUser.emailLavoro ?: "Non inserita")
                        SummaryDataRow(
                            icon = Icons.Default.Phone,
                            label = "Telefono",
                            value = if (!actualUser.telefono.isNullOrBlank()) {
                                formatPhone(actualUser.telefono!!)
                            } else {
                                "Non inserito"
                            }
                        )
                    }
                }

                // Sezione Documenti
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        SummarySectionTitle("Documenti")
                        SummaryDataRow(
                            icon = Icons.Default.Description,
                            label = "Curriculum Vitae (PDF)",
                            value = actualUser.cvFileName?.substringAfterLast('/') ?: "Nessun file caricato"
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
                .padding(top = 16.dp, start = 16.dp)
                .background(Color.White.copy(alpha = 0.7f), CircleShape)
                .size(48.dp)
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Indietro", tint = Color.Black)
        }

        // TASTO MODIFICA FAB (In basso a destra, align nel root Box)
        FloatingActionButton(
            onClick = { navController?.navigate(Destination.PERSONAL_DATA_EDIT.route) },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp),
            containerColor = com.example.superspan.ui.theme.LogoLeft,
            contentColor = Color.White,
            shape = CircleShape
        ) {
            Icon(Icons.Default.Edit, contentDescription = "Modifica Dati")
        }
    }
}

@Composable
fun SummarySectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        color = com.example.superspan.ui.theme.LogoLeft,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
fun SummaryDataRow(icon: ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(
            shape = CircleShape,
            color = com.example.superspan.ui.theme.LogoLeft.copy(alpha = 0.1f),
            modifier = Modifier.size(36.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = com.example.superspan.ui.theme.LogoLeft
                )
            }
        }
        Spacer(Modifier.width(16.dp))
        Column {
            Text(text = label, fontSize = 12.sp, color = Color.Gray)
            Text(
                text = value,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = if (value.contains("Non inserit")) Color.LightGray else Color.Black
            )
        }
    }
}