package com.example.superspan

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import android.content.Intent
import android.net.Uri
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import java.io.File

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AdminCandidaciesPage(navController: NavController?, paddingValues: PaddingValues) {
    // Stati per i filtri
    var selectedRole by remember { mutableStateOf<Role?>(null) }
    var selectedSupermarket by remember { mutableStateOf<Supermarket?>(null) }
    
    // Ordine data (Crescente/Decrescente)
    var dateDescending by remember { mutableStateOf(true) }

    // Dati filtrati
    val filteredCandidacies = remember(AllCandidacies, selectedRole, selectedSupermarket, dateDescending) {
        AllCandidacies.filter { candidacy ->
            val offer = WorkOfferSearchList.find { it.id == candidacy.offerId }
            val roleMatch = selectedRole == null || offer?.ruoloEnum == selectedRole
            val superMatch = selectedSupermarket == null || offer?.supermarket?.id == selectedSupermarket?.id
            roleMatch && superMatch
        }.sortedWith { a, b ->
            if (dateDescending) b.dataInvio.compareTo(a.dataInvio)
            else a.dataInvio.compareTo(b.dataInvio)
        }
    }

    var isSelectionOpen by remember { mutableStateOf(false) }

    if (isSelectionOpen) {
        SupermarketSelectionScreen(
            onBack = { isSelectionOpen = false },
            onSelected = {
                selectedSupermarket = it
                isSelectionOpen = false
            }
        )
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF8F9FA))
        ) {
            // --- SEZIONE FILTRI ---
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Filtri di Ricerca", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        if (selectedRole != null || selectedSupermarket != null) {
                            TextButton(onClick = {
                                selectedRole = null
                                selectedSupermarket = null
                            }, contentPadding = PaddingValues(0.dp)) {
                                Text("Azzera", color = Color.Red)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Filtro Sede
                    OutlinedButton(
                        onClick = { isSelectionOpen = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(selectedSupermarket?.nome ?: "Tutte le Sedi")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Filtro Ruolo
                    ScrollableTabRow(
                        selectedTabIndex = Role.entries.indexOf(selectedRole).takeIf { it >= 0 } ?: 0,
                        edgePadding = 0.dp,
                        containerColor = Color.Transparent,
                        indicator = {},
                        divider = {}
                    ) {
                        FilterChip(
                            selected = selectedRole == null,
                            onClick = { selectedRole = null },
                            label = { Text("Tutti i Ruoli") },
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Role.entries.forEach { role ->
                            FilterChip(
                                selected = selectedRole == role,
                                onClick = { selectedRole = role },
                                label = { Text(role.nome) },
                                modifier = Modifier.padding(end = 8.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Ordinamento Data
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Ordina per Data: ", fontSize = 14.sp, color = Color.Gray)
                        FilterChip(
                            selected = true,
                            onClick = { dateDescending = !dateDescending },
                            label = { Text(if (dateDescending) "Più Recenti" else "Meno Recenti") },
                            leadingIcon = {
                                Icon(if (dateDescending) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward, null, modifier = Modifier.size(16.dp))
                            }
                        )
                    }
                }
            }

            // --- LISTA CANDIDATURE ---
            if (filteredCandidacies.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Nessuna candidatura trovata", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredCandidacies) { candidacy ->
                        CandidacyAdminCard(candidacy)
                    }
                }
            }
        }
    }
}

@Composable
fun CandidacyAdminCard(candidacy: Candidacy) {
    val offer = WorkOfferSearchList.find { it.id == candidacy.offerId }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            // Header: Ruolo e Data
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    text = offer?.titolo ?: "Offerta rimossa",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color(0xFF1565C0)
                )
                Text(
                    text = candidacy.dataInvio,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
            
            // Sede
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                Icon(Icons.Default.LocationOn, null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text(offer?.supermarket?.nome ?: "Sede non specificata", fontSize = 14.sp, color = Color.DarkGray)
            }

            Spacer(Modifier.height(12.dp))
            Divider(color = Color(0xFFEEEEEE))
            Spacer(Modifier.height(12.dp))

            // Info Candidato
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Person, null, tint = Color(0xFF388E3C))
                Spacer(Modifier.width(8.dp))
                Text("${candidacy.nome} ${candidacy.cognome}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
            
            Spacer(Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Email, null, tint = Color.Gray, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text(candidacy.emailContatto, fontSize = 14.sp, color = Color.Gray)
            }
            
            Spacer(Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Phone, null, tint = Color.Gray, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text(candidacy.telefono, fontSize = 14.sp, color = Color.Gray)
            }

            Spacer(Modifier.height(16.dp))

            // Bottoni Download
            val context = LocalContext.current
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (candidacy.cvPath != null) {
                    Button(
                        onClick = { 
                            val resId = context.resources.getIdentifier(candidacy.cvPath, "raw", context.packageName)
                            if (resId != 0) {
                                val file = File(context.cacheDir, "${candidacy.cvPath}.pdf")
                                if (!file.exists()) {
                                    context.resources.openRawResource(resId).use { input ->
                                        file.outputStream().use { output ->
                                            input.copyTo(output)
                                        }
                                    }
                                }
                                val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
                                val intent = Intent(Intent.ACTION_VIEW).apply {
                                    setDataAndType(uri, "application/pdf")
                                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                }
                                context.startActivity(intent)
                            } else {
                                val file = File(candidacy.cvPath)
                                if (file.exists()) {
                                    val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
                                    val intent = Intent(Intent.ACTION_VIEW).apply {
                                        setDataAndType(uri, "application/pdf")
                                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                    }
                                    context.startActivity(intent)
                                }
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE8F5E9), contentColor = Color(0xFF2E7D32))
                    ) {
                        Icon(Icons.Default.Description, null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Vedi CV")
                    }
                }
                if (candidacy.videoPath != null) {
                    Button(
                        onClick = { 
                            val resId = context.resources.getIdentifier(candidacy.videoPath, "raw", context.packageName)
                            if (resId != 0) {
                                val file = File(context.cacheDir, "${candidacy.videoPath}.mp4")
                                if (!file.exists()) {
                                    context.resources.openRawResource(resId).use { input ->
                                        file.outputStream().use { output ->
                                            input.copyTo(output)
                                        }
                                    }
                                }
                                val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
                                val intent = Intent(Intent.ACTION_VIEW).apply {
                                    setDataAndType(uri, "video/mp4")
                                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                }
                                context.startActivity(intent)
                            } else {
                                val file = File(candidacy.videoPath)
                                if (file.exists()) {
                                    val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
                                    val intent = Intent(Intent.ACTION_VIEW).apply {
                                        setDataAndType(uri, "video/mp4")
                                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                    }
                                    context.startActivity(intent)
                                }
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFF3E0), contentColor = Color(0xFFE65100))
                    ) {
                        Icon(Icons.Default.PlayArrow, null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Vedi Video")
                    }
                }
            }
        }
    }
}
