package com.example.superspan

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
                Text("LOGO", fontSize = 55.sp, modifier = Modifier.padding(bottom = 35.dp))
                Text("Benvenuto!", fontSize = 40.sp, modifier = Modifier.padding(bottom = 15.dp))
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(5f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {
                if (!formPass) FormDati(email, nome, cognome, {email = it}, {nome = it}, {cognome = it}, {formPass = it})
                else FormPassword({password = it}, {check = it})
            }
        }
        Box(Modifier.weight(0.5f), contentAlignment = Alignment.Center) {
            Button({
                MapOfUser.put(email, User(nome, cognome, email, password))
                navController?.navigate(Destination.HOME.route)}, enabled = check) {Text("Registrati")}
        }
        Box(contentAlignment = Alignment.BottomStart, modifier = Modifier.fillMaxWidth()) {
            Button({ navController?.navigate(Destination.LOGIN.route) }) {
                Text("Cambia")
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
    exist = MapOfUser.contains(email)
    check = !email.isEmpty() && !nome.isEmpty() && !cognome.isEmpty() && !exist
    if(exist) Text("E-mail già registrata!", modifier = Modifier.padding(bottom = 10.dp, top = 2.dp), color = Color.Red)
    OutlinedTextField(
        value = email,
        isError = exist,
        onValueChange = {
                email = it
                onEmailChange(email)
            },
        label = {Text("E-mail") },
        modifier = Modifier.padding(bottom = 25.dp),
        shape = RoundedCornerShape(30.dp),
        singleLine = true
    )
    OutlinedTextField(
        value = nome,
        onValueChange = {
                nome = it
                onNameChange(nome)
            },
        label = {Text("Nome") },
        modifier = Modifier.padding(bottom = 25.dp),
        shape = RoundedCornerShape(30.dp),
        singleLine = true
    )
    OutlinedTextField(
        value = cognome,
        onValueChange = {
                cognome = it
                onSurnameChange(cognome)
            },
        label = {Text("Cognome") },
        modifier = Modifier.padding(bottom = 50.dp),
        shape = RoundedCornerShape(30.dp),
        singleLine = true
    )
    Spacer(Modifier.size(70.dp))
    Button({onAdvance(true)}, enabled = check, modifier = Modifier.padding(bottom = 15.dp)) {Text("Avanti")}
}




@ExperimentalMaterial3Api
@Composable
@Preview(showBackground = true)
fun RegisterPreview() {
    Register(PaddingValues(0.dp), null)
}



