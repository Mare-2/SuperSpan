package com.example.superspan

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminWorkOfferEditPage(
    offer: WorkOffer? = null,
    navController: NavController?,
    paddingValues: PaddingValues
) {
    val context = LocalContext.current
    var isSelectionOpen by remember { mutableStateOf(false) }
    
    var showSaveConfirm by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    
    var titolo by remember { mutableStateOf(offer?.titolo ?: "") }
    var ruoloEnum by remember { mutableStateOf<Role?>(offer?.ruoloEnum) }
    var descrizioneBreve by remember { mutableStateOf(offer?.descrizioneBreve ?: "") }
    var descrizioneEstesa by remember { mutableStateOf(offer?.descrizioneEstesa ?: "") }
    var requisiti by remember { mutableStateOf(offer?.requisiti ?: "") }
    var selectedSupermarket by remember { mutableStateOf<Supermarket?>(offer?.supermarket) }
    var tipoContratto by remember { mutableStateOf(offer?.tipoContratto ?: TipoContratto.DETERMINATO) }
    var orario by remember { mutableStateOf(offer?.orario ?: OrarioLavoro.FULL_TIME) }

    if (isSelectionOpen) {
        SupermarketSelectionScreen(
            onBack = { isSelectionOpen = false },
            onSelected = {
                selectedSupermarket = it
                isSelectionOpen = false
            }
        )
    } else {
        Box(modifier = Modifier.fillMaxSize()) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text(if (offer == null) "Aggiungi Offerta Lavoro" else "Modifica Offerta") },
                        navigationIcon = {
                            IconButton(onClick = { navController?.popBackStack() }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Indietro")
                            }
                        },
                        actions = {
                            if (offer != null) {
                                IconButton(onClick = { showDeleteConfirm = true }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Elimina", tint = Color.Red)
                                }
                            }
                        }
                    )
                },
                modifier = Modifier.padding(paddingValues)
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // STEP 1: SEDE DI LAVORO
                    Text("Sede di Lavoro", fontWeight = FontWeight.Bold)
                    if (selectedSupermarket == null) {
                        OutlinedButton(
                            onClick = { isSelectionOpen = true },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.LocationOn, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Seleziona Supermercato")
                        }
                    } else {
                        selectedSupermarket?.let { s ->
                            Card(
                                modifier = Modifier.fillMaxWidth().clickable { isSelectionOpen = true },
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                                elevation = CardDefaults.cardElevation(2.dp)
                            ) {
                                Row(Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.LocationOn, null, tint = Color(0xFF388E3C), modifier = Modifier.size(32.dp))
                                    Spacer(Modifier.width(12.dp))
                                    Column(Modifier.weight(1f)) {
                                        Text(s.nome, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF1B5E20))
                                        Text("${s.indirizzo}, ${s.citta}", fontSize = 14.sp, color = Color(0xFF2E7D32))
                                    }
                                    Text("Modifica", color = Color(0xFF388E3C), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // STEP 2: CATEGORIA / RUOLO
                    Text("Ruolo (Categoria)", fontWeight = FontWeight.Bold)
                    @OptIn(ExperimentalLayoutApi::class)
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Role.entries.forEach { role ->
                            FilterChip(
                                selected = ruoloEnum == role,
                                onClick = { ruoloEnum = role },
                                label = { Text(role.nome) }
                            )
                        }
                    }

                    // STEP 3: DETTAGLI
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(value = titolo, onValueChange = { titolo = it }, label = { Text("Titolo Offerta") }, modifier = Modifier.fillMaxWidth())

                        OutlinedTextField(value = descrizioneBreve, onValueChange = { descrizioneBreve = it }, label = { Text("Descrizione Breve") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = descrizioneEstesa, onValueChange = { descrizioneEstesa = it }, label = { Text("Descrizione Estesa") }, modifier = Modifier.fillMaxWidth(), minLines = 3)
                        OutlinedTextField(value = requisiti, onValueChange = { requisiti = it }, label = { Text("Requisiti") }, modifier = Modifier.fillMaxWidth(), minLines = 2)

                            Text("Tipo Contratto", fontWeight = FontWeight.Bold)
                            @OptIn(ExperimentalLayoutApi::class)
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                TipoContratto.entries.forEach { tipo ->
                                    FilterChip(
                                        selected = tipoContratto == tipo,
                                        onClick = { tipoContratto = tipo },
                                        label = { Text(tipo.nome) }
                                    )
                                }
                            }

                            Text("Orario Lavoro", fontWeight = FontWeight.Bold)
                            @OptIn(ExperimentalLayoutApi::class)
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OrarioLavoro.entries.forEach { o ->
                                    FilterChip(
                                        selected = orario == o,
                                        onClick = { orario = o },
                                        label = { Text(o.nome) }
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            Button(
                                onClick = { showSaveConfirm = true },
                                modifier = Modifier.fillMaxWidth().height(56.dp),
                                shape = RoundedCornerShape(12.dp),
                                enabled = titolo.isNotBlank() && descrizioneBreve.isNotBlank() && selectedSupermarket != null && ruoloEnum != null
                            ) {
                                Icon(Icons.Default.Save, contentDescription = null)
                                Spacer(Modifier.width(8.dp))
                                Text("Salva Offerta")
                            }
                            
                            Spacer(modifier = Modifier.height(100.dp))
                        }
                    }
                }
                
            if (showSaveConfirm) {
                AlertDialog(
                    onDismissRequest = { showSaveConfirm = false },
                    title = { Text(if (offer != null) "Conferma Modifica" else "Conferma Creazione") },
                    text = { Text(if (offer != null) "Vuoi salvare le modifiche apportate all'offerta?" else "Vuoi salvare la nuova offerta di lavoro?") },
                    confirmButton = {
                        TextButton(onClick = {
                            showSaveConfirm = false
                            val sp = selectedSupermarket ?: return@TextButton
                            val enumRuolo = ruoloEnum ?: return@TextButton
                            val dist = calcolaDistanzaSimulata(sp.citta)
                            val newOffer = WorkOffer(
                                id = offer?.id ?: ((WorkOfferSearchList.maxOfOrNull { it.id } ?: 0) + 1),
                                titolo = titolo,
                                ruoloEnum = enumRuolo,
                                descrizioneBreve = descrizioneBreve,
                                descrizioneEstesa = descrizioneEstesa,
                                requisiti = requisiti,
                                supermarket = sp,
                                tipoContratto = tipoContratto,
                                orario = orario,
                                distanzaKm = dist
                            )

                            if (offer != null) {
                                val index = WorkOfferSearchList.indexOf(offer)
                                if (index != -1) {
                                    WorkOfferSearchList[index] = newOffer
                                }
                            } else {
                                WorkOfferSearchList.add(0, newOffer) // Aggiungiamo in cima per comodità
                            }
                            highlightedWorkOfferId = newOffer.id
                            Toast.makeText(context, "Salvato con successo", Toast.LENGTH_SHORT).show()
                            navController?.popBackStack()
                        }) {
                            Text("Salva", color = Color(0xFF388E3C))
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showSaveConfirm = false }) { Text("Annulla", color = Color.Gray) }
                    }
                )
            }

            if (showDeleteConfirm) {
                AlertDialog(
                    onDismissRequest = { showDeleteConfirm = false },
                    title = { Text("Conferma Eliminazione") },
                    text = { Text("Sei sicuro di voler eliminare questa offerta di lavoro?") },
                    confirmButton = {
                        TextButton(onClick = {
                            showDeleteConfirm = false
                            WorkOfferSearchList.remove(offer)
                            Toast.makeText(context, "Eliminato con successo", Toast.LENGTH_SHORT).show()
                            navController?.popBackStack()
                        }) {
                            Text("Elimina", color = Color.Red)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDeleteConfirm = false }) { Text("Annulla", color = Color.Gray) }
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupermarketSelectionScreen(
    onBack: () -> Unit,
    onSelected: (Supermarket) -> Unit
) {
    var query by remember { mutableStateOf("") }

    val filteredList = remember(query) {
        if (query.isBlank()) ListOfSupermarkets
        else ListOfSupermarkets.filter {
            it.nome.contains(query, ignoreCase = true) || 
            it.citta.contains(query, ignoreCase = true) ||
            it.indirizzo.contains(query, ignoreCase = true)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Scegli la Sede", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Indietro")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF8F9FA)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            TextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Cerca per città, via o nome...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = { query = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Cancella", tint = Color.Gray)
                        }
                    }
                },
                shape = RoundedCornerShape(24.dp),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                ),
                singleLine = true
            )

            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredList) { s ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelected(s) },
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(Color(0xFFE3F2FD), shape = RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFF1976D2))
                            }
                            Spacer(Modifier.width(16.dp))
                            Column {
                                Text(s.nome, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF1A1A1A))
                                Spacer(Modifier.height(4.dp))
                                Text("${s.indirizzo}, ${s.citta}", fontSize = 14.sp, color = Color.Gray)
                            }
                        }
                    }
                }
            }
        }
    }
}
