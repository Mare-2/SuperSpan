package com.example.superspan


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
    var showLogoutDialog by remember { mutableStateOf(false) }

    if (showLogoutDialog) {
        ModernAlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = "Conferma Logout",
            text = "Sei sicuro di voler uscire dal tuo account?",
            icon = Icons.AutoMirrored.Filled.Logout,
            isDestructive = true,
            confirmText = "Esci",
            onConfirm = {
                showLogoutDialog = false
                Destination.entries.forEach { dest ->
                    navController?.clearBackStack(dest.route)
                }
                navController?.navigate(Destination.LOGIN.route) {
                    popUpTo(0)
                }
            },
            dismissText = "Annulla",
            onDismiss = { showLogoutDialog = false }
        )
    }

    AuraBackground(modifier = Modifier.fillMaxSize()) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        // --- 1. HEADER (logo sopra lo sfondo Aura, niente rettangolo) ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 60.dp, bottom = 32.dp, start = 24.dp, end = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            androidx.compose.foundation.Image(
                painter = androidx.compose.ui.res.painterResource(id = R.drawable.superspan),
                contentDescription = "Logo SuperSpan",
                modifier = Modifier.height(48.dp),
                contentScale = androidx.compose.ui.layout.ContentScale.Fit
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            // --- 2. NOME UTENTE ---
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp), 
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val initials = "${user.nome.firstOrNull()?.uppercaseChar() ?: ""}${user.cognome.firstOrNull()?.uppercaseChar() ?: ""}"
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .background(
                            androidx.compose.ui.graphics.Brush.linearGradient(
                                listOf(com.example.superspan.ui.theme.LogoLeft.copy(alpha=0.2f), com.example.superspan.ui.theme.LogoRight.copy(alpha=0.2f))
                            ), 
                            CircleShape
                        )
                        .border(2.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        initials,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = com.example.superspan.ui.theme.BrandDark
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text("${user.nome} ${user.cognome}", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold)
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = com.example.superspan.ui.theme.LogoLeft.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = if (user.admin) "Amministratore" else "Cliente SuperSpan",
                        color = com.example.superspan.ui.theme.LogoLeft,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- SEZIONI CONTENUTO (TILES) ---
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp), 
                verticalArrangement = Arrangement.Top
            ) {
                // Sezione Comune
                ProfileMenuGroup {
                    ProfileMenuTile(
                        icon = Icons.Default.AccountCircle,
                        title = "Il mio account",
                        subtitle = "Nome e password",
                        onClick = { navController?.navigate(Destination.ACCOUNT_SUMMARY.route) },
                        showDivider = false
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))

                if (user.admin) {
                    ProfileSectionTitle("Gestione Negozio")
                    ProfileMenuGroup {
                        ProfileMenuTile(Icons.Default.LocalOffer, "Gestione Offerte e Coupon", "Crea o modifica le promozioni", onClick = { navController?.navigateTopLevel(Destination.OFFERTE.route) }, showDivider = true)
                        ProfileMenuTile(Icons.Default.Badge, "Revisione Candidature", "Vedi i CV ricevuti", onClick = { navController?.navigateTopLevel(Destination.ADMIN_CANDIDACIES.route) }, showDivider = false)
                    }
                } else {
                    ProfileSectionTitle("Candidature")
                    ProfileMenuGroup {
                        ProfileMenuTile(
                            icon = Icons.Default.Description,
                            title = "Dati personali",
                            subtitle = "Contatti e Curriculum Vitae",
                            onClick = { navController?.navigate(Destination.PERSONAL_DATA_SUMMARY.route) },
                            showDivider = true
                        )
                        ProfileMenuTile(Icons.Default.AssignmentTurnedIn, "Candidature in corso", "Stato delle tue domande", onClick = { navController?.navigate(Destination.DRAFTS.route) }, showDivider = false)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // --- 3. TASTO ESCI ---
                ProfileMenuGroup(
                    containerColor = com.example.superspan.ui.theme.AppError.copy(alpha = 0.08f),
                    borderColor = com.example.superspan.ui.theme.AppError.copy(alpha = 0.2f),
                    shadowElevation = 0.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showLogoutDialog = true }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Logout, null, modifier = Modifier.size(24.dp), tint = com.example.superspan.ui.theme.AppError)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Esci dal tuo account", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = com.example.superspan.ui.theme.AppError)
                    }
                }
                
                Spacer(Modifier.height(paddingValues.calculateBottomPadding() + 24.dp))
            }
        }
    }
    }
}

@Composable
fun ProfileSectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = com.example.superspan.ui.theme.LogoLeft,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp, top = 12.dp, bottom = 4.dp),
        textAlign = TextAlign.Start
    )
}

@Composable
fun ProfileMenuGroup(
    containerColor: Color = Color.White,
    borderColor: Color = Color.LightGray.copy(alpha = 0.3f),
    shadowElevation: androidx.compose.ui.unit.Dp = 2.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = containerColor,
        shadowElevation = shadowElevation,
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            content()
        }
    }
}

@Composable
fun ProfileMenuTile(icon: ImageVector, title: String, subtitle: String, onClick: () -> Unit, showDivider: Boolean = false) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(com.example.superspan.ui.theme.LogoLeft.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, modifier = Modifier.size(24.dp), tint = com.example.superspan.ui.theme.LogoLeft)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(subtitle, fontSize = 13.sp, color = Color.Gray)
            }
            Icon(Icons.Default.ChevronRight, null, tint = Color.LightGray)
        }
        if (showDivider) {
            HorizontalDivider(modifier = Modifier.padding(start = 76.dp, end = 16.dp), color = Color.LightGray.copy(alpha = 0.3f))
        }
    }
}

