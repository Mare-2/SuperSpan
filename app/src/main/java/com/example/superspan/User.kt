package com.example.superspan

import android.graphics.drawable.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.ui.graphics.vector.ImageVector

val MapOfUser = mutableMapOf<String, User>(
    "d.tinti@superspan.it" to User(
        "Daniela", "Tinti", "d.tinti@superspan.it", "caccacacca", true
    )
)

enum class Category(val id: Int, val nome: String, val icon: ImageVector) {
    PANETTERIA(0, "Panetteria", Icons.Default.Image),
    FRUTTA_E_VERDURA(1, "Frutta e verdura", Icons.Default.Image),
    LATTICINI(2, "Latticini", Icons.Default.Image),
    DISPENSA(3, "Dispensa", Icons.Default.Image),
    COLAZIONE(4, "Colazione", Icons.Default.Image),
    PULIZIA_CASA(5, "Pulizia casa", Icons.Default.Image),
    IGIENE_PERSONALE(6, "Igiene personale", Icons.Default.Image),
    BEVANDE(7, "Bevande", Icons.Default.Image)
}

val ListOfProduct = mutableListOf<Product>(
    Product("Pane Fresco", 1.20f, null, Category.PANETTERIA),
    Product("Latte Intero 1L", 1.50f, null, Category.LATTICINI),
    Product("Pasta Barilla 500g", 0.99f, null, Category.DISPENSA),
    Product("Salsa di Pomodoro", 1.10f, null, Category.DISPENSA),
    Product("Mele (sacchetto 1kg)", 2.30f, null, Category.FRUTTA_E_VERDURA),
    Product("Uova (confezione da 6)", 1.80f, null, Category.LATTICINI),
    Product("Parmigiano Reggiano 200g", 5.50f, null, Category.LATTICINI),
    Product("Biscotti al Cioccolato", 2.99f, null, Category.COLAZIONE),
    Product("Detersivo Piatti", 1.45f, null, Category.PULIZIA_CASA),
    Product("Carta Igienica (4 rotoli)", 2.20f, null, Category.IGIENE_PERSONALE),
    Product("Acqua Naturale 1.5L", 0.40f, null, Category.BEVANDE),
    Product("Caffè Macinato 250g", 3.50f, null, Category.DISPENSA)
)

data class User(
    private var _nome: String = "",
    private var _cognome: String = "",
    private var _email: String = "",
    private var _password: String = "",
    private var _admin: Boolean = false
) {
    var nome get() = _nome
        set(value) {_nome = value}

    var cognome get() = _cognome
        set(value) {_cognome=value}

    var email get() = _email
        set(value) {_email=value}

    var password get() = _password
        set(value) {_password=value}

    var admin get() = _admin
        set(value) {_admin = value}
}

data class Product(
    private var _nome: String,
    private var _prezzo: Float,
    private var _image: ImageVector?,
    private var _categoria: Category
) {
    var nome get() = _nome
        set(value) {_nome=value}

    var prezzo get() = _prezzo
        set(value) {_prezzo=value}

    var image get() = _image
        set(value) {_image=value}
    
    var categoria get() = _categoria
        set(value) {_categoria = value}
}

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

fun searchProduct(prodotti: List<Product>, nome: String, categorie: List<Category>): List<Product> {
    var list: List<Product> = ListOfProduct
    if (categorie.isNotEmpty()) {
        list = filterProduct(list, categorie)
    }
    if (nome.isNotEmpty()) {
        list = list.filter { product ->
            product.nome.contains(nome, ignoreCase = true)
        }
    }
    return list
}

fun filterProduct(prodotti: List<Product>, categorie: List<Category>): List<Product> {
    return prodotti.filter { product ->
        categorie.contains(product.categoria)
    }
}

class TopOvalShape(private val curveDepth: androidx.compose.ui.unit.Dp) : androidx.compose.ui.graphics.Shape {
    override fun createOutline(
        size: androidx.compose.ui.geometry.Size,
        layoutDirection: androidx.compose.ui.unit.LayoutDirection,
        density: androidx.compose.ui.unit.Density
    ): androidx.compose.ui.graphics.Outline {
        val depthPx = with(density) { curveDepth.toPx() }
        val startY = size.height / 3f
        val path = androidx.compose.ui.graphics.Path().apply {
            moveTo(0f, startY + depthPx)
            quadraticBezierTo(
                x1 = size.width / 2f, y1 = startY - depthPx,
                x2 = size.width,      y2 = startY + depthPx
            )
            lineTo(size.width, size.height)
            lineTo(0f, size.height)
            close()
        }
        return androidx.compose.ui.graphics.Outline.Generic(path)
    }
}
