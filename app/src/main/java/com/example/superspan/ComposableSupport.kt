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
import androidx.compose.material3.IconButton
import androidx.compose.runtime.remember
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.layout.*
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff




@Composable
fun ProfileIcon() {
    Icon(Icons.Default.AccountCircle, null, modifier = Modifier.size(60.dp))
}

@Composable
fun Header(modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth()) {
        // La "Top Bar" vera e propria col logo (senza rettangolo bianco)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 60.dp, bottom = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            // Effetto bagliore (glow) dietro al logo per farlo risaltare sullo sfondo sfumato
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .background(
                        brush = androidx.compose.ui.graphics.Brush.radialGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.8f),
                                Color.White.copy(alpha = 0.3f),
                                Color.Transparent
                            )
                        )
                    )
            )
            androidx.compose.foundation.Image(
                painter = androidx.compose.ui.res.painterResource(id = R.drawable.logo_superspan),
                contentDescription = "Logo SuperSpan",
                modifier = Modifier.height(60.dp),
                contentScale = androidx.compose.ui.layout.ContentScale.Fit
            )
        }
        
        // Il testo del saluto, fuori dal rettangolo bianco e immerso nello sfondo grigio
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, start = 24.dp, end = 24.dp, bottom = 8.dp)
        ) {
            Text(
                text = "Ciao ${actualUser.nome}!",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = com.example.superspan.ui.theme.LogoLeft
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Cosa cerchi oggi?",
                fontSize = 16.sp,
                color = Color.Gray,
                fontWeight = FontWeight.Medium
            )
        }
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
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var confirmPasswordVisible by rememberSaveable { mutableStateOf(false) }
    
    Column(modifier = Modifier.padding(horizontal = 25.dp)) {
        EditTextField(
            label = "Password",
            value = password,
            keyboardType = androidx.compose.ui.text.input.KeyboardType.Password,
            visualTransformation = if (passwordVisible) androidx.compose.ui.text.input.VisualTransformation.None else androidx.compose.ui.text.input.PasswordVisualTransformation(),
            modifier = Modifier.padding(bottom = 10.dp),
            trailingIcon = {
                val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                androidx.compose.material3.IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    androidx.compose.material3.Icon(imageVector  = image, contentDescription = if (passwordVisible) "Nascondi password" else "Mostra password")
                }
            },
            onValueChange = {
                password = it
                onPasswordChange(password)
            }
        )
        EditTextField(
            label = "Confirm Password",
            value = confirmPassword,
            keyboardType = androidx.compose.ui.text.input.KeyboardType.Password,
            visualTransformation = if (confirmPasswordVisible) androidx.compose.ui.text.input.VisualTransformation.None else androidx.compose.ui.text.input.PasswordVisualTransformation(),
            isError = password != confirmPassword && confirmPassword.isNotEmpty(),
            errorMessage = "Le password non coincidono",
            modifier = Modifier.padding(bottom = 30.dp),
            trailingIcon = {
                val image = if (confirmPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                androidx.compose.material3.IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                    androidx.compose.material3.Icon(imageVector  = image, contentDescription = if (confirmPasswordVisible) "Nascondi password" else "Mostra password")
                }
            },
            onValueChange = {
                confirmPassword = it
                check = password.isNotEmpty() && confirmPassword.isNotEmpty() && password == confirmPassword
                checking(check)
            }
        )
        CheckPassword(password, {check = it})

        Spacer(Modifier.size(50.dp))
    }
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

@Composable
fun HeaderHomeAlt(modifier: Modifier, navController: NavController?) {
    Column(modifier = modifier.fillMaxWidth()) {
        // Top Bar
        androidx.compose.material3.Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.White,
            shadowElevation = 4.dp,
            shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 36.dp, bottom = 12.dp, start = 16.dp, end = 16.dp)
            ) {
                // Logo al centro
                androidx.compose.foundation.Image(
                    painter = androidx.compose.ui.res.painterResource(id = R.drawable.logo_superspan),
                    contentDescription = "Logo SuperSpan",
                    modifier = Modifier.height(45.dp).align(Alignment.Center),
                    contentScale = androidx.compose.ui.layout.ContentScale.Fit
                )
                
                // Profilo a destra
                IconButton(
                    onClick = { navController?.navigateTopLevel(Destination.PROFILO.route) },
                    modifier = Modifier
                        .size(45.dp)
                        .align(Alignment.CenterEnd)
                        .background(androidx.compose.material3.MaterialTheme.colorScheme.primary.copy(alpha = 0.08f), androidx.compose.foundation.shape.CircleShape)
                ) {
                    Icon(
                        Icons.Default.AccountCircle,
                        contentDescription = "Profilo",
                        modifier = Modifier.size(28.dp),
                        tint = androidx.compose.material3.MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
        
        // Testo fuori dal rettangolo
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 28.dp, start = 24.dp, end = 24.dp, bottom = 8.dp)
        ) {
            Text(
                text = "Ciao ${actualUser?.nome ?: "Ospite"}!",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = com.example.superspan.ui.theme.LogoLeft
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Bentornato in SuperSpan",
                fontSize = 16.sp,
                color = Color.Gray,
                fontWeight = FontWeight.Medium
            )
        }
    }
}


val longTestText = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam bibendum lobortis lobortis. Phasellus pretium sagittis interdum. Donec posuere sapien eget enim placerat cursus. Phasellus condimentum bibendum enim in vestibulum. Vivamus varius quam odio, eget bibendum diam tincidunt nec. In eu posuere metus. Aliquam erat volutpat. Nam non iaculis nibh, eget luctus orci. Integer id scelerisque enim, non placerat elit. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam bibendum lobortis lobortis. Phasellus pretium sagittis interdum. Donec posuere sapien eget enim placerat cursus. Phasellus condimentum bibendum enim in vestibulum. Vivamus varius quam odio, eget bibendum diam tincidunt nec. In eu posuere metus. Aliquam erat volutpat. Nam non iaculis nibh, eget luctus orci. Integer id scelerisque enim, non placerat elit."