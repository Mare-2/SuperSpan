package com.example.superspan

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight

@Composable
fun Register(
    padding: PaddingValues,
    navController: NavController?
) {
    var email by rememberSaveable { mutableStateOf("") }
    var nome by rememberSaveable { mutableStateOf("") }
    var cognome by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var formPass by rememberSaveable { mutableStateOf(false) }
    var check by rememberSaveable { mutableStateOf(false) }
    var text by rememberSaveable { mutableStateOf("Registrati") }
    Column(
        Modifier.padding(padding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Column(
            Modifier
                .weight(3.5f)
                .clip(BottomOvalShape(30.dp))
                .fillMaxWidth()
                .background(Color.Gray),
            horizontalAlignment = Alignment.CenterHorizontally
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
                    .weight(5f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                if (!formPass) FormDati(email, nome, cognome, {email = it}, {nome = it}, {cognome = it}, {formPass = it})
                else FormPassword({password = it}, {check = it})
            }
        }
        Column(
            Modifier.weight(0.5f).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (formPass) {
                Button({
                    val newUser = User(nome, cognome, email, password)
                    MapOfUser.put(email, newUser)
                    actualUser = newUser
                    navController?.navigate(Destination.HOME.route)}, enabled = check) {Text("Registrati")}
                
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Hai già un account?", color = Color.Gray)
                TextButton(onClick = { navController?.navigate(Destination.LOGIN.route) }) {
                    Text("Accedi", fontWeight = FontWeight.Bold, color = Color(0xFF388E3C))
                }
            }
        }
    }
}

@Composable
fun FormDati(
    _email: String,
    _nome: String,
    _cognome: String,
    onEmailChange: (String)-> Unit,
    onNameChange: (String)-> Unit,
    onSurnameChange: (String)-> Unit,
    onAdvance: (Boolean)-> Unit
) {
    var email by rememberSaveable { mutableStateOf(_email) }
    var nome by rememberSaveable { mutableStateOf(_nome) }
    var cognome by rememberSaveable { mutableStateOf(_cognome) }
    var check by rememberSaveable { mutableStateOf(false) }
    var exist by rememberSaveable { mutableStateOf(false) }
    val isEmailValid = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    exist = MapOfUser.contains(email)
    check = email.isNotEmpty() && isEmailValid && nome.isNotEmpty() && cognome.isNotEmpty() && !exist
    val isEmailError = exist || (!isEmailValid && email.isNotEmpty())
    
    Column(modifier = Modifier.padding(horizontal = 25.dp)) {
        EditTextField(
            label = "E-mail",
            value = email,
            keyboardType = androidx.compose.ui.text.input.KeyboardType.Email,
            isError = isEmailError,
            errorMessage = if (exist) "E-mail già registrata!" else if (!isEmailValid && email.isNotEmpty()) "Formato email non valido" else "",
            modifier = Modifier.padding(bottom = 10.dp),
            onValueChange = {
                email = it
                onEmailChange(email)
            }
        )
        EditTextField(
            label = "Nome",
            value = nome,
            keyboardType = androidx.compose.ui.text.input.KeyboardType.Text,
            modifier = Modifier.padding(bottom = 10.dp),
            onValueChange = {
                nome = it
                onNameChange(nome)
            }
        )
        EditTextField(
            label = "Cognome",
            value = cognome,
            keyboardType = androidx.compose.ui.text.input.KeyboardType.Text,
            modifier = Modifier.padding(bottom = 10.dp),
            onValueChange = {
                cognome = it
                onSurnameChange(cognome)
            }
        )
        Spacer(Modifier.height(10.dp))
        Button({onAdvance(true)}, enabled = check, modifier = Modifier.padding(bottom = 15.dp).align(Alignment.CenterHorizontally)) {Text("Avanti")}
    }
}

@ExperimentalMaterial3Api
@Composable
@Preview(showBackground = true)
fun RegisterPreview() {
    Register(PaddingValues(0.dp), null)
}
