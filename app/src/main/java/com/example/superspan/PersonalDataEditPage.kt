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
            .background(Color.White)
    ) {
        // --- HEADER CON PARABOLA ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .clip(BottomOvalShape(30.dp))
                .background(Color.Gray)
        ) {
            IconButton(
                onClick = { navController?.popBackStack() },
                modifier = Modifier.padding(8.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Indietro", tint = Color.White)
            }
            Text(
                text = "Modifica Dati",
                color = Color.White,
                fontSize = 26.sp,
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
                    Text(cvName, color = if (cvName.contains("Seleziona")) Color.Gray else Color.Black, fontSize = 15.sp)
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
                color = Color.Red,
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
    var telefono by remember { mutableStateOf(actualUser.telefono ?: "") }
    var prefisso by remember { mutableStateOf("+39") }
    var cvName by remember { mutableStateOf(actualUser.cvFileName ?: "") }

    // Validazioni per la tranquillità di Paolo
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
            .background(Color.White)
    ) {
        // --- HEADER ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
                .clip(BottomOvalShape(25.dp))
                .background(Color.Gray)
        ) {
            IconButton(onClick = { navController?.popBackStack() }, modifier = Modifier.padding(8.dp)) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Indietro", tint = Color.White)
            }
            Column(Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Modifica Dati", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Text("I campi verdi sono completati", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 25.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(20.dp))

            // Campi Nome e Cognome
            EditTextField("Nome", nome, KeyboardType.Text) { nome = it }
            EditTextField("Cognome", cognome, KeyboardType.Text) { cognome = it }

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
                    onValueChange = { if (it.length <= 4) prefisso = it },
                    label = { Text("Prefisso") },
                    modifier = Modifier.width(90.dp).background(Color(0xFFE8F5E9), RoundedCornerShape(20.dp)),
                    shape = RoundedCornerShape(20.dp),
                    singleLine = true
                )
                Spacer(Modifier.width(8.dp))
                EditTextField(
                    label = "Telefono",
                    value = telefono,
                    keyboardType = KeyboardType.Phone,
                    visualTransformation = PhoneVisualTransformation(), // <--- APPLICA LA TRASFORMAZIONE
                    isError = !isPhoneValid,
                    errorMessage = "Solo numeri",
                    modifier = Modifier.weight(1f),
                    onValueChange = {
                        // Paolo può scrivere solo numeri e massimo 10 cifre
                        if (it.all { char -> char.isDigit() } && it.length <= 10) {
                            telefono = it
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

            Spacer(Modifier.height(30.dp))

            // TASTO SALVA (Attivo solo se i campi minimi sono pieni)
            Button(
                onClick = {
                    actualUser.nome = nome
                    actualUser.cognome = cognome
                    actualUser.emailLavoro = emailLavoro
                    actualUser.telefono = "$prefisso $telefono"
                    // cvFileName viene già aggiornato nel launcher
                    navController?.popBackStack()
                },
                enabled = nome.isNotEmpty() && cognome.isNotEmpty() && isEmailValid && isPhoneValid,
                modifier = Modifier.height(55.dp).width(220.dp),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF388E3C))
            ) {
                Text("Conferma e Salva", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(40.dp))
        }
    }
}

@Composable
fun EditTextField(
    label: String,
    value: String,
    keyboardType: KeyboardType,
    isError: Boolean = false,
    errorMessage: String = "",
    visualTransformation: VisualTransformation = VisualTransformation.None,
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit
) {
    // Logica colori per Paolo:
    // Errore -> Rosso chiarissimo
    // Vuoto -> Arancione chiarissimo (Attenzione)
    // Pieno -> Verde chiarissimo (Confermato)
    val containerColor = when {
        isError -> Color(0xFFFDECEA)
        value.isEmpty() -> Color(0xFFFFF3E0)
        else -> Color(0xFFE8F5E9)
    }

    val borderColor = when {
        isError -> Color.Red
        value.isEmpty() -> Color(0xFFFFB74D)
        else -> Color(0xFF81C784)
    }

    Column(modifier = modifier.padding(vertical = 4.dp)) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            visualTransformation = visualTransformation, // <--- USALO QUI
            modifier = Modifier.fillMaxWidth().background(containerColor, RoundedCornerShape(20.dp)),
            shape = RoundedCornerShape(20.dp),
            singleLine = true,
            isError = isError,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = borderColor,
                focusedBorderColor = borderColor,
                errorBorderColor = Color.Red
            )
        )
        if (isError) {
            Text(errorMessage, color = Color.Red, fontSize = 12.sp, modifier = Modifier.padding(start = 12.dp))
        }
    }
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