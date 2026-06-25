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
    Column(
        Modifier.padding(padding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Column(
            Modifier
                .weight(3.5f)
                .clip(BottomOvalShape(30.dp))
                .fillMaxSize()
                .background(Color.Gray),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {
            Column(
                Modifier
                    .weight(2.5f)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo_superspan),
                    contentDescription = "Logo SuperSpan",
                    modifier = Modifier.height(80.dp).padding(bottom = 20.dp),
                    contentScale = ContentScale.Fit
                )
                Text("Benvenuto!", fontSize = 40.sp, modifier = Modifier.padding(bottom = 15.dp))
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(5f)
                    .padding(horizontal = 25.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
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
                    modifier = Modifier.padding(bottom = 10.dp),
                    onValueChange = { email = it; loginError = false }
                )
                EditTextField(
                    label = "Password",
                    value = password,
                    keyboardType = KeyboardType.Password,
                    visualTransformation = if (passwordVisible) androidx.compose.ui.text.input.VisualTransformation.None else PasswordVisualTransformation(),
                    isError = loginError,
                    errorMessage = "Credenziali errate",
                    modifier = Modifier.padding(bottom = 20.dp),
                    trailingIcon = {
                        val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                        androidx.compose.material3.IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            androidx.compose.material3.Icon(imageVector  = image, contentDescription = if (passwordVisible) "Nascondi password" else "Mostra password")
                        }
                    },
                    onValueChange = { password = it; loginError = false }
                )
                Spacer(Modifier.height(15.dp))
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
                    modifier = Modifier.padding(bottom = 15.dp)) { Text(text) }
            }
        }
        Box(Modifier.weight(0.5f), contentAlignment = Alignment.Center) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Non hai un account?", color = Color.Gray)
                TextButton(onClick = { navController?.navigate(Destination.REGISTER.route) }) {
                    Text("Registrati", fontWeight = FontWeight.Bold, color = Color(0xFF388E3C))
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