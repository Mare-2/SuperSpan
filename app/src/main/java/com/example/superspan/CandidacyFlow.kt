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
import androidx.lifecycle.compose.LocalLifecycleOwner
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
        ModernAlertDialog(
            onDismissRequest = onDismiss,
            title = "Salvare la bozza?",
            text = "Vuoi salvare i progressi di questa candidatura per riprenderla in un secondo momento?",
            icon = Icons.Default.Save,
            confirmText = "Salva ed Esci",
            onConfirm = onSave,
            altText = "Scarta Modifiche",
            onAlt = onDiscard,
            dismissText = "Annulla",
            onDismiss = onDismiss,
            isDestructive = false,
            isAltDestructive = true
        )
    }
}

@Composable
fun ApplyHeader(step: String, title: String, onBack: () -> Unit, onClose: () -> Unit) {
    val progress = when(step) {
        "1" -> 0.33f
        "2" -> 0.66f
        "3" -> 1.0f
        else -> 0f
    }
    Column(
        modifier = Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack, modifier = Modifier.background(Color.White.copy(alpha = 0.7f), CircleShape)) { 
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = com.example.superspan.ui.theme.LogoLeft)
            }
            Image(
                painter = androidx.compose.ui.res.painterResource(id = R.drawable.superspan),
                contentDescription = "Logo SuperSpan",
                modifier = Modifier.height(28.dp),
                contentScale = androidx.compose.ui.layout.ContentScale.Fit
            )
            IconButton(onClick = onClose, modifier = Modifier.background(Color.White.copy(alpha = 0.7f), CircleShape)) {
                Icon(Icons.Default.Close, null, tint = MaterialTheme.colorScheme.error)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth(0.6f).height(8.dp).clip(CircleShape),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text("Step $step di 3", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(4.dp))
        Text(title, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onSurface)
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
    var cvName by remember { mutableStateOf(currentDraft.cvFileName.ifEmpty { draft.cvFileName.ifEmpty { actualUser.cvFileName?.substringAfterLast('/') ?: "" } }) }
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
        ModernAlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = "Svuota modulo",
            text = "Sei sicuro? I dati del profilo rimarranno salvati.",
            icon = Icons.Default.Delete,
            isDestructive = true,
            confirmText = "Sì, svuota",
            onConfirm = {
                nome = ""; cognome = ""; emailLavoro = ""; telefonoDigits = ""; cvName = ""; cvPath = ""
                showResetDialog = false
            },
            dismissText = "Annulla",
            onDismiss = { showResetDialog = false }
        )
    }

    Column(Modifier.fillMaxSize().padding(padding)) {
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

        Box(modifier = Modifier.fillMaxSize()) {
            Column(Modifier.fillMaxSize().padding(horizontal = 24.dp).verticalScroll(rememberScrollState())) {
                androidx.compose.material3.Card(
                    colors = androidx.compose.material3.CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    elevation = androidx.compose.material3.CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.padding(20.dp)) {
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
                            EditTextField("Telefono", telefonoDigits, KeyboardType.Phone, !isPhoneValid && telefonoDigits.isNotEmpty(), "Numero non valido", PhoneVisualTransformation(), Modifier.weight(1f)) {
                                if (it.all { c -> c.isDigit() } && it.length <= 10) telefonoDigits = it
                            }
                        }
            
                        Text("Documento Curriculum", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Surface(
                            onClick = { launcher.launch("application/pdf") },
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                            shape = RoundedCornerShape(16.dp),
                            color = if (cvPath.isNotEmpty()) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else Color(0xFFF5F5F5),
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (cvPath.isNotEmpty()) MaterialTheme.colorScheme.primary else Color.LightGray)
                        ) {
                            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(if (cvPath.isNotEmpty()) Icons.Default.CheckCircle else Icons.Default.FileUpload, null, tint = if (cvPath.isNotEmpty()) MaterialTheme.colorScheme.primary else Color.Gray)
                                Spacer(Modifier.width(12.dp))
                                Text(if (cvName.isEmpty()) "Scegli il tuo CV (PDF)" else cvName, Modifier.weight(1f))
                            }
                        }
                    }
                }
                Spacer(Modifier.height(100.dp))
            }
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    onClick = {
                        currentDraft = CandidacyDraft(nome, cognome, emailLavoro, "+39 $telefonoDigits", cvName, cvPath, currentDraft.videoPath)
                        if (isReturnToSummary) { isReturnToSummary = false; navController?.popBackStack() }
                        else { navController?.navigate(Destination.APPLY_STEP_2_INTRO.route) }
                    },
                    modifier = Modifier.height(55.dp),
                    contentPadding = PaddingValues(horizontal = 32.dp),
                    enabled = nome.isNotBlank() && cognome.isNotBlank() && isEmailValid && isPhoneValid && cvPath.isNotEmpty(),
                    shape = CircleShape
                ) { Text(if (isReturnToSummary) "Torna al riepilogo" else "Avanti: Video Presentazione") }
            }
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
    
    val exoPlayer = remember {
        androidx.media3.exoplayer.ExoPlayer.Builder(context).build()
    }
    DisposableEffect(Unit) { onDispose { exoPlayer.release() } }

    LaunchedEffect(currentDraft.videoPath) {
        if (hasVideo) {
            exoPlayer.setMediaItem(androidx.media3.common.MediaItem.fromUri(android.net.Uri.fromFile(videoFile)))
            exoPlayer.prepare()
        } else {
            exoPlayer.clearMediaItems()
        }
    }

    val videoPickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { selectedUri ->
            val destinationName = "video_galleria_${actualUser.nome.lowercase()}_${currentOfferIdApplying}_${System.currentTimeMillis()}.mp4"
            val savedPath = saveFileToInternalStorage(context, selectedUri, destinationName)
            if (savedPath != null) {
                currentDraft = currentDraft.copy(videoPath = savedPath)
                android.widget.Toast.makeText(context, "Video caricato con successo!", android.widget.Toast.LENGTH_SHORT).show()
            }
        }
    }

    Column(Modifier.fillMaxSize().padding(padding), horizontalAlignment = Alignment.CenterHorizontally) {
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
                Button(
                    onClick = { navController?.navigate(Destination.APPLY_STEP_2_RECORD.route) }, 
                    modifier = Modifier.weight(1f).height(55.dp), 
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEEEEEE), contentColor = Color.Black), 
                    shape = CircleShape
                ) {
                    Icon(Icons.Default.Videocam, null, Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Registra", fontSize = 14.sp)
                }
                Button(
                    onClick = { videoPickerLauncher.launch("video/*") }, 
                    modifier = Modifier.weight(1f).height(55.dp), 
                    shape = CircleShape, 
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEEEEEE), contentColor = Color.Black)
                ) {
                    Icon(Icons.Default.PhotoLibrary, null, Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Galleria", fontSize = 14.sp)
                }
            }
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = {
                    if (isReturnToSummary) {
                        isReturnToSummary = false
                        navController?.navigate(Destination.APPLY_STEP_3.route) { popUpTo(Destination.APPLY_STEP_3.route) { inclusive = true } }
                    } else navController?.navigate(Destination.APPLY_STEP_3.route)
                }, 
                Modifier.padding(vertical = 24.dp).height(55.dp), 
                contentPadding = PaddingValues(horizontal = 32.dp), 
                shape = CircleShape, 
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Avanti")
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(30.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                androidx.compose.material3.Card(
                    colors = androidx.compose.material3.CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    elevation = androidx.compose.material3.CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.padding(24.dp)) {
                        Text("${actualUser.nome}, ecco cosa dire:", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("• Presentati\n• Esperienze lavorative\n• Perché SuperSpan?", Modifier.padding(vertical = 10.dp), lineHeight = 24.sp)
                        Text("Il video deve durare tra i 15 e i 30 secondi", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                }
                Spacer(Modifier.height(40.dp))
                Button(
                    onClick = { navController?.navigate(Destination.APPLY_STEP_2_RECORD.route) }, 
                    Modifier.height(55.dp), 
                    contentPadding = PaddingValues(horizontal = 32.dp), 
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary), 
                    shape = CircleShape
                ) {
                    Icon(Icons.Default.Videocam, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Inizia Registrazione")
                }
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = { videoPickerLauncher.launch("video/*") }, 
                    Modifier.height(55.dp), 
                    contentPadding = PaddingValues(horizontal = 32.dp), 
                    shape = CircleShape, 
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFEEEEEE), 
                        contentColor = Color.Black
                    )
                ) {
                    Icon(Icons.Default.PhotoLibrary, null, tint = Color.Black)
                    Spacer(Modifier.width(8.dp))
                    Text("Carica da Galleria")
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
    // Numero del tentativo: incrementarlo fa ripartire la registrazione da zero (video troppo corto)
    var attempt by remember { mutableStateOf(0) }
    // Motivo dello stop letto dentro il callback: 0 = nessuno, 1 = salva (valido), 2 = scarta e ricomincia
    val stopMode = remember { mutableStateOf(0) }

    LaunchedEffect(errorMsg) {
        if (errorMsg.isNotEmpty()) {
            delay(2000)
            errorMsg = ""
        }
    }
    
    val videoFile = remember(attempt) { File(context.filesDir, "video_${actualUser.email.replace("@", "_")}_off${currentOfferIdApplying}_${System.currentTimeMillis()}.mp4") }

    var hasPermissions by remember {
        mutableStateOf(ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)
    }

    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { p ->
        hasPermissions = p[Manifest.permission.CAMERA] == true && p[Manifest.permission.RECORD_AUDIO] == true
    }

    LaunchedEffect(hasPermissions, attempt) {
        if (!hasPermissions) {
            permissionLauncher.launch(arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO))
        } else {
            // (Ri)partenza pulita
            stopMode.value = 0
            seconds = 0
            countdown = 3
            while (countdown > 0) { delay(1000); countdown-- }
            isRecording = true
            val opts = androidx.camera.video.FileOutputOptions.Builder(videoFile).build()
            activeRecording.value = videoCapture.output.prepareRecording(context, opts).withAudioEnabled()
                .start(ContextCompat.getMainExecutor(context)) { event ->
                    if (event is androidx.camera.video.VideoRecordEvent.Finalize) {
                        when (stopMode.value) {
                            2 -> {
                                // Troppo corto: buttiamo il file e ricominciamo da capo
                                videoFile.delete()
                                seconds = 0
                                attempt++
                            }
                            1 -> {
                                // Registrazione valida: salviamo
                                if (!event.hasError()) {
                                    currentDraft = currentDraft.copy(videoPath = videoFile.absolutePath)
                                    android.widget.Toast.makeText(context, "Video registrato con successo!", android.widget.Toast.LENGTH_SHORT).show()
                                }
                            }
                            else -> { /* finalize inatteso: non facciamo nulla */ }
                        }
                    }
                }
        }
    }

    LaunchedEffect(isRecording) {
        if (isRecording) {
            while (seconds < 30 && isRecording) { delay(1000); seconds++ }
            if (seconds >= 30) {
                stopMode.value = 1
                activeRecording.value?.stop()
                isRecording = false
                delay(500)
                navController?.popBackStack()
            }
        }
    }

    Box(Modifier.fillMaxSize().background(Color.Black)) {
        // Anteprima SEMPRE montata (finché ci sono i permessi): il countdown viene disegnato sopra.
        // Così non viene rimontata a ogni riavvio, evitando che 'unbindAll' stacchi la fotocamera
        // mentre parte la nuova registrazione (che altrimenti finiva in errore e non veniva salvata).
        if (hasPermissions) {
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
            Column(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.6f))
                    .padding(top = padding.calculateTopPadding())
            ) {
                LinearProgressIndicator(
                    progress = { seconds / 30f },
                    modifier = Modifier.fillMaxWidth().height(6.dp),
                    color = com.example.superspan.ui.theme.AppError,
                    trackColor = Color.White.copy(alpha = 0.3f)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(com.example.superspan.ui.theme.AppError)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "00:${seconds.toString().padStart(2, '0')}",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                    Text(
                        text = "00:30",
                        color = Color.White.copy(alpha = 0.7f),
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp
                    )
                }
            }
        }

        if (isRecording) {
            Box(Modifier.align(Alignment.BottomCenter).padding(bottom = 60.dp).size(80.dp).border(4.dp, Color.White, CircleShape).padding(8.dp).clip(CircleShape).background(com.example.superspan.ui.theme.AppError).clickable {
                if (seconds < 15) {
                    // Troppo corto: avvisiamo e facciamo ripartire la registrazione da zero.
                    // Fermiamo la registrazione corrente; il riavvio avviene nel callback Finalize
                    // (quando il file è chiuso), così non si sovrappongono due registrazioni.
                    errorMsg = "Troppo corto! Si ricomincia da capo."
                    stopMode.value = 2
                    isRecording = false
                    activeRecording.value?.stop()
                } else {
                    stopMode.value = 1
                    isRecording = false
                    activeRecording.value?.stop()
                    navController?.popBackStack()
                }
            })
        }

        // Messaggio (es. "troppo corto"): fuori dal blocco isRecording, così resta visibile
        // anche durante il breve riavvio, quando isRecording è momentaneamente false.
        if (errorMsg.isNotEmpty()) {
            Surface(Modifier.align(Alignment.Center).padding(bottom = 100.dp), color = Color.Black.copy(0.7f), shape = RoundedCornerShape(8.dp)) {
                Text(errorMsg, color = Color.Yellow, modifier = Modifier.padding(12.dp))
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
        ModernAlertDialog(
            onDismissRequest = { showEditConfirm = null },
            title = "Modifica?",
            text = "Tornerai allo step di $showEditConfirm.",
            confirmText = "Sì",
            onConfirm = { isReturnToSummary = true; val t = if (showEditConfirm == "Dati") Destination.APPLY_STEP_1.route else Destination.APPLY_STEP_2_INTRO.route; showEditConfirm = null; navController?.navigate(t) },
            dismissText = "No",
            onDismiss = { showEditConfirm = null }
        )
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
        ModernAlertDialog(
            onDismissRequest = { showPreviewCV = false },
            title = "Anteprima Curriculum",
            content = {
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
            confirmText = "Chiudi",
            onConfirm = { showPreviewCV = false }
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
        ModernAlertDialog(
            onDismissRequest = { 
                showPreviewVideo = false
                exoPlayer.stop() 
            },
            title = "Anteprima Video",
            content = {
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
            confirmText = "Chiudi",
            onConfirm = {
                showPreviewVideo = false
                exoPlayer.stop()
            }
        )
    }

    Column(Modifier.fillMaxSize().padding(padding)) {
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
            ModernAlertDialog(
                onDismissRequest = { showSendConfirm = false },
                title = "Conferma Invio",
                text = "Sei sicuro di voler inviare definitivamente la tua candidatura?",
                confirmText = "Invia",
                onConfirm = {
                    showSendConfirm = false
                    val c = Candidacy(id = AllCandidacies.size + 1, userEmail = actualUser.email, offerId = currentOfferIdApplying, nome = currentDraft.nome, cognome = currentDraft.cognome, emailContatto = currentDraft.emailLavoro, telefono = currentDraft.telefono, cvPath = currentDraft.cvPath, videoPath = currentDraft.videoPath)
                    AllCandidacies.add(c)
                    actualUser.candidacyDraftsByOfferId.remove(currentOfferIdApplying)
                    currentDraft = CandidacyDraft()
                    android.widget.Toast.makeText(context, "Candidatura inviata con successo", android.widget.Toast.LENGTH_SHORT).show()
                    navController?.navigate(Destination.LAVORO.route) { popUpTo(Destination.LAVORO.route) { inclusive = true } }
                },
                dismissText = "Annulla",
                onDismiss = { showSendConfirm = false }
            )
        }

        Box(modifier = Modifier.fillMaxSize()) {
            Column(Modifier.fillMaxSize().padding(horizontal = 24.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("${currentDraft.nome.ifEmpty { actualUser.nome }}, controlla un'ultima volta:", color = Color.Gray)
                SummaryInteractiveRow(Icons.Default.Person, "Candidato", "${currentDraft.nome} ${currentDraft.cognome}", { showEditConfirm = "Dati" }) { previewContent = "Nome: ${currentDraft.nome}\nCognome: ${currentDraft.cognome}" }
                SummaryInteractiveRow(Icons.Default.Email, "Email", currentDraft.emailLavoro, { showEditConfirm = "Dati" }) { previewContent = "Email: ${currentDraft.emailLavoro}" }
                SummaryInteractiveRow(Icons.Default.Phone, "Telefono", currentDraft.telefono, { showEditConfirm = "Dati" }) { previewContent = "Telefono: ${currentDraft.telefono}" }
                SummaryInteractiveRow(Icons.Default.Description, "CV", currentDraft.cvFileName.ifEmpty { "Nessun file" }, { showEditConfirm = "Dati" }) { showPreviewCV = true }
                SummaryInteractiveRow(Icons.Default.Videocam, "Video", if (currentDraft.videoPath != null) "Registrato correttamente" else "Nessun video", { showEditConfirm = "Video" }) { showPreviewVideo = true }

                Spacer(Modifier.height(100.dp))
            }
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Button(onClick = { showSendConfirm = true }, Modifier.height(55.dp), contentPadding = PaddingValues(horizontal = 32.dp), colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary), shape = CircleShape) {
                    Text("Conferma e Invia", fontWeight = FontWeight.Bold)
                }
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
        color = Color.White,
        shadowElevation = 2.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(36.dp).background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(label, fontSize = 11.sp, color = Color.Gray)
                Text(value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            }
            IconButton(onClick = onEdit, modifier = Modifier.size(36.dp)) { Icon(Icons.Default.Edit, "Modifica", Modifier.size(18.dp), tint = MaterialTheme.colorScheme.secondary) }
        }
    }
}
