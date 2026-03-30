package com.example.superspan

import android.graphics.drawable.shapes.OvalShape
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
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
    //var debug by rememberSaveable { mutableStateOf(false) }
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
                    .weight(2f)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {
                Text("LOGO", fontSize = 55.sp, modifier = Modifier.padding(bottom = 35.dp))
                Text("Benvenuto!", fontSize = 40.sp, modifier = Modifier.padding(bottom = 30.dp))
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
        Box(Modifier.weight(0.5f), contentAlignment = Alignment.Center) {
            Button({
                ListOfUser.add(User(nome, cognome, email, password))
                navController?.navigate(Destination.HOME.route)}, enabled = check) {Text("Registrati")}
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
    onSecondNameChange: (String)-> Unit,
    onAdvance: (Boolean)-> Unit
    ) {
    var email by rememberSaveable { mutableStateOf(_email) }
    var nome by rememberSaveable { mutableStateOf(_nome) }
    var cognome by rememberSaveable { mutableStateOf(_cognome) }
    var check by rememberSaveable { mutableStateOf(false) }
    check = !email.isEmpty() && !nome.isEmpty() && !cognome.isEmpty()
    OutlinedTextField(
        value = email,
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
                onSecondNameChange(cognome)
            },
        label = {Text("Cognome") },
        modifier = Modifier.padding(bottom = 50.dp),
        shape = RoundedCornerShape(30.dp),
        singleLine = true
    )
    Spacer(Modifier.size(70.dp))
    Button({onAdvance(true)}, enabled = check, modifier = Modifier.padding(bottom = 15.dp)) {Text("Avanti")}
}

@Composable
fun FormPassword(
    onPasswordChange: (String)-> Unit,
    checking: (Boolean)-> Unit
) {
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    var check by rememberSaveable { mutableStateOf(false) }
    OutlinedTextField(
        value = password,
        onValueChange = {
            password = it
            onPasswordChange(password) },
        label = {Text("Password") },
        modifier = Modifier.padding(bottom = 25.dp),
        shape = RoundedCornerShape(30.dp),
        singleLine = true
    )
    OutlinedTextField(
        value = confirmPassword,
        onValueChange = {
            confirmPassword = it
            check = !password.isEmpty() && !confirmPassword.isEmpty() && password==confirmPassword
            checking(check)
        },
        label = {Text("Confirm Password") },
        modifier = Modifier.padding(bottom = 50.dp),
        shape = RoundedCornerShape(30.dp),
        singleLine = true
    )
    Spacer(Modifier.size(80.dp))
}


@ExperimentalMaterial3Api
@Composable
@Preview(showBackground = true)
fun RegisterPreview() {
    Register(PaddingValues(0.dp), null)
}
