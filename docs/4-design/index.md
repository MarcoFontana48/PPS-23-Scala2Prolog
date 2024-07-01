# Design

## Design architetturale
Il sistema è composto di un singolo modulo che contiene tutto il codice necessario.

Di seguito il diagramma delle classi complessivo che sarà discusso nel dettaglio nella prossima sezione:

![UML-class-Entity](..\img\S2P_UML_class_diagram-full diagram.drawio.svg)

## Design di dettaglio
Il sistema è composto da diverse classi Scala che si occupano di estrarre e/o eseguire informazioni prolog associate alle annotazioni definite nel codice sorgente.

### Diagrammi delle classi

![UML-class-annotations](..\img\S2P_UML_class_diagram-Processor(Annotations).drawio.svg)

- **Annotazioni**: classi per definire le annotazioni prolog.
  - **PrologMethod**: annotazione per definire diverse informazioni Prolog per un metodo Scala. Può essere utilizzata in due modi differenti:
    - Indicando esplicitamente le informazioni riguardo al _predicato_ come nome del predicato stesso e notazione per indicare come il motore tuProlog debba considerare le variabili passate come argomento al metodo, _tipi_ delle variabili in ingresso e in uscita, _signature_ per indicare quali variabili sono da considerarsi come ingresso e quali come uscita e _clauses_ per indicare le regole prolog.
    - Indicando solamente le regole prolog, in questo caso il nome del predicato sarà il nome del metodo stesso, le variabili passate come argomento al metodo Scala verranno considerate come argomenti del predicato e il tipo di ritorno come risultato effettuando _type inference_. In questo è possibile di definire informazioni prolog in maniera più concisa rispetto al precedente ottenendo lo stesso risultato.
  - **PrologClass**: annotazione per definire una _teoria_ prolog comune a tutti i metodi della classe annotata. Ogni metodo annotato con PrologMethod può avere delle regole prolog che vengono aggiunte a quelle definite in PrologClass solo per quel metodo. Se un metodo non ha regole prolog, verranno utilizzate solo quelle definite in PrologClass.
  - **PrologAddSharedClauses**: annotazione per aggiungere regole prolog condivise tra tutti i metodi della classe annotata. Queste regole verranno aggiunte a quelle definite in PrologClass come estensione della teoria prolog comune a tutti i metodi della classe.

Per ciascuna annotazione è definita una classe omonima che termina per _'Processor'_ che si occupa di estrarre e/o eseguire le informazioni prolog associate alla specifica annotazione. Ciascuna di esse implementa il _trait_ _'Processor'_ che identifica la classe come processor.

![UML-class-Entity](..\img\S2P_UML_class_diagram-Entity.drawio.svg)

I possibili _method fields_ delle annotazioni sono i 4 citati in precedenza, ciascuno di essi è rappresentato da una classe Scala che contiene codice per estrarre l'informazione contenuta nel campo mediante _companion object_ per generare un'istanza della classe mediante pattern _Factory_, senza l'utilizzo della keyword _new_. Ognuno di essi implementa il _trait_ _'Entity'_ che identifica la classe come _method field_ delle annotazioni.

![UML-class-Interceptor](..\img\S2P_UML_class_diagram-Interceptor.drawio.svg)

La classe _'Scala2Prolog'_ è il punto di accesso per eseguire i metodi prolog annotati, permette di creare un oggetto
proxy a partire da un oggetto originale che contiene annotazioni prolog.

- **Interceptor**: classi per intercettare ed eseguire metodi prolog annotati.
  - **PrologInterceptor**: contiene un _object_ Scala per creare un oggetto proxy a partire dall'oggetto originale. L'oggetto così generato potrà essere utilizzato esattamente come l'oggetto originale, con la differenza che le chiamate ai metodi annotati verranno intercettate per eseguirne l'informazione prolog associata. Metodi dell'oggetto non annotati verranno eseguiti come se fosse l'oggetto originale. Implementa il _trait_ _'Interceptor'_ che aggiunge alla classe la proprietà stessa. Dipende dalla classe **PrologHandler** che è utilizzata per gestire le annotazioni prolog quando sono invocate. 
  - **PrologHandler**: classe che gestisce le annotazioni prolog, quando un metodo annotato viene invocato mediante l'oggetto proxy generato in precedenza, il metodo _'invoke'_ di questa classe (che viene implementato mediante _override_ a partire dall'interfaccia _'InvocationHandler'_ della libreria Java _'reflect'_) viene invocato per rilevare l'annotazione prolog associata al metodo e passare l'informazione alla classe processor corrispondente, che si occuperà di estrarre e/o eseguire l'informazione prolog associata in base al tipo di annotazione. Il trait **Handler** viene utilizzato per identificare la classe come handler.

La classi processor possono appartenere a due diverse categorie (una di esse o entrambe) denominate _'Extractor'_ e _'Executor'_, l'appartenenza alle stesse è data in base alle classi che estendono.
- **Extractor**: classi per estrarre informazioni prolog associate alle annotazioni.
- **Executor**: classi per eseguire informazioni prolog associate alle annotazioni.

![UML-class-Extractor](..\img\S2P_UML_class_diagram-Processor(Extractor).drawio.svg)

<!-- ![UML-class-Extractor-mixin](..\img\S2P_UML_class_diagram-extractor-mixin.drawio.svg) -->

![UML-class-Executor](..\img\S2P_UML_class_diagram-Processor(Executor).drawio.svg)

- **PrologMethodProcessor**: classe che appartiene a entrambe le categorie, si occupa di estrarre le informazioni prolog associate all'annotazione _'PrologMethod'_ e poi di eseguirle. Il corpo dei metodi viene ignorato ed è eseguita al loro posto l'informazione Prolog associata all'annotazione, restituendone la soluzione. Estende la classe **PrologMethodExtractorUtils** che contiene metodi per estrarre informazioni prolog associate alla annotazione corrispondente e _'PrologAnnotationExecutor'_ che permette di eseguire le informazioni prolog estratte al posto dell'esecuzione del corpo del metodo annotato.
  - **PrologMethodExtractorUtils**: classe che contiene metodi di utilità per estrarre informazioni prolog associate alla annotazione _'PrologMethod'_.
  

- **PrologClassProcessor**: classe che appartiene solo alla categoria _'Extractor'_ si occupa di estrarre le informazioni prolog associate all'annotazione _'PrologClass'_ da utilizzare come informazioni Prolog aggiuntive oltre a quelle già presenti nelle annotazioni _'PrologMethod'_ appartenenti ai metodi delle classi annotate con _'PrologClass'_. Estende la classe **PrologClassExtractorUtils** che contiene metodi per estrarre informazioni prolog associate alla annotazione corrispondente.
  - **PrologClassExtractorUtils**: classe che contiene metodi di utilità per estrarre informazioni prolog associate alla annotazione _'PrologClass'_.
  

- **PrologAddSharedClausesProcessor**: classe che appartiene a entrambe le categorie, si occupa di estrarre le informazioni prolog associate alla annotazione _'PrologAddSharedClauses'_, da utilizzare come informazioni Prolog aggiuntive (esattamente come _'PrologClass'_), con la differenza che può annotare solo metodi e le informazioni che contiene sono aggiunte solo una volta che il metodo è stato invocato; a differenza del processor _'PrologMethodProcessor'_ permette anche di eseguire il body del metodo originale, restituendo il suo risultato. Le informazioni Prolog sono aggiunte definitivamente alla classe comune a tutti i metodi annotati con _'PrologMethod'_ e una volta aggiunte non sono più rimosse. Estende la classe **PrologAddSharedClausesExtractorUtils** che contiene metodi per estrarre informazioni prolog associate alla annotazione corrispondente e _'PrologBodyMethodExecutor'_ che permette di eseguire il corpo del metodo annotato e restituire il suo risultato (a differenza di _'PrologAnnotationExecutor'_ che ignora il corpo del metodo e restituisce come risultato la soluzione Prolog trovata a partire dalle informazioni estratte dalle annotazioni).
  - **PrologAddSharedClausesExtractorUtils**: classe che contiene metodi di utilità per estrarre informazioni prolog associate alla annotazione _'PrologAddSharedClauses'_.
  
  \
  ciascuna di queste classi estende e implementa metodi di diverse classi mediante _mixin_ tra esse:


  - **ExtractorUtils**: contiene metodi di utilità generici per estrarre il contenuto dei method fields di generiche annotazioni.
  - **ClausesExtractor**: permette alla classe che la implementa di estrarre _clauses_ prolog (method field) associate alle annotazioni definite in precedenza.
  - **TypesExtractor**: permette alla classe che la implementa di estrarre _types_ prolog (method field) associate alle annotazioni definite in precedenza.
  - **PredicateExtractor**: permette alla classe che la implementa di estrarre _predicate_ prolog (method field) associato alle annotazioni definite in precedenza.
  - **SignatureExtractor**: permette alla classe che la implementa di estrarre _signature_ prolog (method field) associate alle annotazioni definite in precedenza.
    - **EntityExtractor**: ciascuna delle classi che implementano il _mixin_ implementa questa classe che la identifica come estrattore di informazioni prolog.

### Diagrammi delle sequenze

![UML-sequence-ProxyCreation](..\img\S2P_UML_sequence_diagram-proxy-creation.drawio.svg)

Per utilizzare le informazioni definite nelle annotazioni l'utente deve prima definire un oggetto proxy a partire 
dall'oggetto originale, in questo modo l'oggetto proxy generato potrà essere utilizzato esattamente come l'oggetto 
originale, con la differenza che le chiamate ai metodi annotati verranno intercettate per eseguirne l'informazione 
prolog associata.

Nel momento in cui si invoca il metodo '_newProxyInstanceOf'_ dello Scala _object_ _'Scala2Prolog'_ l'oggetto passato 
come argomento è passato al _'PrologInterceptor'_ che si occupa di creare un oggetto proxy a partire dall'oggetto
originale e assegnare un _'PrologHandler'_ per gestire le chiamate ai metodi dell'oggetto proxy generato.
Il nuovo oggetto proxy generato è restituito come risultato e si può utilizzare esattamente come l'oggetto originale.

Se l'oggetto passato come argomento appartiene a una classe che è annotata con _'PrologClass_' allora verranno
anche estratte le informazioni prolog associate all'annotazione e aggiunte alla teoria prolog comune a tutti i metodi 
della classe.

![UML-sequence-Invocation](..\img\S2P_UML_sequence_diagram-proxy-invocation.drawio.svg)

Nel momento in cui si invoca un metodo dell'oggetto proxy generato, il metodo _'invoke'_ del _'PrologHandler'_ viene
invocato per rilevare l'annotazione prolog associata al metodo e passare l'informazione alla classe processor 
corrispondente, che si occuperà di estrarre e/o eseguire l'informazione prolog associata in base al tipo di annotazione.

[Torna al sommario](../index.md) |
[Capitolo precedente (requisiti di sistema)](../3-system-requirements/index.md) |
[Capitolo successivo (implementazione)](../5-implementation/index.md)