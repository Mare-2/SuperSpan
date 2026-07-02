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
    AuraBackground(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
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
                    text = "Il mio account",
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Credenziali e accessi",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Gray
                )
            }

            // --- CONTENUTO ---
            Column(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .fillMaxWidth()
                    .widthIn(max = 520.dp) // Su tablet: più stretto e centrato
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        SummarySectionTitle("Dati di base")
                        SummaryDataRow(Icons.Default.Person, "Nome", user.nome)
                        SummaryDataRow(Icons.Default.Badge, "Cognome", user.cognome)
                    }
                }

                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        SummarySectionTitle("Credenziali di accesso")
                        SummaryDataRow(Icons.Default.Email, "Email di accesso", user.email)
                        SummaryDataRow(Icons.Default.Lock, "Password", "••••••••") // Mostriamo sempre dei pallini per la password
                    }
                }
                
            }
        }

        // Floating Back Button
        IconButton(
            onClick = { navController?.popBackStack() },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = padding.calculateTopPadding() + 16.dp, start = 16.dp)
                .background(Color.White.copy(alpha = 0.7f), CircleShape)
                .size(48.dp)
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Indietro", tint = com.example.superspan.ui.theme.LogoLeft)
        }

        // TASTO MODIFICA FAB (In basso a destra)
        FloatingActionButton(
            onClick = { navController?.navigate(Destination.ACCOUNT_EDIT.route) },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = padding.calculateBottomPadding() + 24.dp, end = 24.dp),
            containerColor = com.example.superspan.ui.theme.LogoLeft,
            contentColor = Color.White,
            shape = CircleShape
        ) {
            Icon(Icons.Default.Edit, contentDescription = "Modifica Dati")
        }
    }
}
