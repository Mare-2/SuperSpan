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
        AuraBackground(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = paddingValues.calculateTopPadding())
                    .verticalScroll(rememberScrollState())
            ) {
                // --- HEADER ---
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 80.dp, bottom = 16.dp)
                ) {
                    Column(Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (offer == null) "Aggiungi Offerta" else "Modifica Offerta",
                            color = Color.Black,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (offer != null) {
                        IconButton(
                            onClick = { showDeleteConfirm = true },
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .padding(end = 16.dp)
                                .background(Color.White.copy(alpha = 0.7f), CircleShape)
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Elimina", tint = com.example.superspan.ui.theme.AppError)
                        }
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (offer == null) "Aggiungi Offerta di lavoro" else "Modifica Offerta di lavoro",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF1A1A1A)
                    )
                }

                    // STEP 1: SEDE DI LAVORO
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text("Sede di Lavoro", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF1A1A1A))
                            if (selectedSupermarket == null) {
                                OutlinedButton(
                                    onClick = { isSelectionOpen = true },
                                    modifier = Modifier.fillMaxWidth().height(56.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    border = androidx.compose.foundation.BorderStroke(2.dp, com.example.superspan.ui.theme.LogoLeft.copy(alpha = 0.5f)),
                                    colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(containerColor = Color.White)
                                ) {
                                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = com.example.superspan.ui.theme.LogoLeft)
                                    Spacer(Modifier.width(8.dp))
                                    Text("Seleziona Supermercato", color = com.example.superspan.ui.theme.LogoLeft)
                                }
                            } else {
                                selectedSupermarket?.let { s ->
                                    Card(
                                        modifier = Modifier.fillMaxWidth().clickable { isSelectionOpen = true },
                                        shape = RoundedCornerShape(16.dp),
                                        colors = CardDefaults.cardColors(containerColor = com.example.superspan.ui.theme.LogoLeft.copy(alpha = 0.05f)),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, com.example.superspan.ui.theme.LogoLeft.copy(alpha = 0.2f)),
                                        elevation = CardDefaults.cardElevation(0.dp)
                                    ) {
                                        Row(Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.LocationOn, null, tint = com.example.superspan.ui.theme.LogoLeft, modifier = Modifier.size(32.dp))
                                            Spacer(Modifier.width(12.dp))
                                            Column(Modifier.weight(1f)) {
                                                Text(s.nome, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = com.example.superspan.ui.theme.LogoLeft)
                                                Text("${s.indirizzo}, ${s.citta}", fontSize = 14.sp, color = Color.DarkGray)
                                            }
                                            Text("Modifica", color = com.example.superspan.ui.theme.LogoLeft, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Spacer(modifier = Modifier.height(8.dp))

                    // STEP 2: CATEGORIA / RUOLO
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text("Ruolo (Categoria)", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF1A1A1A))
                            @OptIn(ExperimentalLayoutApi::class)
                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Role.entries.forEach { role ->
                                    val isSelected = ruoloEnum == role
                                    Surface(
                                        selected = isSelected,
                                        onClick = { ruoloEnum = role },
                                        shape = RoundedCornerShape(12.dp),
                                        color = if (isSelected) com.example.superspan.ui.theme.LogoLeft else com.example.superspan.ui.theme.LogoLeft.copy(alpha = 0.08f),
                                        modifier = Modifier.padding(2.dp)
                                    ) {
                                        Text(
                                            text = role.nome,
                                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                                            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.SemiBold,
                                            fontSize = 14.sp,
                                            color = if (isSelected) Color.White else com.example.superspan.ui.theme.LogoLeft
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // STEP 3: DETTAGLI
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text("Dettagli dell'Offerta", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF1A1A1A))
                            
                            EditTextField(value = titolo, onValueChange = { titolo = it }, label = "Titolo Offerta", modifier = Modifier.fillMaxWidth())
                            EditTextField(value = descrizioneBreve, onValueChange = { descrizioneBreve = it }, label = "Descrizione Breve", modifier = Modifier.fillMaxWidth())
                            EditTextField(value = descrizioneEstesa, onValueChange = { descrizioneEstesa = it }, label = "Descrizione Estesa", modifier = Modifier.fillMaxWidth(), minLines = 3)
                            EditTextField(value = requisiti, onValueChange = { requisiti = it }, label = "Requisiti", modifier = Modifier.fillMaxWidth(), minLines = 2)

                            Spacer(Modifier.height(4.dp))
                            
                            Text("Tipo Contratto", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF1A1A1A))
                            @OptIn(ExperimentalLayoutApi::class)
                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                TipoContratto.entries.forEach { tipo ->
                                    val isSelected = tipoContratto == tipo
                                    Surface(
                                        selected = isSelected,
                                        onClick = { tipoContratto = tipo },
                                        shape = RoundedCornerShape(12.dp),
                                        color = if (isSelected) com.example.superspan.ui.theme.LogoLeft else com.example.superspan.ui.theme.LogoLeft.copy(alpha = 0.08f),
                                        modifier = Modifier.padding(2.dp)
                                    ) {
                                        Text(
                                            text = tipo.nome,
                                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                                            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.SemiBold,
                                            fontSize = 14.sp,
                                            color = if (isSelected) Color.White else com.example.superspan.ui.theme.LogoLeft
                                        )
                                    }
                                }
                            }

                            Spacer(Modifier.height(4.dp))

                            Text("Orario Lavoro", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF1A1A1A))
                            @OptIn(ExperimentalLayoutApi::class)
                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OrarioLavoro.entries.forEach { o ->
                                    val isSelected = orario == o
                                    Surface(
                                        selected = isSelected,
                                        onClick = { orario = o },
                                        shape = RoundedCornerShape(12.dp),
                                        color = if (isSelected) com.example.superspan.ui.theme.LogoLeft else com.example.superspan.ui.theme.LogoLeft.copy(alpha = 0.08f),
                                        modifier = Modifier.padding(2.dp)
                                    ) {
                                        Text(
                                            text = o.nome,
                                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                                            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.SemiBold,
                                            fontSize = 14.sp,
                                            color = if (isSelected) Color.White else com.example.superspan.ui.theme.LogoLeft
                                        )
                                    }
                                }
                            }
                        }
                    } // closes inner column of step 3
                Spacer(modifier = Modifier.height(30.dp))

                Button(
                    onClick = { showSaveConfirm = true },
                    modifier = Modifier.height(55.dp),
                    shape = CircleShape,
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = com.example.superspan.ui.theme.LogoLeft),
                    enabled = titolo.isNotBlank() && descrizioneBreve.isNotBlank() && selectedSupermarket != null && ruoloEnum != null
                ) {
                    Text("Salva Offerta", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
                
                Spacer(modifier = Modifier.height(paddingValues.calculateBottomPadding() + 24.dp))
            } // closes form wrapper column
        } // closes very outer column

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
