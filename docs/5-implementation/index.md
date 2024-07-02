# Implementazione

Praticamente ogni meccanismo avanzato di Scala che è stato affrontato durante il corso è stato utilizzato nel progetto.\
In particolare, si è fatto uso di mixins, generics e laziness per rendere il codice più flessibile e performante.\
Di seguito alcuni esempi sull'utilizzo di ciascuno di essi:

## Mixins e Generics

Diversi trait e classi astratte sono stati definiti per permettere l'implementazione di nuove funzionalità in modo
modulare, utilizzando anche i generics per generalizzarne le funzionalità.\
Questi sono stati sfruttati, ad esempio, per fornire proprietà aggiuntive ai vari _'Processors'_, fornendo a ciascuno
di essi capacità aggiuntive per svolgere il proprio compito.

Di seguito un esempio della classe _'PrologMethodProcessor'_ e _'PrologMethodExtractorUtils'_ che mediante mixin
ottengono diverse proprietà utili a estrarre ed eseguire le informazioni Prolog annotate, come descritto in
precedenza:

``` scala
case class PrologMethodProcessor(classClauses: Option[Clauses])
  extends PrologMethodExtractorUtils
  with PrologAnnotationExecutor
  with PrologProcessor:
  /**
   * Executes the @PrologMethod annotation, by extracting its method fields and the annotated method's arguments and
   * parsing them to extract and query its theory.
   *
   * @param args   an optional array containing the arguments of the method annotated with @PrologMethod.
   * @param method a Method that represents the method annotated with @PrologMethod.
   * @return an Iterable containing the result of the goal
   */
  override def executeAnnotation(method: Method, args: Array[AnyRef]): Any =
    // extracts the fields of the @PrologMethod annotation, then set the theory and solve the goal using tuProlog
    val prologMethodAnnotation = method.getAnnotation(classOf[PrologMethod])
    val fields = extractMethodFields(prologMethodAnnotation)
    val rules = generateRules(fields)
    val goal = generateGoal(Option(args), method, fields)
    val solutions = computeAllSolutions(rules, goal)

    // formats the output based on the return type specified in the annotation or inferred from the method
    val typesOption = fields.get("types").flatten.asInstanceOf[Option[Types]]
    val signaturesOption = fields.get("signatures").flatten.asInstanceOf[Option[Signature]]
    (typesOption, signaturesOption) match
      case (Some(types), Some(signatures)) => processTypesAndSignatures(solutions, types, signatures)
      case _ => inferReturnType(solutions, method, args)
```

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

Nella classe mostrata precedentemente, il metodo _'computeAllSolutions'_ utilizza una _'LazyList'_ per rimandare
l'esecuzione della soluzione del goal Prolog solo quando richiesta (cioè solo quando si prova ad accedervi).

Di seguito la porzione del metodo _'computeAllSolutions'_ della classe _'PrologMethodProcessor'_ citata, che calcola
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

[Torna al sommario](../index.md) |
[Capitolo precedente (design)](../4-design/index.md) |
[Capitolo successivo (esempio di utilizzo)](../6-example/index.md)