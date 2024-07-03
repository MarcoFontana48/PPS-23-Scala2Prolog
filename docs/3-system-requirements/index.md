# Requisiti di sistema

## Funzionali
I seguenti requisiti funzionali sono stati identificati per il sistema:
- Il sistema deve avere una interfaccia che permetta l'integrazione tra Scala e Prolog
- Il sistema deve permettere l'integrazione tra i paradigmi di programmazione logica e
  funzionale in modo da permettere all'utente di sfruttare le caratteristiche di entrambi
- Il sistema deve permettere l'uso di codice Prolog come possibile
  implementazione di un metodo Scala tramite annotazioni, che agisca come estensione del linguaggio Prolog in Scala
- L'utente deve poter definire interamente _teorie_ Prolog mediante annotazioni
- L'utente deve poter definire ed eseguire _goal_ Prolog mediante metodi Scala annotati
- Il sistema deve poter sfruttare l'inferenza di tipo di Scala per inferire automaticamente le informazioni Prolog
- Il sistema deve sfruttare classi proxy in modo che l'utente possa invocare metodi Scala come se fossero metodi Prolog

## Non funzionali
I seguenti requisiti non funzionali sono stati identificati per il sistema:
- **Efficienza**: Il sistema deve poter essere in grado di eseguire operazioni in modo efficiente e rapido, minimizzando
  l'uso delle risorse del sistema.
- **Scalabilità**: Il sistema deve essere facilmente estendibile e manutenibile, in modo da permettere l'aggiunta, in 
  maniera semplice, di nuove funzionalità.
- **Affidabilità**: Il sistema deve essere affidabile e robusto, minimizzando la presenza di errori e garantendo la 
  corretta esecuzione delle operazioni richieste.
- **Usabilità**: Il sistema deve essere progettato in modo da essere semplice da usare e da capire.

## Di implementazione
I seguenti requisiti di implementazione sono stati identificati per il sistema:

- Scala 3.x
- tuProlog 3.3
- JDK 11+

[Torna al sommario](../index.md) |
[Capitolo precedente (processo di sviluppo)](../2-development-process/index.md) |
[Capitolo successivo (design)](../4-design/index.md)