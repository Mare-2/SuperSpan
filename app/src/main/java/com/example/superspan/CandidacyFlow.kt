/*package com.example.superspan

import android.net.Uri
import android.util.Patterns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.BackHandler
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
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.FileOutputOptions
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.view.PreviewView
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import java.io.File
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.compose.ui.viewinterop.AndroidView // Necessario per mostrare il player
import androidx.camera.video.VideoRecordEvent
import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult


// --- STATO TEMPORANEO ---

var currentDraft by mutableStateOf(CandidacyDraft())
var isReturnToSummary by mutableStateOf(false)

fun goToOfferPresentation(navController: NavController?) {
    navController?.navigate("dettaglio_offerta/$currentOfferIdApplying") {
    popUpTo(Destination.APPLY_STEP_1.route) { inclusive = true }
        launchSingleTop = true
    }
}

@Composable
fun ExitDraftDialog(
    visible: Boolean,
    onDismiss: () -> Unit,
    onSave: () -> Unit,
    onDiscard: () -> Unit
) {
    if (visible) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Esci dalla candidatura") },
            text = { Text("Vuoi salvare la candidatura tra le bozze prima di uscire?") },
            confirmButton = { TextButton(onClick = onSave) { Text("Salva") } },
            dismissButton = {
                Row {
                    TextButton(onClick = onDiscard) { Text("Scarta", color = com.example.superspan.ui.theme.AppError) }
                    TextButton(onClick = onDismiss) { Text("Annulla") }
                }
            }
        )
    }
}

// --- HEADER ---
@Composable
fun ApplyHeader(step: String, title: String, onBack: () -> Unit, onClose: () -> Unit) {
    Column(
        Modifier.fillMaxWidth().background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack, modifier = androidx.compose.ui.Modifier.background(androidx.compose.ui.graphics.Color.White.copy(alpha = 0.7f), androidx.compose.foundation.shape.CircleShape)) { Icon(androidx.compose.material.icons.Icons.AutoMirrored.Filled.ArrowBack, null)
            }
            IconButton(onClick = onClose) {
                Icon(Icons.Default.Close, null, tint = com.example.superspan.ui.theme.AppError)
            }
        }
        Box(
            Modifier.fillMaxWidth().height(80.dp).clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)).background(Color.LightGray),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Candidatura: Step $step di 3", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF388E3C))
                Text(title, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
            }
        }
    }
}*/

// --- STEP 1: DATI E CV ---
/*@Composable
fun ApplyStep1(navController: NavController?, padding: PaddingValues) {
    var nome by remember { mutableStateOf(currentDraft.nome.ifEmpty { actualUser.nome }) }
    var cognome by remember { mutableStateOf(currentDraft.cognome.ifEmpty { actualUser.cognome }) }
    var emailLavoro by remember { mutableStateOf(currentDraft.emailLavoro.ifEmpty { actualUser.emailLavoro ?: "" }) }
    var showExitDialog by remember { mutableStateOf(false) }

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
                    nome = actualUser.nome; cognome = actualUser.cognome; emailLavoro = actualUser.emailLavoro ?: ""; telefonoDigits = ""; cvName = ""
                    showResetDialog = false
                }) { Text("Sì, svuota", color = com.example.superspan.ui.theme.AppError) }
            },
            dismissButton = { TextButton(onClick = { showResetDialog = false }) { Text("Annulla") } }
        )
    }

    Column(Modifier.fillMaxSize().padding(padding).background(com.example.superspan.ui.theme.AppBackgroundBrush)) {
        ApplyHeader("1", "I tuoi dati e CV", onBack = { navController?.popBackStack() }, onClose = { showExitDialog = true })

        ExitDraftDialog(
            visible = showExitDialog,
            onDismiss = { showExitDialog = false },
            onSave = {
                val draft = CandidacyDraft(
                    nome = nome,
                    cognome = cognome,
                    email = emailLavoro,
                    telefono = "$prefisso ${formatPhone(telefonoDigits)}",
                    cvFileName = cvName.ifEmpty { null }
                )
                saveCandidacyDraftForOffer(actualUser, currentOfferIdApplying, draft)
                showExitDialog = false
                goToOfferPresentation(navController)
            },
            onDiscard = {
                showExitDialog = false
                goToOfferPresentation(navController)
            }
        )

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
}*/
/*
@Composable
fun ApplyStep1(navController: NavController?, padding: PaddingValues) {
    val context = LocalContext.current

    // 1. Carichiamo la bozza specifica per questa offerta se esiste, altrimenti creiamo un oggetto vuoto
    val existingDraft = getCandidacyDraftForOffer(actualUser, currentOfferIdApplying) ?: CandidacyDraft()

    // 2. Inizializziamo gli stati. Se la bozza è vuota, peschiamo dal profilo (actualUser)
    var nome by remember { mutableStateOf(existingDraft.nome.ifEmpty { actualUser.nome }) }
    var cognome by remember { mutableStateOf(existingDraft.cognome.ifEmpty { actualUser.cognome }) }
    var emailLavoro by remember {
        mutableStateOf(existingDraft.emailLavoro.ifEmpty { actualUser.emailLavoro ?: actualUser.email })
    }

    // Gestione telefono: puliamo il numero per l'input (solo cifre, senza prefisso)
    var telefonoDigits by remember {
        val basePhone = existingDraft.telefono.ifEmpty { actualUser.telefono ?: "" }
        mutableStateOf(basePhone.filter { it.isDigit() }.removePrefix("39"))
    }

    var cvName by remember { mutableStateOf(existingDraft.cvFileName.ifEmpty { actualUser.cvFileName ?: "" }) }
    var cvPath by remember { mutableStateOf(existingDraft.cvPath.ifEmpty { actualUser.cvPath ?: "" }) }

    var showExitDialog by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }

    // Validazioni
    val isEmailValid = Patterns.EMAIL_ADDRESS.matcher(emailLavoro).matches()
    val isPhoneValid = telefonoDigits.length >= 9

    // Launcher per caricamento CV reale
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { selectedUri ->
            val destinationName = "cv_${actualUser.nome.lowercase()}_${currentOfferIdApplying}.pdf"
            val savedPath = saveFileToInternalStorage(context, selectedUri, destinationName)
            if (savedPath != null) {
                cvName = destinationName
                cvPath = savedPath
            }
        }
    }

    // --- DIALOG RESET ---
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Svuota modulo") },
            text = { Text("Vuoi cancellare i dati inseriti in questa candidatura? I dati del tuo profilo non verranno modificati.") },
            confirmButton = {
                TextButton(onClick = {
                    nome = ""; cognome = ""; emailLavoro = ""; telefonoDigits = ""; cvName = ""; cvPath = ""
                    showResetDialog = false
                }) { Text("Sì, svuota", color = com.example.superspan.ui.theme.AppError) }
            },
            dismissButton = { TextButton(onClick = { showResetDialog = false }) { Text("Annulla") } }
        )
    }

    Column(Modifier.fillMaxSize().padding(padding).background(com.example.superspan.ui.theme.AppBackgroundBrush)) {
        // Header con tasto chiudi per la bozza
        ApplyHeader(
            step = "1",
            title = "I tuoi dati e CV",
            onBack = { navController?.popBackStack() },
            onClose = { showExitDialog = true }
        )

        // --- DIALOG USCITA / SALVA BOZZA ---
        ExitDraftDialog(
            visible = showExitDialog,
            onDismiss = { showExitDialog = false },
            onSave = {
                val newDraft = CandidacyDraft(
                    nome = nome,
                    cognome = cognome,
                    emailLavoro = emailLavoro,
                    telefono = "+39 ${formatPhone(telefonoDigits)}",
                    cvFileName = cvName,
                    cvPath = cvPath,
                    videoPath = currentDraft.videoPath
                )
                saveCandidacyDraftForOffer(actualUser, currentOfferIdApplying, newDraft)
                showExitDialog = false
                goToOfferPresentation(navController)
            },
            onDiscard = {
                actualUser.candidacyDraftsByOfferId.remove(currentOfferIdApplying)
                showExitDialog = false
                goToOfferPresentation(navController)
            }
        )

        Column(Modifier.padding(horizontal = 24.dp).verticalScroll(rememberScrollState())) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Dati per la candidatura", fontSize = 12.sp, color = Color.Gray)
                TextButton(onClick = { showResetDialog = true }) {
                    Icon(Icons.Default.Refresh, null, Modifier.size(16.dp))
                    Text(" Reset")
                }
            }

            EditTextField("Nome", nome, KeyboardType.Text) { nome = it }
            EditTextField("Cognome", cognome, KeyboardType.Text) { cognome = it }
            EditTextField("Email di contatto", emailLavoro, KeyboardType.Email, isError = !isEmailValid && emailLavoro.isNotEmpty(), errorMessage = "Email non valida") { emailLavoro = it }

            // Telefono con VisualTransformation (Fix bug cursore)
            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                OutlinedTextField(
                    value = "+39",
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
                    visualTransformation = PhoneVisualTransformation(),
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

            // Visualizzazione caricamento CV
            Surface(
                onClick = { launcher.launch("application/pdf") },
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                color = if (cvPath.isNotEmpty()) Color(0xFFE8F5E9) else Color(0xFFFFF3E0),
                border = androidx.compose.foundation.BorderStroke(1.dp, if (cvPath.isNotEmpty()) Color(0xFF81C784) else Color(0xFFFFB74D))
            ) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        if (cvPath.isNotEmpty()) Icons.Default.CheckCircle else Icons.Default.FileUpload,
                        null,
                        tint = if (cvPath.isNotEmpty()) Color(0xFF388E3C) else Color.Gray
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(if (cvName.isEmpty()) "Scegli il tuo CV (PDF)" else cvName, modifier = Modifier.weight(1f))
                }
            }

            Spacer(Modifier.height(30.dp))

            // Verifica se possiamo andare avanti
            val canGoForward = nome.isNotBlank() && cognome.isNotBlank() && isEmailValid && isPhoneValid

            Button(
                onClick = {
                    // Aggiorniamo il draft globale per il passaggio agli step successivi
                    currentDraft = CandidacyDraft(
                        nome = nome,
                        cognome = cognome,
                        emailLavoro = emailLavoro,
                        telefono = "+39 ${formatPhone(telefonoDigits)}",
                        cvFileName = cvName,
                        cvPath = cvPath,
                        videoPath = existingDraft.videoPath // Mantieni il video se già registrato
                    )

                    if (isReturnToSummary) {
                        isReturnToSummary = false
                        navController?.popBackStack()
                    } else {
                        navController?.navigate(Destination.APPLY_STEP_2_INTRO.route)
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = canGoForward,
                shape = CircleShape
            ) {
                Text(if (isReturnToSummary) "Torna al riepilogo" else "Avanti: Video Presentazione")
            }
            Spacer(Modifier.height(20.dp))
        }
    }
}*/



// --- STEP 2a: ISTRUZIONI VIDEO ---
/*@Composable
fun ApplyStep2Intro(navController: NavController?, padding: PaddingValues) {
    var showExitDialog by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize().padding(padding).background(com.example.superspan.ui.theme.AppBackgroundBrush)) {
        ApplyHeader("2", "Preparati al Video", onBack = { navController?.popBackStack() }, onClose = { showExitDialog = true })

        ExitDraftDialog(
            visible = showExitDialog,
            onDismiss = { showExitDialog = false },
            onSave = {
                val draft = CandidacyDraft(
                    nome = currentDraft.nome,
                    cognome = currentDraft.cognome,
                    email = currentDraft.emailLavoro,
                    telefono = currentDraft.telefono,
                    cvFileName = currentDraft.cvFileName.ifEmpty { null }
                )
                saveCandidacyDraftForOffer(actualUser, currentOfferIdApplying, draft)
                showExitDialog = false
                goToOfferPresentation(navController)
            },
            onDiscard = {
                showExitDialog = false
                goToOfferPresentation(navController)
            }
        )

        Column(Modifier.padding(30.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)).background(Color(0xFFF5F5F5)).padding(24.dp)) {
                Column {
                    Text("Paolo, ecco cosa dire:", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(Modifier.height(10.dp))
                    Text("• Presentati (Nome e città)\n• Breve esperienza lavorativa\n• Perché SuperSpan?", lineHeight = 24.sp)
                    Spacer(Modifier.height(15.dp))
                    Text("Hai 30 secondi di tempo.", fontWeight = FontWeight.Bold, color = com.example.superspan.ui.theme.AppError)
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
}*/
/*
@Composable
fun ApplyStep2Intro(navController: NavController?, padding: PaddingValues) {
    var showExitDialog by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize().padding(padding).background(com.example.superspan.ui.theme.AppBackgroundBrush)) {
        ApplyHeader(
            step = "2",
            title = "Preparati al Video",
            onBack = { navController?.popBackStack() },
            onClose = { showExitDialog = true }
        )

        ExitDraftDialog(
            visible = showExitDialog,
            onDismiss = { showExitDialog = false },
            onSave = {
                // SALVATAGGIO REALE: prendiamo lo stato attuale del draft e lo salviamo nella mappa dell'utente
                val draftToSave = currentDraft.copy()
                saveCandidacyDraftForOffer(actualUser, currentOfferIdApplying, draftToSave)

                showExitDialog = false
                goToOfferPresentation(navController)
            },
            onDiscard = {
                // SCARTA: Rimuoviamo la bozza per questa specifica offerta
                actualUser.candidacyDraftsByOfferId.remove(currentOfferIdApplying)

                showExitDialog = false
                goToOfferPresentation(navController)
            }
        )

        Column(Modifier.padding(30.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)).background(Color(0xFFF5F5F5)).padding(24.dp)) {
                Column {
                    Text("Paolo, ecco cosa dire:", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(Modifier.height(10.dp))
                    Text("• Presentati (Nome e città)\n• Breve esperienza lavorativa\n• Perché SuperSpan?", lineHeight = 24.sp)
                    Spacer(Modifier.height(15.dp))
                    Text("Hai 30 secondi di tempo.", fontWeight = FontWeight.Bold, color = com.example.superspan.ui.theme.AppError)
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
}*/

// --- STEP 2b: REGISTRAZIONE ---
/*@Composable
fun ApplyStep2Record(navController: NavController?, padding: PaddingValues) {
    var countdown by remember { mutableStateOf(3) }
    var seconds by remember { mutableStateOf(0) }
    var isRecording by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf("") }
    var showExitDialog by remember { mutableStateOf(false) }

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
        ApplyHeader("2", "Registrazione", onBack = { navController?.popBackStack() }, onClose = { showExitDialog = true })

        ExitDraftDialog(
            visible = showExitDialog,
            onDismiss = { showExitDialog = false },
            onSave = {
                val draft = CandidacyDraft(
                    nome = currentDraft.nome,
                    cognome = currentDraft.cognome,
                    email = currentDraft.emailLavoro,
                    telefono = currentDraft.telefono,
                    cvFileName = currentDraft.cvFileName.ifEmpty { null }
                )
                saveCandidacyDraftForOffer(actualUser, currentOfferIdApplying, draft)
                showExitDialog = false
                goToOfferPresentation(navController)
            },
            onDiscard = {
                showExitDialog = false
                goToOfferPresentation(navController)
            }
        )

        Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
            if (countdown > 0) {
                Text(countdown.toString(), fontSize = 120.sp, color = Color.White, fontWeight = FontWeight.ExtraBold)
            } else {
                Column(Modifier.fillMaxSize()) {
                    LinearProgressIndicator(progress = { seconds / 30f }, modifier = Modifier.fillMaxWidth().height(8.dp), color = com.example.superspan.ui.theme.AppError, trackColor = Color.White.copy(0.2f))
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Surface(Modifier.size(12.dp), shape = CircleShape, color = com.example.superspan.ui.theme.AppError) {}
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
                Box(Modifier.size(70.dp).clip(CircleShape).background(Color.White).padding(4.dp).clip(CircleShape).background(com.example.superspan.ui.theme.AppError).clickable {
                    if (seconds < 15) {
                        errorMsg = "Video troppo corto! Registra almeno 15 secondi."
                    } else {
                        isRecording = false
                        currentDraft = currentDraft.copy(videoFileName = "Video_Paolo_Presentazione.mp4")
                        if (isReturnToSummary) { isReturnToSummary = false; navController?.popBackStack() }
                        else { navController?.navigate(Destination.APPLY_STEP_2_INTRO.route) }
                    }
                })
            }
        }

    }
}
*/
/*
@Composable
fun ApplyStep2Record(navController: NavController?, padding: PaddingValues) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var countdown by remember { mutableStateOf(3) }
    var seconds by remember { mutableStateOf(0) }
    var isRecording by remember { mutableStateOf(false) }

    // Usiamo un Ref per la registrazione per gestirla fuori dallo stato di ricomposizione se necessario
    val activeRecording = remember { mutableStateOf<Recording?>(null) }

    val videoCapture = remember {
        val recorder = Recorder.Builder()
            .setQualitySelector(QualitySelector.from(Quality.SD))
            .build()
        VideoCapture.withOutput(recorder)
    }

    // --- GESTIONE PERMESSI ---
    // Paolo ha paura di sbagliare: gestiamo i permessi prima di iniziare
    var hasPermissions by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasPermissions = permissions[Manifest.permission.CAMERA] == true &&
                permissions[Manifest.permission.RECORD_AUDIO] == true
    }

    LaunchedEffect(Unit) {
        if (!hasPermissions) {
            permissionLauncher.launch(arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO))
        }
    }

    // --- LOGICA REGISTRAZIONE ---
    LaunchedEffect(hasPermissions) {
        if (hasPermissions) {
            while (countdown > 0) {
                delay(1000)
                countdown--
            }

            // Inizio registrazione reale
            val videoFile = File(context.filesDir, "video_${actualUser.email.replace("@","_")}.mp4")
            val outputOptions = FileOutputOptions.Builder(videoFile).build()

            // Correzione errore: avviamo la registrazione e gestiamo gli eventi
            isRecording = true
            activeRecording.value = videoCapture.output
                .prepareRecording(context, outputOptions)
                .withAudioEnabled()
                .start(ContextCompat.getMainExecutor(context)) { recordEvent ->
                    // EVENTI DA GESTIRE:
                    when(recordEvent) {
                        is VideoRecordEvent.Start -> { /* Iniziato! */ }
                        is VideoRecordEvent.Finalize -> {
                            if (recordEvent.hasError()) {
                                isRecording = false
                                // Qui potresti gestire l'errore se il file non si salva
                            } else {
                                // Salvataggio avvenuto con successo
                                currentDraft = currentDraft.copy(videoPath = videoFile.absolutePath)
                            }
                        }
                    }
                }
        }
    }

    // Timer 30 secondi
    LaunchedEffect(isRecording) {
        if (isRecording) {
            while (seconds < 30 && isRecording) {
                delay(1000)
                seconds++
            }
            if (seconds >= 30) {
                activeRecording.value?.stop()
                activeRecording.value = null
                isRecording = false
                navController?.navigate(Destination.APPLY_STEP_2_INTRO.route)
            }
        }
    }

    Box(Modifier.fillMaxSize().background(Color.Black)) {
        if (hasPermissions && countdown == 0) {
            AndroidView(
                factory = { ctx ->
                    val previewView = PreviewView(ctx)
                    val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                    cameraProviderFuture.addListener({
                        val cameraProvider = cameraProviderFuture.get()
                        val preview = Preview.Builder().build()
                        preview.setSurfaceProvider(previewView.surfaceProvider)
                        try {
                            cameraProvider.unbindAll()
                            cameraProvider.bindToLifecycle(
                                lifecycleOwner,
                                CameraSelector.DEFAULT_FRONT_CAMERA,
                                preview,
                                videoCapture
                            )
                        } catch (e: Exception) { e.printStackTrace() }
                    }, ContextCompat.getMainExecutor(ctx))
                    previewView
                },
                modifier = Modifier.fillMaxSize()
            )
        }

        // --- UI OVERLAY ---
        if (countdown > 0) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(countdown.toString(), fontSize = 100.sp, color = Color.White, fontWeight = FontWeight.Bold)
            }
        } else {
            LinearProgressIndicator(
                progress = { seconds / 30f },
                modifier = Modifier.fillMaxWidth().height(8.dp).padding(top = 10.dp),
                color = com.example.superspan.ui.theme.AppError,
                trackColor = Color.White.copy(0.2f)
            )
            Text(
                "REC 00:${seconds.toString().padStart(2, '0')}",
                Modifier.padding(20.dp).align(Alignment.TopStart),
                color = com.example.superspan.ui.theme.AppError, fontWeight = FontWeight.Bold
            )
        }

        if (isRecording) {
            // Tasto Interrompi (Paolo vuole finire prima se ha finito di parlare)
            Box(
                Modifier.align(Alignment.BottomCenter).padding(bottom = 60.dp).size(80.dp)
                    .border(4.dp, Color.White, CircleShape).padding(8.dp)
                    .clip(CircleShape).background(com.example.superspan.ui.theme.AppError)
                    .clickable {
                        activeRecording.value?.stop()
                        activeRecording.value = null
                        isRecording = false
                        // Aspettiamo un attimo per il salvataggio file prima di navigare
                        navController?.navigate(Destination.APPLY_STEP_2_INTRO.route)
                    }
            )
        }
    }
}*/

/*@Composable
fun ApplyStep2Review(navController: NavController?, padding: PaddingValues) {
    Column(
        Modifier.fillMaxSize().padding(padding).background(com.example.superspan.ui.theme.AppBackgroundBrush),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Freccia per tornare semplicemente indietro, senza conferma
        Row(
            Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController?.popBackStack() }) {
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
}*/
/*
@Composable
fun ApplyStep2Review(navController: NavController?, padding: PaddingValues) {
    val context = LocalContext.current
    val exoPlayer = remember {
        androidx.media3.exoplayer.ExoPlayer.Builder(context).build().apply {
            val mediaItem = MediaItem.fromUri(Uri.fromFile(File(currentDraft.videoPath)))
            setMediaItem(mediaItem)
            prepare()
        }
    }

    // Rilascia il player quando esci dalla pagina
    DisposableEffect(Unit) {
        onDispose { exoPlayer.release() }
    }

    Column(Modifier.fillMaxSize().padding(padding).background(com.example.superspan.ui.theme.AppBackgroundBrush), horizontalAlignment = Alignment.CenterHorizontally) {
        Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { navController?.popBackStack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) }
            Text("Riprova", Modifier.clickable { navController?.popBackStack() }, color = Color.Gray)
        }

        // --- PLAYER VIDEO REALE ---
        Box(
            Modifier.weight(1f).fillMaxWidth(0.9f).clip(RoundedCornerShape(20.dp)).background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            AndroidView(
                factory = { ctx ->
                    PlayerView(ctx).apply {
                        player = exoPlayer
                        useController = true // Mostra tasti play/pausa
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }

        Button(
            onClick = { navController?.navigate(Destination.APPLY_STEP_3.route) },
            modifier = Modifier.padding(32.dp).height(56.dp).width(200.dp),
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
        ) {
            Text("Avanti", fontSize = 18.sp)
        }
    }
}*/

// --- STEP 3: RIEPILOGO FINALE CON EMAIL ---
/*@Composable
fun ApplyStep3(navController: NavController?, padding: PaddingValues) {
    var showEditConfirm by remember { mutableStateOf<String?>(null) }
    var previewContent by remember { mutableStateOf<String?>(null) }
    var showExitDialog by remember { mutableStateOf(false) }

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

    Column(Modifier.fillMaxSize().padding(padding).background(com.example.superspan.ui.theme.AppBackgroundBrush)) {
        ApplyHeader("3", "Riepilogo e Invio", onBack = { navController?.popBackStack() }, onClose = { showExitDialog = true })

        ExitDraftDialog(
            visible = showExitDialog,
            onDismiss = { showExitDialog = false },
            onSave = {
                val draft = CandidacyDraft(
                    nome = currentDraft.nome,
                    cognome = currentDraft.cognome,
                    email = currentDraft.emailLavoro,
                    telefono = currentDraft.telefono,
                    cvFileName = currentDraft.cvFileName.ifEmpty { null }
                )
                saveCandidacyDraftForOffer(actualUser, currentOfferIdApplying, draft)
                showExitDialog = false
                goToOfferPresentation(navController)
            },
            onDiscard = {
                showExitDialog = false
                goToOfferPresentation(navController)
            }
        )

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
                    val nuovaCandidatura = Candidacy(
                        id = AllCandidacies.size + 1,
                        userEmail = actualUser.email,
                        offerId = currentOfferIdApplying,
                        nome = currentDraft.nome,
                        cognome = currentDraft.cognome,
                        emailContatto = currentDraft.emailLavoro,
                        cvFileName = currentDraft.cvFileName,
                        videoSimulatoPath = currentDraft.videoPath, // SALVA IL PATH REALE QUI
                        stato = "Inviata")
                    AllCandidacies.add(nuovaCandidatura)
                    currentDraft = CandidacyDraft()
                    navController?.navigateTopLevel(Destination.LAVORO.route)
                },
                modifier = Modifier.fillMaxWidth().height(60.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF388E3C)),
                shape = CircleShape
            ) {
                Text("CONFERMA E INVIA", fontWeight = FontWeight.ExtraBold)
            }
        }
    }
}*/

/*@Composable
fun ApplyStep3(navController: NavController?, padding: PaddingValues) {
    var showEditConfirm by remember { mutableStateOf<String?>(null) }
    var previewContent by remember { mutableStateOf<String?>(null) }
    var showExitDialog by remember { mutableStateOf(false) }

    // --- DIALOG DI CONFERMA MODIFICA ---
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

    // --- DIALOG DI ANTEPRIMA ---
    if (previewContent != null) {
        AlertDialog(
            onDismissRequest = { previewContent = null },
            title = { Text("Anteprima") },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = if (previewContent!!.contains("PDF")) Icons.Default.PictureAsPdf else Icons.Default.PlayCircle,
                        contentDescription = null,
                        modifier = Modifier.size(60.dp),
                        tint = Color.Gray
                    )
                    Spacer(Modifier.height(16.dp))
                    Text("Stai visualizzando:\n$previewContent", textAlign = TextAlign.Center)
                }
            },
            confirmButton = { Button(onClick = { previewContent = null }) { Text("Chiudi") } }
        )
    }

    Column(Modifier.fillMaxSize().padding(padding).background(com.example.superspan.ui.theme.AppBackgroundBrush)) {
        ApplyHeader(
            step = "3",
            title = "Riepilogo e Invio",
            onBack = { navController?.popBackStack() },
            onClose = { showExitDialog = true }
        )

        // --- DIALOG USCITA / SALVA BOZZA (Fix nomi parametri) ---
        ExitDraftDialog(
            visible = showExitDialog,
            onDismiss = { showExitDialog = false },
            onSave = {
                // Usiamo copy() per essere sicuri di non sbagliare nomi
                val draftToSave = currentDraft.copy()
                saveCandidacyDraftForOffer(actualUser, currentOfferIdApplying, draftToSave)
                showExitDialog = false
                goToOfferPresentation(navController)
            },
            onDiscard = {
                actualUser.candidacyDraftsByOfferId.remove(currentOfferIdApplying)
                showExitDialog = false
                goToOfferPresentation(navController)
            }
        )

        Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Paolo, controlla un'ultima volta i dati:", color = Color.Gray, fontSize = 13.sp)

            SummaryInteractiveRow(Icons.Default.Person, "Candidato", "${currentDraft.nome} ${currentDraft.cognome}", onEdit = { showEditConfirm = "Dati" }) {
                previewContent = "Nome: ${currentDraft.nome}\nCognome: ${currentDraft.cognome}"
            }

            SummaryInteractiveRow(Icons.Default.Email, "Email di contatto", currentDraft.emailLavoro, onEdit = { showEditConfirm = "Dati" }) {
                previewContent = "Email per comunicazioni:\n${currentDraft.emailLavoro}"
            }

            SummaryInteractiveRow(Icons.Default.Phone, "Telefono", currentDraft.telefono, onEdit = { showEditConfirm = "Dati" }) {
                previewContent = "Recapito telefonico:\n${currentDraft.telefono}"
            }

            SummaryInteractiveRow(Icons.Default.Description, "Documento CV", currentDraft.cvFileName.ifEmpty { "Selezionato" }, onEdit = { showEditConfirm = "Dati" }) {
                previewContent = "File PDF: ${currentDraft.cvFileName}"
            }

            SummaryInteractiveRow(Icons.Default.Videocam, "Video Presentazione", "Registrato correttamente", onEdit = { showEditConfirm = "Video" }) {
                previewContent = "Video: ${currentDraft.videoPath}"
            }

            Spacer(Modifier.weight(1f))

            // --- TASTO CONFERMA E INVIA (Fix nomi e campi mancanti) ---
            Button(
                onClick = {
                    val nuovaCandidatura = Candidacy(
                        id = AllCandidacies.size + 1,
                        userEmail = actualUser.email,
                        offerId = currentOfferIdApplying,
                        nome = currentDraft.nome,
                        cognome = currentDraft.cognome,
                        emailContatto = currentDraft.emailLavoro, // Nome campo corretto
                        telefono = currentDraft.telefono,       // Aggiunto campo mancante
                        cvPath = currentDraft.cvPath,           // Usiamo il percorso reale
                        videoPath = currentDraft.videoPath,     // Usiamo il percorso reale
                        stato = "Inviata"
                    )
                    AllCandidacies.add(nuovaCandidatura)

                    // Pulizia
                    currentDraft = CandidacyDraft()
                    actualUser.candidacyDraftsByOfferId.remove(currentOfferIdApplying)

                    navController?.navigateTopLevel(Destination.LAVORO.route)
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
}*/

package com.example.superspan

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Patterns
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.*
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import org.intellij.lang.annotations.JdkConstants
import java.io.File


var currentDraft by mutableStateOf(CandidacyDraft())
var isReturnToSummary by mutableStateOf(false)
var candidacySourceRoute by mutableStateOf<String?>(null)

// --- UTILITY NAV ---
fun goToOfferPresentation(navController: NavController?) {
    val targetRoute = candidacySourceRoute ?: Destination.LAVORO.route
    if (targetRoute == Destination.LAVORO.route) {
        navController?.navigate(targetRoute) {
            popUpTo(targetRoute) { inclusive = true }
            launchSingleTop = true
        }
    } else {
        navController?.popBackStack(targetRoute, inclusive = false)
    }
}

// --- COMPONENTI UI COMUNI ---
@Composable
fun ExitDraftDialog(visible: Boolean, onDismiss: () -> Unit, onSave: () -> Unit, onDiscard: () -> Unit) {
    if (visible) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Esci dalla candidatura") },
            text = { Text("Vuoi salvare questa bozza prima di uscire?") },
            confirmButton = { TextButton(onClick = onSave) { Text("Salva") } },
            dismissButton = {
                Row {
                    TextButton(onClick = onDiscard) { Text("Scarta", color = com.example.superspan.ui.theme.AppError) }
                    TextButton(onClick = onDismiss) { Text("Annulla") }
                }
            }
        )
    }
}

@Composable
fun ApplyHeader(step: String, title: String, onBack: () -> Unit, onClose: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack, modifier = androidx.compose.ui.Modifier.background(androidx.compose.ui.graphics.Color.White.copy(alpha = 0.7f), androidx.compose.foundation.shape.CircleShape)) { Icon(androidx.compose.material.icons.Icons.AutoMirrored.Filled.ArrowBack, null, tint = androidx.compose.material3.MaterialTheme.colorScheme.primary)
            }
            androidx.compose.foundation.Image(
                painter = androidx.compose.ui.res.painterResource(id = R.drawable.logo_superspan),
                contentDescription = "Logo SuperSpan",
                modifier = Modifier.height(35.dp),
                contentScale = androidx.compose.ui.layout.ContentScale.Fit
            )
            IconButton(onClick = onClose) {
                Icon(Icons.Default.Close, null, tint = androidx.compose.material3.MaterialTheme.colorScheme.error)
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text("STEP $step DI 3", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
        Text(title, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = androidx.compose.material3.MaterialTheme.colorScheme.primary)
    }
}

// --- STEP 1: DATI E CV ---
@Composable
fun ApplyStep1(navController: NavController?, padding: PaddingValues) {
    val context = LocalContext.current
    val draft = getCandidacyDraftForOffer(actualUser, currentOfferIdApplying) ?: CandidacyDraft()

    var nome by remember { mutableStateOf(currentDraft.nome.ifEmpty { draft.nome.ifEmpty { actualUser.nome } }) }
    var cognome by remember { mutableStateOf(currentDraft.cognome.ifEmpty { draft.cognome.ifEmpty { actualUser.cognome } }) }
    var emailLavoro by remember { mutableStateOf(currentDraft.emailLavoro.ifEmpty { draft.emailLavoro.ifEmpty { actualUser.emailLavoro ?: actualUser.email } }) }
    var telefonoDigits by remember {
        val base = currentDraft.telefono.ifEmpty { draft.telefono.ifEmpty { actualUser.telefono ?: "" } }
        mutableStateOf(base.filter { it.isDigit() }.removePrefix("39"))
    }
    var cvName by remember { mutableStateOf(currentDraft.cvFileName.ifEmpty { draft.cvFileName.ifEmpty { actualUser.cvFileName ?: "" } }) }
    var cvPath by remember { mutableStateOf(currentDraft.cvPath.ifEmpty { draft.cvPath.ifEmpty { actualUser.cvPath ?: "" } }) }

    var showExitDialog by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }

    val isEmailValid = Patterns.EMAIL_ADDRESS.matcher(emailLavoro).matches()
    val isPhoneValid = telefonoDigits.length >= 9

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { selectedUri ->
            val dest = "cv_${actualUser.nome.lowercase()}_off${currentOfferIdApplying}.pdf"
            saveFileToInternalStorage(context, selectedUri, dest)?.let { path ->
                cvName = dest
                cvPath = path
                currentDraft = currentDraft.copy(cvFileName = dest, cvPath = path)
                android.widget.Toast.makeText(context, "Curriculum caricato con successo!", android.widget.Toast.LENGTH_SHORT).show()
            }
        }
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Svuota modulo") },
            text = { Text("Sei sicuro? I dati del profilo rimarranno salvati.") },
            confirmButton = {
                TextButton(onClick = {
                    nome = ""; cognome = ""; emailLavoro = ""; telefonoDigits = ""; cvName = ""; cvPath = ""
                    showResetDialog = false
                }) { Text("Sì, svuota", color = com.example.superspan.ui.theme.AppError) }
            },
            dismissButton = { TextButton(onClick = { showResetDialog = false }) { Text("Annulla") } }
        )
    }

    Column(Modifier.fillMaxSize().padding(padding).background(com.example.superspan.ui.theme.AppBackgroundBrush)) {
        BackHandler { showExitDialog = true }
        ApplyHeader("1", "I tuoi dati e CV", { showExitDialog = true }, { showExitDialog = true })

        ExitDraftDialog(showExitDialog, { showExitDialog = false }, {
            val d = CandidacyDraft(nome, cognome, emailLavoro, "+39 $telefonoDigits", cvName, cvPath, currentDraft.videoPath, Destination.APPLY_STEP_1.route)
            saveCandidacyDraftForOffer(actualUser, currentOfferIdApplying, d)
            currentDraft = d
            showExitDialog = false
            goToOfferPresentation(navController)
        }, {
            actualUser.candidacyDraftsByOfferId.remove(currentOfferIdApplying)
            showExitDialog = false
            goToOfferPresentation(navController)
        })

        Column(Modifier.padding(horizontal = 24.dp).verticalScroll(rememberScrollState())) {
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                Text("Tutti i campi sono obbligatori", fontSize = 12.sp, color = Color.Gray)
                TextButton(onClick = { showResetDialog = true }) { Icon(Icons.Default.Refresh, null); Text(" Reset") }
            }
            EditTextField("Nome", nome, KeyboardType.Text) { nome = it }
            EditTextField("Cognome", cognome, KeyboardType.Text) { cognome = it }
            EditTextField("Email di contatto", emailLavoro, KeyboardType.Email, !isEmailValid && emailLavoro.isNotEmpty(), "Email non valida") { emailLavoro = it }

            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                OutlinedTextField("+39", {}, readOnly = true, label = { Text("Pre") }, modifier = Modifier.width(80.dp), shape = RoundedCornerShape(20.dp))
                Spacer(Modifier.width(8.dp))
                EditTextField("Telefono", telefonoDigits, KeyboardType.Phone, false, "", PhoneVisualTransformation(), Modifier.weight(1f)) {
                    if (it.all { c -> c.isDigit() } && it.length <= 10) telefonoDigits = it
                }
            }

            Text("Documento Curriculum", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Surface(
                onClick = { launcher.launch("application/pdf") },
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                color = if (cvPath.isNotEmpty()) Color(0xFFE8F5E9) else Color(0xFFFFF3E0),
                border = androidx.compose.foundation.BorderStroke(1.dp, if (cvPath.isNotEmpty()) Color(0xFF81C784) else Color(0xFFFFB74D))
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(if (cvPath.isNotEmpty()) Icons.Default.CheckCircle else Icons.Default.FileUpload, null, tint = if (cvPath.isNotEmpty()) Color(0xFF388E3C) else Color.Gray)
                    Spacer(Modifier.width(12.dp))
                    Text(if (cvName.isEmpty()) "Scegli il tuo CV (PDF)" else cvName, Modifier.weight(1f))
                }
            }

            Spacer(Modifier.height(30.dp))
            Button(
                onClick = {
                    currentDraft = CandidacyDraft(nome, cognome, emailLavoro, "+39 $telefonoDigits", cvName, cvPath, currentDraft.videoPath)
                    if (isReturnToSummary) { isReturnToSummary = false; navController?.popBackStack() }
                    else { navController?.navigate(Destination.APPLY_STEP_2_INTRO.route) }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = nome.isNotBlank() && cognome.isNotBlank() && isEmailValid && isPhoneValid,
                shape = CircleShape
            ) { Text(if (isReturnToSummary) "Torna al riepilogo" else "Avanti: Video Presentazione") }
            Spacer(Modifier.height(20.dp))
        }
    }
}

// --- STEP 2: INTRO & REVIEW ---
@Composable
fun ApplyStep2Intro(navController: NavController?, padding: PaddingValues) {
    var showExitDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    
    val videoFile = File(currentDraft.videoPath ?: "")
    val hasVideo = currentDraft.videoPath != null && videoFile.exists()
    
    val exoPlayer = remember(currentDraft.videoPath) {
        androidx.media3.exoplayer.ExoPlayer.Builder(context).build().apply {
            if (hasVideo) {
                setMediaItem(androidx.media3.common.MediaItem.fromUri(android.net.Uri.fromFile(videoFile)))
                prepare()
            }
        }
    }
    DisposableEffect(Unit) { onDispose { exoPlayer.release() } }

    val videoPickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { selectedUri ->
            val destinationName = "video_galleria_${actualUser.nome.lowercase()}_${currentOfferIdApplying}.mp4"
            val savedPath = saveFileToInternalStorage(context, selectedUri, destinationName)
            if (savedPath != null) {
                currentDraft = currentDraft.copy(videoPath = savedPath)
                android.widget.Toast.makeText(context, "Video caricato con successo!", android.widget.Toast.LENGTH_SHORT).show()
            }
        }
    }

    Column(Modifier.fillMaxSize().padding(padding).background(com.example.superspan.ui.theme.AppBackgroundBrush), horizontalAlignment = Alignment.CenterHorizontally) {
        ApplyHeader("2", if (hasVideo) "Rivedi il tuo video" else "Preparati al Video", { navController?.popBackStack() }, { showExitDialog = true })
        ExitDraftDialog(showExitDialog, { showExitDialog = false }, {
            saveCandidacyDraftForOffer(actualUser, currentOfferIdApplying, currentDraft.copy(lastStepRoute = Destination.APPLY_STEP_2_INTRO.route))
            showExitDialog = false
            goToOfferPresentation(navController)
        }, {
            actualUser.candidacyDraftsByOfferId.remove(currentOfferIdApplying)
            showExitDialog = false
            goToOfferPresentation(navController)
        })

        if (hasVideo) {
            Spacer(Modifier.height(10.dp))
            Box(Modifier.weight(1f).fillMaxWidth(0.9f).clip(RoundedCornerShape(20.dp)).background(Color.Black)) {
                AndroidView(factory = { ctx -> androidx.media3.ui.PlayerView(ctx).apply { player = exoPlayer; useController = true } }, Modifier.fillMaxSize())
            }
            Spacer(Modifier.height(16.dp))
            Row(Modifier.fillMaxWidth(0.9f), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { navController?.navigate(Destination.APPLY_STEP_2_RECORD.route) }, modifier = Modifier.weight(1f).height(50.dp), colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray), shape = CircleShape) {
                    Icon(Icons.Default.Videocam, null, Modifier.size(18.dp)); Spacer(Modifier.width(4.dp)); Text("Registra", fontSize = 12.sp)
                }
                OutlinedButton(onClick = { videoPickerLauncher.launch("video/*") }, modifier = Modifier.weight(1f).height(50.dp), shape = CircleShape) {
                    Icon(Icons.Default.PhotoLibrary, null, Modifier.size(18.dp), tint = Color.DarkGray); Spacer(Modifier.width(4.dp)); Text("Galleria", color = Color.DarkGray, fontSize = 12.sp)
                }
            }
            Spacer(Modifier.height(16.dp))
            Button(onClick = {
                if (isReturnToSummary) {
                    isReturnToSummary = false
                    navController?.navigate(Destination.APPLY_STEP_3.route) { popUpTo(Destination.APPLY_STEP_3.route) { inclusive = true } }
                } else navController?.navigate(Destination.APPLY_STEP_3.route)
            }, Modifier.padding(vertical = 24.dp).height(56.dp).width(200.dp), shape = CircleShape, colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)) {
                Text("Avanti")
            }
        } else {
            Column(Modifier.padding(30.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)).background(Color(0xFFF5F5F5)).padding(24.dp)) {
                    Column {
                        Text("${actualUser.nome}, ecco cosa dire:", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("• Presentati\n• Esperienze lavorative\n• Perché SuperSpan?", Modifier.padding(vertical = 10.dp), lineHeight = 24.sp)
                        Text("Hai 30 secondi.", fontWeight = FontWeight.Bold, color = com.example.superspan.ui.theme.AppError)
                    }
                }
                Spacer(Modifier.weight(1f))
                Button(onClick = { navController?.navigate(Destination.APPLY_STEP_2_RECORD.route) }, Modifier.height(60.dp).fillMaxWidth(0.8f), colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray), shape = CircleShape) {
                    Icon(Icons.Default.Videocam, null); Spacer(Modifier.width(8.dp)); Text("Inizia Registrazione")
                }
                Spacer(Modifier.height(16.dp))
                OutlinedButton(onClick = { videoPickerLauncher.launch("video/*") }, Modifier.height(60.dp).fillMaxWidth(0.8f), shape = CircleShape) {
                    Icon(Icons.Default.PhotoLibrary, null, tint = Color.DarkGray); Spacer(Modifier.width(8.dp)); Text("Carica da Galleria", color = Color.DarkGray)
                }
            }
        }
    }
}

// --- STEP 2b: RECORDING (CON FIX SALVATAGGIO) ---
@Composable
fun ApplyStep2Record(navController: NavController?, padding: PaddingValues) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val activeRecording = remember { mutableStateOf<androidx.camera.video.Recording?>(null) }
    val videoCapture = remember {
        val r = androidx.camera.video.Recorder.Builder().setQualitySelector(androidx.camera.video.QualitySelector.from(androidx.camera.video.Quality.SD)).build()
        androidx.camera.video.VideoCapture.withOutput(r)
    }

    var countdown by remember { mutableStateOf(3) }
    var seconds by remember { mutableStateOf(0) }
    var isRecording by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf("") }
    val videoFile = remember { File(context.filesDir, "video_${actualUser.email.replace("@", "_")}_off${currentOfferIdApplying}.mp4") }

    var hasPermissions by remember {
        mutableStateOf(ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)
    }

    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { p ->
        hasPermissions = p[Manifest.permission.CAMERA] == true && p[Manifest.permission.RECORD_AUDIO] == true
    }

    LaunchedEffect(hasPermissions) {
        if (!hasPermissions) permissionLauncher.launch(arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO))
        else {
            while (countdown > 0) { delay(1000); countdown-- }
            isRecording = true
            val opts = androidx.camera.video.FileOutputOptions.Builder(videoFile).build()
            activeRecording.value = videoCapture.output.prepareRecording(context, opts).withAudioEnabled()
                .start(ContextCompat.getMainExecutor(context)) { event ->
                    if (event is androidx.camera.video.VideoRecordEvent.Finalize) {
                        if (!event.hasError()) {
                            currentDraft = currentDraft.copy(videoPath = videoFile.absolutePath)
                            android.widget.Toast.makeText(context, "Video registrato con successo!", android.widget.Toast.LENGTH_SHORT).show()
                        }
                    }
                }
        }
    }

    LaunchedEffect(isRecording) {
        if (isRecording) {
            while (seconds < 30 && isRecording) { delay(1000); seconds++ }
            if (seconds >= 30) {
                activeRecording.value?.stop()
                isRecording = false
                delay(500)
                navController?.popBackStack()
            }
        }
    }

    Box(Modifier.fillMaxSize().background(Color.Black)) {
        if (hasPermissions && countdown == 0) {
            AndroidView(factory = { ctx ->
                androidx.camera.view.PreviewView(ctx).apply {
                    val cf = androidx.camera.lifecycle.ProcessCameraProvider.getInstance(ctx)
                    cf.addListener({
                        val cp = cf.get()
                        val p = androidx.camera.core.Preview.Builder().build()
                        p.setSurfaceProvider(this.surfaceProvider)
                        cp.unbindAll()
                        cp.bindToLifecycle(lifecycleOwner, androidx.camera.core.CameraSelector.DEFAULT_FRONT_CAMERA, p, videoCapture)
                    }, ContextCompat.getMainExecutor(ctx))
                }
            }, Modifier.fillMaxSize())
        }

        if (countdown > 0) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(countdown.toString(), fontSize = 100.sp, color = Color.White, fontWeight = FontWeight.Bold)
            }
        } else {
            // BARRA DI SCORRIMENTO PIÙ SPESSA (12.dp)
            LinearProgressIndicator(
                progress = { seconds / 30f },
                modifier = Modifier.fillMaxWidth().height(12.dp).padding(top = 10.dp),
                color = com.example.superspan.ui.theme.AppError,
                trackColor = Color.White.copy(0.2f)
            )
            Text("REC 00:${seconds.toString().padStart(2, '0')}", Modifier.padding(25.dp).align(Alignment.TopStart), color = com.example.superspan.ui.theme.AppError, fontWeight = FontWeight.Bold)
        }

        if (isRecording) {
            Box(Modifier.align(Alignment.BottomCenter).padding(bottom = 60.dp).size(80.dp).border(4.dp, Color.White, CircleShape).padding(8.dp).clip(CircleShape).background(com.example.superspan.ui.theme.AppError).clickable {
                if (seconds < 15) { errorMsg = "Video troppo corto! (Min 15s)" }
                else {
                    activeRecording.value?.stop()
                    isRecording = false
                    navController?.popBackStack()
                }
            })
            if (errorMsg.isNotEmpty()) {
                Surface(Modifier.align(Alignment.Center).padding(bottom = 100.dp), color = Color.Black.copy(0.7f), shape = RoundedCornerShape(8.dp)) {
                    Text(errorMsg, color = Color.Yellow, modifier = Modifier.padding(12.dp))
                }
            }
        }
    }
}

// --- STEP 3: SUMMARY ---
@Composable
fun ApplyStep3(navController: NavController?, padding: PaddingValues) {
    var showEditConfirm by remember { mutableStateOf<String?>(null) }
    var previewContent by remember { mutableStateOf<String?>(null) }
    var showPreviewVideo by remember { mutableStateOf(false) }
    var showPreviewCV by remember { mutableStateOf(false) }
    var showExitDialog by remember { mutableStateOf(false) }
    var showSendConfirm by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val exoPlayer = remember {
        androidx.media3.exoplayer.ExoPlayer.Builder(context).build()
    }

    DisposableEffect(Unit) {
        onDispose { exoPlayer.release() }
    }

    if (showEditConfirm != null) {
        AlertDialog(onDismissRequest = { showEditConfirm = null }, title = { Text("Modifica?") }, text = { Text("Tornerai allo step di $showEditConfirm.") },
            confirmButton = { TextButton(onClick = { isReturnToSummary = true; val t = if (showEditConfirm == "Dati") Destination.APPLY_STEP_1.route else Destination.APPLY_STEP_2_INTRO.route; showEditConfirm = null; navController?.navigate(t) }) { Text("Sì") } },
            dismissButton = { TextButton(onClick = { showEditConfirm = null }) { Text("No") } })
    }

    var pdfBitmap by remember { mutableStateOf<android.graphics.Bitmap?>(null) }

    LaunchedEffect(currentDraft.cvPath) {
        if (currentDraft.cvPath.isNotEmpty()) {
            pdfBitmap = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                try {
                    val file = java.io.File(currentDraft.cvPath)
                    if (file.exists() && currentDraft.cvPath.endsWith(".pdf", ignoreCase = true)) {
                        val pfd = android.os.ParcelFileDescriptor.open(file, android.os.ParcelFileDescriptor.MODE_READ_ONLY)
                        val renderer = android.graphics.pdf.PdfRenderer(pfd)
                        var bitmap: android.graphics.Bitmap? = null
                        if (renderer.pageCount > 0) {
                            val page = renderer.openPage(0)
                            val width = 800
                            val height = (width.toFloat() / page.width * page.height).toInt()
                            bitmap = android.graphics.Bitmap.createBitmap(width, height, android.graphics.Bitmap.Config.ARGB_8888)
                            val canvas = android.graphics.Canvas(bitmap)
                            canvas.drawColor(android.graphics.Color.WHITE)
                            page.render(bitmap, null, null, android.graphics.pdf.PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                            page.close()
                        }
                        renderer.close()
                        pfd.close()
                        bitmap
                    } else null
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
            }
        }
    }

    if (showPreviewCV) {
        AlertDialog(
            onDismissRequest = { showPreviewCV = false },
            title = { Text("Anteprima Curriculum", fontWeight = FontWeight.Bold) },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    if (pdfBitmap != null) {
                        androidx.compose.foundation.Image(
                            bitmap = pdfBitmap!!.asImageBitmap(),
                            contentDescription = "Anteprima PDF",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(350.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp)),
                            contentScale = androidx.compose.ui.layout.ContentScale.Fit
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(currentDraft.cvFileName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    } else {
                        Icon(Icons.Default.Description, null, modifier = Modifier.size(64.dp), tint = Color(0xFFD32F2F))
                        Spacer(Modifier.height(16.dp))
                        Text(currentDraft.cvFileName.ifEmpty { "Nessun file selezionato" }, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(8.dp))
                        Text("Il file PDF è stato allegato correttamente ed è pronto per essere inviato con la tua candidatura.", textAlign = TextAlign.Center)
                    }
                }
            },
            confirmButton = { Button(onClick = { showPreviewCV = false }) { Text("Chiudi") } }
        )
    }

    if (showPreviewVideo) {
        LaunchedEffect(currentDraft.videoPath) {
            if (currentDraft.videoPath != null) {
                val mediaItem = androidx.media3.common.MediaItem.fromUri(android.net.Uri.fromFile(java.io.File(currentDraft.videoPath!!)))
                exoPlayer.setMediaItem(mediaItem)
                exoPlayer.prepare()
                exoPlayer.play()
            }
        }
        AlertDialog(
            onDismissRequest = { 
                showPreviewVideo = false
                exoPlayer.stop() 
            },
            title = { Text("Anteprima Video", fontWeight = FontWeight.Bold) },
            text = {
                if (currentDraft.videoPath != null) {
                    Box(modifier = Modifier.fillMaxWidth().height(300.dp)) {
                        androidx.compose.ui.viewinterop.AndroidView(
                            factory = { ctx ->
                                androidx.media3.ui.PlayerView(ctx).apply {
                                    player = exoPlayer
                                    useController = true
                                }
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                } else {
                    Text("Nessun video registrato.", Modifier.padding(16.dp))
                }
            },
            confirmButton = { 
                Button(onClick = { 
                    showPreviewVideo = false
                    exoPlayer.stop() 
                }) { Text("Chiudi") } 
            }
        )
    }

    Column(Modifier.fillMaxSize().padding(padding).background(com.example.superspan.ui.theme.AppBackgroundBrush)) {
        ApplyHeader("3", "Riepilogo e Invio", { navController?.popBackStack() }, { showExitDialog = true })
        ExitDraftDialog(showExitDialog, { showExitDialog = false }, {
            saveCandidacyDraftForOffer(actualUser, currentOfferIdApplying, currentDraft.copy(lastStepRoute = Destination.APPLY_STEP_3.route))
            showExitDialog = false
            goToOfferPresentation(navController)
        }, {
            actualUser.candidacyDraftsByOfferId.remove(currentOfferIdApplying)
            showExitDialog = false
            goToOfferPresentation(navController)
        })

        if (showSendConfirm) {
            AlertDialog(
                onDismissRequest = { showSendConfirm = false },
                title = { Text("Conferma Invio") },
                text = { Text("Sei sicuro di voler inviare definitivamente la tua candidatura?") },
                confirmButton = {
                    TextButton(onClick = {
                        showSendConfirm = false
                        val c = Candidacy(id = AllCandidacies.size + 1, userEmail = actualUser.email, offerId = currentOfferIdApplying, nome = currentDraft.nome, cognome = currentDraft.cognome, emailContatto = currentDraft.emailLavoro, telefono = currentDraft.telefono, cvPath = currentDraft.cvPath, videoPath = currentDraft.videoPath)
                        AllCandidacies.add(c)
                        actualUser.candidacyDraftsByOfferId.remove(currentOfferIdApplying)
                        currentDraft = CandidacyDraft()
                        android.widget.Toast.makeText(context, "Candidatura inviata con successo", android.widget.Toast.LENGTH_SHORT).show()
                        navController?.navigate(Destination.LAVORO.route) { popUpTo(Destination.LAVORO.route) { inclusive = true } }
                    }) {
                        Text("Invia", color = Color(0xFF388E3C))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showSendConfirm = false }) { Text("Annulla", color = Color.Gray) }
                }
            )
        }

        Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Paolo, controlla un'ultima volta:", color = Color.Gray)
            SummaryInteractiveRow(Icons.Default.Person, "Candidato", "${currentDraft.nome} ${currentDraft.cognome}", { showEditConfirm = "Dati" }) { previewContent = "Nome: ${currentDraft.nome}\nCognome: ${currentDraft.cognome}" }
            SummaryInteractiveRow(Icons.Default.Email, "Email", currentDraft.emailLavoro, { showEditConfirm = "Dati" }) { previewContent = "Email: ${currentDraft.emailLavoro}" }
            SummaryInteractiveRow(Icons.Default.Phone, "Telefono", currentDraft.telefono, { showEditConfirm = "Dati" }) { previewContent = "Telefono: ${currentDraft.telefono}" }
            SummaryInteractiveRow(Icons.Default.Description, "CV", currentDraft.cvFileName.ifEmpty { "Nessun file" }, { showEditConfirm = "Dati" }) { showPreviewCV = true }
            SummaryInteractiveRow(Icons.Default.Videocam, "Video", if (currentDraft.videoPath != null) "Registrato correttamente" else "Nessun video", { showEditConfirm = "Video" }) { showPreviewVideo = true }

            Spacer(Modifier.weight(1f))
            Button(onClick = { showSendConfirm = true }, Modifier.fillMaxWidth().height(60.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF388E3C)), shape = CircleShape) {
                Text("CONFERMA E INVIA", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun SummaryInteractiveRow(icon: ImageVector, label: String, value: String, onEdit: () -> Unit, onView: () -> Unit) {
    Surface(
        onClick = onView,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFF9F9F9),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically // FIX ALIGNMENT
        ) {
            Icon(icon, null, tint = Color.Gray)
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(label, fontSize = 11.sp, color = Color.Gray)
                Text(value, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }
            IconButton(onClick = onEdit) { Icon(Icons.Default.Edit, "Modifica", Modifier.size(20.dp), tint = Color(0xFF388E3C)) }
        }
    }
}
