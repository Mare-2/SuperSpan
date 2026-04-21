package com.example.superspan

import android.media.Image

val MapOfUser = mutableMapOf<String, User>(
    "d.tinti@superspan.it" to User(
        "Daniela", "Tinti", "d.tinti@superspan.it", "caccacacca", true
    )
)

val ListOfProduct = mutableListOf<Product>(
    Product("a", "a", null),
    Product("b", "b", null),
    Product("c", "c", null),
    Product("d", "d", null),
    Product("e", "e", null),
    Product("f", "f", null),
    Product("g", "g", null)
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
    private var _descrizione: String,
    private var _image: Image?
) {
    var nome get() = _nome
        set(value) {_nome=value}

    var descrizione get() = _descrizione
        set(value) {_descrizione=value}

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
