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
    private var _cvFileName: String? = null // Nome del file PDF caricato
) {
    // ... getter e setter ...
    var telefono get() = _telefono; set(v) { _telefono = v }
    var emailLavoro get() = _emailLavoro; set(v) { _emailLavoro = v }
    var cvFileName get() = _cvFileName; set(v) { _cvFileName = v }
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
val WorkOfferSearchList = listOf(
    WorkOffer(
        id = 1,
        ruolo = "Addetto alle vendite",
        ruoloEnum = Role.ADDETTO_VENDITE,
        descrizioneBreve = "Cerchiamo personale dinamico per il reparto ortofrutta.",
        descrizioneEstesa = "Il candidato si occuperà dell'allestimento del banco, della pesatura dei prodotti e dell'assistenza alla clientela nel reparto freschi. È richiesta precisione e puntualità.",
        requisiti = "Esperienza minima nel settore, orientamento al cliente, flessibilità oraria e possesso di attestato HACCP.",
        citta = "Milano",
        indirizzo = "Via Torino, 12",
        tipoContratto = TipoContratto.DETERMINATO,
        orario = OrarioLavoro.FULL_TIME,
        distanzaKm = 8
    ),
    WorkOffer(
        id = 2,
        ruolo = "Cassiere/a",
        ruoloEnum = Role.CASSIERE,
        descrizioneBreve = "Gestione cassa e assistenza clienti nel punto vendita.",
        descrizioneEstesa = "La figura inserita gestirà le transazioni di pagamento, l'apertura e chiusura cassa e fornirà informazioni sui programmi fedeltà del supermercato.",
        requisiti = "Diploma di scuola superiore, ottime doti comunicative, dimestichezza con i sistemi informatici di base.",
        citta = "Roma",
        indirizzo = "Viale Marconi, 45",
        tipoContratto = TipoContratto.INDETERMINATO,
        orario = OrarioLavoro.PART_TIME,
        distanzaKm = 60
    ),
    WorkOffer(
        id = 3,
        ruolo = "Magazziniere",
        ruoloEnum = Role.MAGAZZINIERE,
        descrizioneBreve = "Gestione merci in entrata e uscita.",
        descrizioneEstesa = "Il lavoro prevede lo scarico dei camion, il controllo delle bolle d'accompagnamento e lo stoccaggio dei prodotti nelle celle frigorifere o negli scaffali del magazzino.",
        requisiti = "Patentino per il muletto in corso di validità, buona forza fisica, capacità di lavorare in team.",
        citta = "Torino",
        indirizzo = "Corso Francia, 120",
        tipoContratto = TipoContratto.DETERMINATO,
        orario = OrarioLavoro.FULL_TIME,
        distanzaKm = 140
    ),
    WorkOffer(
        id = 4,
        ruolo = "Addetto al Banco Gastronomia",
        ruoloEnum = Role.ADDETTO_BANCO,
        descrizioneBreve = "Servizio al cliente e preparazione piatti pronti.",
        descrizioneEstesa = "La risorsa si occuperà del taglio di salumi e formaggi, della preparazione di panini e piatti pronti e della pulizia delle attrezzature di reparto.",
        requisiti = "Esperienza nell'uso dell'affettatrice, conoscenza dei prodotti caseari, attestato HACCP obbligatorio.",
        citta = "Napoli",
        indirizzo = "Via Toledo, 200",
        tipoContratto = TipoContratto.INDETERMINATO,
        orario = OrarioLavoro.PART_TIME,
        distanzaKm = 720
    ),
    WorkOffer(
        id = 5,
        ruolo = "Responsabile del Punto Vendita",
        ruoloEnum = Role.RESPONSABILE,
        descrizioneBreve = "Coordinamento del team e gestione ordini.",
        descrizioneEstesa = "Lo Store Manager supervisiona tutte le attività del negozio: gestione dei turni, analisi delle vendite, controllo degli stock e raggiungimento degli obiettivi commerciali.",
        requisiti = "Pregressa esperienza di almeno 3 anni in ruoli di gestione retail, leadership, ottime capacità analitiche.",
        citta = "Firenze",
        indirizzo = "Piazza della Libertà, 5",
        tipoContratto = TipoContratto.INDETERMINATO,
        orario = OrarioLavoro.FULL_TIME,
        distanzaKm = 300
    ),
    WorkOffer(
        id = 6,
        ruolo = "Addetto Reparto Macelleria",
        ruoloEnum = Role.MACELLERIA,
        descrizioneBreve = "Lavorazione carni e vendita assistita.",
        descrizioneEstesa = "Il candidato si occuperà del disosso, del taglio delle carni e della preparazione di preparati pronto-cuoci. Gestirà inoltre il rifornimento del banco frigo.",
        requisiti = "Ottima manualità nel taglio della carne, conoscenza delle norme igienico-sanitarie, cortesia verso il cliente.",
        citta = "Bologna",
        indirizzo = "Via dell'Indipendenza, 12",
        tipoContratto = TipoContratto.DETERMINATO,
        orario = OrarioLavoro.FULL_TIME,
        distanzaKm = 210
    ),
    WorkOffer(
        id = 7,
        ruolo = "Addetto Rifornimento Scaffali (Notturno)",
        ruoloEnum = Role.ADDETTO_SCAFFALI,
        descrizioneBreve = "Allestimento corsie durante la chiusura.",
        descrizioneEstesa = "L'attività prevede il posizionamento dei prodotti negli scaffali, la rotazione delle scadenze e la rimozione degli imballaggi per garantire l'ordine all'apertura del negozio.",
        requisiti = "Massima serietà, velocità d'esecuzione, disponibilità immediata al turno notturno (22:00 - 06:00).",
        citta = "Milano",
        indirizzo = "Viale Monza, 80",
        tipoContratto = TipoContratto.DETERMINATO,
        orario = OrarioLavoro.PART_TIME,
        distanzaKm = 12
    ),
    WorkOffer(
        id = 8,
        ruolo = "Specialista Reparto Pescheria",
        ruoloEnum = Role.PESCHERIA,
        descrizioneBreve = "Pulizia pesce e servizio al banco.",
        descrizioneEstesa = "Gestione del banco pesce fresco: pulizia, sfilettatura e consulenza ai clienti sulle tipologie di pescato e metodi di cottura.",
        requisiti = "Esperienza nella sfilettatura, conoscenza del pescato di stagione, flessibilità nei turni mattutini.",
        citta = "Genova",
        indirizzo = "Via XX Settembre, 10",
        tipoContratto = TipoContratto.INDETERMINATO,
        orario = OrarioLavoro.FULL_TIME,
        distanzaKm = 145
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
