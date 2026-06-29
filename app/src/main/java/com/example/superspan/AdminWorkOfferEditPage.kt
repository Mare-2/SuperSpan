package com.example.superspan

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.foundation.shape.CircleShape
import androidx.activity.compose.BackHandler

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

    val hasUnsavedChanges = if (offer == null) {
        titolo.isNotEmpty() || ruoloEnum != null || descrizioneBreve.isNotEmpty() || descrizioneEstesa.isNotEmpty() || requisiti.isNotEmpty() || selectedSupermarket != null
    } else {
        titolo != offer.titolo || ruoloEnum != offer.ruoloEnum || descrizioneBreve != offer.descrizioneBreve || descrizioneEstesa != offer.descrizioneEstesa || requisiti != offer.requisiti || selectedSupermarket != offer.supermarket || tipoContratto != offer.tipoContratto || orario != offer.orario
    }

    var showBackConfirm by remember { mutableStateOf(false) }

    BackHandler(enabled = hasUnsavedChanges) {
        showBackConfirm = true
    }

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
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // --- HEADER ---
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 16.dp)
                ) {
                    if (offer != null) {
                        IconButton(
                            onClick = { showDeleteConfirm = true },
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .background(Color.White.copy(alpha = 0.7f), CircleShape)
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Elimina", tint = com.example.superspan.ui.theme.AppError)
                        }
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (offer == null) "Aggiungi Offerta" else "Modifica Offerta",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF1A1A1A)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // STEP 1: SEDE DI LAVORO
                    Text("Sede di Lavoro", fontWeight = FontWeight.Bold)
                    if (selectedSupermarket == null) {
                        OutlinedButton(
                            onClick = { isSelectionOpen = true },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            shape = CircleShape,
                            border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF81C784)),
                            colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(containerColor = Color.White)
                        ) {
                            Icon(Icons.Default.LocationOn, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Seleziona Supermercato")
                        }
                    } else {
                        selectedSupermarket?.let { s ->
                            Card(
                                modifier = Modifier.fillMaxWidth().clickable { isSelectionOpen = true },
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(2.dp)
                            ) {
                                Row(Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.LocationOn, null, tint = androidx.compose.material3.MaterialTheme.colorScheme.primary, modifier = Modifier.size(32.dp))
                                    Spacer(Modifier.width(12.dp))
                                    Column(Modifier.weight(1f)) {
                                        Text(s.nome, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = androidx.compose.material3.MaterialTheme.colorScheme.primary)
                                        Text("${s.indirizzo}, ${s.citta}", fontSize = 14.sp, color = androidx.compose.material3.MaterialTheme.colorScheme.primary)
                                    }
                                    Text("Modifica", color = androidx.compose.material3.MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
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
                            Surface(
                                selected = ruoloEnum == role,
                                onClick = { ruoloEnum = role },
                                shape = CircleShape,
                                color = if (ruoloEnum == role) com.example.superspan.ui.theme.LogoLeft else Color.White,
                                border = if (ruoloEnum == role) null else androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF81C784)),
                                modifier = Modifier.padding(2.dp)
                            ) {
                                Text(
                                    text = role.nome,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                                    fontWeight = FontWeight.Bold,
                                    color = if (ruoloEnum == role) Color.White else com.example.superspan.ui.theme.LogoLeft
                                )
                            }
                        }
                    }

                    // STEP 3: DETTAGLI
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Spacer(modifier = Modifier.height(8.dp))
                        EditTextField(value = titolo, onValueChange = { titolo = it }, label = "Titolo Offerta", modifier = Modifier.fillMaxWidth())

                        EditTextField(value = descrizioneBreve, onValueChange = { descrizioneBreve = it }, label = "Descrizione Breve", modifier = Modifier.fillMaxWidth())
                        EditTextField(value = descrizioneEstesa, onValueChange = { descrizioneEstesa = it }, label = "Descrizione Estesa", modifier = Modifier.fillMaxWidth(), minLines = 3)
                        EditTextField(value = requisiti, onValueChange = { requisiti = it }, label = "Requisiti", modifier = Modifier.fillMaxWidth(), minLines = 2)

                            Text("Tipo Contratto", fontWeight = FontWeight.Bold)
                            @OptIn(ExperimentalLayoutApi::class)
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                TipoContratto.entries.forEach { tipo ->
                                    Surface(
                                        selected = tipoContratto == tipo,
                                        onClick = { tipoContratto = tipo },
                                        shape = CircleShape,
                                        color = if (tipoContratto == tipo) com.example.superspan.ui.theme.LogoLeft else Color.White,
                                        border = if (tipoContratto == tipo) null else androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF81C784)),
                                        modifier = Modifier.padding(2.dp)
                                    ) {
                                        Text(
                                            text = tipo.nome,
                                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                                            fontWeight = FontWeight.Bold,
                                            color = if (tipoContratto == tipo) Color.White else com.example.superspan.ui.theme.LogoLeft
                                        )
                                    }
                                }
                            }

                            Text("Orario Lavoro", fontWeight = FontWeight.Bold)
                            @OptIn(ExperimentalLayoutApi::class)
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OrarioLavoro.entries.forEach { o ->
                                    Surface(
                                        selected = orario == o,
                                        onClick = { orario = o },
                                        shape = CircleShape,
                                        color = if (orario == o) com.example.superspan.ui.theme.LogoLeft else Color.White,
                                        border = if (orario == o) null else androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF81C784)),
                                        modifier = Modifier.padding(2.dp)
                                    ) {
                                        Text(
                                            text = o.nome,
                                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                                            fontWeight = FontWeight.Bold,
                                            color = if (orario == o) Color.White else com.example.superspan.ui.theme.LogoLeft
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            Button(
                                onClick = { showSaveConfirm = true },
                                modifier = Modifier.height(55.dp).width(220.dp).align(Alignment.CenterHorizontally),
                                shape = CircleShape,
                                colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = com.example.superspan.ui.theme.LogoLeft),
                                enabled = titolo.isNotBlank() && descrizioneBreve.isNotBlank() && selectedSupermarket != null && ruoloEnum != null
                            ) {
                                Text("Salva Offerta", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }
                            
                            Spacer(modifier = Modifier.height(100.dp))
                        }
                    }
                }

            // Floating Back Button
            IconButton(
                onClick = { 
                    if (hasUnsavedChanges) {
                        showBackConfirm = true
                    } else {
                        navController?.popBackStack() 
                    }
                },
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(top = 16.dp + paddingValues.calculateTopPadding(), start = 16.dp)
                    .background(Color.White.copy(alpha = 0.7f), CircleShape)
                    .size(48.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Indietro", tint = com.example.superspan.ui.theme.LogoLeft)
            }
                

            if (showSaveConfirm) {
                ModernAlertDialog(
                    onDismissRequest = { showSaveConfirm = false },
                    title = if (offer != null) "Conferma Modifica" else "Conferma Creazione",
                    text = if (offer != null) "Vuoi salvare le modifiche apportate all'offerta?" else "Vuoi salvare la nuova offerta di lavoro?",
                    icon = Icons.Default.Save,
                    confirmText = "Salva",
                    onConfirm = {
                        showSaveConfirm = false
                        val sp = selectedSupermarket ?: return@ModernAlertDialog
                        val enumRuolo = ruoloEnum ?: return@ModernAlertDialog
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
                    },
                    dismissText = "Annulla",
                    onDismiss = { showSaveConfirm = false }
                )
            }

            if (showDeleteConfirm) {
                ModernAlertDialog(
                    onDismissRequest = { showDeleteConfirm = false },
                    title = "Conferma Eliminazione",
                    text = "Sei sicuro di voler eliminare questa offerta di lavoro?",
                    icon = Icons.Default.Delete,
                    isDestructive = true,
                    confirmText = "Elimina",
                    onConfirm = {
                        showDeleteConfirm = false
                        WorkOfferSearchList.remove(offer)
                        Toast.makeText(context, "Eliminato con successo", Toast.LENGTH_SHORT).show()
                        navController?.popBackStack()
                    },
                    dismissText = "Annulla",
                    onDismiss = { showDeleteConfirm = false }
                )
            }

            if (showBackConfirm) {
                ModernAlertDialog(
                    onDismissRequest = { showBackConfirm = false },
                    title = "Attenzione",
                    text = "Hai delle modifiche non salvate. Vuoi uscire comunque senza salvare?",
                    icon = Icons.Default.Close,
                    isDestructive = true,
                    confirmText = "Esci",
                    onConfirm = {
                        showBackConfirm = false
                        navController?.popBackStack()
                    },
                    dismissText = "Annulla",
                    onDismiss = { showBackConfirm = false }
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
                    IconButton(onClick = onBack, modifier = androidx.compose.ui.Modifier.background(androidx.compose.ui.graphics.Color.White.copy(alpha = 0.7f), androidx.compose.foundation.shape.CircleShape)) { Icon(androidx.compose.material.icons.Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Indietro", tint = com.example.superspan.ui.theme.LogoLeft)
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
                leadingIcon = {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = null,
                        tint = Color.Gray
                    )
                },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = { query = "" }) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Cancella",
                                tint = Color.Gray
                            )
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
                                    .background(
                                        androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant,
                                        shape = RoundedCornerShape(12.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = androidx.compose.material3.MaterialTheme.colorScheme.tertiary
                                )
                            }
                            Spacer(Modifier.width(16.dp))
                            Column {
                                Text(
                                    s.nome,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = Color(0xFF1A1A1A)
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    "${s.indirizzo}, ${s.citta}",
                                    fontSize = 14.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
