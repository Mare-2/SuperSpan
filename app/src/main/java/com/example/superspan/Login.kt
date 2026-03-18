package com.example.superspan

import android.graphics.drawable.shapes.OvalShape
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
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


class BottomOvalShape(private val curveDepth: androidx.compose.ui.unit.Dp) : androidx.compose.ui.graphics.Shape {
    override fun createOutline(size: androidx.compose.ui.geometry.Size, layoutDirection: androidx.compose.ui.unit.LayoutDirection, density: androidx.compose.ui.unit.Density): androidx.compose.ui.graphics.Outline {
        val depthPx = with(density) { curveDepth.toPx() }
        val path = androidx.compose.ui.graphics.Path().apply {
            moveTo(0f, 0f)
            lineTo(size.width, 0f)
            lineTo(size.width, size.height - depthPx)
            quadraticBezierTo(size.width / 2f, size.height + depthPx, 0f, size.height - depthPx)
            close()
        }
        return androidx.compose.ui.graphics.Outline.Generic(path)
    }
}
@Composable
fun Login(

) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var isLogin by rememberSaveable { mutableStateOf(false) }
    var text by rememberSaveable { mutableStateOf("Accedi") }
    Column(
        Modifier.padding(0.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(Modifier.weight(3.5f)) {
            Column(
                Modifier
                    .clip(BottomOvalShape(30.dp))
                    .fillMaxSize()
                    .background(Color.Gray),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {
                Box(Modifier.weight(8f).fillMaxSize()) {
                    Column(
                        Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("LOGO", fontSize = 100.sp)
                    }
                }
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("E-mail") },
                    modifier = Modifier,
                    singleLine = true
                )
                Spacer(Modifier
                    .size(20.dp)
                    .weight(0.7f))
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = {Text("Password") },
                    modifier = Modifier,
                    singleLine = true
                )
                Spacer(Modifier
                    .size(40.dp)
                    .weight(1.8f))
                /*if (isLogin) text = "Accedi"
                else text = "Registrati"*/
                CreateButton(text, {isLogin = !isLogin}) //TODO: inserire funzione
                Spacer(Modifier
                    .size(20.dp)
                    .weight(0.7f))
            }
        }
        Box(Modifier.weight(1.2f))
    }
}

@Composable
fun CreateButton(text: String, func: () -> Unit) {
    Button(
        modifier = Modifier,
        onClick = func
    ) { Text(text) }
}

@ExperimentalMaterial3Api
@Composable
@Preview(showBackground = true)
fun LoginPreview() {
    Login()
}