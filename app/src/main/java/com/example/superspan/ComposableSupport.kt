package com.example.superspan

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
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
import androidx.compose.material.icons.filled.Photo
import androidx.compose.runtime.remember
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign




@Composable
fun ProfileIcon() {
    Icon(Icons.Default.AccountCircle, null, modifier = Modifier.size(60.dp))
}

@Composable
fun Header(modifier: Modifier = Modifier) {
    Column(
        modifier
            .clip(BottomOvalShape(30.dp))
            .fillMaxSize()
            .background(Color.Gray),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        Text("LOGO", fontSize = 50.sp)
        Text("Ciao ${actualUser?.nome}", fontSize = 20.sp,
            modifier = Modifier.padding(bottom = 20.dp, top = 15.dp)
        )
    }
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
        isError = password!=confirmPassword && confirmPassword.isNotEmpty(),
        label = {Text("Confirm Password") },
        modifier = Modifier.padding(bottom = 50.dp, top = 55.dp),
        shape = RoundedCornerShape(30.dp),
        singleLine = true
    )
    CheckPassword(password, {check = it})


    Spacer(Modifier.size(80.dp))
}

@Composable
fun CheckPassword(
    password: String,
    checking: (Boolean) -> Unit
) {
    var upperCase = false
    var minLen = false
    var specialChar = false
    var digitChar = false
    var check: Boolean
    if (password.length>=8) minLen = true
    password.forEach { c ->
        if (c.isUpperCase()) upperCase = true
        else if (c.isDigit()) digitChar = true
        else if (!c.isLetterOrDigit()) specialChar = true
    }
    check = upperCase && minLen && digitChar && specialChar
    checking(check)

    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 15.dp).clip(RoundedCornerShape(10.dp)).background(Color.LightGray),
        horizontalArrangement = Arrangement.spacedBy(30.dp)

    ) {
        Column(Modifier.weight(1f)) {
            PasswordCheckItem(minLen, "Almeno 8 caratteri")
            Spacer(Modifier.size(5.dp))
            PasswordCheckItem(specialChar, "Almeno un carattere speciale")
        }
        Column(Modifier.weight(1f)) {
            PasswordCheckItem(upperCase, "Almeno una lettera maiuscola")
            Spacer(Modifier.size(5.dp))
            PasswordCheckItem(digitChar, "Almeno una cifra")
        }
    }
}

@Composable
fun IconOk() {
    Icon(
        Icons.Filled.Check,
        "",
        tint = Color.Green,
        modifier = Modifier.padding(end = 5.dp)
    )
}

@Composable
fun IconFail() {
    Icon(
        Icons.Filled.Clear,
        "",
        tint = Color.Red,
        modifier = Modifier.padding(end = 5.dp)
    )
}

@Composable
fun PasswordCheckItem(condition: Boolean, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        if(condition) IconOk()
        else IconFail()
        Spacer(Modifier.size(7.dp))
        Text(text, fontSize = 11.sp, lineHeight = 14.sp)
    }
}

@Composable
fun TestoAdattabile(testo: String, modifier: Modifier = Modifier, fontSize: TextUnit, textAlign: TextAlign = TextAlign.Start) {
    var dim by remember { mutableStateOf(fontSize) }
    var check by remember { mutableStateOf(false) }
    Text(
        testo,
        modifier =
            modifier
                .drawWithContent({ if (check) drawContent() })
                .fillMaxWidth(),
        onTextLayout = { layout ->
            if(layout.hasVisualOverflow) dim*=0.95f
            else check = true },
        fontWeight = FontWeight.Bold,
        fontSize = dim,
        textAlign = textAlign,
        maxLines = 1,
        softWrap = false
    )
}


val longTestText = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam bibendum lobortis lobortis. Phasellus pretium sagittis interdum. Donec posuere sapien eget enim placerat cursus. Phasellus condimentum bibendum enim in vestibulum. Vivamus varius quam odio, eget bibendum diam tincidunt nec. In eu posuere metus. Aliquam erat volutpat. Nam non iaculis nibh, eget luctus orci. Integer id scelerisque enim, non placerat elit. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam bibendum lobortis lobortis. Phasellus pretium sagittis interdum. Donec posuere sapien eget enim placerat cursus. Phasellus condimentum bibendum enim in vestibulum. Vivamus varius quam odio, eget bibendum diam tincidunt nec. In eu posuere metus. Aliquam erat volutpat. Nam non iaculis nibh, eget luctus orci. Integer id scelerisque enim, non placerat elit."