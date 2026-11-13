# v1.0 — GitHub download label

- APK download renamed to `Recurio-v1.0.apk`; internal app version, screens and text unchanged.
- Payment notifications include localized amount, explicit currency and a bundled/cached service image, with a payment initial as fallback. No website request is started when a reminder arrives.

# 0.1.0-alpha09

- Logo approvato integrato nella launcher Android adattiva, nella home, nella guida e nelle informazioni.
- Sesta diapositiva dedicata a feedback GitHub, stato F-Droid e sostegno su Ko-fi, prima della richiesta notifiche.
- Feedback, suggerimenti, sostegno e condivisione/copia del link download nelle impostazioni.
- Repository GitHub reale e modelli per segnalazioni e suggerimenti. F-Droid resta Prossimamente, senza collegamenti fittizi.
- Testi aggiornati in en/it/de/fr; nessun dato personale viene aggiunto alla condivisione dell’app.
- Anteprima con la stessa firma debug di alpha08, non firma di produzione.

# 0.1.0-alpha08

- Password dei backup da un minimo di 5 caratteri, mantenendo compatibilità di lettura con i backup precedenti.
- Messaggi distinti per password di esportazione, password errata in ripristino, file non leggibili ed errori di scrittura durante il ripristino.
- Impostazioni a schermo intero, compatte e divise in schede: generale, dati, icone e privacy, promemoria, aiuto. Testi lunghi spostati nelle finestre di dettaglio e lingua con menu.
- Interruttore delle icone sempre visibile, blu quando disattivato e verde GiuClab #16875C quando attivo; stato esplicito accanto al comando.
- Recupero icone predefinito nelle nuove installazioni, con informativa e controllo prima delle richieste. Le scelte già salvate non vengono sovrascritte.
- Popup dopo il ripristino per scegliere il recupero delle icone, con richieste di rete bloccate fino alla scelta.
- Tutti i testi aggiornati nelle quattro lingue. Logo ancora provvisorio.

# 0.1.0-alpha07

- Aggiunti Spliiit, Sharesub, Diveedi, GoSplit, Sharingful, GamsGo, Coosub, GoingBus e Togetherprice: 145 voci totali.
- Forme singolari e plurali corrette dei giorni di anticipo in tutte le quattro lingue.
- Orario del promemoria e giorni di anticipo in due righe distinte, allineate a destra sotto il prezzo.
- Nome suggerito anche dai link scritti o incollati manualmente; nomi personalizzati conservati durante la modifica del link.
- Per siti sconosciuti: suggerimento con iniziale maiuscola senza i suffissi di dominio comuni. Il nome rimane modificabile; i marchi noti mantengono la grafia del catalogo.
- Nessun cambiamento al formato dei backup o dei pagamenti. Il logo definitivo resta da realizzare.

# 0.1.0-alpha06

- Prima installazione nella lingua primaria del telefono se supportata; altrimenti selettore in inglese tra en/it/de/fr.
- Guida introduttiva di cinque passaggi, saltabile e riapribile dalle impostazioni.
- Spiegazione della privacy e del permesso notifiche al termine della guida; consenso facoltativo.
- Backup manuale cifrato tramite password e ripristino sul nuovo telefono, con anteprima e conferma di sostituzione dei dati.
- File picker Android senza permesso di accesso generale ai file; crittografia e I/O fuori dal thread UI.
- Nessuna funzione di condivisione, account o sincronizzazione.

# 0.1.0-alpha05

- Catalogo di 136 voci derivato dalla lista richiesta, mantenendo i servizi esistenti e i marchi generici.
- Separati i servizi raggruppati, i prodotti Amazon, Apple, Google e le voci Everand/Scribd.
- Riconoscimento per host più specifico e percorso delimitato, con prefissi di lingua comuni. Un link generico non viene attribuito automaticamente a un piano specifico.
- Selettore ricercabile di servizi nel modulo, con categoria e collegamento pubblico; i nomi dei piani rimangono modificabili.
- Icone GitHub e GitHub Copilot integrate da Octicons (MIT), con licenza consultabile nelle impostazioni. Gli altri loghi non sono integrati in assenza di una verifica sufficiente delle condizioni di redistribuzione.
- Nessuna modifica al formato dati dei pagamenti, ai promemoria o alla firma dell’app.

# 0.1.0-alpha04

- Pulsante Modifica esplicito, più piccolo, accanto ad Apri abbonamento su ogni scheda; disponibile anche per pagamenti senza collegamento.
- Menu di ordinamento per prossima scadenza oppure servizio A–Z. La scelta resta salvata sul dispositivo.
- Ordinamento per servizio basato sul catalogo quando il link è riconosciuto, altrimenti sul nome inserito. Più pagamenti Netflix restano distinti e vengono ordinati per scadenza all’interno del servizio.
- Nessuna modifica al formato dei pagamenti o alla programmazione delle notifiche.

# 0.1.0-alpha03

- Riepilogo del promemoria sotto il prezzo nella scheda del pagamento, oppure indicazione che il promemoria è disattivato.
- Scelta della data del promemoria prima dell’alternativa in giorni di anticipo; i due controlli restano sincronizzati. I rinnovi successivi mantengono l’anticipo scelto.
- Menu con 30 valute, codici e nomi localizzati.
- Importi da inserire con il punto (15.99); virgola e notazione scientifica rifiutate con messaggio esplicito. Per JPY e KRW si richiedono importi interi.
- Totali mensili e annuali in righe separate e identificate per valuta, senza conversioni.
- Stesso identificativo, formato dati e certificato della versione precedente.

# 0.1.0-alpha02

- Corretto il rifiuto dei link senza protocollo, come netflix.com/account/membership: ora viene aggiunto https:// prima del salvataggio e dell’apertura nel browser.
- Normalizzazione applicata anche ai link condivisi senza protocollo.
- Errori specifici per nome, importo, valuta, collegamento e anticipo, mostrati in una finestra visibile senza scorrere.
- Il campo dei giorni non blocca il salvataggio quando il promemoria è disattivato.
- Stesso identificativo e stessa firma debug della prima alpha per consentire l’aggiornamento senza disinstallare.
