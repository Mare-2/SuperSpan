package com.example.superspan

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import android.content.Context
import android.net.Uri
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import java.io.File
import java.io.FileOutputStream

//----Serve a scrvere i numeri di telefono con lo spazio
fun formatPhone(number: String): String {
    // Rimuove tutto ciò che non è un numero (pulisce spazi vecchi o prefissi con +)
    val digits = number.filter { it.isDigit() }

    // Se il numero inizia con 39 (prefisso Italia), lo ignoriamo per la formattazione
    // dei blocchi ma lo teniamo per visualizzarlo, oppure formattiamo solo le ultime 10 cifre.
    // Per Paolo, assumiamo che vogliamo formattare le ultime 10 cifre:

    val coreNumber = if (digits.startsWith("39") && digits.length > 10) {
        digits.substring(2)
    } else {
        digits
    }

    val res = StringBuilder()
    for (i in coreNumber.indices) {
        res.append(coreNumber[i])
        if ((i == 2 || i == 5) && i != coreNumber.lastIndex) {
            res.append(" ")
        }
    }

    // Se c'era il 39, lo rimettiamo davanti
    return if (digits.startsWith("39") && digits.length > 10) "+39 $res" else res.toString()
}

//----serve a salvare i curriculum vitae nell'applicazione
fun saveFileToInternalStorage(context: Context, uri: Uri, fileName: String): String? {
    return try {
        // Apriamo il file originale tramite l'Uri
        val inputStream = context.contentResolver.openInputStream(uri)
        // Creiamo il file di destinazione nella cartella "files" dell'app
        val file = File(context.filesDir, fileName)
        val outputStream = FileOutputStream(file)

        // Copiamo i dati
        inputStream?.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }
        // Restituiamo il percorso assoluto o il nome del file salvato
        file.absolutePath
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

data class User(
    private var _nome: String = "",
    private var _cognome: String = "",
    private var _email: String = "",
    private var _password: String = "",
    private var _admin: Boolean = false,
    // Campi aggiuntivi per dati personali/lavoro
    private var _telefono: String? = null,
    private var _emailLavoro: String? = null,
    private var _cvFileName: String? = null, // Nome del file PDF caricato
    private var _draftWorksByOfferId: MutableMap<Int, DraftWork> = mutableStateMapOf()
    
) {
    // ... getter e setter ...
    var telefono get() = _telefono; set(v) { _telefono = v }
    var emailLavoro get() = _emailLavoro; set(v) { _emailLavoro = v }
    var cvFileName get() = _cvFileName; set(v) { _cvFileName = v }
    var draftWorksByOfferId get() = _draftWorksByOfferId; set(v) { _draftWorksByOfferId = v }
    var nome get() = _nome; set(v) { _nome = v }
    var cognome get() = _cognome; set(v) { _cognome = v }
    var email get() = _email; set(v) { _email = v }
    var password get() = _password; set(v) { _password = v }
    var admin get() = _admin; set(v) { _admin = v }
}
val MapOfUser = mutableMapOf<String, User>(
    // DANIELA TINTI (Admin)
    "d.tinti@superspan.it" to User(
        _nome = "Daniela",
        _cognome = "Tinti",
        _email = "d.tinti@superspan.it",
        _password = "caccacacca",
        _admin = true
        // telefono e infoCandidatura restano null di default
    ),

    // PAOLO CORTELLESI (Non Admin)
    "p.cortellesi@gmail.com" to User(
        _nome = "Paolo",
        _cognome = "Cortellesi",
        _email = "p.cortellesi@gmail.com",
        _password = "password123",
        _admin = false
    )
)

//var currentUser by mutableStateOf(MapOfUser["p.cortellesi@gmail.com"]!!)

//------------------------------Da sistemare-------------------------------------------
/*class FilterData() {
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
}*/

class FilterData() {
    var nome: String by mutableStateOf("")

    // MODIFICA: Inizia con stringa vuota per non avere ordinamenti attivi all'avvio
    var ordinamento: String by mutableStateOf("")

    var ordinamentoCrescente: Boolean by mutableStateOf(true)
    var categorie: MutableList<Category> = mutableStateListOf<Category>()
    var minPrice: Double by mutableDoubleStateOf(0.0)
    var maxPrice: Double by mutableDoubleStateOf(maxPossiblePrice().toDouble())

    fun minPossiblePrice(): Float {
        // Aggiunto un controllo di sicurezza se la lista è vuota
        if (ListOfProduct.isEmpty()) return 0f
        return ListOfProduct.minOf { product -> product.prezzo }
    }

    fun maxPossiblePrice(): Float {
        // Aggiunto un controllo di sicurezza se la lista è vuota
        if (ListOfProduct.isEmpty()) return 100f
        return ListOfProduct.maxOf { product -> product.prezzo }
    }
}

class WorkFilterData() {
    var nome: String by mutableStateOf("")
    var ordinamento: String by mutableStateOf("Nome")
    var ordinamentoCrescente: Boolean by mutableStateOf(true)
    var tipiContratto: MutableList<TipoContratto> = mutableStateListOf<TipoContratto>()
    var orari: MutableList<OrarioLavoro> = mutableStateListOf<OrarioLavoro>()
    var ruoli: MutableList<Role> = mutableStateListOf<Role>()
    var distanzaMax: Float by mutableStateOf(100f)
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

enum class Role(val id: Int, val nome: String, val icon: ImageVector) {
    ADDETTO_VENDITE(0, "Addetto Vendite", Icons.Default.Image),
    CASSIERE(1, "Cassiere", Icons.Default.Image),
    MAGAZZINIERE(2, "Magazziniere", Icons.Default.Image),
    ADDETTO_BANCO(3, "Banco Gastronomia", Icons.Default.Image),
    RESPONSABILE(4, "Responsabile", Icons.Default.Image),
    MACELLERIA(5, "Macelleria", Icons.Default.Image),
    ADDETTO_SCAFFALI(6, "Rifornimento Scaffali", Icons.Default.Image),
    PESCHERIA(7, "Pescheria", Icons.Default.Image),
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

val ListOfCoupon = mutableStateListOf<Coupon>(
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



//-------------------------------PRODUCTS--------------------------------------------------

data class Product(
    private var _nome: String,
    private var _prezzo: Float,
    private var _image: ImageVector?,
    private var _categoria: Category,
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

data class Work(
    private var _nome: String,
    private var _stipendio: Float,
    private var _image: ImageVector?,
    private var _ruolo: Role,
) {
    var nome get() = _nome
        set(value) {_nome=value}

    var stipendio get() = _stipendio
        set(value) {_stipendio=value}

    var image get() = _image
        set(value) {_image=value}

    var categoria get() = _ruolo
        set(value) {_ruolo = value}
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

fun searchWorkOffer(filterData: WorkFilterData): List<WorkOffer> {
    var list: List<WorkOffer> = WorkOfferSearchList
    // Ricerca per ruolo o città
    if (filterData.nome.isNotEmpty()) {
        list = list.filter { offer ->
            offer.ruolo.contains(filterData.nome, ignoreCase = true) ||
            offer.citta.contains(filterData.nome, ignoreCase = true)
        }
    }
    // Filtro per ruolo
    if (filterData.ruoli.isNotEmpty()) {
        list = list.filter { offer -> filterData.ruoli.contains(offer.ruoloEnum) }
    }
    // Filtro per tipo contratto
    if (filterData.tipiContratto.isNotEmpty()) {
        list = list.filter { offer -> filterData.tipiContratto.contains(offer.tipoContratto) }
    }
    // Filtro per orario
    if (filterData.orari.isNotEmpty()) {
        list = list.filter { offer -> filterData.orari.contains(offer.orario) }
    }
    // Filtro per distanza
    list = list.filter { offer -> offer.distanzaKm <= filterData.distanzaMax }
    // Ordinamento
    list = when (filterData.ordinamento) {
        "Città" -> list.sortedBy { it.citta }
        else    -> list.sortedBy { it.ruolo }
    }
    if (!filterData.ordinamentoCrescente) list = list.reversed()
    return list
}

//-------------------------------SHAPES--------------------------------------------------


//Parabola header
class BottomOvalShape(private val curveDepth: androidx.compose.ui.unit.Dp) : androidx.compose.ui.graphics.Shape {
    override fun createOutline(
        size: androidx.compose.ui.geometry.Size,
        layoutDirection: androidx.compose.ui.unit.LayoutDirection,
        density: androidx.compose.ui.unit.Density
    ): androidx.compose.ui.graphics.Outline {
        val depthPx = with(density) { curveDepth.toPx() }
        val path = androidx.compose.ui.graphics.Path().apply {
            moveTo(0f, 0f)
            lineTo(size.width, 0f)
            // Arriviamo quasi in fondo
            lineTo(size.width, size.height - depthPx)
            // Curva più dolce: il punto di controllo (y1) ora è esattamente size.height
            // invece di size.height + depthPx
            quadraticBezierTo(
                x1 = size.width / 2f, y1 = size.height,
                x2 = 0f, y2 = size.height - depthPx
            )
            close()
        }
        return androidx.compose.ui.graphics.Outline.Generic(path)
    }
}
/*class BottomOvalShape(private val curveDepth: androidx.compose.ui.unit.Dp) : androidx.compose.ui.graphics.Shape {
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
}*/

/*class TopOvalShape(private val curveDepth: androidx.compose.ui.unit.Dp) : androidx.compose.ui.graphics.Shape {
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
}*/

//Patabola altre pagine
class TopOvalShape(private val curveDepth: androidx.compose.ui.unit.Dp) : androidx.compose.ui.graphics.Shape {
    override fun createOutline(
        size: androidx.compose.ui.geometry.Size,
        layoutDirection: androidx.compose.ui.unit.LayoutDirection,
        density: androidx.compose.ui.unit.Density
    ): androidx.compose.ui.graphics.Outline {
        val depthPx = with(density) { curveDepth.toPx() }

        // MODIFICA: startY deve essere 0f per eliminare il vuoto in alto
        val startY = 0f

        val path = androidx.compose.ui.graphics.Path().apply {
            // Iniziamo il disegno sul lato sinistro, un po' più in basso rispetto alla cima
            moveTo(0f, startY + depthPx)

            // Creiamo la curva che tocca il punto più alto (0) al centro dello schermo
            quadraticBezierTo(
                x1 = size.width / 2f, y1 = startY,
                x2 = size.width,      y2 = startY + depthPx
            )

            // Chiudiamo il rettangolo verso il basso
            lineTo(size.width, size.height)
            lineTo(0f, size.height)
            close()
        }
        return androidx.compose.ui.graphics.Outline.Generic(path)
    }
}

//-------------------------------WORK OFFERS--------------------------------------------------

// 1. Definizione delle opzioni fisse
enum class TipoContratto(val nome: String) {
    DETERMINATO("Determinato"),
    INDETERMINATO("Indeterminato")
}

enum class OrarioLavoro(val nome: String) {
    FULL_TIME("Full-time"),
    PART_TIME("Part-time")
}

// 2. Struttura dati
data class WorkOffer(
    val id: Int,
    val ruolo: String,
    val ruoloEnum: Role,
    val descrizioneBreve: String,
    val descrizioneEstesa: String,
    val requisiti: String,
    val citta: String,
    val indirizzo: String,
    val tipoContratto: TipoContratto,
    val orario: OrarioLavoro,
    val distanzaKm: Int = 50
)

// 3. Lista di offerte di lavoro disponibili
val WorkOfferSearchList = mutableStateListOf(
    // --- CAGLIARI (6 SEDI) ---
    WorkOffer(
        id = 9,
        ruolo = "Addetto alle vendite",
        ruoloEnum = Role.ADDETTO_VENDITE,
        descrizioneBreve = "Supporto alla clientela e allestimento reparti.",
        descrizioneEstesa = "La risorsa si occuperà dell'accoglienza dei clienti, della gestione dei prodotti a scaffale e del mantenimento dell'ordine nel punto vendita. È prevista la partecipazione alle attività di inventario.",
        requisiti = "Ottime doti comunicative, capacità di lavorare in team, flessibilità negli orari e attitudine al problem solving.",
        citta = "Cagliari",
        indirizzo = "Via Roma, 50",
        tipoContratto = TipoContratto.INDETERMINATO,
        orario = OrarioLavoro.FULL_TIME,
        distanzaKm = 1
    ),
    WorkOffer(
        id = 15,
        ruolo = "Cassiere/a esperto/a",
        ruoloEnum = Role.CASSIERE,
        descrizioneBreve = "Gestione dei pagamenti e delle procedure di cassa.",
        descrizioneEstesa = "Il ruolo prevede la responsabilità della gestione del denaro, l'apertura e chiusura cassa, e la gestione dei programmi fedeltà aziendali. Si richiede precisione e velocità nelle operazioni.",
        requisiti = "Diploma di scuola superiore, precisione nel calcolo, cordialità e pregressa esperienza nell'uso di software gestionali di cassa.",
        citta = "Cagliari",
        indirizzo = "Via Dante, 102",
        tipoContratto = TipoContratto.DETERMINATO,
        orario = OrarioLavoro.PART_TIME,
        distanzaKm = 2
    ),
    WorkOffer(
        id = 16,
        ruolo = "Addetto Rifornimento Scaffali",
        ruoloEnum = Role.ADDETTO_SCAFFALI,
        descrizioneBreve = "Gestione scorte e posizionamento merce.",
        descrizioneEstesa = "L'attività principale consiste nel prelievo della merce dal magazzino, nel posizionamento ordinato sugli scaffali e nella rotazione dei prodotti in base alla data di scadenza (FIFO).",
        requisiti = "Resistenza fisica, puntualità, attenzione ai dettagli e capacità di seguire le indicazioni dei responsabili di reparto.",
        citta = "Cagliari",
        indirizzo = "Via Riva Villasanta, 25",
        tipoContratto = TipoContratto.DETERMINATO,
        orario = OrarioLavoro.FULL_TIME,
        distanzaKm = 4
    ),
    WorkOffer(
        id = 17,
        ruolo = "Specialista Gastronomia",
        ruoloEnum = Role.ADDETTO_BANCO,
        descrizioneBreve = "Vendita assistita e preparazione di prodotti freschi.",
        descrizioneEstesa = "La figura si occuperà del servizio al banco salumi e formaggi, della preparazione di preparati pronti e della pulizia quotidiana degli strumenti di taglio e delle vetrine espositive.",
        requisiti = "Conoscenza approfondita dei prodotti alimentari, abilità nell'uso dell'affettatrice, possesso dell'attestato HACCP e cortesia verso il pubblico.",
        citta = "Cagliari",
        indirizzo = "Viale Diaz, 40",
        tipoContratto = TipoContratto.INDETERMINATO,
        orario = OrarioLavoro.FULL_TIME,
        distanzaKm = 2
    ),
    WorkOffer(
        id = 31,
        ruolo = "Magazziniere di Punto Vendita",
        ruoloEnum = Role.MAGAZZINIERE,
        descrizioneBreve = "Ricezione merci e organizzazione stock.",
        descrizioneEstesa = "Il candidato si occuperà dello scarico dei mezzi, della verifica della merce in entrata rispetto agli ordini e dell'organizzazione del magazzino per ottimizzare i tempi di rifornimento.",
        requisiti = "Capacità organizzative, dimestichezza con terminali portatili per inventario, velocità e attenzione alla sicurezza sul lavoro.",
        citta = "Cagliari",
        indirizzo = "Viale Trieste, 110",
        tipoContratto = TipoContratto.DETERMINATO,
        orario = OrarioLavoro.FULL_TIME,
        distanzaKm = 2
    ),
    WorkOffer(
        id = 32,
        ruolo = "Responsabile di Turno",
        ruoloEnum = Role.RESPONSABILE,
        descrizioneBreve = "Coordinamento del personale e apertura/chiusura.",
        descrizioneEstesa = "Figura di responsabilità che assicura il corretto funzionamento del punto vendita durante il proprio turno, gestendo le priorità dei vari reparti e l'assistenza clienti critica.",
        requisiti = "Esperienza pregressa nel retail, doti di leadership, affidabilità e capacità di gestione dei conflitti.",
        citta = "Cagliari",
        indirizzo = "Via Paoli, 15",
        tipoContratto = TipoContratto.INDETERMINATO,
        orario = OrarioLavoro.FULL_TIME,
        distanzaKm = 3
    ),

    // --- QUARTU SANT'ELENA (3 SEDI) ---
    WorkOffer(
        id = 10,
        ruolo = "Addetto Cassa e Informazioni",
        ruoloEnum = Role.CASSIERE,
        descrizioneBreve = "Assistenza al cliente e operazioni di pagamento.",
        descrizioneEstesa = "Oltre alle normali operazioni di cassa, la risorsa fungerà da punto di riferimento per le informazioni sui servizi del supermercato e sulla risoluzione di piccoli reclami.",
        requisiti = "Ottima dialettica, pazienza, orientamento al cliente e capacità di gestione dello stress nei momenti di affluenza.",
        citta = "Quartu Sant'Elena",
        indirizzo = "Via Merello, 85",
        tipoContratto = TipoContratto.INDETERMINATO,
        orario = OrarioLavoro.FULL_TIME,
        distanzaKm = 8
    ),
    WorkOffer(
        id = 33,
        ruolo = "Addetto Vendite Reparto Ortofrutta",
        ruoloEnum = Role.ADDETTO_VENDITE,
        descrizioneBreve = "Cura del reparto freschi e assistenza.",
        descrizioneEstesa = "La figura garantisce la freschezza e la qualità dei prodotti esposti nel reparto ortofrutta, occupandosi della pesatura e della consulenza ai clienti.",
        requisiti = "Attenzione alla qualità del prodotto, dinamismo, forza fisica per la movimentazione delle cassette e disponibilità al turno mattutino.",
        citta = "Quartu Sant'Elena",
        indirizzo = "Viale Colombo, 42",
        tipoContratto = TipoContratto.DETERMINATO,
        orario = OrarioLavoro.FULL_TIME,
        distanzaKm = 9
    ),
    WorkOffer(
        id = 34,
        ruolo = "Specialista Macelleria",
        ruoloEnum = Role.MACELLERIA,
        descrizioneBreve = "Lavorazione carni e vendita assistita.",
        descrizioneEstesa = "Il candidato si occuperà del disosso, del taglio delle carni bovini e suini e della preparazione dei preparati pronto-cuoci.",
        requisiti = "Maneggevolezza nell'uso dei coltelli, pregressa esperienza nel ruolo, conoscenza delle norme igieniche e cortesia al banco.",
        citta = "Quartu Sant'Elena",
        indirizzo = "Via Fiume, 2",
        tipoContratto = TipoContratto.INDETERMINATO,
        orario = OrarioLavoro.FULL_TIME,
        distanzaKm = 11
    ),

    // --- MONSERRATO (1 SEDE) ---
    WorkOffer(
        id = 20,
        ruolo = "Macellaio/a di reparto",
        ruoloEnum = Role.MACELLERIA,
        descrizioneBreve = "Lavorazione carni e allestimento banco macelleria.",
        descrizioneEstesa = "Garantirà il rispetto rigoroso delle norme igienico-sanitarie occupandosi della preparazione dei tagli di carne richiesti dalla clientela.",
        requisiti = "Esperienza specifica nel settore carni, possesso di attestato HACCP, serietà e capacità di gestire gli ordini di reparto.",
        citta = "Monserrato",
        indirizzo = "Via Cesare Cabras, 12",
        tipoContratto = TipoContratto.INDETERMINATO,
        orario = OrarioLavoro.FULL_TIME,
        distanzaKm = 7
    ),

    // --- SESTU (1 SEDE) ---
    WorkOffer(
        id = 35,
        ruolo = "Addetto Logistica di Magazzino",
        ruoloEnum = Role.MAGAZZINIERE,
        descrizioneBreve = "Gestione flussi logistici e stoccaggio.",
        descrizioneEstesa = "La risorsa coordinerà lo smistamento dei colli in arrivo verso i vari reparti del punto vendita tramite l'utilizzo di transpallet elettrici.",
        requisiti = "Patentino per il muletto in corso di validità, affidabilità, capacità di leggere le bolle di carico e attitudine al lavoro di precisione.",
        citta = "Sestu",
        indirizzo = "Ex SS 131, Km 10",
        tipoContratto = TipoContratto.DETERMINATO,
        orario = OrarioLavoro.FULL_TIME,
        distanzaKm = 12
    ),

    // --- SELARGIUS (1 SEDE) ---
    WorkOffer(
        id = 36,
        ruolo = "Addetto Banco Pescheria",
        ruoloEnum = Role.PESCHERIA,
        descrizioneBreve = "Selezione, pulizia e vendita di pescato fresco.",
        descrizioneEstesa = "Responsabile della vendita assistita al banco pesce: si occuperà della pulizia, sfilettatura e preparazione del banco per l'esposizione giornaliera.",
        requisiti = "Conoscenza dei prodotti ittici locali, ottima manualità nella sfilettatura, igiene rigorosa e capacità di consigliare i metodi di cottura.",
        citta = "Selargius",
        indirizzo = "Via Lussu, 8",
        tipoContratto = TipoContratto.DETERMINATO,
        orario = OrarioLavoro.PART_TIME,
        distanzaKm = 6
    ),

    // --- ALTRE SARDEGNA E ITALIA---
    WorkOffer(
        id = 23,
        ruolo = "Magazziniere Logistico",
        ruoloEnum = Role.MAGAZZINIERE,
        descrizioneBreve = "Movimentazione merci Iglesias.",
        descrizioneEstesa = "Controllo merci in entrata e verifica bolle di trasporto.",
        requisiti = "Conoscenza base Office, puntualità e organizzazione.",
        citta = "Iglesias",
        indirizzo = "Via Valverde, 10",
        tipoContratto = TipoContratto.DETERMINATO,
        orario = OrarioLavoro.FULL_TIME,
        distanzaKm = 55
    ),
    WorkOffer(
        id = 24,
        ruolo = "Store Manager",
        ruoloEnum = Role.RESPONSABILE,
        descrizioneBreve = "Direzione operativa Sanluri.",
        descrizioneEstesa = "Supervisione completa del punto vendita e coordinamento team.",
        requisiti = "Leadership comprovata, esperienza retail pluriennale.",
        citta = "Sanluri",
        indirizzo = "Piazza San Rocco, 4",
        tipoContratto = TipoContratto.INDETERMINATO,
        orario = OrarioLavoro.FULL_TIME,
        distanzaKm = 45
    ),
    WorkOffer(
        id = 26,
        ruolo = "Specialista Banco Pesce",
        ruoloEnum = Role.PESCHERIA,
        descrizioneBreve = "Pulizia e vendita Muravera.",
        descrizioneEstesa = "Gestione del banco pesce fresco ed esposizione.",
        requisiti = "Ottima manualità, conoscenza stagionalità ittica.",
        citta = "Muravera",
        indirizzo = "Via Roma, 210",
        tipoContratto = TipoContratto.DETERMINATO,
        orario = OrarioLavoro.FULL_TIME,
        distanzaKm = 65
    ),
    WorkOffer(
        id = 13,
        ruolo = "Addetto Macelleria Senior",
        ruoloEnum = Role.MACELLERIA,
        descrizioneBreve = "Specialista nella lavorazione carni.",
        descrizioneEstesa = "Gestione completa del reparto macelleria, dalla ricezione mezzene al banco servito.",
        requisiti = "Esperienza decennale, autonomia totale, leadership.",
        citta = "Sassari",
        indirizzo = "Viale Italia, 88",
        tipoContratto = TipoContratto.INDETERMINATO,
        orario = OrarioLavoro.FULL_TIME,
        distanzaKm = 212
    ),
    WorkOffer(
        id = 2,
        ruolo = "Operatore di Cassa",
        ruoloEnum = Role.CASSIERE,
        descrizioneBreve = "Supporto alle operazioni di front-end.",
        descrizioneEstesa = "Gestione transazioni monetarie e informazioni ai clienti. Supporto all'allestimento avancassa.",
        requisiti = "Cordialità, velocità d'esecuzione, precisione nel conteggio.",
        citta = "Roma",
        indirizzo = "Viale Marconi, 45",
        tipoContratto = TipoContratto.INDETERMINATO,
        orario = OrarioLavoro.PART_TIME,
        distanzaKm = 410
    ),
    WorkOffer(
        id = 1,
        ruolo = "Addetto Reparto Ortofrutta",
        ruoloEnum = Role.ADDETTO_VENDITE,
        descrizioneBreve = "Cura del reparto freschi e assistenza.",
        descrizioneEstesa = "Selezione qualità frutta e verdura, pesatura per i clienti e rifornimento banchi.",
        requisiti = "Dinamismo, forza fisica e attenzione alla freschezza.",
        citta = "Milano",
        indirizzo = "Via Torino, 12",
        tipoContratto = TipoContratto.DETERMINATO,
        orario = OrarioLavoro.FULL_TIME,
        distanzaKm = 695
    )
)


//-------------------------------Profile--------------------------------------------------

data class UserProfile(
    val nomeUtente: String,
    val email: String,
    val nome: String,
    val cognome: String,
    val numeroTelefono: String
)


//-------------------------------------Candidature lavoro-------------------------------
// Inserisci questo dove hai le altre classi dati (User.kt)
data class Candidacy(
    val id: Int,
    val userEmail: String,       // Chi si candida
    val offerId: Int,            // Per quale lavoro
    val nome: String,
    val cognome: String,
    val emailContatto: String,
    val cvFileName: String?,
    val videoSimulatoPath: String = "video_registrato.mp4",
    val stato: String = "Inviata" // "Inviata", "In Revisione", "Rifiutata", "Accettata", "Bozza"
)

// Lista globale accessibile agli Admin
val AllCandidacies = mutableStateListOf<Candidacy>()

// Variabile temporanea per sapere per quale offerta Paolo sta scrivendo
var currentOfferIdApplying by mutableIntStateOf(0)