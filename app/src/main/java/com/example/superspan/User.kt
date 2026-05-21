package com.example.superspan

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector

val MapOfUser = mutableMapOf<String, User>(
    "d.tinti@superspan.it" to User(
        "Daniela", "Tinti", "d.tinti@superspan.it", "caccacacca", true
    )
)

class FilterData() {
    var nome: String by mutableStateOf("")
    var ordinamento: String by mutableStateOf("Nome")
    var ordinamentoCrescente: Boolean by mutableStateOf(true)
    var categorie: MutableList<Category> = mutableStateListOf<Category>()
    var minPrice: Double by mutableDoubleStateOf( 0.0)
    var maxPrice: Double by mutableDoubleStateOf( maxPossiblePrice().toDouble())

    fun minPossiblePrice(): Float {
        return ListOfProduct.minOf { product ->
            product.prezzo
        }
    }

    fun maxPossiblePrice(): Float {
        return ListOfProduct.maxOf { product ->
            product.prezzo
        }
    }
}

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
    Product("Caffè Macinato 250g", 3.50f, null, Category.DISPENSA),
    Product("Pane Integrale 500g", 1.80f, null, Category.PANETTERIA),
    Product("Banane (al kg)", 1.20f, null, Category.FRUTTA_E_VERDURA),
    Product("Yogurt Naturale 125g", 0.85f, null, Category.LATTICINI),
    Product("Riso Arborio 1kg", 2.40f, null, Category.DISPENSA),
    Product("Olio d'Oliva Extra Vergine 500ml", 7.99f, null, Category.DISPENSA),
    Product("Cereali Muesli", 3.20f, null, Category.COLAZIONE),
    Product("Shampoo Neutro 250ml", 2.75f, null, Category.IGIENE_PERSONALE),
    Product("Succo d'Arancia 1L", 1.30f, null, Category.BEVANDE),
    Product("Pollo Intero 1kg", 6.50f, null, Category.FRUTTA_E_VERDURA),
    Product("Spazzolino da Denti", 1.99f, null, Category.IGIENE_PERSONALE)
)

class Coupon(
    private var _code: String = "",
    private var _discount: Float = 0f,
    private var _description: String = "",
    private var _dateOfExpiration: String = "",
    vararg products: Product
) {

    var code get() = _code
        set(value) {_code=value}

    var discount get() = _discount
        set(value) {_discount=value}

    var description get() = _description
        set(value) {_description=value}

    var dateOfExpiration get() = _dateOfExpiration
        set(value) {_dateOfExpiration=value}

    var products: List<Product> = products.toList()

    fun calculateDiscountedPrice(product: Product): Float {
        return if (products.contains(product)) {
            product.prezzo * (1 - discount / 100)
        } else {
            product.prezzo
        }
    }
}

val ListOfCoupon = mutableListOf<Coupon>(
    // Offerte (2-3 prodotti)
    Coupon("Sconto10", 10f, "10% di sconto su Pane Fresco e Latte Intero", "2026-05-20",
        ListOfProduct.find { it.nome == "Pane Fresco" }!!,
        ListOfProduct.find { it.nome == "Latte Intero 1L" }!!
    ),
    Coupon("Sconto20", 20f, "20% di sconto su Pasta Barilla e Salsa di Pomodoro", "2026-05-12",
        ListOfProduct.find { it.nome == "Pasta Barilla 500g" }!!,
        ListOfProduct.find { it.nome == "Salsa di Pomodoro" }!!
    ),
    Coupon("Sconto15", 15f, "15% di sconto su Mele e Banane", "2026-05-02",
        ListOfProduct.find { it.nome == "Mele (sacchetto 1kg)" }!!,
        ListOfProduct.find { it.nome == "Banane (al kg)" }!!
    ),
    Coupon("FRESCO25", 25f, "25% di sconto su Pane Integrale e Uova", "2026-05-14",
        ListOfProduct.find { it.nome == "Pane Integrale 500g" }!!,
        ListOfProduct.find { it.nome == "Uova (confezione da 6)" }!!
    ),
    Coupon("LATTICINI30", 30f, "30% di sconto su Parmigiano Reggiano, Latte e Yogurt", "2026-06-10",
        ListOfProduct.find { it.nome == "Parmigiano Reggiano 200g" }!!,
        ListOfProduct.find { it.nome == "Latte Intero 1L" }!!,
        ListOfProduct.find { it.nome == "Yogurt Naturale 125g" }!!
    ),
    Coupon("CUCINA18", 18f, "18% di sconto su Riso Arborio e Olio d'Oliva", "2026-05-13",
        ListOfProduct.find { it.nome == "Riso Arborio 1kg" }!!,
        ListOfProduct.find { it.nome == "Olio d'Oliva Extra Vergine 500ml" }!!
    ),
    Coupon("BREAKFAST12", 12f, "12% di sconto su Cereali Muesli e Caffè Macinato", "2026-06-01",
        ListOfProduct.find { it.nome == "Cereali Muesli" }!!,
        ListOfProduct.find { it.nome == "Caffè Macinato 250g" }!!
    ),
    Coupon("CASA22", 22f, "22% di sconto su Detersivo e Carta Igienica", "2026-04-30",
        ListOfProduct.find { it.nome == "Detersivo Piatti" }!!,
        ListOfProduct.find { it.nome == "Carta Igienica (4 rotoli)" }!!
    ),
    Coupon("IGIENE28", 28f, "28% di sconto su Shampoo Neutro e Spazzolino", "2026-07-15",
        ListOfProduct.find { it.nome == "Shampoo Neutro 250ml" }!!,
        ListOfProduct.find { it.nome == "Spazzolino da Denti" }!!
    ),
    Coupon("BEVANDE16", 16f, "16% di sconto su Acqua Naturale e Succo d'Arancia", "2026-05-25",
        ListOfProduct.find { it.nome == "Acqua Naturale 1.5L" }!!,
        ListOfProduct.find { it.nome == "Succo d'Arancia 1L" }!!
    ),
    Coupon("VERDURA20", 20f, "20% di sconto su Pollo Intero e Mele", "2026-05-19",
        ListOfProduct.find { it.nome == "Pollo Intero 1kg" }!!,
        ListOfProduct.find { it.nome == "Mele (sacchetto 1kg)" }!!
    ),
    // Coupon (1 solo prodotto)
    Coupon("PANE5", 5f, "Sconto 5% su Pane Fresco", "2026-05-22",
        ListOfProduct.find { it.nome == "Pane Fresco" }!!
    ),
    Coupon("LATTE8", 8f, "Sconto 8% su Latte Intero 1L", "2026-05-13",
        ListOfProduct.find { it.nome == "Latte Intero 1L" }!!
    ),
    Coupon("PASTA10", 10f, "Sconto 10% su Pasta Barilla", "2026-05-07",
        ListOfProduct.find { it.nome == "Pasta Barilla 500g" }!!
    ),
    Coupon("POMODORO7", 7f, "Sconto 7% su Salsa di Pomodoro", "2026-05-14",
        ListOfProduct.find { it.nome == "Salsa di Pomodoro" }!!
    ),
    Coupon("UOVA12", 12f, "Sconto 12% su Uova confezione da 6", "2026-06-05",
        ListOfProduct.find { it.nome == "Uova (confezione da 6)" }!!
    ),
    Coupon("PARMIGIANO15", 15f, "Sconto 15% su Parmigiano Reggiano", "2026-05-18",
        ListOfProduct.find { it.nome == "Parmigiano Reggiano 200g" }!!
    ),
    Coupon("BISCOTTI9", 9f, "Sconto 9% su Biscotti al Cioccolato", "2026-04-25",
        ListOfProduct.find { it.nome == "Biscotti al Cioccolato" }!!
    ),
    Coupon("YOUGURT6", 6f, "Sconto 6% su Yogurt Naturale", "2026-05-30",
        ListOfProduct.find { it.nome == "Yogurt Naturale 125g" }!!
    ),
    Coupon("RISO11", 11f, "Sconto 11% su Riso Arborio", "2026-05-17",
        ListOfProduct.find { it.nome == "Riso Arborio 1kg" }!!
    ),
    Coupon("OLIO14", 14f, "Sconto 14% su Olio d'Oliva Extra Vergine", "2026-05-01",
        ListOfProduct.find { it.nome == "Olio d'Oliva Extra Vergine 500ml" }!!
    ),
    Coupon("CAFFE13", 13f, "Sconto 13% su Caffè Macinato", "2026-06-20",
        ListOfProduct.find { it.nome == "Caffè Macinato 250g" }!!
    ),
    Coupon("ACQUA4", 4f, "Sconto 4% su Acqua Naturale", "2026-05-28",
        ListOfProduct.find { it.nome == "Acqua Naturale 1.5L" }!!
    ),
    Coupon("POLLO19", 19f, "Sconto 19% su Pollo Intero", "2026-04-15",
        ListOfProduct.find { it.nome == "Pollo Intero 1kg" }!!
    ),
    Coupon("BANANE8", 8f, "Sconto 8% su Banane", "2026-05-27",
        ListOfProduct.find { it.nome == "Banane (al kg)" }!!
    )
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

//-------------------------------PRODUCTS--------------------------------------------------

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

fun searchProduct(filterData: FilterData): List<Product> {
    var list: List<Product> = ListOfProduct
    if (filterData.ordinamento=="Nome") {
        list = list.sortedBy { it.nome }
    } else {
        list = list.sortedBy { it.prezzo }
    }
    if(!filterData.ordinamentoCrescente) list = list.reversed()
    if (filterData.categorie.isNotEmpty()) {
        list = filterProduct(list, filterData.categorie)
    }
    if (filterData.nome.isNotEmpty()) {
        list = list.filter { product ->
            product.nome.contains(filterData.nome, ignoreCase = true)
        }
    }

    list = list.filter { product ->
        product.prezzo >= filterData.minPrice && product.prezzo <= filterData.maxPrice
    }
    return list
}

fun filterProduct(prodotti: List<Product>, categorie: List<Category>): List<Product> {
    return prodotti.filter { product ->
        categorie.contains(product.categoria)
    }
}

//-------------------------------SHAPES--------------------------------------------------


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

//-------------------------------WORK OFFERS--------------------------------------------------
data class WorkOffer(
    val id: Int,
    val ruolo: String,
    val descrizioneBreve: String,
    val citta: String,
    val tipoContratto: String, // es. "Determinato", "Indeterminato"
    val orario: String,        // es. "Full-time", "Part-time"
    val iconaRes: Int? = null  // Per un'eventuale immagine specifica
)

val WorkOfferSearchList = listOf(
    WorkOffer(
        id = 1,
        ruolo = "Addetto alle vendite",
        descrizioneBreve = "Cerchiamo personale dinamico per il reparto ortofrutta.",
        citta = "Milano",
        tipoContratto = "Determinato",
        orario = "Full-time"
    ),
    WorkOffer(
        id = 2,
        ruolo = "Cassiere/a",
        descrizioneBreve = "Gestione cassa e assistenza clienti nel punto vendita.",
        citta = "Roma",
        tipoContratto = "Indeterminato",
        orario = "Part-time"
    ),
    WorkOffer(
        id = 3,
        ruolo = "Magazziniere",
        descrizioneBreve = "Carico/scarico merci e gestione scorte di magazzino.",
        citta = "Torino",
        tipoContratto = "Determinato",
        orario = "Full-time"
    )
)