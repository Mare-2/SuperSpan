package com.example.superspan

import android.util.Patterns
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.launch

@Composable
fun AccountSettingsPage(user: User, navController: NavController?, paddingValues: PaddingValues) {
    val scrollState = rememberScrollState()

    var nome by remember { mutableStateOf(user.nome) }
    var cognome by remember { mutableStateOf(user.cognome) }
    var email by remember { mutableStateOf(user.email) }
    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf(user.password) }
    
    var oldPasswordVisible by remember { mutableStateOf(false) }
    var newPasswordVisible by remember { mutableStateOf(false) }
    var showSaveConfirm by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val context = androidx.compose.ui.platform.LocalContext.current

    val isEmailValid = Patterns.EMAIL_ADDRESS.matcher(email).matches() || email.isEmpty()
    val isOldPasswordValid = oldPassword == user.password || oldPassword.isEmpty()

    // Controllo abilitazione tasto salva
    val canSave = nome.isNotBlank() && cognome.isNotBlank() && isEmailValid && 
                  (oldPassword.isEmpty() || oldPassword == user.password)

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = Modifier.padding(paddingValues)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color.White)
        ) {
            // --- HEADER (Come PersonalDataEditPage) ---
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
                    Text("Modifica Account", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Text("I campi verdi sono completati", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                }
            }

            // --- FORM ---
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 25.dp)
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(20.dp))

                EditTextField("Nome", nome, KeyboardType.Text) { nome = it }
                EditTextField("Cognome", cognome, KeyboardType.Text) { cognome = it }

                // Email
                if (user.admin) {
                    // Admin non può modificare la mail
                    EditTextField(
                        label = "Email di accesso",
                        value = email,
                        keyboardType = KeyboardType.Email,
                        onValueChange = {}, // Ignora l'input
                    )
                    Text(
                        text = "L'indirizzo email dell'amministratore non può essere modificato.",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(start = 4.dp, top = 2.dp, bottom = 8.dp)
                    )
                } else {
                    // Utente normale
                    EditTextField(
                        label = "Email di accesso",
                        value = email,
                        keyboardType = KeyboardType.Email,
                        isError = !isEmailValid && email.isNotEmpty(),
                        errorMessage = "Formato email non valido",
                        onValueChange = { email = it }
                    )
                }

                Spacer(Modifier.height(16.dp))
                Text("Modifica Password", Modifier.fillMaxWidth(), fontWeight = FontWeight.Bold, color = Color.Gray, fontSize = 14.sp)
                Spacer(Modifier.height(8.dp))

                // Vecchia Password
                EditTextField(
                    label = "Vecchia Password",
                    value = oldPassword,
                    keyboardType = KeyboardType.Password,
                    isError = oldPassword.isNotEmpty() && oldPassword != user.password,
                    errorMessage = "Password errata",
                    visualTransformation = if (oldPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val image = if (oldPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                        IconButton(onClick = { oldPasswordVisible = !oldPasswordVisible }) {
                            Icon(imageVector = image, contentDescription = "Mostra/Nascondi")
                        }
                    },
                    onValueChange = { oldPassword = it }
                )

                // Nuova Password
                EditTextField(
                    label = "Nuova Password",
                    value = newPassword,
                    keyboardType = KeyboardType.Password,
                    visualTransformation = if (newPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val image = if (newPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                        IconButton(onClick = { newPasswordVisible = !newPasswordVisible }) {
                            Icon(imageVector = image, contentDescription = "Mostra/Nascondi")
                        }
                    },
                    onValueChange = { newPassword = it }
                )

                Spacer(Modifier.height(30.dp))

                // TASTO SALVA
                Button(
                    onClick = { showSaveConfirm = true },
                    enabled = canSave,
                    modifier = Modifier.fillMaxWidth().height(55.dp),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF388E3C))
                ) {
                    Text("Salva Modifiche", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
                
                Spacer(Modifier.height(40.dp))
            }
        }

        if (showSaveConfirm) {
            AlertDialog(
                onDismissRequest = { showSaveConfirm = false },
                title = { Text("Conferma Modifica") },
                text = { Text("Vuoi salvare le modifiche apportate al tuo account?") },
                confirmButton = {
                    TextButton(onClick = {
                        showSaveConfirm = false
                        user.nome = nome.trim()
                        user.cognome = cognome.trim()
                        
                        if (oldPassword == user.password && newPassword.isNotBlank()) {
                            user.password = newPassword.trim()
                        }

                        if (!user.admin) {
                            user.email = email.trim()
                        }

                        coroutineScope.launch {
                            android.widget.Toast.makeText(context, "Dati salvati con successo!", android.widget.Toast.LENGTH_SHORT).show()
                        }
                    }) {
                        Text("Salva", color = Color(0xFF388E3C))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showSaveConfirm = false }) { Text("Annulla", color = Color.Gray) }
                }
            )
        }
    }
}
