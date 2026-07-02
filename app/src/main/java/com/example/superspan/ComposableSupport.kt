package com.example.superspan

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.geometry.Offset

import androidx.compose.foundation.layout.BoxScope

@Composable
fun AuraBackground(modifier: Modifier = Modifier, alphaMultiplier: Float = 1f, content: @Composable BoxScope.() -> Unit) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF7FBF8)) // Option 2 base (very light mint white)
            .drawBehind {
                drawRect(
                    brush = Brush.radialGradient(
                        colors = listOf(com.example.superspan.ui.theme.LogoLeft.copy(alpha = (0.20f * alphaMultiplier).coerceAtMost(1f)), Color.Transparent),
                        center = Offset(0f, 0f),
                        radius = size.width * 0.8f
                    )
                )
                drawRect(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFF388E3C).copy(alpha = (0.15f * alphaMultiplier).coerceAtMost(1f)), Color.Transparent),
                        center = Offset(size.width, size.height),
                        radius = size.width * 0.8f
                    )
                )
            }
    ) {
        content()
    }
}



@Composable
fun ProfileIcon() {
    Icon(Icons.Default.AccountCircle, null, modifier = Modifier.size(60.dp))
}

@Composable
fun Header(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(com.example.superspan.ui.theme.AppHeaderFadeBrush)
            .padding(top = 60.dp, bottom = 32.dp, start = 24.dp, end = 24.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                androidx.compose.foundation.Image(
                    painter = androidx.compose.ui.res.painterResource(id = R.drawable.superspan),
                    contentDescription = "Logo SuperSpan",
                    modifier = Modifier.height(48.dp),
                    contentScale = androidx.compose.ui.layout.ContentScale.Fit
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Ciao ${actualUser.nome}!",
                style = MaterialTheme.typography.displayLarge,
                color = com.example.superspan.ui.theme.LogoLeft,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Cosa cerchi oggi?",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.DarkGray,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun FilterTitle(
    modifier: Modifier = Modifier,
    title: String = "Filtri",
    paddingValues: PaddingValues
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(com.example.superspan.ui.theme.AppHeaderFadeBrush)
            .padding(top = paddingValues.calculateTopPadding() + 64.dp, bottom = 32.dp, start = 24.dp, end = 24.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.displayLarge,
            color = com.example.superspan.ui.theme.LogoLeft
        )
    }
}

@Composable
fun FloatingFilterActions(
    onDismiss: () -> Unit,
    onReset: (() -> Unit)?,
    paddingValues: PaddingValues
) {
    Column(Modifier.fillMaxWidth().padding(top = paddingValues.calculateTopPadding())) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .size(45.dp)
                    .background(Color.White.copy(alpha = 0.9f), CircleShape)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Indietro",
                    modifier = Modifier.size(24.dp),
                    tint = com.example.superspan.ui.theme.LogoLeft
                )
            }

            if (onReset != null) {
                TextButton(
                    onClick = onReset,
                    modifier = Modifier
                        .background(Color.White.copy(alpha = 0.9f), CircleShape)
                        .height(45.dp)
                ) {
                    Text("Reset", color = com.example.superspan.ui.theme.LogoLeft, fontWeight = FontWeight.Bold)
                }
            } else {
                Spacer(Modifier.size(45.dp))
            }
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
    var isPasswordStrong by rememberSaveable { mutableStateOf(false) }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var confirmPasswordVisible by rememberSaveable { mutableStateOf(false) }
    
    val doPasswordsMatch = password.isNotEmpty() && confirmPassword.isNotEmpty() && password == confirmPassword
    
    // Notifichiamo all'esterno se il form è valido
    LaunchedEffect(isPasswordStrong, doPasswordsMatch) {
        checking(isPasswordStrong && doPasswordsMatch)
    }
    
    Column(modifier = Modifier.padding(horizontal = 25.dp)) {
        // Controllo della password visibile prima del campo
        CheckPassword(password, { isPasswordStrong = it })
        Spacer(Modifier.size(16.dp))
        
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
            }
        )

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

    val score = listOf(upperCase, minLen, specialChar, digitChar).count { it }

    val strengthText = when (score) {
        0 -> "Troppo debole"
        1 -> "Debole"
        2 -> "Media"
        3 -> "Buona"
        4 -> "Forte"
        else -> ""
    }
    
    val strengthColor = when (score) {
        0, 1 -> com.example.superspan.ui.theme.AppError
        2 -> com.example.superspan.ui.theme.AppWarning
        3 -> Color(0xFF81C784) // Verde chiaro (forza media-alta)
        4 -> com.example.superspan.ui.theme.AppSuccess
        else -> Color.LightGray
    }

    androidx.compose.material3.Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFFF9F9F9),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE0E0E0))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Animated Progress Bar
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Forza Password: ", 
                    fontSize = 13.sp, 
                    color = Color.DarkGray,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = strengthText, 
                    fontSize = 13.sp, 
                    color = strengthColor,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(Modifier.height(8.dp))
            
            val animatedProgress by androidx.compose.animation.core.animateFloatAsState(
                targetValue = score / 4f, 
                animationSpec = androidx.compose.animation.core.tween(durationMillis = 300)
            )
            
            androidx.compose.material3.LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = strengthColor,
                trackColor = Color(0xFFE0E0E0)
            )

            Spacer(Modifier.height(16.dp))
            
            // Checklist
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        PasswordCheckItem(minLen, "Almeno 8 caratteri")
                        PasswordCheckItem(specialChar, "Almeno 1 carattere speciale")
                    }
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        PasswordCheckItem(upperCase, "Almeno 1 maiuscola")
                        PasswordCheckItem(digitChar, "Almeno 1 numero")
                    }
                }
            }
        }
    }
}

@Composable
fun PasswordCheckItem(condition: Boolean, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        if(condition) {
            Icon(
                Icons.Filled.Check,
                contentDescription = null,
                tint = com.example.superspan.ui.theme.AppSuccess,
                modifier = Modifier.size(16.dp)
            )
        } else {
            // Empty dot
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(androidx.compose.foundation.shape.CircleShape)
                    .background(Color(0xFFE0E0E0)),
                contentAlignment = Alignment.Center
            ) {
                Box(modifier = Modifier.size(6.dp).clip(androidx.compose.foundation.shape.CircleShape).background(Color.White))
            }
        }
        Spacer(Modifier.width(8.dp))
        Text(
            text = text, 
            fontSize = 12.sp, 
            color = if (condition) Color.DarkGray else Color.Gray,
            fontWeight = if (condition) FontWeight.Medium else FontWeight.Normal,
            lineHeight = 14.sp
        )
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
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(com.example.superspan.ui.theme.AppHeaderFadeBrush)
            .padding(top = 60.dp, bottom = 32.dp, start = 24.dp, end = 24.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                androidx.compose.foundation.Image(
                    painter = androidx.compose.ui.res.painterResource(id = R.drawable.superspan),
                    contentDescription = "Logo SuperSpan",
                    modifier = Modifier.height(36.dp),
                    contentScale = androidx.compose.ui.layout.ContentScale.Fit
                )
                
                IconButton(
                    onClick = { navController?.navigateTopLevel(Destination.PROFILO.route) },
                    modifier = Modifier
                        .size(45.dp)
                        .background(Color.White.copy(alpha = 0.5f), CircleShape)
                ) {
                    Icon(
                        Icons.Default.AccountCircle,
                        contentDescription = "Profilo",
                        modifier = Modifier.size(28.dp),
                        tint = com.example.superspan.ui.theme.LogoLeft
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Ciao ${actualUser?.nome ?: "Ospite"}!",
                style = MaterialTheme.typography.displayLarge,
                color = com.example.superspan.ui.theme.LogoLeft
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Bentornato in SuperSpan",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.DarkGray,
                fontWeight = FontWeight.Medium
            )
        }
    }
}


val longTestText = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam bibendum lobortis lobortis. Phasellus pretium sagittis interdum. Donec posuere sapien eget enim placerat cursus. Phasellus condimentum bibendum enim in vestibulum. Vivamus varius quam odio, eget bibendum diam tincidunt nec. In eu posuere metus. Aliquam erat volutpat. Nam non iaculis nibh, eget luctus orci. Integer id scelerisque enim, non placerat elit. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam bibendum lobortis lobortis. Phasellus pretium sagittis interdum. Donec posuere sapien eget enim placerat cursus. Phasellus condimentum bibendum enim in vestibulum. Vivamus varius quam odio, eget bibendum diam tincidunt nec. In eu posuere metus. Aliquam erat volutpat. Nam non iaculis nibh, eget luctus orci. Integer id scelerisque enim, non placerat elit."

@Composable
fun EditTextField(
    label: String,
    value: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    isError: Boolean = false,
    errorMessage: String = "",
    visualTransformation: VisualTransformation = VisualTransformation.None,
    modifier: Modifier = Modifier,
    trailingIcon: @Composable (() -> Unit)? = null,
    readOnly: Boolean = false,
    singleLine: Boolean = true,
    minLines: Int = 1,
    onClick: (() -> Unit)? = null,
    onValueChange: (String) -> Unit
) {
    val containerColor = when {
        isError -> Color(0xFFFDECEA)             // errore: rosso tenue
        value.isNotEmpty() -> Color(0xFFF0F9F0)  // compilato: verde tenue
        else -> Color.Transparent                // vuoto: trasparente (si fonde con card/Aura)
    }

    val borderColor = when {
        isError -> com.example.superspan.ui.theme.AppError
        value.isNotEmpty() -> Color(0xFF81C784)
        else -> Color.LightGray
    }

    Column(modifier = modifier.padding(vertical = 4.dp)) {
        Box {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                visualTransformation = visualTransformation,
                label = { Text(label) },
                modifier = Modifier.fillMaxWidth().background(containerColor, RoundedCornerShape(12.dp)),
                shape = RoundedCornerShape(12.dp),
                // Se il campo chiede più righe, dev'essere multi-riga: altrimenti 'singleLine=true'
                // vincerebbe e taglierebbe il testo lungo a una sola riga.
                singleLine = singleLine && minLines <= 1,
                minLines = minLines,
                isError = isError,
                readOnly = readOnly,
                trailingIcon = trailingIcon,
                keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = borderColor,
                    focusedBorderColor = com.example.superspan.ui.theme.LogoLeft,
                    errorBorderColor = com.example.superspan.ui.theme.AppError,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    errorContainerColor = Color.Transparent
                )
            )
            if (onClick != null) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onClick
                        )
                )
            }
        }
        if (isError) {
            Text(errorMessage, color = com.example.superspan.ui.theme.AppError, fontSize = 12.sp, modifier = Modifier.padding(start = 12.dp))
        }
    }
}

// NOTA: EmptyState, InfoChip, AnimatedSegmentedControl e FakeBarcode vivono in DesignSystem.kt
// (versioni con l'animazione a molla del selettore). Le copie duplicate che stavano qui sono
// state rimosse perché generavano un conflitto di compilazione.