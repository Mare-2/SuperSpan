package com.example.superspan

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
}
