# Design

## Design architetturale
Il sistema è composto da un singolo modulo e diversi package che contengono tutto il codice necessario.

![UML-package-architecture](..\img\S2P_UML_package_diagram.drawio.svg)

Il package _'annotation'_ contiene classi e annotazioni per definire informazioni Prolog che verranno estratte ed 
eseguite nelle classi contenute in: 
- _'interceptor'_, che si occupa di intercettare le chiamate ai metodi annotati e fornire le informazioni estratte alle classi contenute nel package _'processor'_
- _'processor'_, che si occupa di estrarre e/o eseguire le informazioni Prolog associate alle annotazioni

## Design di dettaglio
Il sistema è composto da diverse classi Scala che si occupano di estrarre e/o eseguire informazioni prolog associate alle annotazioni definite nel codice sorgente.

### Diagrammi delle classi

![UML-class-annotations](..\img\S2P_UML_class_diagram-Processor(Annotations).drawio.svg)

**Annotazioni**: diverse classi sono state definite per specificare le informazioni Prolog all'interno di annotazioni.
  - **PrologMethod**: annotazione da utilizzare esclusivamente su metodi Scala, definiscono diverse informazioni Prolog. Può essere utilizzata in due modi differenti:
    - Indicando esplicitamente le informazioni riguardo:
      - _**predicate**_: nome del predicato e notazione per indicare come il motore tuProlog debba considerare le variabili passate come argomento al metodo.
      - _**types**_: tipi Scala delle variabili in ingresso e in uscita definite al punto precedente
      - _**signature**_: per indicare quali variabili sono da considerarsi come ingresso e quali come uscita
      - _**clauses**_ per indicare le regole prolog.
    - Oppure indicando solamente le regole prolog, in questo caso il nome del predicato sarà il nome del metodo stesso, le variabili passate come argomento al metodo Scala verranno considerate come argomenti del predicato e il tipo di ritorno come risultato effettuando _type inference_. In questo modo è possibile di definire informazioni prolog in maniera più concisa rispetto al precedente ottenendo lo stesso risultato.
  - **PrologClass**: annotazione da utilizzare esclusivamente su classi Scala, utilizzate per definire una _teoria_ prolog comune a tutti i metodi della classe annotata. Ogni metodo annotato con _PrologMethod_ può definire proprie regole prolog che sono da considerarsi in aggiunta a quelle definite in _PrologClass_, regole così specificate sono valide solo per il metodo in cui sono definite. Se un metodo non ha regole prolog definite nella propria annotazione, verranno considerate solamente quelle definite in _PrologClass_.
  - **PrologAddSharedClauses**: annotazione da utilizzare esclusivamente su metodi Scala, permette di aggiungere, dal momento in cui il metodo viene invocato, regole prolog che sono condivise tra tutti i metodi della classe _PrologClass_ di cui fa parte. Queste regole verranno aggiunte a quelle definite in _PrologClass_ come estensione della teoria prolog comune a tutti i metodi della classe.

Per ciascuna annotazione è stata definita una classe omonima _'Processor'_ che si occupa di estrarre e/o eseguire 
informazioni prolog associate a una specifica annotazione (ciascuna di esse implementa il _trait_ _'Processor'_ che 
identifica la classe come processor).\
Per rendere più chiara e immediata la comprensione dei diagrammi e le relazioni tra le classi, sono stati associati dei
colori unici alle seguenti classi: _'PrologClassProcessor'_, _'PrologMethodProcessor'_, 
_'PrologAddSharedClasusesProcessor'_ e le rispettive classi _'Utils'_, che sono stati usati per tutti i diagrammi delle 
classi a seguire.

![UML-class-Entity](..\img\S2P_UML_class_diagram-Entity.drawio.svg)

I possibili _method fields_ delle annotazioni sono i 4 citati in precedenza (_predicate_, _types_, _signature_, _clauses_), ciascuno di essi è rappresentato da una classe Scala che contiene codice per estrarre l'informazione contenuta nel campo mediante _companion object_ per generare un'istanza della classe mediante _apply method_. Ognuno di essi implementa il _trait_ _'Entity'_ che identifica la classe come _method field_ delle annotazioni, ed è utilizzato dal _companion object_ per verificare che il rispettivo campo sia stato definito.

![UML-class-Interceptor](..\img\S2P_UML_class_diagram-Interceptor.drawio.svg)

La classe _'Scala2Prolog'_ è il punto di accesso per eseguire i metodi prolog annotati, permette di creare un oggetto
_proxy_ a partire da un oggetto originale che contiene annotazioni prolog.

**Interceptor**: classi per intercettare l'invocazione di metodi prolog annotati ed eseguirli.
  - **PrologInterceptor**: contiene un _object_ Scala per generare un oggetto _proxy_ a partire da quello originale. L'oggetto così generato potrà essere utilizzato esattamente come quello originale, con la differenza che le chiamate ai metodi annotati verranno intercettate per eseguirne l'informazione prolog associata. Metodi dell'oggetto non annotati verranno eseguiti come se fosse l'originale. Implementa il _trait_ _'Interceptor'_ che aggiunge alla classe la proprietà stessa. Ha una dipendenza con la classe **PrologHandler** che viene utilizzata per gestire le annotazioni prolog, quando invocate. 
  - **PrologHandler**: classe che gestisce le annotazioni prolog, quando un metodo annotato viene invocato mediante l'oggetto _proxy_ generato in precedenza, il metodo _'invoke'_ di questa classe (che è stato implementato mediante _override_ a partire dall'interfaccia _'InvocationHandler'_ della libreria Java _'reflect'_) viene eseguito per rilevare l'annotazione associata al metodo e passare l'informazione alla classe processor corrispondente, che si occuperà di estrarre e/o eseguire l'informazione contenuta nell'annotazione, in base al suo tipo. Il trait **Handler** è utilizzato per identificare la classe come _handler_.

La classi processor possono appartenere a due diverse categorie (una di esse o entrambe) denominate _'Extractor'_ ed _'Executor'_, l'appartenenza alle stesse è data in base alle classi che estendono.

**Extractor**: classi per estrarre informazioni prolog associate alle annotazioni.\
**Executor**: classi per eseguire informazioni prolog associate alle annotazioni.

![UML-class-Extractor](..\img\S2P_UML_class_diagram-Processor(Extractor).drawio.svg)

<!-- ![UML-class-Extractor-mixin](..\img\S2P_UML_class_diagram-extractor-mixin.drawio.svg) -->

![UML-class-Executor](..\img\S2P_UML_class_diagram-Processor(Executor).drawio.svg)

- **PrologMethodProcessor**: classe che appartiene a entrambe le categorie, si occupa di estrarre le informazioni prolog associate all'annotazione _'PrologMethod'_ e poi di eseguirle.\
  Il corpo dei metodi viene ignorato ed è eseguita al loro posto l'informazione Prolog associata all'annotazione, restituendone la soluzione.\
  Estende la classe **PrologMethodExtractorUtils** che contiene metodi per estrarre informazioni prolog associate alla annotazione corrispondente e **PrologAnnotationExecutor** che permette di eseguire le informazioni prolog al posto del corpo del metodo annotato.
  - **PrologMethodExtractorUtils**: classe che contiene metodi di utilità per estrarre informazioni prolog associate all'annotazione _'PrologMethod'_.
  

- **PrologClassProcessor**: classe che appartiene solo alla categoria _'Extractor'_ si occupa di estrarre le informazioni prolog associate all'annotazione _'PrologClass'_ da utilizzare come informazioni Prolog aggiuntive oltre a quelle già presenti nelle annotazioni _'PrologMethod'_. Estende la classe **PrologClassExtractorUtils** che contiene metodi per estrarre informazioni prolog associate all'annotazione stessa.
  - **PrologClassExtractorUtils**: classe che contiene metodi di utilità per estrarre informazioni prolog associate alla annotazione _'PrologClass'_.
  

- **PrologAddSharedClausesProcessor**: classe che appartiene a entrambe le categorie, si occupa di estrarre le informazioni prolog associate alla annotazione _'PrologAddSharedClauses'_, da utilizzare come informazioni Prolog aggiuntive (esattamente come quelle contenute in _'PrologClass'_), con la differenza che può annotare solo metodi, e le informazioni che contiene sono aggiunte solo una volta che il metodo è stato invocato.\
  A differenza della classe processor _'PrologMethodProcessor'_, questa classe permette anche di eseguire il body del metodo originale, restituendo il suo risultato. Le informazioni Prolog sono aggiunte definitivamente alla classe che accomuna tutti i metodi annotati, quindi una volta aggiunte non possono più essere rimosse. Estende la classe **PrologAddSharedClausesExtractorUtils** che contiene metodi per estrarre informazioni prolog associate alla annotazione corrispondente e _'PrologBodyMethodExecutor'_ che permette di eseguire il corpo del metodo annotato e restituire il suo risultato (a differenza di _'PrologAnnotationExecutor'_ che ignora il corpo del metodo e restituisce come risultato la soluzione Prolog trovata a partire dalle informazioni estratte dalle annotazioni).
  - **PrologAddSharedClausesExtractorUtils**: classe che contiene metodi di utilità per estrarre informazioni prolog associate alla annotazione _'PrologAddSharedClauses'_.
  
  \
  ciascuna di queste classi estende e implementa metodi di diverse classi mediante _mixin_ tra esse:


  - **ExtractorUtils**: contiene metodi di utilità generici per estrarre il contenuto dei method fields di generiche annotazioni.
  - **ClausesExtractor**: permette alla classe che la implementa di estrarre _clauses_ prolog (method field) associate alle annotazioni definite in precedenza.
  - **TypesExtractor**: permette alla classe che la implementa di estrarre _types_ prolog (method field) associate alle annotazioni definite in precedenza.
  - **PredicateExtractor**: permette alla classe che la implementa di estrarre _predicate_ prolog (method field) associato alle annotazioni definite in precedenza.
  - **SignatureExtractor**: permette alla classe che la implementa di estrarre _signature_ prolog (method field) associate alle annotazioni definite in precedenza.
    
    \
    **EntityExtractor**: ciascuna di quelle classi implementa questa garantendogli di identificarla come estrattore di informazioni prolog associate alle annotazioni.

### Diagrammi delle sequenze

![UML-sequence-ProxyCreation](..\img\S2P_UML_sequence_diagram-proxy-creation.drawio.svg)

Per utilizzare le informazioni definite nelle annotazioni l'utente deve prima definire un oggetto _proxy_ a partire 
da quello originale, in questo modo l'oggetto _proxy_ generato potrà essere utilizzato esattamente come l'oggetto 
originale, con la differenza che le chiamate ai metodi annotati verranno intercettate per eseguirne l'informazione 
prolog associata.

Nel momento in cui si invoca il metodo '_newProxyInstanceOf'_ dello Scala _object_ _'Scala2Prolog'_ l'oggetto passato 
come argomento è passato al _'PrologInterceptor'_ che si occupa di creare un oggetto _proxy_ a partire da quello
originale e assegnare un _'PrologHandler'_ per gestire le chiamate ai metodi dell'oggetto _proxy_ generato.\
Il nuovo oggetto _proxy_ così generato è restituito come risultato e si può utilizzare esattamente come se fosse 
quello originale.

Se l'oggetto passato come argomento appartiene a una classe che è annotata con _'PrologClass_' allora verranno
anche estratte le informazioni prolog associate all'annotazione e aggiunte alla teoria prolog comune a tutti i metodi 
annotati della classe.

![UML-sequence-Invocation](..\img\S2P_UML_sequence_diagram-proxy-invocation.drawio.svg)

Nel momento in cui si invoca un metodo dell'oggetto _proxy_, il metodo _'invoke'_ del _'PrologHandler'_ viene
eseguito per rilevare l'annotazione prolog associata al metodo e passare l'informazione alla classe processor 
corrispondente, che si occuperà di estrarre e/o eseguire l'informazione prolog che contiene, in base al suo tipo.

[Torna al sommario](../index.md) |
[Capitolo precedente (requisiti di sistema)](../3-system-requirements/index.md) |
[Capitolo successivo (implementazione)](../5-implementation/index.md)