package com.example.superspan

import android.graphics.drawable.Icon
import android.media.Image
import androidx.compose.ui.graphics.vector.ImageVector

val MapOfUser = mutableMapOf<String, User>(
    "d.tinti@superspan.it" to User(
        "Daniela", "Tinti", "d.tinti@superspan.it", "caccacacca", true
    )
)

val ListOfProduct = mutableListOf<Product>(
    Product("Pane Fresco", 1.20f, null),
    Product("Latte Intero 1L", 1.50f, null),
    Product("Pasta Barilla 500g", 0.99f, null),
    Product("Salsa di Pomodoro", 1.10f, null),
    Product("Mele (sacchetto 1kg)", 2.30f, null),
    Product("Uova (confezione da 6)", 1.80f, null),
    Product("Parmigiano Reggiano 200g", 5.50f, null),
    Product("Biscotti al Cioccolato", 2.99f, null),
    Product("Detersivo Piatti", 1.45f, null),
    Product("Carta Igienica (4 rotoli)", 2.20f, null),
    Product("Acqua Naturale 1.5L", 0.40f, null),
    Product("Caffè Macinato 250g", 3.50f, null)
)



data class User(
    private var _nome: String = "",
    private var _cognome: String = "",
    private var _email: String = "",
    private var _password: String = "",
    private var _admin: Boolean = false
) {
    //TODO: inserire una reference al CV
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

class Product(
    private var _nome: String,
    private var _prezzo: Float,
    private var _image: ImageVector?
) {
    var nome get() = _nome
        set(value) {_nome=value}

    var prezzo get() = _prezzo
        set(value) {_prezzo=value}

    var image get() = _image
        set(value) {_image=value}
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

fun searchProduct(prodotti: List<Product>, nome: String): List<Product> {
    var lista: List<Product>
    lista = prodotti.filter { product ->
        product.nome.contains(nome, ignoreCase = true)
    }
    return lista
}

class TopOvalShape(private val curveDepth: androidx.compose.ui.unit.Dp) : androidx.compose.ui.graphics.Shape {
    override fun createOutline(
        size: androidx.compose.ui.geometry.Size,
        layoutDirection: androidx.compose.ui.unit.LayoutDirection,
        density: androidx.compose.ui.unit.Density
    ): androidx.compose.ui.graphics.Outline {
        val depthPx = with(density) { curveDepth.toPx() }

        // Calcoliamo la metà dell'altezza totale
        val startY = size.height / 3f

        val path = androidx.compose.ui.graphics.Path().apply {
            // 1. Partiamo da metà schermo a sinistra (scendendo di depthPx per far spazio alla curva)
            moveTo(0f, startY + depthPx)

            // 2. Disegniamo la curva. Il punto più alto toccherà esattamente 'startY - depthPx'
            quadraticBezierTo(
                x1 = size.width / 2f, y1 = startY - depthPx,
                x2 = size.width,      y2 = startY + depthPx
            )

            // 3. Linea fino in basso a destra
            lineTo(size.width, size.height)

            // 4. Linea fino in basso a sinistra
            lineTo(0f, size.height)

            close()
        }
        return androidx.compose.ui.graphics.Outline.Generic(path)
    }
}