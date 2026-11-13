# Recurio — by GiuClab
Tutti i pagamenti, in un posto. Anteprima Android 8+, gratuita e open source, GPL-3.0-or-later.

## Disponibile nella prima alpha
Inserimento, modifica, sospensione ed eliminazione dei pagamenti; frequenze settimanali, mensili, trimestrali, annuali e una tantum. Totale del mese e previsione dei prossimi 12 mesi, separati per valuta. Ricezione dei link condivisi dal browser, catalogo di 145 voci di catalogo e apertura del link nel browser esterno.

Promemoria facoltativi con importo, valuta e logo del servizio già disponibile sul telefono, oppure iniziale del pagamento. Anticipo da 0 a 365 giorni e orario scelto. La ricezione di un promemoria non avvia richieste ai siti. Android può ritardarli: non viene richiesta l’autorizzazione per allarmi esatti. Consenti le notifiche e verifica il funzionamento sul telefono. L’arresto forzato blocca gli avvisi finché non riapri l’app.

## Primo avvio e trasferimento dati
Se il telefono usa italiano, inglese, tedesco o francese, Recurio parte direttamente in quella lingua. Per le altre lingue appare la scelta iniziale in inglese. Seguono sei passaggi di guida e la spiegazione facoltativa del permesso notifiche. Gli utenti delle alpha precedenti mantengono la scelta già salvata. La guida è sempre nelle impostazioni.

In Impostazioni → Backup e ripristino puoi esportare i pagamenti e i loro promemoria in un file .recurio cifrato, con password di almeno 5 caratteri. Sul nuovo telefono scegli il file e inserisci la password. Il ripristino mostra il numero di pagamenti e richiede conferma perché sostituisce tutti i pagamenti presenti, senza unirli. Password non recuperabile; conserva file e password separatamente. Non sono incluse icone, preferenze dell’app o autorizzazioni Android. Nessuna condivisione o sincronizzazione.

## Privacy e icone
Nessun account, pubblicità, tracciamento, collegamento bancario o server Recurio. Dati nello spazio privato dell’app, senza cifratura aggiuntiva del database. Backup automatico Android disabilitato.

Il recupero delle icone è facoltativo e attivo per impostazione predefinita, con avviso iniziale e possibilità di disattivarlo prima delle richieste. Contatta il sito pubblico tramite HTTPS, senza cookie del browser o invio di importi, scadenze o percorso del link personale. Il sito vede l’indirizzo IP. Le immagini vengono conservate nella cache locale. Icone mancanti, incompatibili o ospitate su un altro dominio vengono sostituite dall’iniziale. Sono integrate le icone GitHub e GitHub Copilot da Octicons (MIT); per gli altri servizi resta il recupero facoltativo delle favicon.

## Limiti e distribuzione
Le anteprime sono su [GitHub](https://github.com/GiuClab/Recurio/releases); nessuna pubblicazione F-Droid è ancora disponibile. L’APK debug è una prova, non una versione stabile. Nessun test su dispositivo fisico è stato ancora eseguito. Sono disponibili backup cifrati e ripristino manuale. Mancano categorie dei pagamenti, intervalli ricorrenti personalizzati, sincronizzazione e lettura automatica delle scadenze personali. Disinstallando o cancellando i dati perdi i pagamenti. Logo Recurio approvato e integrato.

## Compilazione e contributi
OpenJDK 17, SDK Android 35 e Build Tools 35.0.0; esegui `./gradlew testDebugUnitTest lintDebug assembleDebug`. Il workflow incluso genera un artefatto debug, senza pubblicare release. Contribuisci con modifiche verificabili, traduzioni e risorse ridistribuibili. I nomi e le icone dei servizi appartengono ai rispettivi titolari.

[Documentazione tecnica e privacy completa](../README.md) · [Licenza](../LICENSE) · [Sostieni il progetto](https://ko-fi.com/giuclab/tip)

Made with ❤️ by GiuClab

[GitHub](https://github.com/GiuClab/Recurio) · [Feedback / suggerimenti](https://github.com/GiuClab/Recurio/issues/new/choose)
