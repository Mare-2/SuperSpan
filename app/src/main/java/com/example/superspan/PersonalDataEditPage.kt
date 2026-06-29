/*package com.example.superspan

import android.net.Uri
import android.util.Patterns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun PersonalDataEditPage(navController: NavController?, padding: PaddingValues) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // Stati locali
    var nome by remember { mutableStateOf(actualUser.nome) }
    var cognome by remember { mutableStateOf(actualUser.cognome) }
    var emailLavoro by remember { mutableStateOf(actualUser.emailLavoro ?: "") }
    var telefono by remember { mutableStateOf(actualUser.telefono ?: "") }
    var prefisso by remember { mutableStateOf("+39") } // Prefisso predefinito
    var cvName by remember { mutableStateOf(actualUser.cvFileName ?: "Seleziona file PDF") }

    // Funzioni di validazione
    val isEmailValid = Patterns.EMAIL_ADDRESS.matcher(emailLavoro).matches() || emailLavoro.isEmpty()
    val isPhoneValid = telefono.all { it.isDigit() } && (telefono.length in 8..11 || telefono.isEmpty())

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { selectedUri ->
            val destinationName = "cv_${actualUser.email.replace("@", "_")}.pdf"
            val savedPath = saveFileToInternalStorage(context, selectedUri, destinationName)
            if (savedPath != null) {
                cvName = destinationName
                actualUser.cvFileName = savedPath
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
    ) {
        // --- HEADER ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp)
        ) {
            Text(
                text = "Modifica Dati",
                color = Color.Black,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        // --- FORM ---
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 30.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(24.dp))

            EditTextField("Nome", nome, KeyboardType.Text) { nome = it }
            EditTextField("Cognome", cognome, KeyboardType.Text) { cognome = it }

            // Campo Email con validazione
            EditTextField(
                label = "Email di contatto",
                value = emailLavoro,
                keyboardType = KeyboardType.Email,
                isError = !isEmailValid,
                errorMessage = "Formato email non valido",
                onValueChange = { emailLavoro = it }
            )

            // --- SEZIONE TELEFONO CON PREFISSO ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.Top // Allineato in alto per gestire l'errore sotto
            ) {
                // Prefisso (piccolo)
                OutlinedTextField(
                    value = prefisso,
                    onValueChange = { if (it.length <= 4) prefisso = it },
                    label = { Text("Prefisso") },
                    modifier = Modifier.width(85.dp),
                    shape = RoundedCornerShape(20.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                )

                Spacer(Modifier.width(8.dp))

                // Numero di telefono
                EditTextField(
                    label = "Telefono",
                    value = telefono,
                    keyboardType = KeyboardType.Phone,
                    isError = !isPhoneValid,
                    errorMessage = "Solo numeri (8-11 cifre)",
                    modifier = Modifier.weight(1f),
                    onValueChange = { if (it.length <= 11) telefono = it }
                )
            }

            Spacer(Modifier.height(16.dp))

            // SEZIONE CARICAMENTO CV
            Text("Curriculum Vitae", Modifier.fillMaxWidth(), fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.Gray)
            Spacer(Modifier.height(8.dp))
            Surface(
                onClick = { launcher.launch("application/pdf") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFF1F1F1)
            ) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.FileUpload, null, tint = Color.DarkGray)
                    Spacer(Modifier.width(12.dp))
                    Text(cvName.ifEmpty { "Seleziona file PDF" }, color = if (cvName.contains("Seleziona")) Color.Gray else Color.Black, fontSize = 15.sp)
                }
            }

            Spacer(Modifier.height(40.dp))

            // --- TASTO SALVA ---
            Button(
                onClick = {
                    actualUser.nome = nome
                    actualUser.cognome = cognome
                    actualUser.emailLavoro = emailLavoro
                    actualUser.telefono = "$prefisso $telefono"
                    navController?.popBackStack()
                },
                // Il tasto è attivo solo se i dati sono validi
                enabled = isEmailValid && isPhoneValid && nome.isNotEmpty() && cognome.isNotEmpty(),
                modifier = Modifier.height(55.dp).width(200.dp),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF388E3C))
            ) {
                Text("Salva tutto", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(40.dp))
        }
    }
}

@Composable
fun EditTextField(
    label: String,
    value: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    isError: Boolean = false,
    errorMessage: String = "",
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            singleLine = true,
            isError = isError,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType)
        )
        if (isError) {
            Text(
                text = errorMessage,
                color = com.example.superspan.ui.theme.AppError,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 12.dp, top = 4.dp)
            )
        }
    }
}*/

package com.example.superspan

import android.net.Uri
import android.util.Patterns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.AnnotatedString


@Composable
fun PersonalDataEditPage(navController: NavController?, padding: PaddingValues) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // Stati locali recuperati dall'utente attuale
    var nome by remember { mutableStateOf(actualUser.nome) }
    var cognome by remember { mutableStateOf(actualUser.cognome) }
    var emailLavoro by remember { mutableStateOf(actualUser.emailLavoro ?: "") }
    var prefisso by remember { mutableStateOf("+39") }
    var cvName by remember { mutableStateOf(actualUser.cvFileName?.substringAfterLast('/') ?: "") }
    var telefonoDigits by remember {
        mutableStateOf(
            actualUser.telefono
                ?.filter { it.isDigit() } // Prende solo i numeri: 393331234567
                ?.removePrefix("39")      // Toglie il 39: 3331234567
                ?: ""
        )
    }

    // Validazioni per la tranquillità di Paolo
    val isEmailValid = Patterns.EMAIL_ADDRESS.matcher(emailLavoro).matches() || emailLavoro.isEmpty()
    val isPhoneValid = telefonoDigits.all { it.isDigit() } && (telefonoDigits.length in 9..10 || telefonoDigits.isEmpty())

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { selectedUri ->
            val destinationName = "cv_${actualUser.email.replace("@", "_")}.pdf"
            val savedPath = saveFileToInternalStorage(context, selectedUri, destinationName)
            if (savedPath != null) {
                cvName = destinationName
                actualUser.cvFileName = destinationName
                actualUser.cvPath = savedPath
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // --- HEADER (Senza rettangolo bianco, testo scuro) ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 80.dp, bottom = 16.dp)
            ) {
                Column(Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Modifica Dati", color = Color.Black, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            }
        }

            // --- HEADER ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Modifica Dati",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF1A1A1A)
                )
                Text(
                    text = "I campi verdi sono completati",
                    fontSize = 16.sp,
                    color = Color.Gray
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(8.dp))

                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Campi Nome e Cognome
                        EditTextField("Nome", nome, KeyboardType.Text) { nome = it }
                        Spacer(Modifier.height(16.dp))
                        EditTextField("Cognome", cognome, KeyboardType.Text) { cognome = it }
                        Spacer(Modifier.height(16.dp))

                        // Email con validazione
                        EditTextField(
                            label = "Email di contatto",
                            value = emailLavoro,
                            keyboardType = KeyboardType.Email,
                            isError = !isEmailValid,
                            errorMessage = "Inserisci una mail valida",
                            onValueChange = { emailLavoro = it }
                        )

                        // Telefono e Prefisso
                        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                            OutlinedTextField(
                                value = prefisso,
                                onValueChange = {}, // Lo lasciamo fisso per ora
                                readOnly = true,
                                label = { Text("Pre") },
                                modifier = Modifier.width(80.dp),
                                shape = RoundedCornerShape(20.dp)
                            )

                            Spacer(Modifier.width(8.dp))

                            EditTextField(
                                label = "Telefono",
                                value = telefonoDigits, // USIAMO SOLO LE CIFRE
                                keyboardType = KeyboardType.Phone,
                                visualTransformation = PhoneVisualTransformation(), // Lo spazio è solo un trucco visivo
                                isError = !isPhoneValid,
                                errorMessage = "Inserisci un numero valido",
                                modifier = Modifier.weight(1f),
                                onValueChange = { input ->
                                    // Accettiamo solo se l'utente scrive numeri e non superiamo i 10
                                    if (input.all { it.isDigit() } && input.length <= 10) {
                                        telefonoDigits = input
                                    }
                                }
                            )
                        }

                        Spacer(Modifier.height(16.dp))

                        // --- SEZIONE CV (Con conferma visiva per Paolo) ---
                        Text("Curriculum Vitae", Modifier.fillMaxWidth(), fontWeight = FontWeight.Bold, color = Color.Gray, fontSize = 14.sp)

                        val cvBoxColor = if (cvName.isNotEmpty()) Color(0xFFE8F5E9) else Color(0xFFFFF3E0)
                        val cvBorderColor = if (cvName.isNotEmpty()) Color(0xFF81C784) else Color(0xFFFFB74D)

                        Surface(
                            onClick = { launcher.launch("application/pdf") },
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                            shape = RoundedCornerShape(16.dp),
                            color = cvBoxColor,
                            border = androidx.compose.foundation.BorderStroke(1.dp, cvBorderColor)
                        ) {
                            Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    if (cvName.isNotEmpty()) Icons.Default.CheckCircle else Icons.Default.FileUpload,
                                    null,
                                    tint = if (cvName.isNotEmpty()) Color(0xFF388E3C) else Color.Gray
                                )
                                Spacer(Modifier.width(12.dp))
                                Text(
                                    text = if (cvName.isEmpty()) "Carica il tuo CV (PDF)" else cvName,
                                    color = if (cvName.isEmpty()) Color.Gray else Color.Black,
                                    fontSize = 15.sp,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(30.dp))

            // TASTO SALVA (Attivo solo se i campi minimi sono pieni)
            Button(
                onClick = {
                    actualUser.nome = nome
                    actualUser.cognome = cognome
                    actualUser.emailLavoro = emailLavoro
                    actualUser.telefono = "$prefisso$telefonoDigits"
                    // cvFileName viene già aggiornato nel launcher
                    navController?.popBackStack()
                },
                enabled = nome.isNotEmpty() && cognome.isNotEmpty() && isEmailValid && isPhoneValid && telefonoDigits.isNotEmpty(),
                modifier = Modifier.height(55.dp),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(containerColor = com.example.superspan.ui.theme.LogoLeft)
            ) {
                Text("Conferma e Salva", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(padding.calculateBottomPadding() + 24.dp))
        }
        } // End Column

        // Floating Back Button
        IconButton(
            onClick = { navController?.popBackStack() },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 16.dp + padding.calculateTopPadding(), start = 16.dp)
                .background(Color.White.copy(alpha = 0.7f), CircleShape)
                .size(48.dp)
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Indietro", tint = com.example.superspan.ui.theme.LogoLeft)
        }
    } // End Box
}




class PhoneVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val trimmed = if (text.text.length >= 10) text.text.substring(0..9) else text.text
        var out = ""
        for (i in trimmed.indices) {
            out += trimmed[i]
            if (i == 2 || i == 5) out += " " // Aggiunge lo spazio dopo la 3° e la 6° cifra
        }

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (offset <= 2) return offset
                if (offset <= 5) return offset + 1
                if (offset <= 10) return offset + 2
                return 12
            }

            override fun transformedToOriginal(offset: Int): Int {
                if (offset <= 2) return offset
                if (offset <= 6) return offset - 1
                if (offset <= 12) return offset - 2
                return 10
            }
        }

        return TransformedText(AnnotatedString(out), offsetMapping)
    }
}