package com.example.superspan

data class CandidacyDraft(
    var nome: String = "",
    var cognome: String = "",
    var emailLavoro: String = "",
    var telefono: String = "",
    var cvFileName: String = "", // Qui puoi tenere il nome semplice per la UI
    var cvPath: String = "",     // Qui il percorso reale per il salvataggio
    var videoPath: String? = null,   // Il percorso del video appena registrato
    var lastStepRoute: String = "apply_step_1" // L'ultima schermata visitata
)

/*fun saveCandidacyDraftForOffer(user: User, offerId: Int, draft: CandidacyDraft) {
    // Salviamo una copia per evitare modifiche involontarie condivise tra schermate.
    clearCandidacyDraftForOffer(user, offerId)
    user.candidacyDraftsByOfferId[offerId] = draft.copy()
}

fun getCandidacyDraftForOffer(user: User, offerId: Int): CandidacyDraft? {
    // Restituisce la bozza per una determinata offerta, o null se non esiste.
    return user.candidacyDraftsByOfferId[offerId]
}*/

fun saveCandidacyDraftForOffer(user: User, offerId: Int, draft: CandidacyDraft) {
    user.candidacyDraftsByOfferId[offerId] = draft.copy()
}

fun getCandidacyDraftForOffer(user: User, offerId: Int): CandidacyDraft? {
    return user.candidacyDraftsByOfferId[offerId]
}

fun clearCandidacyDraftForOffer(user: User, offerId: Int) {
    // Rimuove la bozza per una determinata offerta.
    user.candidacyDraftsByOfferId.remove(offerId)
}


