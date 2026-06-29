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
import androidx.compose.foundation.layout.Box
import android.content.Intent
import android.net.Uri
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import java.io.File
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.style.TextAlign
import com.example.superspan.ui.theme.LogoLeft
import com.example.superspan.ui.theme.LogoCenter
import com.example.superspan.ui.theme.AppError

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class, androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun AdminCandidaciesPage(navController: NavController?, paddingValues: PaddingValues, sliderContent: (@Composable () -> Unit)? = null) {
    // Stati per i filtri
    var selectedRole by remember { mutableStateOf<Role?>(null) }
    var selectedSupermarket by remember { mutableStateOf<Supermarket?>(null) }
    
    // Ordine data (Crescente/Decrescente)
    var dateDescending by remember { mutableStateOf(true) }

    // Dati filtrati
    val filteredCandidacies = AllCandidacies.filter { candidacy ->
        val offer = WorkOfferSearchList.find { it.id == candidacy.offerId }
        val roleMatch = selectedRole == null || offer?.ruoloEnum == selectedRole
        val superMatch = selectedSupermarket == null || offer?.supermarket?.id == selectedSupermarket?.id
        roleMatch && superMatch
    }.sortedWith { a, b ->
        val aInoltrata = a.stato != "Inviata"
        val bInoltrata = b.stato != "Inviata"
        if (aInoltrata != bInoltrata) {
            if (aInoltrata) 1 else -1
        } else {
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
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = paddingValues.calculateBottomPadding() + 32.dp)
        ) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 64.dp, bottom = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Gestione Candidature", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = Color.Black)
                    Text("Pannello di Amministrazione", fontSize = 16.sp, color = Color.Gray)
                }
            }

            if (sliderContent != null) {
                stickyHeader {
                    sliderContent()
                }
            }

            item {
                // --- SEZIONE FILTRI ---
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(Modifier.padding(20.dp)) {
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Filtri di Ricerca", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = LogoLeft)
                            if (selectedRole != null || selectedSupermarket != null) {
                                TextButton(onClick = {
                                    selectedRole = null
                                    selectedSupermarket = null
                                }, contentPadding = PaddingValues(0.dp)) {
                                    Text("Azzera", color = AppError)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Filtro Sede
                        OutlinedButton(
                            onClick = { isSelectionOpen = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = LogoLeft)
                        ) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(selectedSupermarket?.nome ?: "Tutte le Sedi", fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Filtro Ruolo
                        ScrollableTabRow(
                            selectedTabIndex = Role.entries.indexOf(selectedRole).takeIf { it >= 0 } ?: 0,
                            edgePadding = 0.dp,
                            containerColor = Color.Transparent,
                            indicator = {},
                            divider = {}
                        ) {
                            val chipColors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = LogoLeft.copy(alpha = 0.1f),
                                selectedLabelColor = LogoLeft,
                                selectedLeadingIconColor = LogoLeft
                            )
                            FilterChip(
                                selected = selectedRole == null,
                                onClick = { selectedRole = null },
                                label = { Text("Tutti i Ruoli") },
                                modifier = Modifier.padding(end = 8.dp),
                                colors = chipColors
                            )
                            Role.entries.forEach { role ->
                                FilterChip(
                                    selected = selectedRole == role,
                                    onClick = { selectedRole = role },
                                    label = { Text(role.nome) },
                                    modifier = Modifier.padding(end = 8.dp),
                                    colors = chipColors
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Ordinamento Data
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Ordina per Data: ", fontSize = 14.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
                            Spacer(modifier = Modifier.width(8.dp))
                            FilterChip(
                                selected = true,
                                onClick = { dateDescending = !dateDescending },
                                label = { Text(if (dateDescending) "Più Recenti" else "Meno Recenti") },
                                leadingIcon = {
                                    Icon(if (dateDescending) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward, null, modifier = Modifier.size(16.dp))
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = LogoCenter.copy(alpha = 0.1f),
                                    selectedLabelColor = LogoCenter,
                                    selectedLeadingIconColor = LogoCenter
                                )
                            )
                        }
                    }
                }
            }

            // --- LISTA CANDIDATURE ---
            if (filteredCandidacies.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                        Text("Nessuna candidatura trovata", color = Color.Gray, fontSize = 16.sp)
                    }
                }
            } else {
                items(filteredCandidacies) { candidacy ->
                    Box(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
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
    val isForwarded = candidacy.stato != "Inviata"
    val context = LocalContext.current
    var showDiscardDialog by remember { mutableStateOf(false) }
    var showHRConfirm by remember { mutableStateOf(false) }
    var showManagerConfirm by remember { mutableStateOf(false) }

    if (showDiscardDialog) {
        ModernAlertDialog(
            onDismissRequest = { showDiscardDialog = false },
            title = "Scartare candidatura?",
            text = "Sei sicuro di voler scartare la candidatura di ${candidacy.nome} ${candidacy.cognome}?",
            icon = Icons.Default.Delete,
            isDestructive = true,
            confirmText = "Sì, scarta",
            onConfirm = {
                showDiscardDialog = false
                val index = AllCandidacies.indexOfFirst { it.id == candidacy.id }
                if (index != -1) {
                    AllCandidacies[index] = candidacy.copy(stato = "Scartata")
                }
                android.widget.Toast.makeText(context, "Candidatura scartata", android.widget.Toast.LENGTH_SHORT).show()
            },
            dismissText = "Annulla",
            onDismiss = { showDiscardDialog = false }
        )
    }

    if (showHRConfirm) {
        ModernAlertDialog(
            onDismissRequest = { showHRConfirm = false },
            title = "Conferma Inoltro",
            text = "Vuoi inoltrare la candidatura a HR?",
            icon = Icons.Default.Send,
            confirmText = "Inoltra",
            onConfirm = {
                showHRConfirm = false
                val index = AllCandidacies.indexOfFirst { it.id == candidacy.id }
                if (index != -1) {
                    AllCandidacies[index] = candidacy.copy(stato = "Inoltrata a HR")
                }
                android.widget.Toast.makeText(context, "Candidatura inviata a HR con successo", android.widget.Toast.LENGTH_SHORT).show()
            },
            dismissText = "Annulla",
            onDismiss = { showHRConfirm = false }
        )
    }

    if (showManagerConfirm) {
        ModernAlertDialog(
            onDismissRequest = { showManagerConfirm = false },
            title = "Conferma Inoltro",
            text = "Vuoi inoltrare la candidatura al Responsabile?",
            icon = Icons.Default.Send,
            confirmText = "Inoltra",
            onConfirm = {
                showManagerConfirm = false
                val index = AllCandidacies.indexOfFirst { it.id == candidacy.id }
                if (index != -1) {
                    AllCandidacies[index] = candidacy.copy(stato = "Inoltrata al Responsabile")
                }
                android.widget.Toast.makeText(context, "Candidatura inviata al Responsabile con successo", android.widget.Toast.LENGTH_SHORT).show()
            },
            dismissText = "Annulla",
            onDismiss = { showManagerConfirm = false }
        )
    }

    Card(
        modifier = if (isForwarded) Modifier.fillMaxWidth().alpha(0.7f) else Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = if (isForwarded) Color(0xFFF9F9F9) else Color.White),
        elevation = CardDefaults.cardElevation(if (isForwarded) 0.dp else 2.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(Modifier.padding(20.dp)) {
            // Header: Ruolo e Delete
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                Text(
                    text = offer?.titolo ?: "Offerta rimossa",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp,
                    color = LogoLeft,
                    modifier = Modifier.weight(1f)
                )
                if (!isForwarded) {
                    IconButton(onClick = { showDiscardDialog = true }, modifier = Modifier.size(28.dp).background(AppError.copy(alpha = 0.1f), RoundedCornerShape(8.dp))) {
                        Icon(Icons.Default.Delete, contentDescription = "Scarta", tint = AppError, modifier = Modifier.size(18.dp))
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            // Sede
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, null, tint = LogoCenter, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text(offer?.supermarket?.nome ?: "Sede non specificata", fontSize = 14.sp, color = Color.DarkGray, fontWeight = FontWeight.Medium)
            }

            // Data Inviata
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                Icon(Icons.Default.DateRange, null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text("Inviata il: ${candidacy.dataInvio}", fontSize = 14.sp, color = Color.Gray)
            }

            Spacer(Modifier.height(16.dp))
            HorizontalDivider(color = Color(0xFFEEEEEE))
            Spacer(Modifier.height(16.dp))

            // Info Candidato
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Person, null, tint = LogoLeft, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text("${candidacy.nome} ${candidacy.cognome}", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
            }
            
            Spacer(Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Email, null, tint = Color.Gray, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text(candidacy.emailContatto, fontSize = 14.sp, color = Color.DarkGray)
            }
            
            Spacer(Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Phone, null, tint = Color.Gray, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text(candidacy.telefono, fontSize = 14.sp, color = Color.DarkGray)
            }

            Spacer(Modifier.height(20.dp))

            // Bottoni Download
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
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
                        modifier = Modifier.weight(1f).height(45.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = LogoLeft.copy(alpha = 0.1f), contentColor = LogoLeft),
                        elevation = ButtonDefaults.buttonElevation(0.dp)
                    ) {
                        Icon(Icons.Default.Description, null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Vedi CV", fontWeight = FontWeight.Bold)
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
                        modifier = Modifier.weight(1f).height(45.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = LogoCenter.copy(alpha = 0.15f), contentColor = LogoCenter),
                        elevation = ButtonDefaults.buttonElevation(0.dp)
                    ) {
                        Icon(Icons.Default.PlayArrow, null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Vedi Video", fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Bottoni Inoltro
            if (!isForwarded) {
                Spacer(Modifier.height(16.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        onClick = { showHRConfirm = true },
                        modifier = Modifier.weight(1f).height(45.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = LogoLeft, contentColor = Color.White),
                        contentPadding = PaddingValues(horizontal = 4.dp)
                    ) {
                        Text("Invia a HR", fontSize = 13.sp, textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = { showManagerConfirm = true },
                        modifier = Modifier.weight(1f).height(45.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = LogoCenter, contentColor = Color.White),
                        contentPadding = PaddingValues(horizontal = 4.dp)
                    ) {
                        Text("A Responsabile", fontSize = 13.sp, textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                Spacer(Modifier.height(16.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFEEEEEE), RoundedCornerShape(12.dp))
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Stato: ${candidacy.stato}",
                        color = Color.DarkGray,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
