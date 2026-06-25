
### 🏗️ Architettura e Refactoring
- [ ] fare il refactor del file User.kt in Utilities.kt o qualche altro nome

### 👤 Profilo Utente
- [x] fare la pagina dei dati personali con area di modifica del profilo utente
- [x] fare pagina del riepilogo delle candidature nel profilo utente con storico 
- [ ] fare pagina dati account nel profilo utente con possibile modifica di email e password (da controllare se mail cambiata funziona )
- [x] nella pagina candidature far vedere se la candidatura viene scartata o mandata all'hr/ responsabile 


### 👨‍💼 Profilo Admin (Gestione)
- [x] nel profilo admin mettere le sezioni per gestione 
  - [x] offerte e coupon e pagina relativa quando ci schiacci, 
  - [x] candidature e pagina relativa quando ci schiacci
- [ ] fare pagina dati account nel profilo admin con possibile modifica password (da verificare)
- [x] togliere username dalla card mio account nel profilo

### Profilo Generico
- [x] far funzionare il tasto logout nel profilo
- [x] aggiustare scorrimento nel profilo
- [x] togliere freccia in alto a sinistra 

### 📝 Flusso Candidatura (Lavora con noi)
- [x] continuare le pagine per le fasi di candidatura
- [x] gestire il caricamento del file per il cv
- [x] controllare e probabilmente aggiustare la funzione di caricamento del cv
- [x] gestire la registrazione del video per la candidatura (per ora è finta)
- [ ] nella fase di candidatura se uno registra un video troppo corto fare in modo che riinizi la registrazione
- [x] nell'ultimo step della candidatura fare in modo che se schiacci sul cv ti apra il cv 
- [x] nell'ultimo step della candidatura fare in modo che schiacci sul video ti faccia rivedere il video
- [x] dopo che un utente invia una candidatura riportarlo alla schermata delle offerte di lavoro (non alla home)
- [x] aggiungere tasto per uscire in quasiasi step e quindi:
  - [x] siccome si potranno salvare le bozze se uno tenta di uscire mettere un messaggio con opzioni "elimina tutto", "annulla" e "salva in bozza"
- [x] mostrare un feedback visivo chiaro e il nome del file caricato per rassicurare l'utente che il CV è stato acquisito (pop-up)
- [x] aggiungere istruzioni chiare sulla durata del video (circa 30 secondi) e un messaggio di conferma al termine della registrazione
- [x] se il video è già stato registrato andare al riepilogo video se si torna indietro e non alla registrazione, perchè si perde
- [x] salvare anche il video quando si salva nelle bozze


### 🔍 Pagina Ricerca Prodotti e pagina Prodotto
- [x] aggiustare pagina prodotto
- [x] aggiustare scorrimento nella pagina di ricerca dei prodotti in modo che scorra tutto e non solo i prodotti
- [x] rimpicciolire la barra di ricerca e mettere il tasto per i filtri affianco
- [x] mettere la freccia per tornare indietro nella pagina filtri
- [x] fare la grafica dei filtri prodotti simile alla grafica dei filtri di lavoro
- [x] aggiustare il fatto che dopo che usi il tasto per ordinare per prezzo o per nome i prodotti si mettono nell'ordine che pare a loro anche se disattivati i tasti (di solito in base al prezzo)
- [x] capire se è necessaria e utile la barra per scorrere massimo e minimo dei prezzi
- [x] aggiungere la descrizione del prodotto e tutto ciò che vogliamo nella pagina prodotto nel suo oggetto
- [ ] aggiungere nuovi prodotti e nuove categorie
- [ ] la barra di ricerca e il tasto filtri sono giganteschi

### Pagina Coupon e Offerte, pagina Coupon e pagina Offerta
- [x] aggiustare pagine coupon e offerte
- [x] mettere icone per distinguere visivamente tra coupon e offerte oppure magari fare una selezione in alto per decidere se guardare gli uni o le altre
- [ ] avrebbe senso aggiungere una barra di ricerca(?), da valutare
- [x] cambiare data in coupon e offerte perchè è tutto scaduto
- [ ] rendere più netta la differenza tra roba scaduta, in scadenza e valida che mi sembra non si veda tanto (non sono sicura era tutto scaduto)
- [x] messaggio di conferma visibile non solo quando viene caricato, ma anche quando viene eliminato o modificato un coupon o un'offerta
- [x] capire esattamente cosa sono i coupon e implementare la logica: Daniela deve poter definire un coupon selezionando esattamente 3 prodotti per la campagna promozionale
- [ ] vedere se mettere il codice a barre 
- [x] aggiungere tasto di eliminazione coupon senza dover entrare nella modifica (per l'admin)
- [ ] barra di ricerca (e filtri ?) per coupon e offerte


### Pagina ricerca Offerte di lavoro e pagina Offerta di lavoro
- [x] aggiustare scorrimento nella pagina di ricerca delle offette di lavoro in modo che scorra tutto e non solo le offerte
- [x] aggiustare e fare bene i filtri per le offerte di lavoro
- [x] rendere i luoghi dei supermercati e i km più realistici scegliendo città e paesi vicino a cagliari e in varie vie di cagliari
- [ ] la barra di ricerca e il tasto filtri sono giganteschi
- [x] messaggi di conferma visibili non solo quando Daniela inserisce un'offerta di lavoro, ma anche quando la modifica o la elimina
- [x] quando guarda le candidature daniela deve poter filtrare per ruolo sede e data 


### 🎨 Design e UI/UX Generale
- [ ] decidere se mantenere la parabola o usare solo rettangoli stondati
- [ ] aggiustare le parabole nelle pagine in cui si son sminchiate
- [ ] aggiustare l'ui di quasi tutte le pagine
- [ ] aggiustare grafica di tutto, ma adesso meglio concentrarsi sulle funzionalità
- [ ] togliere l'header in tutti i posti che non siano la home o comunque farne una versione più piccola e con scritte variabili
- [ ] decidere una palette e creare il logo (magari due versioni, una orizzontale e una con solo l'immagine)
- [ ] mettere la freccia in alto a sinistra per tornare indietro in tutte le pagine secondarie
- [ ] aggiustare la grafica della navbar
- [ ] ricordare di rendere la navbar sempre visibile

### ⚙️ Logica e Contenuti
- [x] fare partire l'app dalla home
- [ ] far partire dal login
- [ ] gestire i messaggi di errore per aiutare nella compilazione di dati e form
- [ ] guardare di aver inserito tutte le feature aggiuntive che abbiamo dedotto con gli scenari
- [x] controllare che la navbar sia sempre evidenziata la sezione corretta 
- [ ] spararci in faccia (scherzo(forse))
- [ ] mettere freccia indietro in tutte le pagine secondarie 



- [ ] metti popup di errore o di conferma uguali a quelli dell'invio candidature da daniela 
