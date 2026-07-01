package com.example.superspan

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.launch


val globalWorkFilterData = WorkFilterData().apply {
    nome = ""
    ruoli.clear()
    tipiContratto.clear()
    orari.clear()
    distanzaMax = 1000f // Mostra tutto all'inizio
}

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun WorkSearchPageComplete(
    padding: PaddingValues,
    navController: NavController?,
    hideHeader: Boolean = false,
    sliderContent: (@Composable () -> Unit)? = null,
    onFilterOpenChange: (Boolean) -> Unit = {}
) {
    var enabled by remember { mutableStateOf(false) }

    LaunchedEffect(enabled) {
        onFilterOpenChange(enabled)
    }

    // Utilizziamo un'istanza globale per mantenere i filtri attivi anche quando si cambia pagina
    val filterData = globalWorkFilterData

    val snackbarHostState = remember { SnackbarHostState() }
    val listState = rememberLazyListState()

    Box(modifier = Modifier.fillMaxSize()) {
        if (!enabled) {
            WorkSearchPage(
                navController = navController,
                filterData = filterData,
                listState = listState,
                snackbarHostState = snackbarHostState,
                padding = padding,
                hideHeader = hideHeader,
                sliderContent = sliderContent,
                onOpenFilters = { enabled = true }
            )
        } else {
            WorkFilterPage(
                modifier = Modifier.fillMaxSize(),
                filterData = filterData,
                padding = padding,
                onDismiss = { enabled = false }
            )
        }
        
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )

        if (actualUser.admin && !enabled) {
            FloatingActionButton(
                onClick = { navController?.navigate(Destination.ADD_WORK_OFFER.route) },
                containerColor = com.example.superspan.ui.theme.LogoLeft,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.align(Alignment.BottomEnd).padding(end = 24.dp, bottom = padding.calculateBottomPadding() + 24.dp)
            ) {
                Icon(Icons.Default.Add, "Aggiungi")
            }
        }
    }
}

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun WorkSearchPage(
    navController: NavController?,
    filterData: WorkFilterData,
    listState: LazyListState,
    snackbarHostState: SnackbarHostState,
    padding: PaddingValues,
    hideHeader: Boolean = false,
    sliderContent: (@Composable () -> Unit)? = null,
    onOpenFilters: () -> Unit
) {
    val workSearchList by remember {
        derivedStateOf { 
            searchWorkOffer(filterData).sortedBy { offer ->
                val hasCandidacy = AllCandidacies.any { it.userEmail == actualUser.email && it.offerId == offer.id }
                val hasDraft = actualUser.candidacyDraftsByOfferId.containsKey(offer.id)
                if (hasCandidacy || hasDraft) 1 else 0
            }
        }
    }
    
    var highlightCurrentId by remember { mutableStateOf<Int?>(null) }
    val context = androidx.compose.ui.platform.LocalContext.current
    val deletingWorkOfferIds = remember { mutableStateListOf<Int>() }
    var offerToDelete by remember { mutableStateOf<WorkOffer?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(highlightedWorkOfferId) {
        val targetId = highlightedWorkOfferId
        if (targetId != null) {
            highlightCurrentId = targetId
            val index = workSearchList.indexOfFirst { it.id == targetId }
            if (index != -1) {
                // Scorriamo aggiungendo +2 per l'header e la barra di ricerca
                listState.animateScrollToItem(index + 2)
            }
            android.widget.Toast.makeText(context, "Offerta inserita con successo!", android.widget.Toast.LENGTH_SHORT).show()
            kotlinx.coroutines.delay(1500)
            highlightCurrentId = null
            highlightedWorkOfferId = null
        }
    }

    if (offerToDelete != null) {
        val offer = offerToDelete!!
        ModernAlertDialog(
            onDismissRequest = { offerToDelete = null },
            title = "Elimina Offerta",
            text = "Sei sicuro di voler eliminare l'offerta di lavoro \"${offer.titolo}\"?",
            icon = Icons.Default.Delete,
            isDestructive = true,
            confirmText = "Elimina",
            onConfirm = {
                val id = offer.id
                offerToDelete = null
                scope.launch {
                    deletingWorkOfferIds.add(id)
                    kotlinx.coroutines.delay(400)
                    WorkOfferSearchList.removeAll { it.id == id }
                    deletingWorkOfferIds.remove(id)
                }
            },
            dismissText = "Annulla",
            onDismiss = { offerToDelete = null }
        )
    }

    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = padding.calculateBottomPadding() + 32.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 1. HEADER (Scorre con la pagina)
        if (!hideHeader) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 64.dp, bottom = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Lavora con noi!",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "Trova la posizione adatta a te",
                        fontSize = 16.sp,
                        color = Color.Gray
                    )
                }
            }
        }

        if (sliderContent != null) {
            stickyHeader {
                sliderContent()
            }
        }

        // 2. BARRA DI RICERCA + TASTO FILTRI
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                androidx.compose.material3.Surface(
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    shape = RoundedCornerShape(25.dp),
                    shadowElevation = 6.dp,
                    color = Color.White
                ) {
                    androidx.compose.material3.TextField(
                        value = filterData.nome,
                        onValueChange = { filterData.nome = it },
                        placeholder = { Text("Cerca ruolo o città...", color = Color.Gray) },
                        modifier = Modifier.fillMaxSize(),
                        leadingIcon = { Icon(Icons.Default.Search, null, tint = Color.Gray) },
                        trailingIcon = {
                            IconButton(onClick = onOpenFilters) {
                                Icon(Icons.Default.Tune, contentDescription = "Filtri", tint = com.example.superspan.ui.theme.LogoLeft)
                            }
                        },
                        singleLine = true,
                        colors = androidx.compose.material3.TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            cursorColor = com.example.superspan.ui.theme.LogoLeft
                        )
                    )
                }
            }
        }

        val hasActiveFilters = filterData.ruoli.isNotEmpty() || filterData.tipiContratto.isNotEmpty() || filterData.orari.isNotEmpty() || filterData.distanzaMax < 1000f

        if (hasActiveFilters) {
            item {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (filterData.distanzaMax < 1000f) {
                        item {
                            FilterChipCustom(
                                text = "< ${filterData.distanzaMax.toInt()} km",
                                onRemove = { filterData.distanzaMax = 1000f }
                            )
                        }
                    }
                    items(filterData.ruoli.toList()) { ruolo ->
                        FilterChipCustom(
                            text = ruolo.nome,
                            onRemove = { filterData.ruoli.remove(ruolo) }
                        )
                    }
                    items(filterData.tipiContratto.toList()) { tipo ->
                        FilterChipCustom(
                            text = tipo.nome,
                            onRemove = { filterData.tipiContratto.remove(tipo) }
                        )
                    }
                    items(filterData.orari.toList()) { orario ->
                        FilterChipCustom(
                            text = orario.nome,
                            onRemove = { filterData.orari.remove(orario) }
                        )
                    }
                }
            }
        }

        // 3. LISTA DELLE OFFERTE
        if (workSearchList.isEmpty()) {
            item {
                EmptyState(
                    icon = Icons.Default.WorkOff,
                    title = "Nessuna offerta trovata",
                    subtitle = "Prova ad ampliare la distanza o a rimuovere qualche filtro."
                )
            }
        }
        items(workSearchList, key = { it.id }) { offer ->
            val hasCandidacy = AllCandidacies.any { it.userEmail == actualUser.email && it.offerId == offer.id }
            val hasDraft = actualUser.candidacyDraftsByOfferId.containsKey(offer.id)
            val hasViewed = actualUser.viewedOffers.contains(offer.id)
            
            val badgeText = if (hasCandidacy) "Inviata" else if (hasDraft) "Bozza" else if (hasViewed) null else "Nuova"
            
            val isDeleting = deletingWorkOfferIds.contains(offer.id)
            AnimatedVisibility(
                visible = !isDeleting,
                enter = fadeIn(),
                exit = slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(400)) + fadeOut(animationSpec = tween(400))
            ) {
                WorkOfferCompose(
                    workOffer = offer, 
                    navController = navController, 
                    isHighlighted = (highlightCurrentId == offer.id),
                    isDisabled = hasCandidacy || hasDraft,
                    badgeText = badgeText,
                    onDeleteClick = { offerToDelete = offer }
                )
            }
        }
    }
}



@Composable
fun WorkOfferCompose(workOffer: WorkOffer, navController: NavController?, isHighlighted: Boolean = false, isDisabled: Boolean = false, badgeText: String? = null, onDeleteClick: () -> Unit = {}) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isHighlighted) Color(0xFFE0E0E0) else Color.White,
        animationSpec = tween(durationMillis = 500)
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(20.dp))
            .clickable { 
                if (!actualUser.viewedOffers.contains(workOffer.id)) {
                    actualUser.viewedOffers.add(workOffer.id)
                }
                navController?.navigate("dettaglio_offerta/${workOffer.id}") 
            },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp, pressedElevation = 8.dp),
        border = BorderStroke(1.dp, Color(0xFFEAEAEA))
    ) {
        Column {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // RIGA SUPERIORE: Titolo e Badge Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = workOffer.titolo,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.weight(1f),
                    color = Color(0xFF1A1A1A)
                )
                if (badgeText != null) {
                    val badgeColor = if (badgeText == "Bozza") androidx.compose.material3.MaterialTheme.colorScheme.secondary else if (badgeText == "Inviata") com.example.superspan.ui.theme.LogoCenter else com.example.superspan.ui.theme.LogoLeft
                    Surface(
                        color = badgeColor.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.padding(start = 12.dp)
                    ) {
                        Text(
                            text = badgeText.uppercase(),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = badgeColor,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }

            // DESCRIZIONE
            Text(
                text = workOffer.descrizioneBreve,
                fontSize = 14.sp,
                color = Color.DarkGray,
                maxLines = 2,
                lineHeight = 20.sp,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )

            HorizontalDivider(color = Color(0xFFF0F0F0), thickness = 1.dp)

            // RIGA INFERIORE: Dettagli e Badge Distanza
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.Top) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp).padding(top = 2.dp),
                        tint = com.example.superspan.ui.theme.LogoLeft
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${workOffer.supermarket.citta} • ${workOffer.tipoContratto.nome} • ${workOffer.orario.nome}",
                        fontSize = 13.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Badge Distanza
                Surface(
                    color = Color(0xFFF5F5F5),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.padding(start = 12.dp)
                ) {
                    Text(
                        "${workOffer.distanzaKm.toInt()} km",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.DarkGray
                    )
                }
            }
            }
            if (actualUser.admin) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF9F9F9))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = { navController?.navigate("edit_work_offer/${workOffer.id}") }) {
                            Icon(Icons.Default.Edit, contentDescription = "Modifica", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Modifica")
                        }
                        TextButton(onClick = onDeleteClick, colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)) {
                            Icon(Icons.Default.Delete, contentDescription = "Elimina", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Elimina")
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkFilterPage(modifier: Modifier, filterData: WorkFilterData, padding: PaddingValues, onDismiss: () -> Unit) {
    val scrollState = rememberScrollState()

    // Stato locale per gestire lo switch "Tutta Italia"
    var tuttaItalia by remember { mutableStateOf(filterData.distanzaMax >= 1000f) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(com.example.superspan.ui.theme.LogoRight.copy(alpha = 0.03f))
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            FilterTitle(
                title = "Filtri Lavoro",
                paddingValues = padding
            )

            Column(Modifier.padding(horizontal = 24.dp)) {

            // --- 2. SEZIONE DISTANZA (CARD MODERNA) ---
            Text("Dove vuoi lavorare?", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.4f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(Modifier.padding(20.dp)) {
                    // Switch Tutta Italia
                    Row(
                        Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Cerca in tutta Italia", fontWeight = FontWeight.Bold)
                            Text("Ignora la distanza da casa", fontSize = 12.sp, color = Color.Gray)
                        }
                        Switch(
                            checked = tuttaItalia,
                            onCheckedChange = {
                                tuttaItalia = it
                                if (it) filterData.distanzaMax = 1000f else filterData.distanzaMax = 50f
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = com.example.superspan.ui.theme.LogoLeft,
                                checkedTrackColor = com.example.superspan.ui.theme.LogoLeft.copy(alpha = 0.5f)
                            )
                        )
                    }

                    Spacer(Modifier.height(20.dp))
                    HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray.copy(alpha = 0.5f))
                    Spacer(Modifier.height(20.dp))

                    // Testo dinamico Distanza
                    Text(
                        text = if (tuttaItalia) "Distanza: Senza limiti" else "Entro ${filterData.distanzaMax.toInt()} km da te",
                        color = if (tuttaItalia) Color.Gray else com.example.superspan.ui.theme.LogoLeft,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp
                    )

                    Spacer(Modifier.height(8.dp))

                    // SLIDER MODERNO (Sottile con pallino bianco)
                    Slider(
                        value = if (tuttaItalia) 100f else filterData.distanzaMax.coerceIn(5f, 100f),
                        onValueChange = { 
                            if (tuttaItalia) tuttaItalia = false
                            filterData.distanzaMax = it 
                        },
                        valueRange = 5f..100f,
                        // Il Pallino (Thumb)
                        thumb = {
                            Box(
                                modifier = Modifier
                                    .size(22.dp)
                                    .background(
                                        if (tuttaItalia) Color.LightGray else com.example.superspan.ui.theme.LogoLeft,
                                        CircleShape
                                    )
                                    .border(2.dp, Color.White, CircleShape)
                            )
                        },
                        // La Barra (Track)
                        track = { sliderState ->
                            SliderDefaults.Track(
                                modifier = Modifier.height(4.dp),
                                sliderState = sliderState,
                                colors = SliderDefaults.colors(
                                    activeTrackColor = if (tuttaItalia) Color.LightGray.copy(alpha = 0.5f) else com.example.superspan.ui.theme.LogoLeft,
                                    inactiveTrackColor = Color.LightGray.copy(alpha = 0.3f),
                                    disabledActiveTrackColor = Color.LightGray.copy(alpha = 0.5f),
                                    disabledInactiveTrackColor = Color.LightGray.copy(alpha = 0.2f)
                                )
                            )
                        }
                    )

                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("5 km", fontSize = 12.sp, color = Color.Gray)
                        Text("100 km", fontSize = 12.sp, color = Color.Gray)
                    }
                }
            }

            Spacer(Modifier.height(32.dp))

            // --- 3. TIPO CONTRATTO + ORARIO ---
            Text("Tipologia di impiego", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.4f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(Modifier.padding(20.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Column(Modifier.weight(1f)) {
                            Text("Contratto", fontSize = 13.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                            TipoContratto.entries.forEach { tipo ->
                                FilterCheckboxRow(tipo.nome, filterData.tipiContratto.contains(tipo)) {
                                    if (it) filterData.tipiContratto.add(tipo) else filterData.tipiContratto.remove(tipo)
                                }
                            }
                        }
                        Column(Modifier.weight(1f)) {
                            Text("Orario", fontSize = 13.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                            OrarioLavoro.entries.forEach { orario ->
                                FilterCheckboxRow(orario.nome, filterData.orari.contains(orario)) {
                                    if (it) filterData.orari.add(orario) else filterData.orari.remove(orario)
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(32.dp))

            // --- 4. RUOLI (GRID) ---
            Text("Ruoli di interesse", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.4f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(Modifier.padding(20.dp)) {
                    Role.entries.chunked(2).forEach { pair ->
                        Row(Modifier.fillMaxWidth()) {
                            pair.forEach { role ->
                                Row(Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                                    Checkbox(
                                        checked = filterData.ruoli.contains(role),
                                        onCheckedChange = { isChecked ->
                                            if (isChecked) filterData.ruoli.add(role) else filterData.ruoli.remove(role)
                                        },
                                        colors = CheckboxDefaults.colors(checkedColor = com.example.superspan.ui.theme.LogoLeft)
                                    )
                                    Text(role.nome, fontSize = 14.sp)
                                }
                            }
                            if (pair.size == 1) Spacer(Modifier.weight(1f))
                        }
                    }
                }
            }

            Spacer(Modifier.height(100.dp + padding.calculateBottomPadding()))
            }
        }

        Button(
            onClick = onDismiss,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = padding.calculateBottomPadding() + 16.dp),
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(containerColor = com.example.superspan.ui.theme.LogoLeft)
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
                filterData.ruoli.clear()
                filterData.tipiContratto.clear()
                filterData.orari.clear()
                filterData.distanzaMax = 1000f
                tuttaItalia = true
            },
            paddingValues = padding
        )
    }
}

@Composable
fun FilterCheckboxRow(label: String, isChecked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!isChecked) }
    ) {
        Checkbox(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(checkedColor = com.example.superspan.ui.theme.LogoLeft)
        )
        Text(label, fontSize = 14.sp)
    }
}

@Composable
fun FilterChipCustom(text: String, onRemove: () -> Unit) {
    androidx.compose.material3.Surface(
        color = com.example.superspan.ui.theme.LogoLeft.copy(alpha = 0.15f),
        shape = RoundedCornerShape(16.dp),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = text,
                color = com.example.superspan.ui.theme.LogoLeft,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Rimuovi filtro",
                tint = com.example.superspan.ui.theme.LogoLeft,
                modifier = Modifier
                    .size(16.dp)
                    .clickable { onRemove() }
            )
        }
    }
}
