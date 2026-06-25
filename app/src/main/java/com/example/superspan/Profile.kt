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
import androidx.compose.material.icons.filled.LocalOffer
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
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(com.example.superspan.ui.theme.AppBackgroundBrush)
    ) {
        // --- 1. HEADER (Solo Logo, senza sfondo bianco) ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 60.dp, bottom = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            // Effetto bagliore (glow) dietro al logo per farlo risaltare sullo sfondo sfumato
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .background(
                        brush = androidx.compose.ui.graphics.Brush.radialGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.8f),
                                Color.White.copy(alpha = 0.3f),
                                Color.Transparent
                            )
                        )
                    )
            )
            androidx.compose.foundation.Image(
                painter = androidx.compose.ui.res.painterResource(id = R.drawable.logo_superspan),
                contentDescription = "Logo SuperSpan",
                modifier = Modifier.height(60.dp),
                contentScale = androidx.compose.ui.layout.ContentScale.Fit
            )
        }

        Column(
            modifier = Modifier
                .weight(1f) // Prende lo spazio rimanente in modo corretto
                .fillMaxWidth()
        ) {
            // --- 2. IMMAGINE PROFILO E NOME ---
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp), 
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(androidx.compose.material3.MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(50.dp),
                        tint = androidx.compose.material3.MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text("${user.nome} ${user.cognome}", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
                Text(
                    text = if (user.admin) "Amministratore" else "Cliente SuperSpan",
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- SEZIONI CONTENUTO (TILES) ---
            Column(
                modifier = Modifier
                    .weight(1f) // Prende lo spazio rimanente per permettere al tasto esci di stare in basso
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = paddingValues.calculateBottomPadding() + 24.dp), 
                verticalArrangement = Arrangement.Top
            ) {
                // Sezione Comune
                ProfileMenuTile(
                    icon = Icons.Default.AccountCircle,
                    title = "Il mio account",
                    subtitle = "Email e password",
                    onClick = { navController?.navigate(Destination.ACCOUNT_SUMMARY.route) }
                )
                
                Spacer(modifier = Modifier.height(12.dp))

                if (user.admin) {
                    ProfileSectionTitle("Gestione Negozio")
                    ProfileMenuTile(Icons.Default.LocalOffer, "Gestione Offerte e Coupon", "Crea o modifica le promozioni") { navController?.navigate(Destination.OFFERTE.route) }
                    Spacer(modifier = Modifier.height(12.dp))
                    ProfileMenuTile(Icons.Default.Badge, "Revisione Candidature", "Vedi i CV ricevuti") { navController?.navigate(Destination.ADMIN_CANDIDACIES.route) }
                } else {
                    ProfileSectionTitle("Candidature")
                    ProfileMenuTile(
                        icon = Icons.Default.Description,
                        title = "Dati personali",
                        subtitle = "Contatti e Curriculum Vitae",
                        onClick = { navController?.navigate(Destination.PERSONAL_DATA_SUMMARY.route) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    ProfileMenuTile(Icons.Default.AssignmentTurnedIn, "Candidature in corso", "Stato delle tue domande") { navController?.navigate(Destination.DRAFTS.route) }
                }

                Spacer(modifier = Modifier.weight(1f)) // Spinge il bottone Esci verso il basso

                // --- 3. TASTO ESCI ---
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    Button(
                        onClick = {
                            navController?.navigate(Destination.LOGIN.route) {
                                popUpTo(0)
                            }
                        },
                        modifier = Modifier.height(56.dp).fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = androidx.compose.material3.MaterialTheme.colorScheme.error)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Logout, null, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Esci", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileSectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = androidx.compose.material3.MaterialTheme.colorScheme.primary,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp, top = 12.dp, bottom = 4.dp),
        textAlign = TextAlign.Start
    )
}

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
                modifier = Modifier
                    .size(44.dp)
                    .background(androidx.compose.material3.MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, modifier = Modifier.size(24.dp), tint = androidx.compose.material3.MaterialTheme.colorScheme.primary)
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

