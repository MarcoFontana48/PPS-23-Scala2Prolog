# Implementazione

Praticamente ogni meccanismo avanzato di Scala che è stato affrontato durante il corso è stato utilizzato nel progetto.\
Di seguito alcuni esempi sull'utilizzo di ciascuno di essi:

## Mixins e Generics

Diversi trait e classi astratte sono stati definiti per permettere l'implementazione di nuove funzionalità in modo
modulare, utilizzando anche i _generics_ per generalizzarli.


Di seguito un esempio della classe _'PrologMethodExtractorUtils'_ che mediante mixin
ottiene diverse proprietà utili a estrarre ed eseguire le informazioni Prolog annotate, come descritto in
precedenza:

``` scala
/**
 * Utility object to extract and parse the fields of @PrologMethod annotations.
 */
abstract class PrologMethodExtractorUtils
  extends ExtractorUtils[PrologMethod, PrologAnnotationFields]
    with SignatureExtractor[PrologMethod]
    with PredicateExtractor[PrologMethod]
    with ClausesExtractor[PrologMethod]
    with TypesExtractor[PrologMethod]:
  /**
   * Method to extract and parse the fields of @PrologMethod annotations.
   *
   * @param prologMethod a @PrologMethod annotation.
   * @return a Map that contains the extracted and parsed method fields of the @PrologMethod annotation
   */
  override def extractMethodFields(prologMethod: PrologMethod): PrologAnnotationFields =
    Map(
      "signatures" -> extractSignature(prologMethod),
      "predicate" -> extractPredicate(prologMethod),
      "clauses" -> extractClauses(prologMethod),
      "types" -> extractTypes(prologMethod)
    )

  /**
   * Method to extract and parse the 'predicate' method field of @PrologMethod annotations.
   *
   * @param prologMethod a @PrologMethod annotation.
   * @return an Option of 'Predicate' that contains informations about the predicate method field
   */
  override def extractPredicate(prologMethod: PrologMethod): Option[Predicate] =
    Predicate(prologMethod.predicate())

  /**
   * Method to extract and parse the 'clauses' method field of @PrologMethod annotations.
   *
   * @param prologMethod a @PrologMethod annotation.
   * @return an Option of 'Clauses' that contains informations about the clauses method field
   */
  override def extractClauses(prologMethod: PrologMethod): Option[Clauses] =
    Clauses(prologMethod.clauses())

  /**
   * Method to extract and parse the 'types' method field of @PrologMethod annotations.
   *
   * @param prologMethod a @PrologMethod annotation.
   * @return an Option of 'Types' that contains informations about the types method field
   */
  override def extractTypes(prologMethod: PrologMethod): Option[Types] =
    Types(prologMethod.types())

  /**
   * Method to extract and parse the 'signature' method field of @PrologMethod annotations.
   *
   * @param prologMethod a @PrologMethod annotation.
   * @return an Option of 'Signature' that contains informations about the signature method field
   */
  override def extractSignature(prologMethod: PrologMethod): Option[Signature] =
    Signature(prologMethod.signature())
```

## Laziness

L'utilizzo della laziness è stato sfruttato per rimandare l'esecuzione delle operazioni più pesanti di soluzione dei 
goal Prolog a quando effettivamente necessario.

Nella classe _'PrologMethodProcessor'_, il metodo _'computeAllSolutions'_ utilizza una _'LazyList'_ per rimandare
l'esecuzione della soluzione del goal Prolog solo quando richiesta (cioè solo quando si prova ad accedervi).

Di seguito la porzione del metodo _'computeAllSolutions'_ che calcola
le soluzioni del goal Prolog solo fino a quando sono _'successes'_:

``` scala
// Compute the goal solutions
// - get the first solution
logger.debug(s"Solving goal: $goal")
val firstSolveInfo = engine.solve(goal)

// - define a high-order function to initialize the LazyList to compute elements only when needed. Uses 'Try' since
//   tuProlog engine may throw an exception 'NoSolutionException' while evaluating the goal
val initializeLazyListFn: (Try[SolveInfo] => Try[SolveInfo]) => LazyList[Try[SolveInfo]] = LazyList.iterate(Try(firstSolveInfo))

// - define the anonymous partial pattern matching function to compute the next solution
val computeNextSolutionFn: Try[SolveInfo] => Try[SolveInfo] =
  case Success(solveInfo) if solveInfo.hasOpenAlternatives => Try(engine.solveNext())
  case _ => Failure(new NoSuchElementException)

// - define the anonymous partial pattern matching function to extract the solution
val getNextSolutionsFn: ((Try[SolveInfo], Int)) => Option[SolveInfo] =
  case (Success(solveInfo), index) if solveInfo.isSuccess =>
    logger.trace(s"Solution ${index + 1} found:\n$solveInfo")
    Some(solveInfo)
  case _ => None

//return the solutions by assembling previously declared functions and collecting them in a collection
//since it's using a lazy list, this computation is suspended and done only when needed
for
  //iterate over each Try[SolveInfo] and its index in the LazyList. Take the results as long as they are successful
  (solveInfoTry, index) <- initializeLazyListFn(computeNextSolutionFn).takeWhile(_.isSuccess).zipWithIndex
  //convert the Try[SolveInfo] to an Option[SolveInfo] to check if it is a success and yield the SolveInfo
  solveInfo <- solveInfoTry.toOption if solveInfo.isSuccess
yield
  //yield the successful SolveInfo, collecting it into a collection
  logger.trace(s"\nSolution ${index + 1} found:\n$solveInfo")
  solveInfo
```

## Pattern Proxy

La classe _'PrologInterceptor'_ è stata definita per permettere l'invocazione di metodi Scala come se fossero metodi 
Prolog mediante l'utilizzo di pattern 'proxy', per generare proxy di oggetti di classi specifiche:

``` scala
/**
 * Module that contains methods to intercept call of methods annotated with @PrologMethod an execute their logic
 */
object PrologInterceptor extends Interceptor:
  /**
   * Creates a Proxy instance of the original object passed as argument that is returned to the caller.
   * Each time the proxy instance is invoked, it will intercept the method call and execute the annotated @PrologMethod
   * logic using the tuProlog engine, returning its result as the method result.
   * The body of the original method is ignored and the Prolog logic is executed instead.
   *
   * @param originalObject the original object to create a new PrologMethodHandler for
   * @tparam A the type of the original object
   * @return a new Proxy instance of the object passed as argument to this method
   */
  override def create[A](originalObject: A): A =
    logger.trace(s"creating a new handler for the original object '$originalObject'...")
    val handler = PrologHandler(originalObject)

    logger.trace("creating a new Proxy instance...")
    Proxy.newProxyInstance(originalObject.getClass.getClassLoader, originalObject.getClass.getInterfaces, handler).asInstanceOf[A]
```

## Extension
La funzionalità _'extension'_ di Scala è stata sfruttata per permettere all'utente di definire un proxy di qualunque
oggetto in maniera più semplice:

``` scala
/**
 * This object is used to create a new S2P proxy instance of the original object passed as argument to handle the
 * annotated Prolog logic execution.
 */
object Scala2Prolog extends Logging:
  /**
   * Method to create a new proxy instance of the original object.
   * The proxy instance will handle the annotated Prolog logic.
   *
   * @return a new proxy instance of the original object.
   */
  extension [A](originalObject: A) def asPrologProxy: A =
    PrologInterceptor.create(originalObject)
```
``` scala
@main
def main(): Unit =
    val pieces = NPiecesImpl().asInstanceOf[NPieces]
    val pieces_proxy = pieces.asPrologProxy    //extension method to create a new proxy instance of the original object from the object itself
    
    val n_queens_solution = pieces_proxy.n_queens(6, "Positions")
    val n_rooks_solution = pieces_proxy.n_rooks(2, "Positions")
```

## Case class e Companion Object
Le case class sono state utilizzate per definire i modelli di dati che rappresentano le informazioni estratte dalle
annotazioni Prolog: _'Signature'_, _'Predicate'_, _'Clauses'_, _'Types'_.
I companion object sono stati utilizzati per definire i metodi di parsing e di creazione delle istanze di queste case
class mediante il metodo _apply_.

Di seguito un esempio della case class _'Signature'_ e del suo companion object:

``` scala
/**
 * Case class that represents the 'signature' method field of the @PrologMethod annotation.
 *
 * @param inputVars  an array that contains the input variables of the signature.
 * @param outputVars an array that contains the output variables of the signature.
 */
case class Signature(inputVars: Array[String], outputVars: Array[String]) extends Entity

/**
 * Object companion that contains the methods to extract and parse the 'signature' method field of the @PrologMethod annotation.
 */
object Signature extends Entity:
  def apply(param: String): Option[Signature] =
    isEmpty(param, {
      logger.trace("signature is empty, returning None signature...")
      None
    }, {
      logger.trace(s"extracted signature from @Prolog* annotation: '$param', extracting input and output variables...")

      /* pattern: (X1, *) -> {Y1, *} */
      val pattern = "\\(([A-Z]\\w*(,\\s*[A-Z]\\w*)*)*\\)\\s*->\\s*\\{([A-Z]\\w*(,\\s*[A-Z]\\w*)*)*}".r
      val matches = pattern.findAllMatchIn(param).toList

      matches.headOption.flatMap { m =>
        val inputVars = Option(m.group(1)).map(_.split(",").map(_.trim)).getOrElse(Array.empty[String])
        val outputVars = Option(m.group(3)).map(_.split(",").map(_.trim)).getOrElse(Array.empty[String])
        logger.trace(s"extracted input and output variables from signature: 'input=${inputVars.mkString("Array(", ", ", ")")}', 'output=${outputVars.mkString("Array(", ", ", ")")}'")
        Some(new Signature(inputVars, outputVars))
      }.orElse(throw new IllegalArgumentException(s"Invalid signature format: '$param'. Signature must be formatted as '(X1,X2,..Xn) -> {Y1,Y2,..Yn}'"))
    })
```

## Tail Recursion
La ricorsione tail è stata utilizzata per ottimizzare le prestazioni di ogni metodo che richiede una ricorsione, come
riportato in questo esempio del metodo _'formatElement'_ della classe _'PrologMethodProcessor'_ che formatta un elemento
in un formato Prolog valido.\
La ricorsione tail è un tipo di ricorsione dove la chiamata ricorsiva è l'ultima operazione della funzione ricorsiva,
questo permette alla funzione di eseguire tutte le computazioni necessarie prima di effettuare la ricorsione.\
Il compilatore può utilizzare in questo modo una ottimizzazione per rendere questa più efficiente.\
(Alcune variabili hanno tipo 'Any' perchè la libreria java _'reflection'_ permette di
accedere ad argomenti dei metodi solo come Java 'Object' per via della _type erasure_, si è cercato di utilizzarne il
meno possibile preferendo tipi generici e specifici quando possibile, i valori di ritorno dei metodi annotati sono 
sempre specifici con le informazioni fornite dalla annotazione, e quelli passati al metodo annotato sono anche loro 
sempre specifici):

``` scala
      /**
       * Formats the given element into a valid Prolog format.
       * If the element is a List, it recursively formats each element of the List, including nested Lists,
       * and represents them as Prolog lists (i.e., enclosed in square brackets and separated by commas).
       * Non-List elements are converted to their string representation.
       *
       * @param element The element to format, which can be of any type.
       * @return A string representation of the element in Prolog format.
       */
      def formatElement(element: Any): String =
        @tailrec
        def formatListHelper(elements: List[Any], acc: String): String = elements match {
          case Nil => acc
          case head :: tail => formatListHelper(tail, acc + (if (acc.isEmpty) "" else ",") + formatElement(head))
        }
        element match 
          case list: List[_] => "[" + formatListHelper(list, "") + "]"
          case other => other.toString
```

## For yield
Il costrutto _'for yield'_ è stato utilizzato per eseguire operazioni su collezioni, applicando una trasformazione a
ciascun elemento e restituendo una nuova collezione con i risultati trasformati.

Un esempio di utilizzo riguarda lo stesso mostrato precedentemente riguardo alla _'lazyness'_, dove le soluzioni dei
goal Prolog vengono calcolate e raccolte in una collezione:

``` scala
//return the solutions by assembling previously declared functions and collecting them in a collection
//since it's using a lazy list, this computation is suspended and done only when needed
for
    //iterate over each Try[SolveInfo] and its index in the LazyList. Take the results as long as they are successful
    (solveInfoTry, index) <- initializeLazyListFn(computeNextSolutionFn).takeWhile(_.isSuccess).zipWithIndex
    //convert the Try[SolveInfo] to an Option[SolveInfo] to check if it is a success and yield the SolveInfo
    solveInfo <- solveInfoTry.toOption if solveInfo.isSuccess
yield
    //yield the successful SolveInfo, collecting it into a collection
    logger.trace(s"\nSolution ${index + 1} found:\n$solveInfo")
    solveInfo
```

## Type Alias
Alcuni type alias sono stati utilizzati per definire tipi di dati complessi in modo più leggibile, utilizzando lo
stesso tipo su cui il tipo alias si appoggia.

Di seguito un esempio di uno di essi:

``` scala
/**
 * Type alias for a Map that contains the extracted and parsed method fields of the Prolog annotations.
 */
type PrologAnnotationFields = Map[String, Option[Entity]]
```

## Curry
Il metodo _'computeAllSolutions'_ della classe _'PrologMethodProcessor'_ contiene un esempio di curry, ovvero la
trasformazione di una funzione che accetta più argomenti in una sequenza di funzioni, in modo da poterla chiamare
passando una parte degli argomenti alla volta:

``` scala
    val computeWithRulesCurry = computeAllSolutions(rules)    // curried function to initially set the rules and then compute the solutions with the goal in a different step
    val goal = generateGoal(Option(args), method, fields)
    val solutions = computeWithRulesCurry(goal)               // compute the solutions with the goal
```

## Immutability
L'immutabilità è stata sfruttata in ogni classe, sia per collezioni che per campi e variabili, per garantire che i dati
non possano essere modificati una volta creati, ed evitare effetti collaterali dovuti alla mutabilità degli stessi.

[Torna al sommario](../index.md) |
[Capitolo precedente (design)](../4-design/index.md) |
[Capitolo successivo (testing)](../6-testing/index.md)