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
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .background(com.example.superspan.ui.theme.AppBackgroundBrush)
    ) {
        // --- NUOVO HEADER ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // 1. TASTO INDIETRO (A sinistra)
            IconButton(
                onClick = { navController?.popBackStack() },
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Indietro")
            }

            // 2. TITOLI CENTRATI
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "I tuoi dati",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = "Velocizza le tue candidature",
                    fontSize = 13.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium
                )
            }

            // 3. TASTO MODIFICA EVIDENTE (A destra)
            // Usiamo una Surface circolare colorata per farlo risaltare
            Surface(
                onClick = { navController?.navigate(Destination.PERSONAL_DATA_EDIT.route) },
                shape = CircleShape,
                color = Color(0xFF388E3C), // Il verde del tuo brand
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .size(42.dp),
                shadowElevation = 4.dp // Aggiunge un po' di profondità
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Modifica",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // --- CONTENUTO ---
        Column(
            modifier = Modifier
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Sezione Informazioni
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

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), thickness = 0.5.dp)

            // Sezione Documenti
            SummarySectionTitle("Documenti")
            if (actualUser.cvFileName == null) {
                Text(
                    "Carica qui il tuo CV!",
                    fontSize = 13.sp,
                    color = Color.Gray,
                    //modifier = Modifier.padding(start = 40.dp)
                )
            }
            SummaryDataRow(
                icon = Icons.Default.Description,
                label = "Curriculum Vitae (PDF)",
                value = actualUser.cvFileName ?: "Nessun file caricato"
            )


        }
    }
}

@Composable
fun SummarySectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF388E3C),
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
fun SummaryDataRow(icon: ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = Color.Gray
        )
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