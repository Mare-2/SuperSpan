package com.example.superspan

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
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
fun Login(
    padding: PaddingValues,
    navController: NavController?
) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var text by rememberSaveable { mutableStateOf("Accedi") }
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
                Text("LOGO", fontSize = 55.sp, modifier = Modifier.padding(bottom = 35.dp))
                Text("Benvenuto!", fontSize = 40.sp, modifier = Modifier.padding(bottom = 15.dp))
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(5f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(Modifier.weight(0.4f))
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("E-mail") },
                    modifier = Modifier.padding(bottom = 20.dp),
                    shape = RoundedCornerShape(30.dp),
                    singleLine = true
                )
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = {Text("Confirm Password") },
                    modifier = Modifier.padding(bottom = 20.dp),
                    shape = RoundedCornerShape(30.dp),
                    singleLine = true
                )
                Spacer(Modifier.weight(0.6f))
                Button(
                    onClick = {
                        _user = accessAccount(email, password)
                        if(_user!=null) {
                            actualUser = _user
                            navController?.navigate(Destination.HOME.route)
                        }
                    },
                    modifier = Modifier.padding(bottom = 15.dp)) { Text(text) }
            }
        }
        Box(Modifier.weight(0.5f), contentAlignment = Alignment.BottomStart) {
        }
        Box(contentAlignment = Alignment.BottomStart, modifier = Modifier.fillMaxWidth()) {
            Button({ navController?.navigate(Destination.REGISTER.route) }) {
                Text("Cambia")
            }
        }
    }
}

@ExperimentalMaterial3Api
@Composable
@Preview(showBackground = true)
fun LoginPreview() {
    Login(PaddingValues(0.dp), null)
}

fun accessAccount(email: String, password: String): User? {
    var user: User
    try {
        user = MapOfUser.getValue(email)
        if(user.email==email) {
           if(user.password==password) return user
           return null
        }
        return null
    } catch (e: Error) {
        return null
    }
}