# Processo di sviluppo

Diversi strumenti sono stati utilizzati per garantire la buona realizzazione del progetto

## Testing e Coverage
Per la scrittura dei test è stata utilizzata la libreria _'scalatest'_, che permette di scrivere test in modo molto
simile al linguaggio umano permettendo a chiunque la comprensione del funzionamento del sistema.\
Per garantire la copertura dei test è stato utilizzato il plugin _'sbt-scoverage'_, che permette di calcolare la percentuale
di righe di codice coperte dai test.

## Quality Assurance
Per il controllo qualità del sistema, è stato utilizzato un formatter per lo stile del codice scala:  '_scalafmt'_, che 
permette di formattare il codice Scala secondo uno stile predefinito.

## Build Automation
Per automatizzare il processo di creazione, compilazione e testing del codice si è scelto di utilizzare _'sbt'_, uno 
strumento per l'automatizzazione di processi scritto appositamente per il linguaggio Scala.

## Continuous Integration
Per garantire la corretta integrazione del codice su più piattaforme si è utilizzato il sistema di _'GitHub Actions'_ 
per eseguire automaticamente i test del progetto sui principali sistemi operativi: _'Linux'_, _'macOS'_ e _'Windows'_.

[Torna al sommario](../index.md) |
[Capitolo precedente (introduzione)](../1-introduction/index.md) |
[Capitolo successivo (requisiti di sistema)](../3-system-requirements/index.md)