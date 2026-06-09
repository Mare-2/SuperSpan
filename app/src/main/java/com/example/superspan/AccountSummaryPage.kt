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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun AccountSummaryPage(user: User, navController: NavController?, padding: PaddingValues) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .background(Color.White)
    ) {
        // --- HEADER ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // TASTO INDIETRO (A sinistra)
            IconButton(
                onClick = { navController?.popBackStack() },
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Indietro")
            }

            // TITOLI CENTRATI
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Il mio account",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = "Credenziali e accessi",
                    fontSize = 13.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium
                )
            }

            // TASTO MODIFICA (A destra)
            Surface(
                onClick = { navController?.navigate(Destination.ACCOUNT_EDIT.route) },
                shape = CircleShape,
                color = Color(0xFF388E3C), // Verde del brand
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .size(42.dp),
                shadowElevation = 4.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Modifica Account",
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
            SummarySectionTitle("Dati di base")

            SummaryDataRow(Icons.Default.Person, "Nome", user.nome)
            SummaryDataRow(Icons.Default.Badge, "Cognome", user.cognome)

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), thickness = 0.5.dp)

            SummarySectionTitle("Credenziali di accesso")
            
            SummaryDataRow(Icons.Default.Email, "Email di accesso", user.email)
            SummaryDataRow(Icons.Default.Lock, "Password", "••••••••") // Mostriamo sempre dei pallini per la password
        }
    }
}
