/*package com.example.superspan

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import androidx.compose.foundation.layout.*


@Composable
fun WorkSearchPageComplete(
    padding: PaddingValues,
    navController: NavController?
) {
    var enabled: Boolean by remember { mutableStateOf(false) }
    val filterData by remember { mutableStateOf(WorkFilterData()) }
    Column(
        Modifier.padding(padding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        if (!enabled) {
            Column(
                modifier = Modifier.padding(vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Lavora con noi!",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Trova la posizione adatta a te",
                    fontSize = 20.sp,
                    color = Color.Gray,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
            WorkSearchPage(Modifier.weight(4f), navController, filterData) {
                enabled = true
            }
        } else {
            WorkFilterPage(Modifier.fillMaxSize(), filterData) { enabled = false }
        }
    }
}

@Composable
fun WorkOfferCompose(workOffer: WorkOffer, navController: NavController?) {
    Box(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(15.dp))
            .background(Color(0xFFDCDCDC))
            .clickable(onClick = {
                navController?.navigate("dettaglio_offerta/${workOffer.id}")
            })
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = workOffer.ruolo,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.DarkGray
            )
            Text(
                text = workOffer.descrizioneBreve,
                fontSize = 14.sp,
                color = Color.Gray,
                lineHeight = 18.sp
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "${workOffer.citta} • ${workOffer.tipoContratto.nome} • ${workOffer.orario.nome} • ${workOffer.distanzaKm} km",
                fontSize = 13.sp,
                color = Color.Gray,
                fontWeight = FontWeight.Light
            )
        }
    }
}


@Composable
fun WorkSearchPage(
    modifier: Modifier,
    navController: NavController?,
    filterData: WorkFilterData,
    onclick: () -> Unit
) {
    val workSearchList: List<WorkOffer> by remember {
        derivedStateOf {
            searchWorkOffer(filterData)
        }
    }
    var showSortMenu by remember { mutableStateOf(false) }

    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        OutlinedTextField(
            filterData.nome,
            { filterData.nome = it },
            label = { Text("Cerca ruolo o città...") },
            modifier = Modifier
                .padding(top = 15.dp, bottom = 10.dp)
                .weight(0.8f),
            shape = RoundedCornerShape(30.dp)
        )
        Spacer(Modifier.height(5.dp))
        Row(
            Modifier
                .weight(0.5f)
                .fillMaxWidth()
                .padding(bottom = 8.dp, end = 10.dp, start = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box {
                Button({ showSortMenu = true }) {
                    Text("Ordina: ${filterData.ordinamento} ${if (filterData.ordinamentoCrescente) "▲" else "▼"}")
                }
                DropdownMenu(
                    expanded = showSortMenu,
                    onDismissRequest = { showSortMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Nome ▲") },
                        onClick = {
                            filterData.ordinamento = "Nome"
                            filterData.ordinamentoCrescente = true
                            showSortMenu = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Nome ▼") },
                        onClick = {
                            filterData.ordinamento = "Nome"
                            filterData.ordinamentoCrescente = false
                            showSortMenu = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Città ▲") },
                        onClick = {
                            filterData.ordinamento = "Città"
                            filterData.ordinamentoCrescente = true
                            showSortMenu = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Città ▼") },
                        onClick = {
                            filterData.ordinamento = "Città"
                            filterData.ordinamentoCrescente = false
                            showSortMenu = false
                        }
                    )
                }
            }
            Button({ onclick() }) {
                Text("Filtri")
            }
        }
        Spacer(Modifier.height(5.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(1),
            modifier = Modifier
                .weight(2.9f)
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(workSearchList, key = { offer -> offer.id }) { offer ->
                WorkOfferCompose(offer, navController)
            }
        }
    }
}

@Composable
fun WorkFilterPage(modifier: Modifier, filterData: WorkFilterData, onDismiss: () -> Unit) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        // --- Header: Titolo + Reset ---
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Filtri", fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Button(onClick = {
                filterData.ruoli.clear()
                filterData.tipiContratto.clear()
                filterData.orari.clear()
                filterData.distanzaMax = 100f
                filterData.ordinamento = "Nome"
                filterData.ordinamentoCrescente = true
            }) {
                Text("Reset")
            }
        }

        Spacer(Modifier.height(20.dp))

        // --- 1. SLIDER DISTANZA ---
        Text(
            "Distanza massima: ${filterData.distanzaMax.toInt()} km",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(4.dp))
        Slider(
            value = filterData.distanzaMax,
            onValueChange = { filterData.distanzaMax = it },
            valueRange = 5f..1000f,
            modifier = Modifier.fillMaxWidth()
        )
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("5 km", fontSize = 12.sp, color = Color.Gray)
            Text("1000 km", fontSize = 12.sp, color = Color.Gray)
        }

        Spacer(Modifier.height(24.dp))

        // --- 2. TIPO CONTRATTO + ORARIO affiancati ---
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Tipo Contratto
            Column(Modifier.weight(1f)) {
                Text(
                    "Contratto",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                TipoContratto.entries.forEach { tipo ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Checkbox(
                            checked = filterData.tipiContratto.contains(tipo),
                            onCheckedChange = {
                                if (filterData.tipiContratto.contains(tipo))
                                    filterData.tipiContratto.remove(tipo)
                                else
                                    filterData.tipiContratto.add(tipo)
                            }
                        )
                        Text(tipo.nome, fontSize = 14.sp)
                    }
                }
            }

            // Orario
            Column(Modifier.weight(1f)) {
                Text(
                    "Orario",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                OrarioLavoro.entries.forEach { orario ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Checkbox(
                            checked = filterData.orari.contains(orario),
                            onCheckedChange = {
                                if (filterData.orari.contains(orario))
                                    filterData.orari.remove(orario)
                                else
                                    filterData.orari.add(orario)
                            }
                        )
                        Text(orario.nome, fontSize = 14.sp)
                    }
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        // --- 3. RUOLI (griglia 2 colonne) ---
        Text(
            "Ruolo",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        // Dividiamo in due colonne manualmente con chunked
        Role.entries.chunked(2).forEach { pair ->
            Row(Modifier.fillMaxWidth()) {
                pair.forEach { role ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Checkbox(
                            checked = filterData.ruoli.contains(role),
                            onCheckedChange = {
                                if (filterData.ruoli.contains(role))
                                    filterData.ruoli.remove(role)
                                else
                                    filterData.ruoli.add(role)
                            }
                        )
                        Text(role.nome, fontSize = 13.sp)
                    }
                }
                // Se la riga ha solo 1 elemento, aggiungiamo un peso vuoto
                if (pair.size == 1) Spacer(Modifier.weight(1f))
            }
        }

        Spacer(Modifier.height(28.dp))

        // --- Tasto Applica ---
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Button(
                onClick = { onDismiss() },
                modifier = Modifier.height(50.dp),
                shape = RoundedCornerShape(25.dp)
            ) {
                Text("Applica filtri", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(Modifier.height(20.dp))
    }
}


@Preview(showBackground = true)
@Composable
fun WorkSearchPreview() {
    WorkSearchPageComplete(PaddingValues(0.dp), null)
}*/

package com.example.superspan

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController


@Composable
fun WorkSearchPageComplete(
    padding: PaddingValues,
    navController: NavController?
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

    Box(modifier = Modifier.padding(padding)) {
        if (!enabled) {
            WorkSearchPage(navController, filterData) {
                enabled = true
            }
        } else {
            WorkFilterPage(Modifier.fillMaxSize(), filterData) {
                enabled = false
            }
        }
    }
}

@Composable
fun WorkSearchPage(
    navController: NavController?,
    filterData: WorkFilterData,
    onOpenFilters: () -> Unit
) {
    val workSearchList by remember {
        derivedStateOf { searchWorkOffer(filterData) }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 1. HEADER (Scorre con la pagina)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
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

        // 2. BARRA DI RICERCA + TASTO FILTRI
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = filterData.nome,
                    onValueChange = { filterData.nome = it },
                    placeholder = { Text("Cerca ruolo o città...") },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    leadingIcon = { Icon(Icons.Default.Search, null) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFF7F7F7),
                        unfocusedContainerColor = Color(0xFFF7F7F7)
                    )
                )

                Spacer(Modifier.width(8.dp))

                FilledIconButton(
                    onClick = onOpenFilters,
                    modifier = Modifier.size(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = Color(0xFF388E3C)
                    )
                ) {
                    Icon(Icons.Default.Tune, contentDescription = "Filtri", tint = Color.White)
                }
            }
        }

        // 3. LISTA DELLE OFFERTE
        items(workSearchList, key = { it.id }) { offer ->
            WorkOfferCompose(offer, navController)
        }
    }
}



@Composable
fun WorkOfferCompose(workOffer: WorkOffer, navController: NavController?) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable { navController?.navigate("dettaglio_offerta/${workOffer.id}") },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F1F1))
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
                    text = workOffer.ruolo,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
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

            Text(
                text = workOffer.descrizioneBreve,
                fontSize = 14.sp,
                color = Color.Gray,
                maxLines = 2
            )

            // RIGA INFERIORE: Città • Contratto • Orario
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = Color.Gray
                )
                Text(
                    // AGGIUNTO: workOffer.orario.nome alla fine della stringa
                    text = " ${workOffer.citta} • ${workOffer.tipoContratto.nome} • ${workOffer.orario.nome}",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}
/*@Composable
fun WorkFilterPage(modifier: Modifier, filterData: WorkFilterData, onDismiss: () -> Unit) {
    val scrollState = rememberScrollState()

    // Stato locale per gestire l'opzione "Tutta Italia"
    var tuttaItalia by remember { mutableStateOf(filterData.distanzaMax >= 1000f) }

    Column(
        modifier = modifier
            .background(Color(0xFFF8F9FA)) // Grigio chiarissimo di sfondo per un look moderno
            .verticalScroll(scrollState)
    ) {
        // --- Header: Professionale e Pulito ---
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.White,
            shadowElevation = 2.dp
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
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
                    filterData.distanzaMax = 100f
                    tuttaItalia = false
                }) {
                    Text("Reset", color = Color.Red, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        Column(Modifier.padding(24.dp)) {

            // --- 1. SEZIONE DISTANZA (Moderna) ---
            Text("Dove vuoi lavorare?", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f))
            ) {
                Column(Modifier.padding(16.dp)) {
                    // Switch per Tutta Italia
                    Row(
                        Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Cerca in tutta Italia", fontWeight = FontWeight.Medium)
                        Switch(
                            checked = tuttaItalia,
                            onCheckedChange = {
                                tuttaItalia = it
                                if (it) filterData.distanzaMax = 1000f else filterData.distanzaMax = 50f
                            },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF388E3C))
                        )
                    }

                    Spacer(Modifier.height(16.dp))
                    HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray)
                    Spacer(Modifier.height(16.dp))

                    // Slider Distanza (disabilitato se Tutta Italia è attivo)
                    Text(
                        text = if (tuttaItalia) "Distanza: Senza limiti" else "Distanza entro: ${filterData.distanzaMax.toInt()} km",
                        color = if (tuttaItalia) Color.Gray else Color.Black,
                        fontWeight = FontWeight.SemiBold
                    )
                    Slider(
                        value = if (tuttaItalia) 100f else filterData.distanzaMax.coerceIn(5f, 100f),
                        onValueChange = { filterData.distanzaMax = it },
                        valueRange = 5f..100f,
                        enabled = !tuttaItalia,
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFF388E3C),
                            activeTrackColor = Color(0xFF388E3C),
                            inactiveTrackColor = Color.LightGray.copy(alpha = 0.3f)
                        )
                    )
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("5 km", fontSize = 12.sp, color = Color.Gray)
                        Text("100 km", fontSize = 12.sp, color = Color.Gray)
                    }
                }
            }

            Spacer(Modifier.height(32.dp))

            // --- 2. TIPO CONTRATTO + ORARIO ---
            Text("Tipologia di impiego", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(16.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                // Contratto
                Column(Modifier.weight(1f)) {
                    Text("Contratto", fontSize = 14.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                    TipoContratto.entries.forEach { tipo ->
                        FilterCheckboxRow(tipo.nome, filterData.tipiContratto.contains(tipo)) { isChecked ->
                            if (isChecked) filterData.tipiContratto.add(tipo) else filterData.tipiContratto.remove(tipo)
                        }
                    }
                }
                // Orario
                Column(Modifier.weight(1f)) {
                    Text("Orario", fontSize = 14.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                    OrarioLavoro.entries.forEach { orario ->
                        FilterCheckboxRow(orario.nome, filterData.orari.contains(orario)) { isChecked ->
                            if (isChecked) filterData.orari.add(orario) else filterData.orari.remove(orario)
                        }
                    }
                }
            }

            Spacer(Modifier.height(32.dp))

            // --- 3. RUOLI (Grid layout più pulito) ---
            Text("Ruoli di interesse", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))

            Role.entries.chunked(2).forEach { pair ->
                Row(Modifier.fillMaxWidth()) {
                    pair.forEach { role ->
                        Row(
                            Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = filterData.ruoli.contains(role),
                                onCheckedChange = { isChecked ->
                                    if (isChecked) filterData.ruoli.add(role) else filterData.ruoli.remove(role)
                                },
                                colors = CheckboxDefaults.colors(checkedColor = Color(0xFF388E3C))
                            )
                            Text(role.nome, fontSize = 14.sp)
                        }
                    }
                    if (pair.size == 1) Spacer(Modifier.weight(1f))
                }
            }

            Spacer(Modifier.height(48.dp))

            // --- Tasto Applica: Grande e rassicurante per Paolo ---
            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF388E3C))
            ) {
                Text("Applica filtri", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(32.dp))
        }
    }
}*/

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
                            colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF388E3C))
                        )
                    }

                    Spacer(Modifier.height(20.dp))
                    HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray.copy(alpha = 0.5f))
                    Spacer(Modifier.height(20.dp))

                    // Testo dinamico Distanza
                    Text(
                        text = if (tuttaItalia) "Distanza: Senza limiti" else "Entro ${filterData.distanzaMax.toInt()} km da te",
                        color = if (tuttaItalia) Color.Gray else Color(0xFF388E3C),
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
                                        if (tuttaItalia) Color.LightGray else Color(0xFF388E3C),
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
                                    activeTrackColor = Color(0xFF388E3C),
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
                                colors = CheckboxDefaults.colors(checkedColor = Color(0xFF388E3C))
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
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF388E3C))
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
            colors = CheckboxDefaults.colors(checkedColor = Color(0xFF388E3C))
        )
        Text(label, fontSize = 14.sp)
    }
}
