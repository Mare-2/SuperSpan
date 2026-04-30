package com.example.superspan

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import androidx.navigation.NavController



@Composable
fun Home(paddingValues: PaddingValues, navController: NavController?) {
    Column(Modifier.padding(paddingValues)) {
        Column(
            Modifier
                .clip(BottomOvalShape(30.dp))
                .fillMaxSize()
                .weight(1f)
                .background(Color.Gray),
            verticalArrangement = Arrangement.Top
        ) {
            Row(Modifier.padding(top = 20.dp, start = 10.dp, end = 20.dp)) {
                Text("LOGO", fontSize = 50.sp)
                Spacer(Modifier.weight(1f))
                IconButton(
                    {navController?.navigate(Destination.PROFILO.route)},
                    content = {ProfileIcon()},
                    modifier = Modifier.size(60.dp)
                )
            }
            Row(Modifier.padding(start = 10.dp, end = 10.dp).fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Text("Ciao ${actualUser?.nome}", fontSize = 20.sp,
                    modifier = Modifier.padding(bottom = 20.dp, top = 15.dp),
                    textAlign = TextAlign.Center
                )
            }

        }
        Column(Modifier
            .weight(4f)
            .padding(top = 30.dp)) {

        }
    }
}

@Composable
@Preview(showBackground = true)
fun HomePreview() {
    Home(PaddingValues(0.dp), null)
}