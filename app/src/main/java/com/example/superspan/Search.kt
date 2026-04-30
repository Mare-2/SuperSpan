package com.example.superspan

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
fun SearchPage(
    padding: PaddingValues,
    navController: NavController?
) {
    var productSearchList: List<Product> by rememberSaveable { mutableStateOf(ListOfProduct) }
    var search: String by remember { mutableStateOf("") }
    Column(
        Modifier.padding(padding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Header(Modifier.weight(1f))
        OutlinedTextField(
            search,
            {
                search = it
                if(search.isEmpty()) productSearchList = ListOfProduct
                else {productSearchList = searchProduct(productSearchList, search)}
                },
            label = {Text("Cerca prodotto")}
        )
        Spacer(Modifier.size(10.dp))
        LazyVerticalGrid(
            columns = GridCells.Adaptive(120.dp),
            modifier = Modifier
                .weight(4f)
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(productSearchList, key = {product -> product.nome}) {
                    product -> ProductCompose(product, navController)
            }
        }
    }
}

@Composable
fun ProductCompose(product: Product, navController: NavController?) {
    Box(
        Modifier
            .fillMaxSize()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(20.dp))
            .background(Color.LightGray)
            .clickable(onClick = {navController?.navigate(Destination.PRODOTTO.route+"/${product.nome}")}),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                Icons.Default.Photo,
                null,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
            )
            Column (Modifier
                .fillMaxWidth()
                .weight(0.6f)) {
                TestoAdattabile(product.nome, Modifier.padding(end = 25.dp, start = 10.dp), 15.sp)
                Spacer(Modifier.weight(1f))
                TestoAdattabile("${product.prezzo} $", Modifier.padding(start = 25.dp, end = 10.dp, bottom = 10.dp), 15.sp,
                    TextAlign.End)
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun SearchPreview() {
    SearchPage(PaddingValues(0.dp), null)
}

