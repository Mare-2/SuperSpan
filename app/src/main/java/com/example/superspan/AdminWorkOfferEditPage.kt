package com.example.superspan

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminWorkOfferEditPage(
    offer: WorkOffer? = null,
    navController: NavController?,
    paddingValues: PaddingValues
) {
    var ruolo by remember { mutableStateOf(offer?.ruolo ?: "") }
    var ruoloEnum by remember { mutableStateOf(offer?.ruoloEnum ?: Role.ADDETTO_VENDITE) }
    var descrizioneBreve by remember { mutableStateOf(offer?.descrizioneBreve ?: "") }
    var descrizioneEstesa by remember { mutableStateOf(offer?.descrizioneEstesa ?: "") }
    var requisiti by remember { mutableStateOf(offer?.requisiti ?: "") }
    var citta by remember { mutableStateOf(offer?.citta ?: "") }
    var indirizzo by remember { mutableStateOf(offer?.indirizzo ?: "") }
    var tipoContratto by remember { mutableStateOf(offer?.tipoContratto ?: TipoContratto.DETERMINATO) }
    var orario by remember { mutableStateOf(offer?.orario ?: OrarioLavoro.FULL_TIME) }
    var distanzaKm by remember { mutableStateOf(offer?.distanzaKm?.toString() ?: "50") }

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
                        IconButton(onClick = {
                            WorkOfferSearchList.remove(offer)
                            navController?.popBackStack()
                        }) {
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
            OutlinedTextField(value = ruolo, onValueChange = { ruolo = it }, label = { Text("Ruolo (Titolo)") }, modifier = Modifier.fillMaxWidth())
            
            Text("Ruolo (Categoria)")
            Role.entries.forEach { role ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    RadioButton(selected = ruoloEnum == role, onClick = { ruoloEnum = role })
                    Text(role.nome, modifier = Modifier.padding(start = 8.dp))
                }
            }

            OutlinedTextField(value = descrizioneBreve, onValueChange = { descrizioneBreve = it }, label = { Text("Descrizione Breve") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = descrizioneEstesa, onValueChange = { descrizioneEstesa = it }, label = { Text("Descrizione Estesa") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = requisiti, onValueChange = { requisiti = it }, label = { Text("Requisiti") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = citta, onValueChange = { citta = it }, label = { Text("Città") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = indirizzo, onValueChange = { indirizzo = it }, label = { Text("Indirizzo") }, modifier = Modifier.fillMaxWidth())
            
            Text("Tipo Contratto")
            TipoContratto.entries.forEach { tipo ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    RadioButton(selected = tipoContratto == tipo, onClick = { tipoContratto = tipo })
                    Text(tipo.nome, modifier = Modifier.padding(start = 8.dp))
                }
            }

            Text("Orario Lavoro")
            OrarioLavoro.entries.forEach { o ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    RadioButton(selected = orario == o, onClick = { orario = o })
                    Text(o.nome, modifier = Modifier.padding(start = 8.dp))
                }
            }

            OutlinedTextField(value = distanzaKm, onValueChange = { distanzaKm = it }, label = { Text("Distanza (Km)") }, modifier = Modifier.fillMaxWidth())

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val dist = distanzaKm.toIntOrNull() ?: 50
                    val newOffer = WorkOffer(
                        id = offer?.id ?: ((WorkOfferSearchList.maxOfOrNull { it.id } ?: 0) + 1),
                        ruolo = ruolo,
                        ruoloEnum = ruoloEnum,
                        descrizioneBreve = descrizioneBreve,
                        descrizioneEstesa = descrizioneEstesa,
                        requisiti = requisiti,
                        citta = citta,
                        indirizzo = indirizzo,
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
                        WorkOfferSearchList.add(newOffer)
                    }
                    navController?.popBackStack()
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Save, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Salva Offerta")
            }
        }
    }
}

// Extension to get all entries of Role since it's likely an enum or similar
// If Role is not an enum, I might need to adjust this. 
// Based on find_declaration it looked like an enum-like object with fields.
// Let me check Role definition again.
