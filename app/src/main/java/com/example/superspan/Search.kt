package com.example.superspan

import android.graphics.drawable.Icon
import android.media.Image
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import androidx.compose.material.icons.filled.Photo
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import kotlin.math.sin


@Composable
fun SearchPage(
    padding: PaddingValues,
    navController: NavController?
) {
    var search: String by remember { mutableStateOf("") }
    val categorySelected: MutableList<Category> = remember {  mutableStateListOf<Category>() }
    val productSearchList: List<Product> by remember { derivedStateOf { searchProduct(ListOfProduct, search, categorySelected) } }
    Column(
        Modifier.padding(padding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Header(Modifier.weight(1.3f))
        OutlinedTextField(
            search,
            {
                search = it
            },
            label = {Text("Cerca prodotto")},
            modifier = Modifier.padding(top = 15.dp, bottom = 25.dp),
            shape = RoundedCornerShape(30.dp)
        )

        LazyHorizontalGrid(
            rows = GridCells.Adaptive(200.dp),
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(30.dp)
        ) {
            items(Category.entries.toList(), key = null) { category ->
                CategoryItem(category, categorySelected.contains(category)) {
                    if(categorySelected.contains(category)) categorySelected.remove(category)
                    else categorySelected.add(category)
                }
            }
        }


        LazyVerticalGrid(
            columns = GridCells.Adaptive(120.dp),
            modifier = Modifier
                .weight(2.5f)
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 20.dp),
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
            .clickable(onClick = { navController?.navigate(Destination.PRODOTTO.route + "/${product.nome}") }),
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
                TestoAdattabile("${product.prezzo} $", Modifier.padding(start = 25.dp, end = 10.dp, bottom = 10.dp),
                    15.sp,
                    TextAlign.End
                )
            }
        }
    }
}

@Composable
fun CategoryItem(category: Category, isSelected: Boolean, onclick: ()->Unit) {
    var color by remember { mutableStateOf(Color.LightGray) }
    if (isSelected) color = Color.LightGray
    else color = Color.Unspecified
    Column(
        Modifier
            .background(color),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            Modifier
                .clip(RoundedCornerShape(10.dp))
                .border(5.dp, Color.Gray)
                .aspectRatio(1f)
                .weight(0.7f)
                .clickable(onClick = {
                    onclick()
                }),
            contentAlignment = Alignment.TopCenter
        ) {
            Image(category.icon, null, modifier = Modifier.fillMaxSize())
        }
        Text(category.nome, modifier = Modifier.weight(0.3f), fontSize = 12.sp)
    }
}


@Preview(showBackground = true)
@Composable
fun SearchPreview() {
    SearchPage(PaddingValues(0.dp), null)
}

