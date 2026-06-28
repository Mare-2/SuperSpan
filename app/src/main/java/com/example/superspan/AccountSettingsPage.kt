package com.example.superspan

import android.util.Patterns
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
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
    var newPassword by remember { mutableStateOf("") }
    
    var oldPasswordVisible by remember { mutableStateOf(false) }
    var newPasswordVisible by remember { mutableStateOf(false) }
    var oldPasswordError by remember { mutableStateOf(false) }
    var showSaveConfirm by remember { mutableStateOf(false) }
    var isNewPasswordValid by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val context = androidx.compose.ui.platform.LocalContext.current

    // Controllo abilitazione tasto salva
    val canSave = nome.isNotBlank() && cognome.isNotBlank() && (newPassword.isEmpty() || isNewPasswordValid)

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = Modifier.padding(paddingValues)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
        // --- HEADER (Senza rettangolo bianco, testo scuro) ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 80.dp, bottom = 16.dp)
        ) {
            IconButton(onClick = { navController?.popBackStack() }, modifier = Modifier.align(Alignment.CenterStart).padding(start = 8.dp).background(androidx.compose.ui.graphics.Color.White.copy(alpha = 0.7f), androidx.compose.foundation.shape.CircleShape)) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Indietro", tint = Color.Black)
            }
            Column(Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Modifica Account", color = Color.Black, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Text("Email e password", color = Color.DarkGray, fontSize = 12.sp)
            }
        }

            // --- FORM ---
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(8.dp))

                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("Modifica Dati Personali", Modifier.fillMaxWidth().padding(bottom = 16.dp), fontWeight = FontWeight.Bold, color = com.example.superspan.ui.theme.LogoLeft, fontSize = 14.sp)
                        EditTextField("Nome", nome, KeyboardType.Text) { nome = it }
                        Spacer(Modifier.height(16.dp))
                        EditTextField("Cognome", cognome, KeyboardType.Text) { cognome = it }
                    }
                }

                Spacer(Modifier.height(24.dp))

                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("Modifica Password", Modifier.fillMaxWidth().padding(bottom = 16.dp), fontWeight = FontWeight.Bold, color = com.example.superspan.ui.theme.LogoLeft, fontSize = 14.sp)

                        // Vecchia Password
                        EditTextField(
                            label = "Vecchia Password",
                            value = oldPassword,
                            keyboardType = KeyboardType.Password,
                            isError = oldPasswordError,
                            errorMessage = "Password errata",
                            visualTransformation = if (oldPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                val image = if (oldPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                                IconButton(onClick = { oldPasswordVisible = !oldPasswordVisible }) {
                                    Icon(imageVector = image, contentDescription = "Mostra/Nascondi")
                                }
                            },
                            onValueChange = { 
                                oldPassword = it
                                oldPasswordError = false
                            }
                        )

                        Spacer(Modifier.height(16.dp))

                        if (newPassword.isNotEmpty()) {
                            CheckPassword(password = newPassword) { isNewPasswordValid = it }
                            Spacer(Modifier.height(8.dp))
                        }

                        // Nuova Password
                        EditTextField(
                            label = "Nuova Password",
                            value = newPassword,
                            keyboardType = KeyboardType.Password,
                            isError = newPassword.isNotEmpty() && !isNewPasswordValid,
                            errorMessage = "La password non rispetta i requisiti",
                            visualTransformation = if (newPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                val image = if (newPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                                IconButton(onClick = { newPasswordVisible = !newPasswordVisible }) {
                                    Icon(imageVector = image, contentDescription = "Mostra/Nascondi")
                                }
                            },
                            onValueChange = { newPassword = it }
                        )
                    }
                }

                Spacer(Modifier.height(30.dp))

                // TASTO SALVA
                Button(
                    onClick = { 
                        if ((oldPassword.isNotEmpty() || newPassword.isNotEmpty()) && oldPassword != user.password) {
                            oldPasswordError = true
                        } else {
                            showSaveConfirm = true 
                        }
                    },
                    enabled = canSave,
                    modifier = Modifier.fillMaxWidth().height(55.dp),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = com.example.superspan.ui.theme.LogoLeft)
                ) {
                    Text("Salva Modifiche", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
                
                Spacer(Modifier.height(40.dp))
            }
        }

        if (showSaveConfirm) {
            ModernAlertDialog(
                onDismissRequest = { showSaveConfirm = false },
                title = "Conferma Modifica",
                text = "Vuoi salvare le modifiche apportate al tuo account?",
                icon = Icons.Default.Save,
                confirmText = "Salva",
                onConfirm = {
                    showSaveConfirm = false
                    user.nome = nome.trim()
                    user.cognome = cognome.trim()
                    
                    if (oldPassword == user.password && newPassword.isNotBlank()) {
                        user.password = newPassword.trim()
                    }

                    coroutineScope.launch {
                        android.widget.Toast.makeText(context, "Dati salvati con successo!", android.widget.Toast.LENGTH_SHORT).show()
                    }
                },
                dismissText = "Annulla",
                onDismiss = { showSaveConfirm = false }
            )
        }
    }
}
