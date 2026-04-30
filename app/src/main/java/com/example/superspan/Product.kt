package com.example.superspan

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Image
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
import androidx.navigation.NavDirections

@Composable
fun ProductPage(product: Product?, navController: NavController?, paddingValues: PaddingValues) {
    Column(
        Modifier
            .padding(paddingValues)
            .background(Color.LightGray, TopOvalShape(50.dp))
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(Modifier.fillMaxWidth().padding(top = 20.dp)) {  IconButton({navController?.popBackStack()}, content = { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) }, modifier = Modifier.size(50.dp))}
        Spacer(Modifier.weight(1f))
        Image(product?.image ?: Icons.Default.Image, null, modifier = Modifier.fillMaxSize().weight(3.5f))
        Column(Modifier.weight(6f).fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(product?.nome ?: "", fontSize = 40.sp)
            Text("${product?.prezzo?:0.0}$", fontSize = 25.sp, modifier = Modifier.padding(top = 10.dp))
            Text(modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 10.dp)
                    .verticalScroll(rememberScrollState()),
                text = longTestText
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun ProductPreview() {
    ProductPage(ListOfProduct[0], null, PaddingValues(0.dp))
}