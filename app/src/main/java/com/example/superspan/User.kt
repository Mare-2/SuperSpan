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
    private var _telefono: String? = null,
    private var _emailLavoro: String? = null,
    private var _cvFileName: String? = null,
    private var _cvPath: String? = null,
    // Mappa delle bozze: ID Offerta -> Bozza
    private var _candidacyDraftsByOfferId: MutableMap<Int, CandidacyDraft> = mutableStateMapOf(),
    private var _viewedOffers: MutableList<Int> = mutableStateListOf()
) {
    var nome get() = _nome; set(v) { _nome = v }
    var cognome get() = _cognome; set(v) { _cognome = v }
    var email get() = _email; set(v) { _email = v }
    var password get() = _password; set(v) { _password = v }
    var admin get() = _admin; set(v) { _admin = v }
    var telefono get() = _telefono; set(v) { _telefono = v }
    var emailLavoro get() = _emailLavoro; set(v) { _emailLavoro = v }
    var cvFileName get() = _cvFileName; set(v) { _cvFileName = v }
    var cvPath get() = _cvPath; set(v) { _cvPath = v }
    var candidacyDraftsByOfferId get() = _candidacyDraftsByOfferId; set(v) { _candidacyDraftsByOfferId = v }
    var viewedOffers get() = _viewedOffers; set(v) { _viewedOffers = v }
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
var actualUser: User by mutableStateOf(MapOfUser["p.cortellesi@gmail.com"]!!)
//------------------------------Da sistemare-------------------------------------------
class FilterData() {
    var nome: String by mutableStateOf("")

    // Ordinamenti indipendenti (null = disattivo, true = crescente, false = decrescente)
    var ordinamentoNomeCrescente: Boolean? by mutableStateOf(null)
    var ordinamentoPrezzoCrescente: Boolean? by mutableStateOf(null)

    var categorie: MutableList<Category> = mutableStateListOf<Category>()
    var minPrice: Double by mutableDoubleStateOf(0.0)
    var maxPrice: Double by mutableDoubleStateOf(maxPossiblePrice().toDouble())

    fun minPossiblePrice(): Float {
        // Aggiunto un controllo di sicurezza se la lista è vuota
        if (ListOfProduct.isEmpty()) return 0f
        return ListOfProduct.minOf { product -> 
            val discount = ListOfCoupon.filter { it.products.contains(product) }.maxOfOrNull { it.discount } ?: 0f
            product.prezzo * (1 - discount / 100)
        }
    }

    fun maxPossiblePrice(): Float {
        // Aggiunto un controllo di sicurezza se la lista è vuota
        if (ListOfProduct.isEmpty()) return 100f
        return ListOfProduct.maxOf { product -> 
            val discount = ListOfCoupon.filter { it.products.contains(product) }.maxOfOrNull { it.discount } ?: 0f
            product.prezzo * (1 - discount / 100)
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
    BEVANDE(7, "Bevande", Icons.Default.Image),
    CARNE(8, "Carne", Icons.Default.Image),
    SALUMI(9, "Salumi", Icons.Default.Image),
    PESCE(10, "Pescheria", Icons.Default.Image)
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

val ListOfProduct = mutableStateListOf<Product>(
    Product("Pane Fresco", 1.20f, R.drawable.pane_fresco, Category.PANETTERIA, "Prodotto da forno preparato quotidianamente dai nostri mastri fornai. Crosta dorata e croccante con una mollica soffice, ideale per la tavola di tutti i giorni. Da conservare in luogo fresco e asciutto.", "Farina di grano tenero tipo '0', acqua, lievito madre, sale, malto d'orzo."),
    Product("Latte Intero 1L", 1.50f, R.drawable.latte_intero, Category.LATTICINI, "Latte bovino intero pastorizzato, proveniente al 100% da allevamenti italiani selezionati. Un alimento completo e genuino, perfetto per la colazione di tutta la famiglia.", null),
    Product("Fusilli - Pasta Barilla 500g", 0.99f, R.drawable.fusilli, Category.DISPENSA, "Gli Fusilli Barilla sono tra i formati di pasta corta più amati dagli italiani. Grazie alla loro forma a spirale, trattengono perfettamente ogni sugo, garantendo un risultato sempre delizioso. Ottimi con ragù, pomodoro fresco o pesto, per un primo piatto che non delude mai.", "Semola di grano duro, acqua. Può contenere tracce di soia e senape."),
    Product("Spaghetti - Pasta Barilla 500g", 1.49f, R.drawable.spaghetti, Category.DISPENSA, "Gli Spaghetti Barilla sono tra i formati di pasta lunga più amati dagli italiani. Grazie alla loro consistenza ruvida e porosa, trattengono perfettamente ogni sugo, garantendo un risultato sempre delizioso. Ottimi con ragù, pomodoro fresco o pesto, per un primo piatto che non delude mai.", "Semola di grano duro, acqua. Può contenere tracce di soia e senape."),
    Product("Penne Rigate - Pasta Barilla 500g", 1.99f, R.drawable.pennette, Category.DISPENSA, "Le Penne Rigate Barilla sono tra i formati di pasta corta più amati dagli italiani. Grazie alla loro rigatura esterna trattengono al meglio ogni sugo, garantendo una tenuta in cottura sempre perfetta.", "Semola di grano duro, acqua. Può contenere tracce di soia e senape."),
    Product("Salsa di Pomodoro La Selva", 1.10f, R.drawable.salsa_pomodoro, Category.DISPENSA, "Passata di pomodoro classica, realizzata esclusivamente con pomodori freschi italiani maturati al sole e lavorati in giornata. La base ideale per sughi genuini e saporiti.", "Pomodoro fresco (99%), sale, correttore di acidità: acido citrico."),
    Product("Salsa di Pomodoro Mutti", 1.30f, R.drawable.salsa_mutti, Category.DISPENSA, "Passata di pomodoro Mutti, realizzata esclusivamente con pomodori freschi italiani maturati al sole e lavorati in giornata. La base ideale per sughi genuini e saporiti.", "Pomodoro fresco (99%), sale, correttore di acidità: acido citrico."),
    Product("Salsa di Pomodoro Casar", 1.20f, R.drawable.salsa_casar, Category.DISPENSA, "Passata di pomodoro Casar, realizzata con pomodori freschi italiani maturati al sole e lavorati in giornata. La base ideale per sughi genuini e saporiti.", "Pomodoro fresco (99%), sale, correttore di acidità: acido citrico."),
    Product("Mele (sacchetto 1kg)", 2.30f, R.drawable.mele, Category.FRUTTA_E_VERDURA, "Mele fresche di prima categoria, calibro 70/80mm. Polpa croccante e succosa con un perfetto equilibrio tra dolcezza e acidità. Origine: Italia. Ideali per il consumo fresco o in pasticceria.", null),
    Product("Uova (confezione da 6)", 1.80f, R.drawable.uova, Category.LATTICINI, "Uova fresche categoria A, calibro grande. Provenienti da galline allevate a terra in fattorie italiane controllate. Ottime per ogni preparazione culinaria dolce o salata.", null),
    Product("Parmigiano Reggiano 200g", 5.50f, R.drawable.parmigiano, Category.LATTICINI, "Formaggio a pasta dura DOP, stagionato minimo 24 mesi. Prodotto artigianale dal sapore ricco, complesso e inconfondibile. Naturalmente privo di lattosio. Confezionato sottovuoto.", "Latte, sale, caglio."),
    Product("Biscotti al Cioccolato", 2.99f, R.drawable.biscotti, Category.COLAZIONE, "Frollini friabili e golosi arricchiti con vere gocce di cioccolato fondente. Perfetti per iniziare la giornata con la giusta carica di energia o per una pausa sfiziosa.", "Farina di frumento, zucchero, olio di semi di girasole, gocce di cioccolato fondente (15%) (zucchero, pasta di cacao, burro di cacao, emulsionante: lecitina di soia), uova fresche, agenti lievitanti, aromi."),
    Product("Detersivo Piatti", 1.45f, R.drawable.sapone_piatti, Category.PULIZIA_CASA, "Detergente liquido per stoviglie a mano. Azione ultra sgrassante al profumo di limone, efficace contro lo sporco difficile anche in acqua fredda. Rispetta il pH della pelle.", null),
    Product("Carta Igienica (4 rotoli)", 2.20f, R.drawable.carta_ig, Category.IGIENE_PERSONALE, "Carta igienica in pura ovatta di cellulosa, a tre veli. Straordinaria morbidezza e resistenza garantita. Prodotto testato dermatologicamente, ideale anche per pelli sensibili.", null),
    Product("Acqua Naturale 1.5L", 0.40f, R.drawable.acqua, Category.BEVANDE, "Acqua oligominerale naturale, pura e leggera. Nasce in alta quota in un ambiente incontaminato. Indicata per le diete povere di sodio e adatta all'alimentazione dei neonati.", null),
    Product("Caffè Macinato 250g", 3.50f, R.drawable.caffe, Category.DISPENSA, "Miscela di caffè tostato e macinato di qualità superiore (Arabica e Robusta). Tostatura lenta per un aroma intenso, crema vellutata e gusto corposo. Ideale per moka e macchine espresso casa.", "100% miscela di caffè tostato e macinato."),
    Product("Pane Integrale 500g", 1.80f, R.drawable.pane_integrale, Category.PANETTERIA, "Prodotto da forno a fette con farina integrale e arricchito con un mix di semi (girasole, lino, sesamo). Elevato apporto di fibre, consistenza morbida. Perfetto per sandwich bilanciati.", "Farina integrale di grano tenero (50%), acqua, lievito, olio di semi di girasole, semi di girasole, semi di lino, semi di sesamo, sale, farina di cereali maltati."),
    Product("Banane (al kg)", 1.20f, R.drawable.banane, Category.FRUTTA_E_VERDURA, "Banane Cavendish di prima qualità, importate e maturate accuratamente. Ricche di potassio e vitamine. Ottime a colazione, come snack sportivo o per preparare frullati.", null),
    Product("Yogurt Naturale 125g", 0.85f, R.drawable.yogurt, Category.LATTICINI, "Yogurt intero bianco, consistenza cremosa e sapore delicatamente acidulo. Preparato solo con latte fresco di alta qualità. Senza conservanti né zuccheri aggiunti.", "Latte intero pastorizzato, fermenti lattici vivi (Streptococcus thermophilus, Lactobacillus bulgaricus)."),
    Product("Riso Arborio 1kg", 2.40f, R.drawable.riso, Category.DISPENSA, "Riso dai chicchi grandi e perlati, tipico della tradizione culinaria italiana. Grazie all'alto contenuto di amido, garantisce una mantecatura eccezionale. Il compagno perfetto per risotti.", null),
    Product("Olio d'Oliva Extra Vergine 500ml", 7.99f, R.drawable.olio, Category.DISPENSA, "Olio Extra Vergine di Oliva ottenuto a freddo da olive raccolte al giusto grado di maturazione. Sapore fruttato, leggermente piccante nel finale. Ideale a crudo su insalate, verdure e zuppe.", null),
    Product("Cereali Muesli", 3.20f, R.drawable.cereali, Category.COLAZIONE, "Mix croccante per la colazione a base di cereali integrali, frutta essiccata e nocciole tostate. Ottima fonte di fibre ed energia per cominciare la giornata con vitalità.", "Fiocchi d'avena integrali (45%), uvetta sultanina (15%), fiocchi di frumento, zucchero di canna, nocciole tostate (5%), miele, olio di semi di girasole."),
    Product("Shampoo Neutro 250ml", 2.75f, R.drawable.schampoo, Category.IGIENE_PERSONALE, "Shampoo delicato per lavaggi frequenti. Deterge a fondo rispettando il film idrolipidico di cute e capelli. Lascia i capelli morbidi, luminosi e facili da pettinare. Formula senza parabeni.", null),
    Product("Succo d'Arancia 1L", 1.30f, R.drawable.succo_arancia, Category.BEVANDE, "Succo 100% arancia bionda da concentrato. Naturalmente ricco di Vitamina C. Gusto dolce e rinfrescante, dissetante e ideale per la colazione di tutta la famiglia.", "Succo d'arancia da concentrato, antiossidante (acido ascorbico)."),
    Product("Pollo Intero 1kg", 6.50f, R.drawable.pollo, Category.CARNE, "Pollo intero classe A, eviscerato. Allevato a terra in Italia senza l'uso di antibiotici. Carni tenere, delicate e versatili, perfette per arrosti domenicali o cotture in umido.", null),
    Product("Spazzolino da Denti", 1.99f, R.drawable.spazzolino, Category.IGIENE_PERSONALE, "Spazzolino manuale per adulti con testina compatta e setole a durezza media. Manico ergonomico antiscivolo. Rimuove efficacemente la placca raggiungendo le zone difficili.", null),
    Product("Burro Arborea", 2.50f, R.drawable.burro, Category.LATTICINI, "Burro tradizionale ottenuto mediante la zangolatura della panna fresca del latte. Dal profumo delicato e sapore inconfondibile, è l'ingrediente principe per pasticceria e pietanze salate.", "Crema di latte (panna), fermenti lattici."),
    Product("Macinato di carne", 4.50f, R.drawable.macinato, Category.CARNE, "Preparato di carne bovina macinata fresca. Taglio magro e saporito, rigorosamente da allevamenti italiani. Ottimo per ragù alla bolognese, polpette morbidissime e profumati hamburger.", null),
    Product("Pan Bauletto", 1.50f, R.drawable.pan_bauletto, Category.PANETTERIA, "Pane a fette tipo bauletto. Consistenza estremamente soffice ed elastica, crosta quasi assente. La base perfetta per sfiziosi toast farciti, sandwich o tramezzini.", "Farina di grano tenero tipo '0', acqua, olio extra vergine di oliva, lievito, zucchero, sale, farina di malto di frumento."),
    Product("Prosciutto Cotto 100g", 2.50f, R.drawable.prosciutto_cotto, Category.SALUMI, "Prosciutto cotto di Alta Qualità affettato fresco. Coscia di suino selezionata, cotta lentamente a vapore per esaltarne la dolcezza e la morbidezza. Senza glutine.", "Coscia di suino (85%), acqua, sale, destrosio, aromi, antiossidante: ascorbato di sodio, conservante: nitrito di sodio."),
    Product("Trancio di Salmone 250g", 5.90f, R.drawable.salmone, Category.PESCE, "Trancio di Salmone fresco (Salmo salar) allevato responsabilmente nei mari della Norvegia. Carni sode e ricche di Omega-3. Pulito, pronto per essere cotto al vapore, al forno o in padella.", null)
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
        return product.prezzo - (product.prezzo * (_discount / 100))
    }

    override fun toString(): String {
        return "$_code, $_discount"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Coupon) return false

        if (this.code != other.code) return false
        if (this.discount != other.discount) return false
        if (this.description != other.description) return false
        if (this.dateOfExpiration != other.dateOfExpiration) return false
        if (this.products != other.products) return false

        return true
    }

    override fun hashCode(): Int {
        var result = _code.hashCode()
        result = 31 * result + _discount.hashCode()
        result = 31 * result + _description.hashCode()
        result = 31 * result + _dateOfExpiration.hashCode()
        result = 31 * result + products.hashCode()
        return result
    }

}

val ListOfCoupon = mutableStateListOf<Coupon>(
    // Coupon (3 prodotti)
    Coupon("Sconto10", 10f, "10% di sconto su Colazione Completa", "2026-06-30",
        ListOfProduct.find { it.nome == "Latte Intero 1L" }!!,
        ListOfProduct.find { it.nome == "Cereali Muesli" }!!,
        ListOfProduct.find { it.nome == "Biscotti al Cioccolato" }!!
    ),
    Coupon("Sconto20", 20f, "20% di sconto su Kit Pranzo Veloce", "2026-07-15",
        ListOfProduct.find { it.nome == "Pasta Barilla 500g" }!!,
        ListOfProduct.find { it.nome == "Salsa di Pomodoro" }!!,
        ListOfProduct.find { it.nome == "Parmigiano Reggiano 200g" }!!
    ),
    Coupon("FRESCO25", 25f, "25% di sconto su Kit Frutta e Verdura", "2026-07-20",
        ListOfProduct.find { it.nome == "Mele (sacchetto 1kg)" }!!,
        ListOfProduct.find { it.nome == "Banane (al kg)" }!!,
        ListOfProduct.find { it.nome == "Yogurt Naturale 125g" }!!
    ),
    Coupon("CASA22", 22f, "22% di sconto su Kit Pulizia", "2026-07-10",
        ListOfProduct.find { it.nome == "Detersivo Piatti" }!!,
        ListOfProduct.find { it.nome == "Carta Igienica (4 rotoli)" }!!,
        ListOfProduct.find { it.nome == "Shampoo Neutro 250ml" }!!
    ),
    Coupon("PROTEIN30", 30f, "30% di sconto su Kit Proteico", "2026-08-10",
        ListOfProduct.find { it.nome == "Pollo Intero 1kg" }!!,
        ListOfProduct.find { it.nome == "Uova (confezione da 6)" }!!,
        ListOfProduct.find { it.nome == "Trancio di Salmone 250g" }!!
    ),
    // Promozioni (1 solo prodotto)
    Coupon("PANE5", 5f, "Sconto 5% su Pane Fresco", "2026-06-25",
        ListOfProduct.find { it.nome == "Pane Fresco" }!!
    ),
    Coupon("LATTE8", 8f, "Sconto 8% su Latte Intero 1L", "2026-07-12",
        ListOfProduct.find { it.nome == "Latte Intero 1L" }!!
    ),
    Coupon("PASTA10", 10f, "Sconto 10% su Pasta Barilla", "2026-07-08",
        ListOfProduct.find { it.nome == "Pasta Barilla 500g" }!!
    ),
    Coupon("POMODORO7", 7f, "Sconto 7% su Salsa di Pomodoro", "2026-07-22",
        ListOfProduct.find { it.nome == "Salsa di Pomodoro" }!!
    ),
    Coupon("UOVA12", 12f, "Sconto 12% su Uova confezione da 6", "2026-07-05",
        ListOfProduct.find { it.nome == "Uova (confezione da 6)" }!!
    ),
    Coupon("PARMIGIANO15", 15f, "Sconto 15% su Parmigiano Reggiano", "2026-08-05",
        ListOfProduct.find { it.nome == "Parmigiano Reggiano 200g" }!!
    ),
    Coupon("BISCOTTI9", 9f, "Sconto 9% su Biscotti al Cioccolato", "2026-06-30",
        ListOfProduct.find { it.nome == "Biscotti al Cioccolato" }!!
    ),
    Coupon("YOUGURT6", 6f, "Sconto 6% su Yogurt Naturale", "2026-07-15",
        ListOfProduct.find { it.nome == "Yogurt Naturale 125g" }!!
    ),
    Coupon("RISO11", 11f, "Sconto 11% su Riso Arborio", "2026-07-27",
        ListOfProduct.find { it.nome == "Riso Arborio 1kg" }!!
    ),
    Coupon("OLIO14", 14f, "Sconto 14% su Olio d'Oliva Extra Vergine", "2026-08-20",
        ListOfProduct.find { it.nome == "Olio d'Oliva Extra Vergine 500ml" }!!
    ),
    Coupon("CAFFE13", 13f, "Sconto 13% su Caffè Macinato", "2026-07-31",
        ListOfProduct.find { it.nome == "Caffè Macinato 250g" }!!
    ),
    Coupon("ACQUA4", 4f, "Sconto 4% su Acqua Naturale", "2026-06-28",
        ListOfProduct.find { it.nome == "Acqua Naturale 1.5L" }!!
    ),
    Coupon("POLLO19", 19f, "Sconto 19% su Pollo Intero", "2026-07-15",
        ListOfProduct.find { it.nome == "Pollo Intero 1kg" }!!
    ),
    Coupon("BANANE8", 8f, "Sconto 8% su Banane", "2026-07-10",
        ListOfProduct.find { it.nome == "Banane (al kg)" }!!
    )
)



//-------------------------------PRODUCTS--------------------------------------------------

data class Product(
    private var _nome: String,
    private var _prezzo: Float,
    private var _image: Int?,
    private var _categoria: Category,
    private var _descrizione: String = "",
    private var _ingredienti: String? = null
) {
    var nome get() = _nome
        set(value) {_nome=value}

    var prezzo get() = _prezzo
        set(value) {_prezzo=value}

    var image get() = _image
        set(value) {_image=value}

    var categoria get() = _categoria
        set(value) {_categoria = value}

    var descrizione get() = _descrizione
        set(value) {_descrizione = value}

    var ingredienti get() = _ingredienti
        set(value) {_ingredienti = value}
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
    
    // Filtro categorie
    if (filterData.categorie.isNotEmpty()) {
        list = filterProduct(list, filterData.categorie)
    }
    // Filtro testo
    if (filterData.nome.isNotEmpty()) {
        list = list.filter { product ->
            product.nome.contains(filterData.nome, ignoreCase = true)
        }
    }
    // Filtro prezzo
    list = list.filter { product ->
        val discount = ListOfCoupon.filter { it.products.contains(product) }.maxOfOrNull { it.discount } ?: 0f
        val actualPrice = product.prezzo * (1 - discount / 100)
        actualPrice >= filterData.minPrice && actualPrice <= filterData.maxPrice
    }

    // Ordinamento combinato
    list = list.sortedWith { p1, p2 ->
        var cmp = 0
        // Ordina prima per Prezzo (se attivo)
        if (filterData.ordinamentoPrezzoCrescente != null) {
            val d1 = ListOfCoupon.filter { it.products.contains(p1) }.maxOfOrNull { it.discount } ?: 0f
            val p1Price = p1.prezzo * (1 - d1 / 100)
            
            val d2 = ListOfCoupon.filter { it.products.contains(p2) }.maxOfOrNull { it.discount } ?: 0f
            val p2Price = p2.prezzo * (1 - d2 / 100)
            
            cmp = p1Price.compareTo(p2Price)
            if (!filterData.ordinamentoPrezzoCrescente!!) cmp = -cmp
        }
        // Se i prezzi sono uguali o il prezzo non è attivo, ordina per Nome (se attivo)
        if (cmp == 0 && filterData.ordinamentoNomeCrescente != null) {
            cmp = p1.nome.compareTo(p2.nome)
            if (!filterData.ordinamentoNomeCrescente!!) cmp = -cmp
        }
        cmp
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
            offer.titolo.contains(filterData.nome, ignoreCase = true) ||
            offer.supermarket.citta.contains(filterData.nome, ignoreCase = true)
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
        "Città" -> list.sortedBy { it.supermarket.citta }
        else    -> list.sortedBy { it.titolo }
    }
    if (!filterData.ordinamentoCrescente) list = list.reversed()
    return list
}

fun calcolaDistanzaSimulata(citta: String): Int {
    return when (citta.lowercase().trim()) {
        "cagliari" -> (1..5).random()
        "quartu sant'elena" -> (6..11).random()
        "monserrato", "selargius" -> (5..8).random()
        "sestu" -> (10..15).random()
        "iglesias" -> 55
        "sanluri" -> 45
        "muravera" -> 65
        "sassari" -> 212
        "roma" -> 410
        "milano" -> 695
        else -> (10..50).random()
    }
}

// Shapes rimosse per il nuovo design moderno (Senza parabole)//-------------------------------WORK OFFERS--------------------------------------------------

data class Supermarket(
    val id: Int,
    val nome: String,
    val citta: String,
    val indirizzo: String
)

val ListOfSupermarkets = mutableStateListOf(
    Supermarket(1, "SuperSpan Cagliari Roma", "Cagliari", "Via Roma, 50"),
    Supermarket(2, "SuperSpan Cagliari Dante", "Cagliari", "Via Dante, 102"),
    Supermarket(3, "SuperSpan Cagliari Villasanta", "Cagliari", "Via Riva Villasanta, 25"),
    Supermarket(4, "SuperSpan Cagliari Diaz", "Cagliari", "Viale Diaz, 40"),
    Supermarket(5, "SuperSpan Cagliari Trieste", "Cagliari", "Viale Trieste, 110"),
    Supermarket(6, "SuperSpan Cagliari Paoli", "Cagliari", "Via Paoli, 15"),
    Supermarket(7, "SuperSpan Quartu Merello", "Quartu Sant'Elena", "Via Merello, 85"),
    Supermarket(8, "SuperSpan Quartu Colombo", "Quartu Sant'Elena", "Viale Colombo, 42"),
    Supermarket(9, "SuperSpan Quartu Fiume", "Quartu Sant'Elena", "Via Fiume, 2"),
    Supermarket(10, "SuperSpan Monserrato", "Monserrato", "Via Cesare Cabras, 12"),
    Supermarket(11, "SuperSpan Sestu", "Sestu", "Ex SS 131, Km 10"),
    Supermarket(12, "SuperSpan Selargius", "Selargius", "Via Lussu, 8"),
    Supermarket(13, "SuperSpan Iglesias", "Iglesias", "Via Valverde, 10"),
    Supermarket(14, "SuperSpan Sanluri", "Sanluri", "Piazza San Rocco, 4"),
    Supermarket(15, "SuperSpan Muravera", "Muravera", "Via Roma, 210"),
    Supermarket(16, "SuperSpan Sassari", "Sassari", "Viale Italia, 88"),
    Supermarket(17, "SuperSpan Roma", "Roma", "Viale Marconi, 45"),
    Supermarket(18, "SuperSpan Milano", "Milano", "Via Torino, 12")
)

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
    val titolo: String,
    val ruoloEnum: Role,
    val descrizioneBreve: String,
    val descrizioneEstesa: String,
    val requisiti: String,
    val supermarket: Supermarket,
    val tipoContratto: TipoContratto,
    val orario: OrarioLavoro,
    val distanzaKm: Int = 50
)

// 3. Lista di offerte di lavoro disponibili
val WorkOfferSearchList = mutableStateListOf(
    // --- CAGLIARI (6 SEDI) ---
    WorkOffer(
        id = 9,
        titolo = "Addetto alle vendite",
        ruoloEnum = Role.ADDETTO_VENDITE,
        descrizioneBreve = "Supporto alla clientela e allestimento reparti.",
        descrizioneEstesa = "La risorsa si occuperà dell'accoglienza dei clienti, della gestione dei prodotti a scaffale e del mantenimento dell'ordine nel punto vendita. È prevista la partecipazione alle attività di inventario.",
        requisiti = "Ottime doti comunicative, capacità di lavorare in team, flessibilità negli orari e attitudine al problem solving.",
        supermarket = ListOfSupermarkets[0],
        tipoContratto = TipoContratto.INDETERMINATO,
        orario = OrarioLavoro.FULL_TIME,
        distanzaKm = 1
    ),
    WorkOffer(
        id = 15,
        titolo = "Cassiere/a esperto/a",
        ruoloEnum = Role.CASSIERE,
        descrizioneBreve = "Gestione dei pagamenti e delle procedure di cassa.",
        descrizioneEstesa = "Il ruolo prevede la responsabilità della gestione del denaro, l'apertura e chiusura cassa, e la gestione dei programmi fedeltà aziendali. Si richiede precisione e velocità nelle operazioni.",
        requisiti = "Diploma di scuola superiore, precisione nel calcolo, cordialità e pregressa esperienza nell'uso di software gestionali di cassa.",
        supermarket = ListOfSupermarkets[1],
        tipoContratto = TipoContratto.DETERMINATO,
        orario = OrarioLavoro.PART_TIME,
        distanzaKm = 2
    ),
    WorkOffer(
        id = 16,
        titolo = "Addetto Rifornimento Scaffali",
        ruoloEnum = Role.ADDETTO_SCAFFALI,
        descrizioneBreve = "Gestione scorte e posizionamento merce.",
        descrizioneEstesa = "L'attività principale consiste nel prelievo della merce dal magazzino, nel posizionamento ordinato sugli scaffali e nella rotazione dei prodotti in base alla data di scadenza (FIFO).",
        requisiti = "Resistenza fisica, puntualità, attenzione ai dettagli e capacità di seguire le indicazioni dei responsabili di reparto.",
        supermarket = ListOfSupermarkets[2],
        tipoContratto = TipoContratto.DETERMINATO,
        orario = OrarioLavoro.FULL_TIME,
        distanzaKm = 4
    ),
    WorkOffer(
        id = 17,
        titolo = "Specialista Gastronomia",
        ruoloEnum = Role.ADDETTO_BANCO,
        descrizioneBreve = "Vendita assistita e preparazione di prodotti freschi.",
        descrizioneEstesa = "La figura si occuperà del servizio al banco salumi e formaggi, della preparazione di preparati pronti e della pulizia quotidiana degli strumenti di taglio e delle vetrine espositive.",
        requisiti = "Conoscenza approfondita dei prodotti alimentari, abilità nell'uso dell'affettatrice, possesso dell'attestato HACCP e cortesia verso il pubblico.",
        supermarket = ListOfSupermarkets[3],
        tipoContratto = TipoContratto.INDETERMINATO,
        orario = OrarioLavoro.FULL_TIME,
        distanzaKm = 2
    ),
    WorkOffer(
        id = 31,
        titolo = "Magazziniere di Punto Vendita",
        ruoloEnum = Role.MAGAZZINIERE,
        descrizioneBreve = "Ricezione merci e organizzazione stock.",
        descrizioneEstesa = "Il candidato si occuperà dello scarico dei mezzi, della verifica della merce in entrata rispetto agli ordini e dell'organizzazione del magazzino per ottimizzare i tempi di rifornimento.",
        requisiti = "Capacità organizzative, dimestichezza con terminali portatili per inventario, velocità e attenzione alla sicurezza sul lavoro.",
        supermarket = ListOfSupermarkets[4],
        tipoContratto = TipoContratto.DETERMINATO,
        orario = OrarioLavoro.FULL_TIME,
        distanzaKm = 2
    ),
    WorkOffer(
        id = 32,
        titolo = "Responsabile di Turno",
        ruoloEnum = Role.RESPONSABILE,
        descrizioneBreve = "Coordinamento del personale e apertura/chiusura.",
        descrizioneEstesa = "Figura di responsabilità che assicura il corretto funzionamento del punto vendita durante il proprio turno, gestendo le priorità dei vari reparti e l'assistenza clienti critica.",
        requisiti = "Esperienza pregressa nel retail, doti di leadership, affidabilità e capacità di gestione dei conflitti.",
        supermarket = ListOfSupermarkets[5],
        tipoContratto = TipoContratto.INDETERMINATO,
        orario = OrarioLavoro.FULL_TIME,
        distanzaKm = 3
    ),

    // --- QUARTU SANT'ELENA (3 SEDI) ---
    WorkOffer(
        id = 10,
        titolo = "Addetto Cassa e Informazioni",
        ruoloEnum = Role.CASSIERE,
        descrizioneBreve = "Assistenza al cliente e operazioni di pagamento.",
        descrizioneEstesa = "Oltre alle normali operazioni di cassa, la risorsa fungerà da punto di riferimento per le informazioni sui servizi del supermercato e sulla risoluzione di piccoli reclami.",
        requisiti = "Ottima dialettica, pazienza, orientamento al cliente e capacità di gestione dello stress nei momenti di affluenza.",
        supermarket = ListOfSupermarkets[6],
        tipoContratto = TipoContratto.INDETERMINATO,
        orario = OrarioLavoro.FULL_TIME,
        distanzaKm = 8
    ),
    WorkOffer(
        id = 33,
        titolo = "Addetto Vendite Reparto Ortofrutta",
        ruoloEnum = Role.ADDETTO_VENDITE,
        descrizioneBreve = "Cura del reparto freschi e assistenza.",
        descrizioneEstesa = "La figura garantisce la freschezza e la qualità dei prodotti esposti nel reparto ortofrutta, occupandosi della pesatura e della consulenza ai clienti.",
        requisiti = "Attenzione alla qualità del prodotto, dinamismo, forza fisica per la movimentazione delle cassette e disponibilità al turno mattutino.",
        supermarket = ListOfSupermarkets[7],
        tipoContratto = TipoContratto.DETERMINATO,
        orario = OrarioLavoro.FULL_TIME,
        distanzaKm = 9
    ),
    WorkOffer(
        id = 34,
        titolo = "Specialista Macelleria",
        ruoloEnum = Role.MACELLERIA,
        descrizioneBreve = "Lavorazione carni e vendita assistita.",
        descrizioneEstesa = "Il candidato si occuperà del disosso, del taglio delle carni bovini e suini e della preparazione dei preparati pronto-cuoci.",
        requisiti = "Maneggevolezza nell'uso dei coltelli, pregressa esperienza nel ruolo, conoscenza delle norme igieniche e cortesia al banco.",
        supermarket = ListOfSupermarkets[8],
        tipoContratto = TipoContratto.INDETERMINATO,
        orario = OrarioLavoro.FULL_TIME,
        distanzaKm = 11
    ),

    // --- MONSERRATO (1 SEDE) ---
    WorkOffer(
        id = 20,
        titolo = "Macellaio/a di reparto",
        ruoloEnum = Role.MACELLERIA,
        descrizioneBreve = "Lavorazione carni e allestimento banco macelleria.",
        descrizioneEstesa = "Garantirà il rispetto rigoroso delle norme igienico-sanitarie occupandosi della preparazione dei tagli di carne richiesti dalla clientela.",
        requisiti = "Esperienza specifica nel settore carni, possesso di attestato HACCP, serietà e capacità di gestire gli ordini di reparto.",
        supermarket = ListOfSupermarkets[9],
        tipoContratto = TipoContratto.INDETERMINATO,
        orario = OrarioLavoro.FULL_TIME,
        distanzaKm = 7
    ),

    // --- SESTU (1 SEDE) ---
    WorkOffer(
        id = 35,
        titolo = "Addetto Logistica di Magazzino",
        ruoloEnum = Role.MAGAZZINIERE,
        descrizioneBreve = "Gestione flussi logistici e stoccaggio.",
        descrizioneEstesa = "La risorsa coordinerà lo smistamento dei colli in arrivo verso i vari reparti del punto vendita tramite l'utilizzo di transpallet elettrici.",
        requisiti = "Patentino per il muletto in corso di validità, affidabilità, capacità di leggere le bolle di carico e attitudine al lavoro di precisione.",
        supermarket = ListOfSupermarkets[10],
        tipoContratto = TipoContratto.DETERMINATO,
        orario = OrarioLavoro.FULL_TIME,
        distanzaKm = 12
    ),

    // --- SELARGIUS (1 SEDE) ---
    WorkOffer(
        id = 36,
        titolo = "Addetto Banco Pescheria",
        ruoloEnum = Role.PESCHERIA,
        descrizioneBreve = "Selezione, pulizia e vendita di pescato fresco.",
        descrizioneEstesa = "Responsabile della vendita assistita al banco pesce: si occuperà della pulizia, sfilettatura e preparazione del banco per l'esposizione giornaliera.",
        requisiti = "Conoscenza dei prodotti ittici locali, ottima manualità nella sfilettatura, igiene rigorosa e capacità di consigliare i metodi di cottura.",
        supermarket = ListOfSupermarkets[11],
        tipoContratto = TipoContratto.DETERMINATO,
        orario = OrarioLavoro.PART_TIME,
        distanzaKm = 6
    ),

    // --- ALTRE SARDEGNA E ITALIA---
    WorkOffer(
        id = 23,
        titolo = "Magazziniere Logistico",
        ruoloEnum = Role.MAGAZZINIERE,
        descrizioneBreve = "Movimentazione merci Iglesias.",
        descrizioneEstesa = "Controllo merci in entrata e verifica bolle di trasporto.",
        requisiti = "Conoscenza base Office, puntualità e organizzazione.",
        supermarket = ListOfSupermarkets[12],
        tipoContratto = TipoContratto.DETERMINATO,
        orario = OrarioLavoro.FULL_TIME,
        distanzaKm = 55
    ),
    WorkOffer(
        id = 24,
        titolo = "Store Manager",
        ruoloEnum = Role.RESPONSABILE,
        descrizioneBreve = "Direzione operativa Sanluri.",
        descrizioneEstesa = "Supervisione completa del punto vendita e coordinamento team.",
        requisiti = "Leadership comprovata, esperienza retail pluriennale.",
        supermarket = ListOfSupermarkets[13],
        tipoContratto = TipoContratto.INDETERMINATO,
        orario = OrarioLavoro.FULL_TIME,
        distanzaKm = 45
    ),
    WorkOffer(
        id = 26,
        titolo = "Specialista Banco Pesce",
        ruoloEnum = Role.PESCHERIA,
        descrizioneBreve = "Pulizia e vendita Muravera.",
        descrizioneEstesa = "Gestione del banco pesce fresco ed esposizione.",
        requisiti = "Ottima manualità, conoscenza stagionalità ittica.",
        supermarket = ListOfSupermarkets[14],
        tipoContratto = TipoContratto.DETERMINATO,
        orario = OrarioLavoro.FULL_TIME,
        distanzaKm = 65
    ),
    WorkOffer(
        id = 13,
        titolo = "Addetto Macelleria Senior",
        ruoloEnum = Role.MACELLERIA,
        descrizioneBreve = "Specialista nella lavorazione carni.",
        descrizioneEstesa = "Gestione completa del reparto macelleria, dalla ricezione mezzene al banco servito.",
        requisiti = "Esperienza decennale, autonomia totale, leadership.",
        supermarket = ListOfSupermarkets[15],
        tipoContratto = TipoContratto.INDETERMINATO,
        orario = OrarioLavoro.FULL_TIME,
        distanzaKm = 212
    ),
    WorkOffer(
        id = 2,
        titolo = "Operatore di Cassa",
        ruoloEnum = Role.CASSIERE,
        descrizioneBreve = "Supporto alle operazioni di front-end.",
        descrizioneEstesa = "Gestione transazioni monetarie e informazioni ai clienti. Supporto all'allestimento avancassa.",
        requisiti = "Cordialità, velocità d'esecuzione, precisione nel conteggio.",
        supermarket = ListOfSupermarkets[16],
        tipoContratto = TipoContratto.INDETERMINATO,
        orario = OrarioLavoro.PART_TIME,
        distanzaKm = 410
    ),
    WorkOffer(
        id = 1,
        titolo = "Addetto Reparto Ortofrutta",
        ruoloEnum = Role.ADDETTO_VENDITE,
        descrizioneBreve = "Cura del reparto freschi e assistenza.",
        descrizioneEstesa = "Selezione qualità frutta e verdura, pesatura per i clienti e rifornimento banchi.",
        requisiti = "Dinamismo, forza fisica e attenzione alla freschezza.",
        supermarket = ListOfSupermarkets[17],
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
    val userEmail: String,
    val offerId: Int,
    val nome: String,
    val cognome: String,
    val emailContatto: String,
    val telefono: String,
    val cvPath: String?,
    val videoPath: String?,
    val dataInvio: String = java.time.LocalDate.now().toString(),
    val stato: String = "Inviata"
)

// --- 4. STATI GLOBALI ---
val AllCandidacies = mutableStateListOf<Candidacy>(
    Candidacy(
        id = 1,
        userEmail = "mario.rossi@email.com",
        offerId = 34, // ID offerta esistente
        nome = "Mario",
        cognome = "Rossi",
        emailContatto = "mario.rossi@email.com",
        telefono = "+39 3331234567",
        cvPath = "dummy_cv",
        videoPath = "dummy_video_mario",
        dataInvio = "2026-06-25"
    ),
    Candidacy(
        id = 2,
        userEmail = "luigi.verdi@email.com",
        offerId = 2, 
        nome = "Luigi",
        cognome = "Verdi",
        emailContatto = "luigi.verdi@email.com",
        telefono = "+39 3339876543",
        cvPath = "dummy_cv",
        videoPath = "dummy_video_luigi", // Aggiunto video
        dataInvio = "2026-05-28"
    ),
    Candidacy(
        id = 3,
        userEmail = "chiara.bianchi@email.com",
        offerId = 1, 
        nome = "Chiara",
        cognome = "Bianchi",
        emailContatto = "chiara.bianchi@email.com",
        telefono = "+39 3335555555",
        cvPath = "dummy_cv",
        videoPath = "dummy_video_chiara",
        dataInvio = "2026-06-27"
    )
)
var currentOfferIdApplying by mutableIntStateOf(0)

// Global UI State
var highlightedWorkOfferId by mutableStateOf<Int?>(null)