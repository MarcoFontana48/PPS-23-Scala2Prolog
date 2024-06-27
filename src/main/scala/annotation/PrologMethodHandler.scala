package pps.exam.application
package annotation

import alice.tuprolog.{Prolog, SolveInfo, Term, Theory}
import org.apache.logging.log4j.scala.Logging

import java.lang.reflect.Method
import scala.annotation.tailrec
import scala.util.{Failure, Success, Try}

type PrologMethodFields = Map[String, Option[PrologMethodEntity]]

/**
 * handle the methods annotated with @PrologMethod.
 */
object PrologMethodHandler:
  def apply(engine: Prolog): PrologMethodHandler = new PrologMethodHandler(engine)

/**
 * Utility object to extract and parse the fields of @PrologMethod annotations.
 */
abstract class PrologMethodUtils extends PrologAnnotationUtils[PrologMethod, PrologMethodFields] with Logging:
  /**
   * Method to extract and parse the fields of @PrologMethod annotations.
   *
   * @param prologMethod a @PrologMethod annotation.
   * @return a Map that contains the extracted and parsed method fields of the @PrologMethod annotation
   */
  override def extractMethodFields(prologMethod: PrologMethod): PrologMethodFields =
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
  def extractPredicate(prologMethod: PrologMethod): Option[Predicate] =
    Predicate(prologMethod.predicate())

  /**
   * Method to extract and parse the 'clauses' method field of @PrologMethod annotations.
   *
   * @param prologMethod a @PrologMethod annotation.
   * @return an Option of 'Clauses' that contains informations about the clauses method field
   */
  def extractClauses(prologMethod: PrologMethod): Option[Clauses] =
    Clauses(prologMethod.clauses())

  /**
   * Method to extract and parse the 'types' method field of @PrologMethod annotations.
   *
   * @param prologMethod a @PrologMethod annotation.
   * @return an Option of 'Types' that contains informations about the types method field
   */
  def extractTypes(prologMethod: PrologMethod): Option[Types] =
    Types(prologMethod.types())

  /**
   * Method to extract and parse the 'signature' method field of @PrologMethod annotations.
   *
   * @param prologMethod a @PrologMethod annotation.
   * @return an Option of 'Signature' that contains informations about the signature method field
   */
  def extractSignature(prologMethod: PrologMethod): Option[Signature] =
    Signature(prologMethod.signature())

class PrologMethodHandler(engine: Prolog) extends PrologMethodUtils with PrologAnnotationHandler with Logging:
  /**
   * Executes the @PrologMethod annotation, by extracting its method fields and the annotated method's arguments and 
   * parsing them to extract and query its theory.
   *
   * @param args   an optional array containing the arguments of the method annotated with @PrologMethod.
   * @param method a Method that represents the method annotated with @PrologMethod.
   * @return an Iterable containing the result of the goal
   */
  override def executeAnnotation(method: Method, args: Array[AnyRef]): AnyRef =
    // extracts the fields of the @PrologMethod annotation, then set the theory and solve the goal using tuProlog
    val prologMethodAnnotation = method.getAnnotation(classOf[PrologMethod])
    val fields = extractMethodFields(prologMethodAnnotation)
    val rules = generateRules(fields)
    val goal = generateGoal(fields, Option(args), method)
    val solutions = computeAllSolutions(rules, goal)
    formatOutput(fields, solutions)
  
  /**
   * Generates rules from the extracted fields of the @PrologMethod annotation.
   *
   * @param fields a map containing the extracted values of method fields of the @PrologMethod annotation.
   * @return a string containing the clauses to set as theory.
   */
  private def generateRules(fields: PrologMethodFields): String =
    fields.get("clauses").flatten match
      case Some(clauses: Clauses) => clauses.value.mkString(" ")
      case _ => throw new Exception("Failed to extract clauses or clauses are not of type Clauses")
  
  /**
   * Generates a goal from the extracted fields of the @PrologMethod annotation.
   *
   * @param fields a map containing the extracted values of method fields of the @PrologMethod annotation.
   * @param args   an optional array containing the arguments of the method annotated with @PrologMethod.
   * @param method a Method that represents the method annotated with @PrologMethod.
   * @return a Term containing the goal to solve.
   */
  private def generateGoal(fields: PrologMethodFields, args: Option[Array[AnyRef]], method: Method): Term =
    /**
     * Method to replace the input variables of the predicate with the input values of the method annotated
     * with @PrologMethod.
     *
     * @param variable a string that represents the input variable of the predicate.
     * @param value    an object that represents the input value of the method annotated with @PrologMethod.
     * @return a string that represents the predicate with the input variable replaced by the input value.
     */
    def replaceVariableNotationPatternWithValue(variable: String)(value: AnyRef): String = value match
      case valueIterable: Iterable[_] =>
        val valueStr = valueIterable.mkString("[", ",", "]")
        logger.trace(s"replacing variable '$variable' with iterable value: '$valueStr'")
        valueStr
      case _ =>
        logger.trace(s"replacing variable '$variable' with non-iterable value: '$value'")
        value.toString
  
    /**
     * Method to get the pattern of the predicate variable notation.
     * The predicate notation is represented as concatenation of operator and term, since the input variable has
     * tuProlog type Term: '-|+'(variable)
     *
     * @param variable a string that represents the input variable of the predicate.
     * @return a string that represents the pattern of the predicate variable notation.
     */
    def getPredicateVariableNotationPattern(variable: String) = s"'[-|+]'\\($variable\\)"
  
    /**
     * Method to get the pattern of the predicate variable standard notation.
     * The predicate standard notation is represented as a variable that starts with an uppercase letter.
     *
     * @return a string that represents the pattern of the predicate variable standard notation.
     */
    def getPredicateVariableStandardPattern = s"[A-Z]\\w*"
  
    /**
     * Method to replace the input variables of the predicate with the input values of the method annotated
     * with @PrologMethod.
     *
     * @param vars   a list that contains the input variables of the predicate.
     * @param values a sequence that contains the input values of the method annotated with @PrologMethod.
     * @param acc    a string that represents the accumulator that will gradually accumulate the predicate with the
     *               input variables
     * @return a string that represents the predicate with the input variables replaced by the input values.
     */
    @tailrec
    def replaceTermsHelper(vars: List[String])(values: List[AnyRef])(acc: String): String =
      logger.trace(s"current accumulator: '$acc'")
      (vars, values) match
        case (varHead :: varTail, valueHead :: valueTail) => replaceTermsHelper(varTail)(valueTail)(acc.replaceFirst(getPredicateVariableNotationPattern(varHead), replaceVariableNotationPatternWithValue(varHead)(valueHead)))
        case (varHead :: varTail, Nil) => replaceTermsHelper(varTail)(values)(acc.replaceFirst(getPredicateVariableNotationPattern(varHead), varHead))
        case (Nil, valueHead :: valueTail) => replaceTermsHelper(vars)(valueTail)(acc.replaceFirst(getPredicateVariableStandardPattern, replaceVariableNotationPatternWithValue("Nil")(valueHead)))
        case (Nil, Nil) => acc
  
    /**
     * Extracts the goal from the predicate field of the @PrologMethod annotation.
     *
     * @param value    a PrologMethodEntity that represents the predicate field of the @PrologMethod annotation.
     * @param argsList a list that contains the arguments of the method annotated with @PrologMethod.
     * @return a Term that represents the goal to solve.
     */
    def extractGoal(value: PrologMethodEntity, argsList: List[AnyRef]) =
      // convert term to string
      val termStr = value.asInstanceOf[Predicate].term.toString
      logger.trace(s"predicate term as string: '$termStr'")
  
      // extract variables from term. The predicate notation is represented as concatenation of operator and term, since
      // the input variable has tuProlog type Term: i.e.: '+'(V) )
      val variablePattern = "'[-|+]?'\\(([A-Z]\\w*)\\)".r
      val variables = variablePattern.findAllMatchIn(termStr).toList.map(_.group(1))
      logger.trace(s"extracted variables from term: '${variables.mkString("List(", ", ", ")")}'")
  
      // replace variables with values
      val replacedTermStr = replaceTermsHelper(variables)(argsList)(termStr)
      logger.trace(s"replaced term: '$replacedTermStr'")
      Term.createTerm(replacedTermStr)
  
    /**
     * Guesses the goal from the arguments of the method and the method name annotated with @PrologMethod.
     *
     * @param argsList a list that contains the arguments of the method annotated with @PrologMethod.
     * @param method   a Method that represents the method annotated with @PrologMethod.
     * @return a Term that represents the guessed goal to solve.
     */
    def guessGoal(argsList: List[AnyRef], method: Method): Term =
      // checks if argsList has 'List(...)' elements and formats them as '[...]' instead, otherwise leaves them as they are
      val formattedArgs = argsList.map {
        case list: List[_] => list.mkString("[", ",", "]") // format List elements
        case other => other.toString
      }
      // builds the goal as 'methodName(methodArg1,...,methodArgN)'
      val argsListStr = method.getName + formattedArgs.mkString("(", ",", ")")
      logger.trace(s"guessing goal: '$argsListStr'")
      Term.createTerm(argsListStr)
  
    // method body
    val argsList = args.getOrElse(Array.empty[AnyRef]).toList
    fields("predicate") match
      case Some(value) =>
        logger.trace("extracting goal from predicate field of @PrologMethod annotation...")
        extractGoal(value, argsList)
      case None =>
        logger.trace("predicate field is empty, guessing goal from method arguments and name...")
        guessGoal(argsList, method)
  
  /**
   * Computes all solutions of a goal in Prolog.
   *
   * @param rules a string containing the clauses to set as theory.
   * @param goal  a Term containing the goal to solve.
   * @return an Iterable containing all the results of the goal
   */
  private def computeAllSolutions(rules: String, goal: Term): Iterable[SolveInfo] =
    engine.setTheory(Theory(rules))
  
    // Compute the goal solutions
    // - get the first solution
    logger.debug(s"Solving goal: $goal")
    val firstSolveInfo = engine.solve(goal)
  
    // - define a high-order function to initialize the LazyList to compute elements only when needed
    val initializeLazyListFn: (Try[SolveInfo] => Try[SolveInfo]) => LazyList[Try[SolveInfo]] = LazyList.iterate(Try(firstSolveInfo))
  
    // - define the anonymous partial pattern matching function to compute the next solution
    val computeNextSolutionFn: Try[SolveInfo] => Try[SolveInfo] = {
      case Success(solveInfo) if solveInfo.hasOpenAlternatives => Try(engine.solveNext())
      case _ => Failure(new NoSuchElementException)
    }
  
    // - define the anonymous partial pattern matching function to extract the solution
    val getNextSolutionsFn: ((Try[SolveInfo], Int)) => Option[SolveInfo] = {
      case (Success(solveInfo), index) if solveInfo.isSuccess =>
        logger.trace(s"Solution ${index + 1} found:\n$solveInfo")
        Some(solveInfo)
      case _ => None
    }
  
    // Return the solutions by assembling previously declared functions
    initializeLazyListFn(computeNextSolutionFn)
      .takeWhile(_.isSuccess)
      .zipWithIndex
      .flatMap(getNextSolutionsFn)
  
  /**
   * Formats the return type of the solutions based on what was declared in the annotation.
   *
   * @param fields     a map containing the extracted values of method fields of the @PrologMethod annotation.
   * @param solveInfos an Iterable containing the solutions of the goal.
   * @return an Iterable (or the return type specified in the annotation) containing the solutions.
   */
  private def formatOutput(fields: PrologMethodFields, solveInfos: Iterable[SolveInfo]): Iterable[AnyRef] =
    val typesOption = fields.get("types").flatten.asInstanceOf[Option[Types]]
    val signaturesOption = fields.get("signatures").flatten.asInstanceOf[Option[Signature]]
  
    (typesOption, signaturesOption) match
      // if both types and signatures are present, return the results based on the output variables types
      case (Some(types), Some(signatures)) =>
        // get the last input variable index
        val lastInputVarIndex = signatures.inputVars.length - 1
  
        // check if all output types are lists, if so return the results as List
        val listContentPattern = "List\\[(.*)]".r
        val outputTypes = signatures.outputVars.indices.map(idx => types.values(lastInputVarIndex + idx + 1))
  
        if (outputTypes.forall(_ matches listContentPattern.pattern.pattern())) {
          logger.trace(s"output types are all lists, returning the results as a list of lists")
          solveInfos.flatMap(info => signatures.outputVars.map(info.getTerm)).toList
        } else {
          logger.trace(s"output types are not all lists, returning the results as a generic iterable of terms")
          solveInfos.map(_.getSolution)
        }
  
      // otherwise return the results as generic Iterable[Term] type
      case _ => solveInfos.map(_.getSolution)
