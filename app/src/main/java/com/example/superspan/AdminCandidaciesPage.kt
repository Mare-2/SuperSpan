package com.example.superspan

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
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
fun AdminCandidaciesPage(
    navController: NavController?,
    paddingValues: PaddingValues,
    hideHeader: Boolean = false,
    sliderContent: (@Composable () -> Unit)? = null,
    onFilterOpenChange: (Boolean) -> Unit = {}
) {
    // Stati per i filtri
    val selectedRoles = remember { androidx.compose.runtime.mutableStateListOf<Role>() }
    var selectedSupermarket by remember { mutableStateOf<Supermarket?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var showFiltersPage by remember { mutableStateOf(false) }
    var isSelectionOpen by remember { mutableStateOf(false) }

    val expanded = isExpandedScreen()
    // Su tablet il pannello filtri è a lato: non conta come "overlay" (l'header admin resta visibile)
    val isOverlayOpen = isSelectionOpen || (showFiltersPage && !expanded)
    LaunchedEffect(isOverlayOpen) {
        onFilterOpenChange(isOverlayOpen)
    }
    
    // Ordine data (Crescente/Decrescente)
    var dateDescending by remember { mutableStateOf(true) }

    // Dati filtrati
    val filteredCandidacies = AllCandidacies.filter { candidacy ->
        val offer = WorkOfferSearchList.find { it.id == candidacy.offerId }
        
        val roleMatch = selectedRoles.isEmpty() || (offer?.ruoloEnum != null && selectedRoles.contains(offer.ruoloEnum))
        val superMatch = selectedSupermarket == null || offer?.supermarket?.id == selectedSupermarket?.id
        val searchMatch = searchQuery.isBlank() || 
                          offer?.titolo?.contains(searchQuery, ignoreCase = true) == true ||
                          "${candidacy.nome} ${candidacy.cognome}".contains(searchQuery, ignoreCase = true)
        roleMatch && superMatch && searchMatch
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

    val filterPanel: @Composable () -> Unit = {
        AdminCandidaciesFilterPage(
            selectedRoles = selectedRoles,
            selectedSupermarket = selectedSupermarket,
            onSupermarketChange = { selectedSupermarket = it },
            paddingValues = paddingValues,
            onDismiss = { showFiltersPage = false },
            onOpenSelection = { isSelectionOpen = true }
        )
    }

    val listContent: @Composable () -> Unit = {
        Column(modifier = Modifier.fillMaxSize()) {
            if (!hideHeader) {
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

            // --- BARRA DI RICERCA ---
            CustomSearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                placeholder = "Cerca candidato o ruolo...",
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 8.dp),
                trailingIcon = {
                    IconButton(onClick = { showFiltersPage = true }) {
                        Icon(Icons.Default.Tune, contentDescription = "Filtri", tint = LogoLeft)
                    }
                }
            )

            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentPadding = PaddingValues(bottom = paddingValues.calculateBottomPadding() + 32.dp)
            ) {
                if (sliderContent != null) {
                    item { sliderContent() }
                }

            // --- CHIPS DEI FILTRI ATTIVI ---
            val hasActiveFilters = selectedRoles.isNotEmpty() || selectedSupermarket != null
            if (hasActiveFilters) {
                item {
                    androidx.compose.foundation.lazy.LazyRow(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (selectedSupermarket != null) {
                            item {
                                FilterChipCustom(
                                    text = selectedSupermarket!!.nome,
                                    onRemove = { selectedSupermarket = null }
                                )
                            }
                        }
                        items(selectedRoles) { role ->
                            FilterChipCustom(
                                text = role.nome,
                                onRemove = { selectedRoles.remove(role) }
                            )
                        }
                    }
                }
            }

            // --- ORDINAMENTO DATA ---
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = { dateDescending = !dateDescending },
                        modifier = Modifier
                            .height(34.dp)
                            .wrapContentWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = LogoLeft,
                            contentColor = Color.White
                        ),
                        border = null,
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp, pressedElevation = 2.dp),
                        shape = androidx.compose.foundation.shape.CircleShape,
                        contentPadding = PaddingValues(horizontal = 12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = "Data",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.width(4.dp))
                            Icon(
                                imageVector = if (!dateDescending) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }

            // --- LISTA CANDIDATURE ---
            if (filteredCandidacies.isEmpty()) {
                item {
                    EmptyState(
                        icon = Icons.Default.Badge,
                        title = "Nessuna candidatura",
                        subtitle = "Nessun risultato per i filtri selezionati. Prova a modificarli o azzerarli."
                    )
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

    if (isSelectionOpen) {
        SupermarketSelectionScreen(
            onBack = { isSelectionOpen = false },
            onSelected = {
                selectedSupermarket = it
                isSelectionOpen = false
            }
        )
    } else if (expanded) {
        // TABLET: lista a sinistra (2/3), filtri agganciati a destra (1/3)
        Row(Modifier.fillMaxSize()) {
            Box(Modifier.weight(2f).fillMaxHeight()) { listContent() }
            if (showFiltersPage) {
                Box(Modifier.fillMaxHeight().width(1.dp).background(Color.LightGray.copy(alpha = 0.4f)))
                Box(Modifier.weight(1f).fillMaxHeight()) { filterPanel() }
            }
        }
    } else {
        // TELEFONO: comportamento attuale (a schermo intero)
        if (showFiltersPage) filterPanel() else listContent()
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
            icon = Icons.AutoMirrored.Filled.Send,
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
            icon = Icons.AutoMirrored.Filled.Send,
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
                Text("Invia a:", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                Spacer(Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        onClick = { showHRConfirm = true },
                        modifier = Modifier.weight(1f).height(45.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = LogoLeft, contentColor = Color.White),
                        contentPadding = PaddingValues(horizontal = 4.dp)
                    ) {
                        Text("Risorse Umane", fontSize = 13.sp, textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = { showManagerConfirm = true },
                        modifier = Modifier.weight(1f).height(45.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = LogoCenter, contentColor = Color.White),
                        contentPadding = PaddingValues(horizontal = 4.dp)
                    ) {
                        Text("Responsabile Sede", fontSize = 13.sp, textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
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

@Composable
fun AdminCandidaciesFilterPage(
    selectedRoles: MutableList<Role>,
    selectedSupermarket: Supermarket?,
    onSupermarketChange: (Supermarket?) -> Unit,
    paddingValues: PaddingValues,
    onDismiss: () -> Unit,
    onOpenSelection: () -> Unit
) {
    val scrollState = androidx.compose.foundation.rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(com.example.superspan.ui.theme.LogoRight.copy(alpha = 0.03f))
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            FilterTitle(
                title = "Filtri Candidature",
                paddingValues = paddingValues
            )

            Column(Modifier.padding(horizontal = 24.dp)) {
                // Sede di Lavoro
                Text("Sede di Lavoro", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(16.dp))
                OutlinedButton(
                    onClick = onOpenSelection,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = LogoLeft)
                ) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(selectedSupermarket?.nome ?: "Tutte le Sedi", fontWeight = FontWeight.Bold)
                }

                Spacer(Modifier.height(32.dp))

                // Ruoli
                Text("Ruoli di interesse", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(12.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(20.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.4f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(Modifier.padding(20.dp)) {
                        Role.entries.chunked(2).forEach { pair ->
                            Row(Modifier.fillMaxWidth()) {
                                pair.forEach { role ->
                                    Row(Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                                        Checkbox(
                                            checked = selectedRoles.contains(role),
                                            onCheckedChange = { isChecked ->
                                                if (isChecked) selectedRoles.add(role) else selectedRoles.remove(role)
                                            },
                                            colors = CheckboxDefaults.colors(checkedColor = LogoLeft)
                                        )
                                        Text(role.nome, fontSize = 14.sp)
                                    }
                                }
                                if (pair.size == 1) Spacer(Modifier.weight(1f))
                            }
                        }
                    }
                }

                Spacer(Modifier.height(100.dp + paddingValues.calculateBottomPadding()))
            }
        }

        // Tasto Applica
        Button(
            onClick = onDismiss,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = paddingValues.calculateBottomPadding() + 16.dp),
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(containerColor = LogoLeft)
        ) {
            Text(
                text = "Applica filtri",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
            )
        }

        FloatingFilterActions(
            onDismiss = onDismiss,
            onReset = {
                selectedRoles.clear()
                onSupermarketChange(null)
            },
            paddingValues = paddingValues
        )
    }
}
