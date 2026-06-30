package com.example.superspan

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight

import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff

@Composable
fun Login(
    padding: PaddingValues,
    navController: NavController?
) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var text by rememberSaveable { mutableStateOf("Accedi") }
    var loginError by rememberSaveable { mutableStateOf(false) }
    var _user : User? = actualUser
    
    AuraBackground(
        modifier = Modifier
            .fillMaxSize(),
        alphaMultiplier = 3f
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(androidx.compose.foundation.rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            
            // Sezione Superiore: Logo e Benvenuto
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 60.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                androidx.compose.foundation.Image(
                    painter = painterResource(id = R.drawable.superspan),
                    contentDescription = "Logo SuperSpan",
                    modifier = Modifier.height(55.dp).padding(bottom = 16.dp),
                    contentScale = ContentScale.Fit
                )
                Text(
                    text = "Bentornato!",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = com.example.superspan.ui.theme.LogoLeft
                )
                Text(
                    text = "Accedi al tuo account per continuare",
                    fontSize = 16.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            
            // Form Centrale
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                var passwordVisible by rememberSaveable { mutableStateOf(false) }
                val isEmailValid = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
                val isEmailError = loginError || (email.isNotEmpty() && !isEmailValid)

                EditTextField(
                    label = "E-mail",
                    value = email,
                    keyboardType = KeyboardType.Email,
                    isError = isEmailError,
                    errorMessage = if (!isEmailValid && email.isNotEmpty()) "Formato email non valido" else "Credenziali errate",
                    modifier = Modifier.padding(bottom = 12.dp),
                    onValueChange = { email = it; loginError = false }
                )
                EditTextField(
                    label = "Password",
                    value = password,
                    keyboardType = KeyboardType.Password,
                    visualTransformation = if (passwordVisible) androidx.compose.ui.text.input.VisualTransformation.None else PasswordVisualTransformation(),
                    isError = loginError,
                    errorMessage = "Credenziali errate",
                    modifier = Modifier.padding(bottom = 32.dp),
                    trailingIcon = {
                        val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                        androidx.compose.material3.IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            androidx.compose.material3.Icon(imageVector  = image, contentDescription = if (passwordVisible) "Nascondi password" else "Mostra password")
                        }
                    },
                    onValueChange = { password = it; loginError = false }
                )
                
                Button(
                    onClick = {
                        val _user = accessAccount(email, password)
                        if(_user!=null) {
                            actualUser = _user
                            navController?.navigate(Destination.HOME.route)
                        } else {
                            loginError = true
                        }
                    },
                    enabled = isEmailValid || email.isEmpty(),
                    modifier = Modifier
                        .width(180.dp)
                        .height(56.dp),
                    shape = androidx.compose.foundation.shape.CircleShape,
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = com.example.superspan.ui.theme.LogoLeft
                    )
                ) { 
                    Text(text, fontSize = 18.sp, fontWeight = FontWeight.Bold) 
                }
            }
            
            // Footer (Link Registrazione)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Non hai un account?", color = Color.Gray)
                    TextButton(onClick = { navController?.navigate(Destination.REGISTER.route) }) {
                        Text(
                            "Registrati", 
                            fontWeight = FontWeight.Bold, 
                            color = com.example.superspan.ui.theme.LogoLeft
                        )
                    }
                }
            }
        }
    }
}

fun accessAccount(email: String, password: String): User? {
    try {
        val user = MapOfUser[email]
        if(user != null && user.email == email && user.password == password) {
            return user
        }
        return null
    } catch (e: Exception) {
        return null
    }
}

@ExperimentalMaterial3Api
@Composable
@Preview(showBackground = true)
fun LoginPreview() {
    Login(PaddingValues(0.dp), null)
}