package com.example.superspan

data class DraftWork(
    var nome: String = "",
    var cognome: String = "",
    var email: String = "",
    var telefono: String = "",
    var cvFileName: String? = null
)

fun saveDraftWorkForOffer(user: User, offerId: Int, draft: DraftWork) {
    // Salviamo una copia per evitare modifiche involontarie condivise tra schermate.
    user.draftWorksByOfferId[offerId] = draft.copy()
}

fun getDraftWorkForOffer(user: User, offerId: Int): DraftWork? {
    // Restituisce la bozza per una determinata offerta, o null se non esiste.
    return user.draftWorksByOfferId[offerId]
}

fun clearDraftWorkForOffer(user: User, offerId: Int) {
    // Rimuove la bozza per una determinata offerta.
    user.draftWorksByOfferId.remove(offerId)
}


