/*package com.example.superspan

import android.net.Uri
import android.util.Patterns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay

// --- HEADER COMUNE (Più compatto per lasciare spazio alla navbar) ---
@Composable
fun ApplyHeader(step: String, title: String, onBack: () -> Unit) {
    Column(
        Modifier.fillMaxWidth().background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp)) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
            }
        }
        Box(
            Modifier.fillMaxWidth().height(80.dp).clip(TopOvalShape(20.dp)).background(Color.LightGray),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Candidatura: Step $step di 3", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF388E3C))
                Text(title, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
            }
        }
    }
}

// --- STATO TEMPORANEO PER LA CANDIDATURA (Per non sporcare actualUser) ---
data class CandidacyDraft(
    var nome: String = "",
    var cognome: String = "",
    var emailLavoro: String = "",
    var telefono: String = "",
    var cvFileName: String = ""
)

var currentDraft by mutableStateOf(CandidacyDraft())
var isReturnToSummary by mutableStateOf(false)

// --- STEP 1: DATI E CV ---
@Composable
fun ApplyStep1(navController: NavController?, padding: PaddingValues) {
    // Inizializziamo i campi con i dati del profilo SOLO la prima volta
    var nome by remember { mutableStateOf(currentDraft.nome.ifEmpty { actualUser.nome }) }
    var cognome by remember { mutableStateOf(currentDraft.cognome.ifEmpty { actualUser.cognome }) }
    var emailLavoro by remember { mutableStateOf(currentDraft.emailLavoro.ifEmpty { actualUser.emailLavoro ?: "" }) }
    var telefonoCompleto = currentDraft.telefono.ifEmpty { actualUser.telefono ?: "" }
    var telefonoInput by remember {
        mutableStateOf(
            if (currentDraft.telefono.isEmpty())
                formatPhone(actualUser.telefono?.replace("+39 ", "") ?: "")
            else
                currentDraft.telefono.replace("+39 ", "")
        )
    }

    // Gestione telefono separata per la UI
    var prefisso by remember { mutableStateOf("+39") }
    var telefono by remember { mutableStateOf(telefonoCompleto.replace("+39 ", "")) }
    var cvName by remember { mutableStateOf(currentDraft.cvFileName.ifEmpty { actualUser.cvFileName ?: "" }) }

    var showResetDialog by remember { mutableStateOf(false) }

    val isEmailValid = Patterns.EMAIL_ADDRESS.matcher(emailLavoro).matches() || emailLavoro.isEmpty()
    val isPhoneValid = telefono.all { it.isDigit() } && (telefono.length in 8..11 || telefono.isEmpty())

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { cvName = "CV_Selezionato.pdf" }
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Svuota modulo") },
            text = { Text("Sei sicuro di voler cancellare i testi? Ricorda che i dati del tuo profilo non verranno toccati.") },
            confirmButton = {
                TextButton(onClick = {
                    nome = ""; cognome = ""; emailLavoro = ""; telefono = ""; cvName = ""
                    showResetDialog = false
                }) { Text("Sì, svuota", color = Color.Red) }
            },
            dismissButton = { TextButton(onClick = { showResetDialog = false }) { Text("Annulla") } }
        )
    }

    Column(Modifier.fillMaxSize().padding(padding).background(Color.White)) {
        ApplyHeader("1", "I tuoi dati e CV") { navController?.popBackStack() }

        Column(Modifier.padding(horizontal = 24.dp).verticalScroll(rememberScrollState())) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Verde = Completo", fontSize = 12.sp, color = Color.Gray)
                TextButton(onClick = { showResetDialog = true }) {
                    Icon(Icons.Default.Refresh, null, Modifier.size(16.dp))
                    Text(" Resetta form")
                }
            }

            EditTextField("Nome", nome, KeyboardType.Text) { nome = it }
            EditTextField("Cognome", cognome, KeyboardType.Text) { cognome = it }
            EditTextField("Email", emailLavoro, KeyboardType.Email, isError = !isEmailValid, errorMessage = "Email non valida") { emailLavoro = it }

            // --- TELEFONO (Struttura come profilo) ---
            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                OutlinedTextField(
                    value = prefisso,
                    onValueChange = { if (it.length <= 4) prefisso = it },
                    label = { Text("Prefisso") },
                    modifier = Modifier.width(90.dp),
                    shape = RoundedCornerShape(20.dp),
                    singleLine = true
                )
                Spacer(Modifier.width(8.dp))
                EditTextField(
                    label = "Telefono",
                    value = telefonoInput,
                    keyboardType = KeyboardType.Phone,
                    isError = !isPhoneValid,
                    errorMessage = "Solo numeri (10 cifre)",
                    modifier = Modifier.weight(1f),
                    onValueChange = { input ->
                        val cleanInput = input.filter { it.isDigit() }
                        if (cleanInput.length <= 10) {
                            telefonoInput = formatPhone(cleanInput) // Applica gli spazi automaticamente
                        }
                    }
                )
            }

            Spacer(Modifier.height(10.dp))
            Text("Documento Curriculum", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Surface(
                onClick = { launcher.launch("application/pdf") },
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                color = if (cvName.isNotEmpty()) Color(0xFFE8F5E9) else Color(0xFFFFF3E0),
                border = androidx.compose.foundation.BorderStroke(1.dp, if (cvName.isNotEmpty()) Color(0xFF81C784) else Color(0xFFFFB74D))
            ) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(if (cvName.isNotEmpty()) Icons.Default.CheckCircle else Icons.Default.FileUpload, null, tint = if (cvName.isNotEmpty()) Color(0xFF388E3C) else Color.Gray)
                    Spacer(Modifier.width(12.dp))
                    Text(if (cvName.isEmpty()) "Scegli il tuo CV (PDF)" else cvName, modifier = Modifier.weight(1f))
                }
            }

            Spacer(Modifier.height(30.dp))

            Button(
                onClick = {
                    // SALVIAMO NEL DRAFT (Non nel profilo!)
                    currentDraft = CandidacyDraft(
                        nome = nome,
                        cognome = cognome,
                        emailLavoro = emailLavoro,
                        telefono = "$prefisso $telefonoInput",
                        cvFileName = cvName
                    )

                    if (isReturnToSummary) {
                        isReturnToSummary = false
                        navController?.popBackStack()
                    } else {
                        navController?.navigate(Destination.APPLY_STEP_2_INTRO.route)
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = nome.isNotEmpty() && cognome.isNotEmpty() && isEmailValid && isPhoneValid,
                shape = CircleShape
            ) {
                Text(if (isReturnToSummary) "Torna al riepilogo" else "Avanti: Video Presentazione")
            }
        }
    }
}

// --- STEP 2a: ISTRUZIONI VIDEO ---
@Composable
fun ApplyStep2Intro(navController: NavController?, padding: PaddingValues) {
    Column(Modifier.fillMaxSize().padding(padding).background(Color.White)) {
        ApplyHeader("2", "Preparati al Video") { navController?.popBackStack() }

        Column(Modifier.padding(30.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)).background(Color(0xFFF5F5F5)).padding(24.dp)) {
                Column {
                    Text("Paolo, ecco cosa dire:", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(Modifier.height(10.dp))
                    Text("• Presentati (Nome e città)\n• Breve esperienza lavorativa\n• Perché SuperSpan?", lineHeight = 24.sp)
                    Spacer(Modifier.height(15.dp))
                    Text("Hai 30 secondi di tempo.", fontWeight = FontWeight.Bold, color = Color.Red)
                }
            }

            Spacer(Modifier.weight(1f))

            Button(
                onClick = { navController?.navigate(Destination.APPLY_STEP_2_RECORD.route) },
                modifier = Modifier.height(60.dp).fillMaxWidth(0.7f),
                colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
                shape = CircleShape
            ) {
                Icon(Icons.Default.Videocam, null)
                Spacer(Modifier.width(8.dp))
                Text("Inizia Registrazione")
            }
        }
    }
}

// --- STEP 2b: REGISTRAZIONE CON CONTROLLO DURATA ---
@Composable
fun ApplyStep2Record(navController: NavController?, padding: PaddingValues) {
    var countdown by remember { mutableStateOf(3) }
    var seconds by remember { mutableStateOf(0) }
    var isRecording by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        while (countdown > 0) { delay(1000); countdown-- }
        isRecording = true
    }

    LaunchedEffect(isRecording) {
        while (isRecording && seconds < 30) { delay(1000); seconds++ }
        if (seconds >= 30) {
            isRecording = false
            navController?.navigate(Destination.APPLY_STEP_3.route)
        }
    }

    Column(Modifier.fillMaxSize().padding(padding).background(Color.Black)) {
        Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
            if (countdown > 0) {
                Text(countdown.toString(), fontSize = 120.sp, color = Color.White, fontWeight = FontWeight.ExtraBold)
            } else {
                Column(Modifier.fillMaxSize()) {
                    LinearProgressIndicator(progress = { seconds / 30f }, modifier = Modifier.fillMaxWidth().height(8.dp), color = Color.Red, trackColor = Color.White.copy(0.2f))
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Surface(Modifier.size(12.dp), shape = CircleShape, color = Color.Red) {}
                        Spacer(Modifier.width(8.dp))
                        Text("REC 00:${seconds.toString().padStart(2, '0')} / 00:30", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                    if (errorMsg.isNotEmpty()) {
                        Text(errorMsg, color = Color.Yellow, modifier = Modifier.padding(16.dp).background(Color.Black.copy(0.6f)))
                    }
                }
            }
        }

        Box(Modifier.fillMaxWidth().height(120.dp).background(Color.Black), contentAlignment = Alignment.Center) {
            if (isRecording) {
                Box(Modifier.size(70.dp).clip(CircleShape).background(Color.White).padding(4.dp).clip(CircleShape).background(Color.Red).clickable {
                    if (seconds < 5) {
                        errorMsg = "Video troppo corto! Registra almeno 5 secondi."
                    } else {
                        isRecording = false
                        if (isReturnToSummary) {
                            isReturnToSummary = false
                            navController?.popBackStack()
                        } else {
                            navController?.navigate(Destination.APPLY_STEP_3.route)
                        }
                    }
                })
            }
        }
    }
}

// --- STEP 3: RIEPILOGO CON DATI DEL DRAFT ---
@Composable
fun ApplyStep3(navController: NavController?, padding: PaddingValues) {
    var showEditConfirm by remember { mutableStateOf<String?>(null) }

    if (showEditConfirm != null) {
        AlertDialog(
            onDismissRequest = { showEditConfirm = null },
            title = { Text("Modifica ${showEditConfirm}") },
            text = { Text("Vuoi tornare indietro a correggere?") },
            confirmButton = {
                TextButton(onClick = {
                    isReturnToSummary = true
                    val target = if (showEditConfirm == "Dati") Destination.APPLY_STEP_1.route else Destination.APPLY_STEP_2_INTRO.route
                    showEditConfirm = null
                    navController?.navigate(target)
                }) { Text("Sì, modifica") }
            },
            dismissButton = { TextButton(onClick = { showEditConfirm = null }) { Text("Annulla") } }
        )
    }

    Column(Modifier.fillMaxSize().padding(padding).background(Color.White)) {
        ApplyHeader("3", "Riepilogo e Invio") { navController?.popBackStack() }

        Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("Controlla i dati per questa candidatura:", color = Color.Gray, fontSize = 14.sp)

            // 1. Nome e Cognome
            SummaryEditRow(Icons.Default.Person, "Candidato", "${currentDraft.nome} ${currentDraft.cognome}") {
                showEditConfirm = "Dati"
            }

            // 2. EMAIL (Aggiunta come richiesto)
            SummaryEditRow(Icons.Default.Email, "Email di contatto", currentDraft.emailLavoro) {
                showEditConfirm = "Dati"
            }

            // 3. TELEFONO (Sarà già spaziato perché lo abbiamo salvato così nel draft)
            SummaryEditRow(Icons.Default.Phone, "Recapito telefonico", currentDraft.telefono) {
                showEditConfirm = "Dati"
            }

            // 4. CV
            SummaryEditRow(Icons.Default.Description, "Documento CV", currentDraft.cvFileName.ifEmpty { "Nessun file" }) {
                showEditConfirm = "Dati"
            }

            // 5. VIDEO
            SummaryEditRow(Icons.Default.Videocam, "Video Presentazione", "Registrato correttamente") {
                showEditConfirm = "Video"
            }

            Spacer(Modifier.weight(1f))

            Button(
                onClick = {
                    val nuovaCandidatura = Candidacy(
                        id = AllCandidacies.size + 1,
                        userEmail = actualUser.email,
                        offerId = currentOfferIdApplying,
                        nome = currentDraft.nome,
                        cognome = currentDraft.cognome,
                        emailContatto = currentDraft.emailLavoro,
                        cvFileName = currentDraft.cvFileName
                    )
                    AllCandidacies.add(nuovaCandidatura)
                    currentDraft = CandidacyDraft() // Puliamo il draft per la prossima volta
                    navController?.navigate(Destination.HOME.route) { popUpTo(Destination.HOME.route) { inclusive = true } }
                },
                modifier = Modifier.fillMaxWidth().height(60.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF388E3C)),
                shape = CircleShape
            ) {
                Text("CONFERMA E INVIA ORA", fontWeight = FontWeight.ExtraBold)
            }
        }
    }
}

@Composable
fun SummaryEditRow(icon: ImageVector, label: String, value: String, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFF9F9F9),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray)
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = Color.Gray)
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(label, fontSize = 12.sp, color = Color.Gray)
                Text(value, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
            Icon(Icons.Default.Edit, null, Modifier.size(18.dp), tint = Color(0xFF388E3C))
        }
    }
}*/

package com.example.superspan

import android.net.Uri
import android.util.Patterns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay

// --- STATO TEMPORANEO ---
data class CandidacyDraft(
    var nome: String = "",
    var cognome: String = "",
    var emailLavoro: String = "",
    var telefono: String = "",
    var cvFileName: String = "",
    var videoFileName: String = ""
)

var currentDraft by mutableStateOf(CandidacyDraft())
var isReturnToSummary by mutableStateOf(false)

// --- HEADER ---
@Composable
fun ApplyHeader(step: String, title: String, onBack: () -> Unit) {
    Column(
        Modifier.fillMaxWidth().background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp)) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
            }
        }
        Box(
            Modifier.fillMaxWidth().height(80.dp).clip(TopOvalShape(20.dp)).background(Color.LightGray),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Candidatura: Step $step di 3", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF388E3C))
                Text(title, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
            }
        }
    }
}

// --- STEP 1: DATI E CV ---
@Composable
fun ApplyStep1(navController: NavController?, padding: PaddingValues) {
    var nome by remember { mutableStateOf(currentDraft.nome.ifEmpty { actualUser.nome }) }
    var cognome by remember { mutableStateOf(currentDraft.cognome.ifEmpty { actualUser.cognome }) }
    var emailLavoro by remember { mutableStateOf(currentDraft.emailLavoro.ifEmpty { actualUser.emailLavoro ?: "" }) }

    val telefonoDiPartenza = if (currentDraft.telefono.isNotEmpty()) {
        currentDraft.telefono
    } else {
        actualUser.telefono ?: ""
    }

    // Ora puliamo la stringa per avere solo le cifre per l'input (togliendo +39 o 39)
    var telefonoDigits by remember {
        mutableStateOf(
            telefonoDiPartenza
                .filter { it.isDigit() } // Prende solo i numeri
                .let { digits ->
                    // Se inizia con 39, lo togliamo perché il prefisso è nel campo accanto
                    if (digits.startsWith("39")) digits.removePrefix("39") else digits
                }
        )
    }

    var prefisso by remember { mutableStateOf("+39") }
    var cvName by remember { mutableStateOf(currentDraft.cvFileName.ifEmpty { actualUser.cvFileName ?: "" }) }
    var showResetDialog by remember { mutableStateOf(false) }

    val isEmailValid = Patterns.EMAIL_ADDRESS.matcher(emailLavoro).matches()
    val isPhoneValid = telefonoDigits.length >= 9 // Validazione base lunghezza

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { cvName = "CV_Selezionato.pdf" }
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Svuota modulo") },
            text = { Text("Sei sicuro? I dati del profilo non verranno toccati.") },
            confirmButton = {
                TextButton(onClick = {
                    nome = ""; cognome = ""; emailLavoro = ""; telefonoDigits = ""; cvName = ""
                    showResetDialog = false
                }) { Text("Sì, svuota", color = Color.Red) }
            },
            dismissButton = { TextButton(onClick = { showResetDialog = false }) { Text("Annulla") } }
        )
    }

    Column(Modifier.fillMaxSize().padding(padding).background(Color.White)) {
        ApplyHeader("1", "I tuoi dati e CV") { navController?.popBackStack() }

        Column(Modifier.padding(horizontal = 24.dp).verticalScroll(rememberScrollState())) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Tutti i campi sono obbligatori", fontSize = 12.sp, color = Color.Gray)
                TextButton(onClick = { showResetDialog = true }) {
                    Icon(Icons.Default.Refresh, null, Modifier.size(16.dp))
                    Text(" Reset")
                }
            }

            EditTextField("Nome", nome, KeyboardType.Text) { nome = it }
            EditTextField("Cognome", cognome, KeyboardType.Text) { cognome = it }
            EditTextField("Email di contatto", emailLavoro, KeyboardType.Email, isError = !isEmailValid && emailLavoro.isNotEmpty(), errorMessage = "Email non valida") { emailLavoro = it }

            // TELEFONO CON VISUAL TRANSFORMATION (Fix bug cursore)
            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                OutlinedTextField(
                    value = prefisso,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Pre") },
                    modifier = Modifier.width(80.dp),
                    shape = RoundedCornerShape(20.dp)
                )
                Spacer(Modifier.width(8.dp))
                EditTextField(
                    label = "Telefono",
                    value = telefonoDigits,
                    keyboardType = KeyboardType.Phone,
                    visualTransformation = PhoneVisualTransformation(), // Lo spazio è solo visivo!
                    modifier = Modifier.weight(1f),
                    onValueChange = { input ->
                        if (input.all { it.isDigit() } && input.length <= 10) {
                            telefonoDigits = input
                        }
                    }
                )
            }

            Spacer(Modifier.height(10.dp))
            Text("Documento Curriculum", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Surface(
                onClick = { launcher.launch("application/pdf") },
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                color = if (cvName.isNotEmpty()) Color(0xFFE8F5E9) else Color(0xFFFFF3E0),
                border = androidx.compose.foundation.BorderStroke(1.dp, if (cvName.isNotEmpty()) Color(0xFF81C784) else Color(0xFFFFB74D))
            ) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(if (cvName.isNotEmpty()) Icons.Default.CheckCircle else Icons.Default.FileUpload, null, tint = if (cvName.isNotEmpty()) Color(0xFF388E3C) else Color.Gray)
                    Spacer(Modifier.width(12.dp))
                    Text(if (cvName.isEmpty()) "Scegli il tuo CV (PDF)" else cvName, modifier = Modifier.weight(1f))
                }
            }

            Spacer(Modifier.height(30.dp))

            // REQUISITO BLOCCANTE: Tutti i campi devono essere pieni e validi
            val canGoForward = nome.isNotBlank() &&
                    cognome.isNotBlank() &&
                    isEmailValid &&
                    emailLavoro.isNotBlank() &&
                    isPhoneValid

            Button(
                onClick = {
                    currentDraft = currentDraft.copy(
                        nome = nome,
                        cognome = cognome,
                        emailLavoro = emailLavoro,
                        telefono = "$prefisso ${formatPhone(telefonoDigits)}",
                        cvFileName = cvName
                    )
                    if (isReturnToSummary) { isReturnToSummary = false; navController?.popBackStack() }
                    else { navController?.navigate(Destination.APPLY_STEP_2_INTRO.route) }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = canGoForward,
                shape = CircleShape
            ) {
                Text(if (isReturnToSummary) "Torna al riepilogo" else "Avanti: Video Presentazione")
            }
        }
    }
}

// --- STEP 2a: ISTRUZIONI VIDEO ---
@Composable
fun ApplyStep2Intro(navController: NavController?, padding: PaddingValues) {
    Column(Modifier.fillMaxSize().padding(padding).background(Color.White)) {
        ApplyHeader("2", "Preparati al Video") { navController?.popBackStack() }

        Column(Modifier.padding(30.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)).background(Color(0xFFF5F5F5)).padding(24.dp)) {
                Column {
                    Text("Paolo, ecco cosa dire:", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(Modifier.height(10.dp))
                    Text("• Presentati (Nome e città)\n• Breve esperienza lavorativa\n• Perché SuperSpan?", lineHeight = 24.sp)
                    Spacer(Modifier.height(15.dp))
                    Text("Hai 30 secondi di tempo.", fontWeight = FontWeight.Bold, color = Color.Red)
                }
            }

            Spacer(Modifier.weight(1f))

            Button(
                onClick = { navController?.navigate(Destination.APPLY_STEP_2_RECORD.route) },
                modifier = Modifier.height(60.dp).fillMaxWidth(0.7f),
                colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
                shape = CircleShape
            ) {
                Icon(Icons.Default.Videocam, null)
                Spacer(Modifier.width(8.dp))
                Text("Inizia Registrazione")
            }
        }
    }
}

// --- STEP 2b: REGISTRAZIONE ---
@Composable
fun ApplyStep2Record(navController: NavController?, padding: PaddingValues) {
    var countdown by remember { mutableStateOf(3) }
    var seconds by remember { mutableStateOf(0) }
    var isRecording by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        while (countdown > 0) { delay(1000); countdown-- }
        isRecording = true
    }

    LaunchedEffect(isRecording) {
        while (isRecording && seconds < 30) { delay(1000); seconds++ }
        if (seconds >= 30) {
            isRecording = false
            currentDraft = currentDraft.copy(videoFileName = "Video_Paolo_Presentazione.mp4")
            navController?.navigate(Destination.APPLY_STEP_3.route)
        }
    }

    Column(Modifier.fillMaxSize().padding(padding).background(Color.Black)) {
        Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
            if (countdown > 0) {
                Text(countdown.toString(), fontSize = 120.sp, color = Color.White, fontWeight = FontWeight.ExtraBold)
            } else {
                Column(Modifier.fillMaxSize()) {
                    LinearProgressIndicator(progress = { seconds / 30f }, modifier = Modifier.fillMaxWidth().height(8.dp), color = Color.Red, trackColor = Color.White.copy(0.2f))
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Surface(Modifier.size(12.dp), shape = CircleShape, color = Color.Red) {}
                        Spacer(Modifier.width(8.dp))
                        Text("REC 00:${seconds.toString().padStart(2, '0')} / 00:30", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                    if (errorMsg.isNotEmpty()) {
                        Text(errorMsg, color = Color.Yellow, modifier = Modifier.padding(16.dp).background(Color.Black.copy(0.6f)))
                    }
                }
            }
        }

        Box(Modifier.fillMaxWidth().height(120.dp).background(Color.Black), contentAlignment = Alignment.Center) {
            if (isRecording) {
                Box(Modifier.size(70.dp).clip(CircleShape).background(Color.White).padding(4.dp).clip(CircleShape).background(Color.Red).clickable {
                    if (seconds < 15) {
                        errorMsg = "Video troppo corto! Registra almeno 15 secondi."
                    } else {
                        isRecording = false
                        currentDraft = currentDraft.copy(videoFileName = "Video_Paolo_Presentazione.mp4")
                        if (isReturnToSummary) { isReturnToSummary = false; navController?.popBackStack() }
                        else { navController?.navigate(Destination.APPLY_STEP_2_REVIEW.route) }
                    }
                })
            }
        }

    }
}

@Composable
fun ApplyStep2Review(navController: NavController?, padding: PaddingValues) {
    Column(
        Modifier.fillMaxSize().padding(padding).background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header con tasto "Riprova" come nella foto
        Row(
            Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController?.popBackStack() }) { // Torna a registrare
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
            }
            Text("riprova", modifier = Modifier.clickable { navController?.popBackStack() })
        }

        Spacer(Modifier.height(20.dp))

        // Box Video (Placeholder)
        Box(
            Modifier
                .weight(1f)
                .fillMaxWidth(0.9f)
                .clip(RoundedCornerShape(20.dp))
                .background(Color.LightGray),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Video", fontSize = 40.sp, color = Color.White, fontWeight = FontWeight.Bold)
                Icon(Icons.Default.PlayArrow, null, Modifier.size(80.dp), tint = Color.White)
            }
        }

        // Tasto Avanti
        Box(Modifier.fillMaxWidth().padding(30.dp), contentAlignment = Alignment.Center) {
            Button(
                onClick = {
                    if (isReturnToSummary) {
                        isReturnToSummary = false
                        navController?.navigate(Destination.APPLY_STEP_3.route) {
                            popUpTo(Destination.APPLY_STEP_3.route) { inclusive = true }
                        }
                    } else {
                        navController?.navigate(Destination.APPLY_STEP_3.route)
                    }
                },
                modifier = Modifier.height(55.dp).width(180.dp),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
            ) {
                Text("Avanti", fontSize = 18.sp)
            }
        }
    }
}

// --- STEP 3: RIEPILOGO FINALE CON EMAIL ---
@Composable
fun ApplyStep3(navController: NavController?, padding: PaddingValues) {
    var showEditConfirm by remember { mutableStateOf<String?>(null) }
    var previewContent by remember { mutableStateOf<String?>(null) }

    if (showEditConfirm != null) {
        AlertDialog(
            onDismissRequest = { showEditConfirm = null },
            title = { Text("Vuoi modificare?") },
            text = { Text("Verrai riportato allo step relativo per cambiare $showEditConfirm.") },
            confirmButton = {
                TextButton(onClick = {
                    isReturnToSummary = true
                    val target = if (showEditConfirm == "Dati") Destination.APPLY_STEP_1.route else Destination.APPLY_STEP_2_INTRO.route
                    showEditConfirm = null
                    navController?.navigate(target)
                }) { Text("Sì, modifica") }
            },
            dismissButton = { TextButton(onClick = { showEditConfirm = null }) { Text("Annulla") } }
        )
    }

    // [Dialog Anteprima uguale a prima...]

    Column(Modifier.fillMaxSize().padding(padding).background(Color.White)) {
        ApplyHeader("3", "Riepilogo e Invio") { navController?.popBackStack() }

        Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Paolo, controlla un'ultima volta i dati:", color = Color.Gray, fontSize = 13.sp)

            SummaryInteractiveRow(Icons.Default.Person, "Candidato", "${currentDraft.nome} ${currentDraft.cognome}", onEdit = { showEditConfirm = "Dati" }) {
                previewContent = "Nome: ${currentDraft.nome}\nCognome: ${currentDraft.cognome}"
            }

            // EMAIL AGGIUNTA NEL RIEPILOGO
            SummaryInteractiveRow(Icons.Default.Email, "Email di contatto", currentDraft.emailLavoro, onEdit = { showEditConfirm = "Dati" }) {
                previewContent = "Riceverai comunicazioni a: ${currentDraft.emailLavoro}"
            }

            SummaryInteractiveRow(Icons.Default.Phone, "Telefono", currentDraft.telefono, onEdit = { showEditConfirm = "Dati" }) {
                previewContent = "Recapito telefonico: ${currentDraft.telefono}"
            }

            SummaryInteractiveRow(Icons.Default.Description, "Documento CV", currentDraft.cvFileName.ifEmpty { "Selezionato" }, onEdit = { showEditConfirm = "Dati" }) {
                previewContent = "File: ${currentDraft.cvFileName}"
            }

            SummaryInteractiveRow(Icons.Default.Videocam, "Video Presentazione", "Registrato", onEdit = { showEditConfirm = "Video" }) {
                previewContent = "Video acquisito correttamente"
            }

            Spacer(Modifier.weight(1f))

            Button(
                onClick = {
                    val nuovaCandidatura = Candidacy(id = AllCandidacies.size + 1, userEmail = actualUser.email, offerId = currentOfferIdApplying, nome = currentDraft.nome, cognome = currentDraft.cognome, emailContatto = currentDraft.emailLavoro, cvFileName = currentDraft.cvFileName)
                    AllCandidacies.add(nuovaCandidatura)
                    currentDraft = CandidacyDraft()
                    navController?.navigate(Destination.HOME.route) { popUpTo(Destination.HOME.route) { inclusive = true } }
                },
                modifier = Modifier.fillMaxWidth().height(60.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF388E3C)),
                shape = CircleShape
            ) {
                Text("CONFERMA E INVIA", fontWeight = FontWeight.ExtraBold)
            }
        }
    }
}

@Composable
fun SummaryInteractiveRow(icon: ImageVector, label: String, value: String, onEdit: () -> Unit, onView: () -> Unit) {
    Surface(
        onClick = onView, // Cliccando sulla riga si vede l'anteprima
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFF9F9F9),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray)
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = Color.Gray)
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(label, fontSize = 11.sp, color = Color.Gray)
                Text(value, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }
            // Icona matita separata per la modifica
            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, "Modifica", Modifier.size(20.dp), tint = Color(0xFF388E3C))
            }
        }
    }
}