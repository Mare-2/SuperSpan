
### 🏗️ Architettura e Refactoring
- [ ] fare il refactor del file User.kt in Utilities.kt o qualche altro nome

### 👤 Profilo Utente
- [x] fare la pagina dei dati personali con area di modifica del profilo utente
- [ ] fare pagina del riepilogo delle candidature nel profilo utente
- [ ] fare pagina dati account nel profilo utente con possibile modifica di username(se lo usiamo) e password

### 👨‍💼 Profilo Admin (Gestione)
- [ ] nel profilo admin mettere le sezioni per gestione 
  - [ ] offerte e coupon e pagina relativa quando ci schiacci, 
  - [ ] prodotti (se serve) e pagina relativa quando ci schiacci 
  - [ ] candidature e pagina relativa quando ci schiacci
- [ ] fare pagina dati account nel profilo admin con possibile modifica password

### Profilo Generico
- [x] far funzionare il tasto logout nel profilo
- [ ] aggiustare scorrimento nel profilo

### 📝 Flusso Candidatura (Lavora con noi)
- [x] continuare le pagine per le fasi di candidatura
- [ ] gestire il caricamento del file per il cv
- [ ] controllare e probabilmente aggiustare la funzione di caricamento del cv
- [x] gestire la registrazione del video per la candidatura (per ora è finta)
- [ ] nella fase di candidatura se uno registra un video troppo corto fare in modo che riinizi la registrazione
- [] nell'ultimo step della candidatura fare in modo che se schiacci sul cv ti apra il cv 
- [x] nell'ultimo step della candidatura fare in modo che schiacci sul video ti faccia rivedere il video
- [x] dopo che un utente invia una candidatura riportarlo alla schermata delle offerte di lavoro (non alla home)
- [ ] aggiungere tasto per uscire in quasiasi step e quindi:
  - [ ] siccome si potranno salvare le bozze se uno tenta di uscire mettere un messaggio con opzioni "elimina tutto", "annulla" e "salva in bozza"


### 🔍 Pagina Ricerca Prodotti e pagina Prodotto
- [ ] aggiustare pagina prodotto
- [x] aggiustare scorrimento nella pagina di ricerca dei prodotti in modo che scorra tutto e non solo i prodotti
- [x] rimpicciolire la barra di ricerca e mettere il tasto per i filtri affianco
- [x] mettere la freccia per tornare indietro nella pagina filtri
- [x] fare la grafica dei filtri prodotti simile alla grafica dei filtri di lavoro
- [ ] aggiustare il fatto che dopo che usi il tasto per ordinare per prezzo o per nome i prodotti si mettono nell'ordine che pare a loro anche se disattivati i tasti (di solito in base al prezzo)
- [ ] capire se è necessaria e utile la barra per scorrere massimo e minimo dei prezzi
- [ ] aggiungere la descrizione del prodotto e tutto ciò che vogliamo nella pagina prodotto nel suo oggetto
- [ ] aggiungere nuovi prodotti e nuove categorie
- [ ] la barra di ricerca e il tasto filtri sono giganteschi

### Pagina Coupon e Offerte, pagina Coupon e pagina Offerta
- [ ] aggiustare pagine coupon e offerte
- [x] mettere icone per distinguere visivamente tra coupon e offerte oppure magari fare una selezione in alto per decidere se guardare gli uni o le altre
- [ ] avrebbe senso aggiungere una barra di ricerca(?), da valutare
- [x] cambiare data in coupon e offerte perchè è tutto scaduto
- [ ] rendere più netta la differenza tra roba scaduta, in scadenza e valida che mi sembra non si veda tanto (non sono sicura era tutto scaduto)
- [ ] messaggio di conferma quando viene caricato un coupon o un'offerta
- [ ] capire esattamente cosa sono i coupon e in caso mettere codice a barre


### Pagina ricerca Offerte di lavoro e pagina Offerta di lavoro
- [x] aggiustare scorrimento nella pagina di ricerca delle offette di lavoro in modo che scorra tutto e non solo le offerte
- [x] aggiustare e fare bene i filtri per le offerte di lavoro
- [x] rendere i luoghi dei supermercati e i km più realistici scegliendo città e paesi vicino a cagliari e in varie vie di cagliari
- [ ] la barra di ricerca e il tasto filtri sono giganteschi
- [ ] quando Daniela inserisce un'offerta di lavoro mettere un messaggio di conferma che è stata caricata
- [ ] quando guarda le candidature daniela deve poter filtrare per ruolo sede e data 


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
- [ ] gestire i messaggi di errore per aiutare nella compilazione di dati e form
- [ ] guardare di aver inserito tutte le feature aggiuntive che abbiamo dedotto con gli scenari
- [ ] spararci in faccia (scherzo(forse))