package com.example.superspan


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController



@Composable
fun ProfilePage(user: User, navController: NavController?, paddingValues: PaddingValues) {
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(paddingValues)
    ) {
        // --- 1. HEADER (Logo e Tasto Indietro) ---
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(modifier = Modifier.fillMaxWidth().padding(10.dp)) {
                IconButton(onClick = { navController?.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Indietro")
                }
            }
            Text(
                text = "LOGO",
                fontSize = 40.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.DarkGray
            )
        }

        // --- 2. LA PARABOLA GRIGIA (Contenuto) ---
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 160.dp) // Dove inizia la curva
                .background(Color.LightGray, TopOvalShape(40.dp)) // Tornata la parabola!
                .verticalScroll(scrollState)
        ) {
            // Spazio per l'immagine del profilo che sporge
            Spacer(modifier = Modifier.height(75.dp))

            // Nome e Ruolo centrati
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("${user.nome} ${user.cognome}", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Text(
                    text = if (user.admin) "Amministratore" else "",
                    color = Color.DarkGray.copy(alpha = 0.7f),
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- SEZIONI CONTENUTO (TILES) ---
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Sezione Comune
                ProfileMenuTile(
                    icon = Icons.Default.AccountCircle,
                    title = "Il mio account",
                    subtitle = "Username, email e password",
                    onClick = { /* TODO */ }
                )

                if (user.admin) { //TODO: Decisamente da rivedere quelle admin
                    // SEZIONE ADMIN (Titolo a sinistra)
                    ProfileSectionTitle("Gestione Negozio")
                    ProfileMenuTile(Icons.Default.Inventory, "Gestione Prodotti", "Aggiungi o modifica prodotti") { }
                    ProfileMenuTile(Icons.Default.Badge, "Revisione Candidature", "Vedi i CV ricevuti") { }
                    ProfileMenuTile(Icons.Default.Assessment, "Statistiche Vendite", "Andamento coupon") { }
                } else {
                    // SEZIONE UTENTE (Titolo a sinistra)
                    ProfileSectionTitle("Candidature")
                    ProfileMenuTile(
                        icon = Icons.Default.Description,
                        title = "Dati personali",
                        subtitle = "Contatti e Curriculum Vitae",
                        onClick = { navController?.navigate(Destination.PERSONAL_DATA_SUMMARY.route) } // Assicurati che questa rotta esista
                    )
                    ProfileMenuTile(Icons.Default.AssignmentTurnedIn, "Candidature in corso", "Stato delle tue domande") { }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // --- 3. TASTO ESCI (Stile pillola centrato, larghezza testo) ---
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    Button(
                        onClick = { /* Logout */ },
                        modifier = Modifier.height(56.dp),
                        shape = CircleShape,
                        contentPadding = PaddingValues(horizontal = 32.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE57373))
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Logout, null, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Esci", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(100.dp))
            }
        }

        // --- 4. IMMAGINE PROFILO (Sovrapposta a metà della parabola) ---
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 110.dp) // Posizionata a cavallo della curva
                .size(110.dp)
                .clip(CircleShape)
                .background(Color.White)
                .padding(4.dp)
                .clip(CircleShape)
                .background(Color(0xFFE0E0E0)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                modifier = Modifier.size(70.dp),
                tint = Color.Gray
            )
        }
    }
}

// Funzione Titolo Sezione (Grande e a SINISTRA)
@Composable
fun ProfileSectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF388E3C), // Il verde dell'app TODO: cambiare
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp, top = 12.dp, bottom = 4.dp),
        textAlign = TextAlign.Start // Allineato a sinistra
    )
}

// Componente Tassello Menu
@Composable
fun ProfileMenuTile(icon: ImageVector, title: String, subtitle: String, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(40.dp).background(Color(0xFFF1F3F4), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, modifier = Modifier.size(20.dp), tint = Color.DarkGray)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(subtitle, fontSize = 13.sp, color = Color.Gray)
            }
            Icon(Icons.Default.ChevronRight, null, tint = Color.LightGray)
        }
    }
}

