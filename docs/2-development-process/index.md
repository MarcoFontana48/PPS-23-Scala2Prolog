# Processo di sviluppo

Diversi strumenti e metodologie di sviluppo sono state utilizzate per garantire la buona realizzazione del progetto

## Test Driven Development (TDD)
Per lo sviluppo del codice si è scelto di applicare il più possibile il _Test Driven Development (TDD)_, anticipando
la fase di testing alla scrittura del codice, e implementando il codice di conseguenza.
Questo ha permesso di scrivere rapidamente codice funzionante e testato, minimizzando gli errori durante lo sviluppo.

Il processo _TDD_ è composto da diversi step _(Red-Green-Refactor)_ che sono ripetuti ciclicamente:
1. **Red**: definizione del test che inizialmente fallirà (da cui il nome _'Red'_), per una specifica funzione ancora da implementare
2. **Green**: scrittura del codice che superi (da cui il nome _'Green'_) il test definito al punto precedente
3. **Refactor**: riscrittura del codice con lo scopo di migliorarlo, senza modificarne il risultato

Per la scrittura dei test è stata utilizzata la libreria _'scalatest'_, che permette di scrivere test in modo molto
simile al linguaggio umano permettendo a chiunque la comprensione del funzionamento del sistema.

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