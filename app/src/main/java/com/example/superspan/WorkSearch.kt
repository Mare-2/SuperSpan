package com.example.superspan

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
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


@Composable
fun WorkSearchPageComplete(
    padding: PaddingValues,
    navController: NavController?,
    hideHeader: Boolean = false
) {
    var enabled by remember { mutableStateOf(false) }

    // Inizializzazione pulita senza filtri pre-applicati
    val filterData by remember {
        mutableStateOf(WorkFilterData().apply {
            nome = ""
            ruoli.clear()
            tipiContratto.clear()
            orari.clear()
            distanzaMax = 1000f // Mostra tutto all'inizio
        })
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val listState = rememberLazyListState()

    Box(modifier = Modifier.fillMaxSize()) {
        if (!enabled) {
            WorkSearchPage(navController, filterData, listState, snackbarHostState, padding, hideHeader) {
                enabled = true
            }
        } else {
            WorkFilterPage(Modifier.fillMaxSize(), filterData) {
                enabled = false
            }
        }
        
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )

        if (actualUser.admin && !enabled) {
            FloatingActionButton(
                onClick = { navController?.navigate(Destination.ADD_WORK_OFFER.route) },
                containerColor = androidx.compose.material3.MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                modifier = Modifier.align(Alignment.BottomEnd).padding(end = 24.dp, bottom = padding.calculateBottomPadding() + 24.dp)
            ) {
                Icon(Icons.Default.Add, "Aggiungi")
            }
        }
    }
}

@Composable
fun WorkSearchPage(
    navController: NavController?,
    filterData: WorkFilterData,
    listState: LazyListState,
    snackbarHostState: SnackbarHostState,
    padding: PaddingValues,
    hideHeader: Boolean = false,
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
                        .height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    shadowElevation = 6.dp,
                    color = Color.White
                ) {
                    androidx.compose.material3.TextField(
                        value = filterData.nome,
                        onValueChange = { filterData.nome = it },
                        placeholder = { Text("Cerca ruolo o città...", color = Color.Gray) },
                        modifier = Modifier.fillMaxSize(),
                        leadingIcon = { Icon(Icons.Default.Search, null, tint = Color.Gray) },
                        singleLine = true,
                        colors = androidx.compose.material3.TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            cursorColor = androidx.compose.material3.MaterialTheme.colorScheme.primary
                        )
                    )
                }

                Spacer(Modifier.width(8.dp))

                FilledIconButton(
                    onClick = onOpenFilters,
                    modifier = Modifier.size(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = androidx.compose.material3.MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(Icons.Default.Tune, contentDescription = "Filtri", tint = Color.White)
                }
            }
        }

        // 3. LISTA DELLE OFFERTE
        items(workSearchList, key = { it.id }) { offer ->
            val hasCandidacy = AllCandidacies.any { it.userEmail == actualUser.email && it.offerId == offer.id }
            val hasDraft = actualUser.candidacyDraftsByOfferId.containsKey(offer.id)
            val hasViewed = actualUser.viewedOffers.contains(offer.id)
            
            val badgeText = if (hasCandidacy) "Inviata" else if (hasDraft) "Bozza" else if (hasViewed) null else "Nuova"
            
            WorkOfferCompose(
                workOffer = offer, 
                navController = navController, 
                isHighlighted = (highlightCurrentId == offer.id),
                isDisabled = hasCandidacy || hasDraft,
                badgeText = badgeText
            )
        }
    }
}



@Composable
fun WorkOfferCompose(workOffer: WorkOffer, navController: NavController?, isHighlighted: Boolean = false, isDisabled: Boolean = false, badgeText: String? = null) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isHighlighted) androidx.compose.material3.MaterialTheme.colorScheme.primaryContainer else Color.White,
        animationSpec = tween(durationMillis = 500)
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable { 
                if (!actualUser.viewedOffers.contains(workOffer.id)) {
                    actualUser.viewedOffers.add(workOffer.id)
                }
                navController?.navigate("dettaglio_offerta/${workOffer.id}") 
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = workOffer.titolo,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                if (badgeText != null) {
                    val badgeColor = if (badgeText == "Bozza") androidx.compose.material3.MaterialTheme.colorScheme.secondary else if (badgeText == "Inviata") com.example.superspan.ui.theme.LogoCenter else Color(0xFF2196F3)
                    Surface(
                        color = badgeColor.copy(alpha = 0.15f),
                        shape = CircleShape,
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Text(
                            text = badgeText.uppercase(),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = badgeColor
                        )
                    }
                }
            }

            Text(
                text = workOffer.descrizioneBreve,
                fontSize = 14.sp,
                color = Color.Gray,
                maxLines = 2
            )

            // RIGA INFERIORE: Città • Contratto • Orario e Badge Distanza
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.Top) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp).padding(top = 2.dp),
                        tint = Color.Gray
                    )
                    Text(
                        text = " ${workOffer.supermarket.citta} • ${workOffer.tipoContratto.nome} • ${workOffer.orario.nome}",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Badge Distanza
                Surface(
                    color = Color.White,
                    shape = CircleShape,
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text(
                        "${workOffer.distanzaKm.toInt()} km",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.DarkGray
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkFilterPage(modifier: Modifier, filterData: WorkFilterData, onDismiss: () -> Unit) {
    val scrollState = rememberScrollState()

    // Stato locale per gestire lo switch "Tutta Italia"
    var tuttaItalia by remember { mutableStateOf(filterData.distanzaMax >= 1000f) }

    Column(
        modifier = modifier
            .background(Color(0xFFF8F9FA)) // Sfondo grigio chiarissimo
            .verticalScroll(scrollState)
    ) {
        // --- 1. HEADER FISSO ---
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.White,
            shadowElevation = 2.dp
        ) {
            Row(
                Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onDismiss) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Indietro")
                }
                Text(
                    "Filtri di ricerca",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 8.dp)
                )
                Spacer(Modifier.weight(1f))
                TextButton(onClick = {
                    filterData.ruoli.clear()
                    filterData.tipiContratto.clear()
                    filterData.orari.clear()
                    filterData.distanzaMax = 50f
                    tuttaItalia = false
                }) {
                    Text("Reset", color = Color.Red, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        Column(Modifier.padding(24.dp)) {

            // --- 2. SEZIONE DISTANZA (CARD MODERNA) ---
            Text("Dove vuoi lavorare?", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.4f))
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
                            colors = SwitchDefaults.colors(checkedThumbColor = androidx.compose.material3.MaterialTheme.colorScheme.primary)
                        )
                    }

                    Spacer(Modifier.height(20.dp))
                    HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray.copy(alpha = 0.5f))
                    Spacer(Modifier.height(20.dp))

                    // Testo dinamico Distanza
                    Text(
                        text = if (tuttaItalia) "Distanza: Senza limiti" else "Entro ${filterData.distanzaMax.toInt()} km da te",
                        color = if (tuttaItalia) Color.Gray else androidx.compose.material3.MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp
                    )

                    Spacer(Modifier.height(8.dp))

                    // SLIDER MODERNO (Sottile con pallino bianco)
                    Slider(
                        value = if (tuttaItalia) 100f else filterData.distanzaMax.coerceIn(5f, 100f),
                        onValueChange = { filterData.distanzaMax = it },
                        valueRange = 5f..100f,
                        enabled = !tuttaItalia,
                        // Il Pallino (Thumb)
                        thumb = {
                            Box(
                                modifier = Modifier
                                    .size(22.dp)
                                    .background(
                                        if (tuttaItalia) Color.LightGray else androidx.compose.material3.MaterialTheme.colorScheme.primary,
                                        CircleShape
                                    )
                                    .border(2.dp, Color.White, CircleShape)
                            )
                        },
                        // La Barra (Track)
                        track = { sliderState -> // Cambia il nome in sliderState
                            SliderDefaults.Track(
                                modifier = Modifier.height(4.dp),
                                sliderState = sliderState, // Passa sliderState qui
                                colors = SliderDefaults.colors(
                                    activeTrackColor = androidx.compose.material3.MaterialTheme.colorScheme.primary,
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

            Spacer(Modifier.height(32.dp))

            // --- 4. RUOLI (GRID) ---
            Text("Ruoli di interesse", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))

            Role.entries.chunked(2).forEach { pair ->
                Row(Modifier.fillMaxWidth()) {
                    pair.forEach { role ->
                        Row(Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = filterData.ruoli.contains(role),
                                onCheckedChange = { isChecked ->
                                    if (isChecked) filterData.ruoli.add(role) else filterData.ruoli.remove(role)
                                },
                                colors = CheckboxDefaults.colors(checkedColor = androidx.compose.material3.MaterialTheme.colorScheme.primary)
                            )
                            Text(role.nome, fontSize = 14.sp)
                        }
                    }
                    if (pair.size == 1) Spacer(Modifier.weight(1f))
                }
            }

            Spacer(Modifier.height(48.dp))

            // --- 5. TASTO APPLICA ---
            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(containerColor = androidx.compose.material3.MaterialTheme.colorScheme.primary)
            ) {
                Text("Applica filtri", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
fun FilterCheckboxRow(label: String, isChecked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!isChecked) } // Rende cliccabile anche il testo per Paolo
    ) {
        Checkbox(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(checkedColor = androidx.compose.material3.MaterialTheme.colorScheme.primary)
        )
        Text(label, fontSize = 14.sp)
    }
}
