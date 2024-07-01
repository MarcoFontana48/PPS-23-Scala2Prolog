package pps.exam.application
package scala2prolog.interceptor.processor

import scala2prolog.annotation.*
import scala2prolog.interceptor.processor.executor.PrologAnnotationExecutor
import scala2prolog.interceptor.processor.extractor.PrologMethodUtils

import alice.tuprolog.{Prolog, SolveInfo, Term, Theory}

import java.lang.reflect.Method
import scala.annotation.tailrec
import scala.util.{Failure, Success, Try}

/**
 * handle the methods annotated with @PrologMethod.
 */
object PrologMethodProcessor:
  def apply(classClauses: Option[Clauses]): PrologMethodProcessor = new PrologMethodProcessor(classClauses)

case class PrologMethodProcessor(classClauses: Option[Clauses])
  extends PrologMethodUtils
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

  /**
   * Generates rules from the extracted fields of the @PrologMethod annotation.
   *
   * @param fields a map containing the extracted values of method fields of the @PrologMethod annotation.
   * @return a string containing the clauses to set as theory.
   */
  private def generateRules(fields: PrologAnnotationFields): String =
    fields.get("clauses").flatten match
      case Some(clauses: Clauses) => clauses.value.mkString(" ")
      case _ => ""  // in case clauses are not present, return an empty string

  /**
   * Generates a goal from the extracted fields of the @PrologMethod annotation.
   *
   * @param fields a map containing the extracted values of method fields of the @PrologMethod annotation.
   * @param args   an optional array containing the arguments of the method annotated with @PrologMethod.
   * @param method a Method that represents the method annotated with @PrologMethod.
   * @return a Term containing the goal to solve.
   */
  private def generateGoal(args: Option[Array[AnyRef]], method: Method, fields: PrologAnnotationFields): Term =
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
        case (varHead :: varTail, valueHead :: valueTail) =>  replaceTermsHelper(varTail)(valueTail)(acc.replaceFirst(getPredicateVariableNotationPattern(varHead), replaceVariableNotationPatternWithValue(varHead)(valueHead)))
        case (varHead :: varTail, Nil) =>                     replaceTermsHelper (varTail)(values)  (acc.replaceFirst(getPredicateVariableNotationPattern(varHead), varHead))
        case (Nil, valueHead :: valueTail) =>                 replaceTermsHelper (vars)(valueTail)  (acc.replaceFirst(getPredicateVariableStandardPattern, replaceVariableNotationPatternWithValue("Nil")(valueHead)))
        case (Nil, Nil) => acc

    /**
     * Extracts the goal from the predicate field of the @PrologMethod annotation.
     *
     * @param value    a PrologMethodEntity that represents the predicate field of the @PrologMethod annotation.
     * @param argsList a list that contains the arguments of the method annotated with @PrologMethod.
     * @return a Term that represents the goal to solve.
     */
    def extractGoal(value: Entity, argsList: List[AnyRef]) =
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
      /**
       * Formats the element in a valid prolog format.
       * Checks if argument 'elements' has 'List(...)' elements and formats them as '[...]', otherwise leaves them as
       * they are
       *
       * @param element an element to format.
       * @return a string that represents the formatted element.
       */
      def formatElement(element: Any): String = element match {
        case list: List[_] => list.map(formatElement).mkString("[", ",", "]") // format list and nested lists
        case other => other.toString
      }

      // formats the arguments of the method in a valid prolog format
      val formattedArgs = argsList.map(formatElement)

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
    val engine = Prolog()

    /**
     * Sets prolog engine's theory based on the extracted fields of the @PrologMethod annotation and optionally
     * the @PrologClass annotation.
     * If the @PrologClass annotation is present, it concatenates the clauses of the @PrologClass annotation with the
     * rules of the @PrologMethod annotation to set the theory into the engine.
     * Otherwise, it sets only the rules of the @PrologMethod annotation into the engine.
     */
    def setEngineTheory(): Unit =
      if classClauses.isDefined then
        val classClausesStr = classClauses.get.value.mkString("", " ", " ")
        val theory = classClausesStr + rules
        logger.trace(s"concatenation of @PrologClass clauses '$classClausesStr' with @PrologMethod rules '$rules' to " +
          s"set new theory into the engine: '$theory'...")
        engine.setTheory(Theory(theory))
      else
        logger.trace(s"no @PrologClass clauses found, setting only @PrologMethod rules into the engine: '$rules'...")
        engine.setTheory(Theory(rules))

    setEngineTheory()

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

    //return the solutions by assembling previously declared functions and collecting them in an Iterable
    for
      //iterate over each Try[SolveInfo] and its index in the LazyList
      (solveInfoTry, index) <- initializeLazyListFn(computeNextSolutionFn).takeWhile(_.isSuccess).zipWithIndex
      //convert the Try[SolveInfo] to an Option[SolveInfo] to check if it is a success and yield the SolveInfo
      solveInfo <- solveInfoTry.toOption if solveInfo.isSuccess
    yield
      //yield the successful SolveInfo, collecting it into a resulting Iterable[SolveInfo]
      logger.trace(s"\nSolution ${index + 1} found:\n$solveInfo")
      solveInfo

    /**
     * Processes the types and signatures of the @PrologMethod annotation to extract the return type.
     *
     * @param types      a Types that represents the types field of the @PrologMethod annotation.
     * @param signatures a Signature that represents the signatures field of the @PrologMethod annotation.
     * @return the solution using the return type specified in the annotation.
     */
  private def processTypesAndSignatures(solveInfos: Iterable[SolveInfo], types: Types, signatures: Signature) =
    val lastInputVarIndex = signatures.inputVars.length - 1
    val listContentPattern = "List\\[(.*)]".r
    val outputTypes = signatures.outputVars.indices.map(idx => types.values(lastInputVarIndex + idx + 1))

    outputTypes match
      case types if types forall (_ matches listContentPattern.pattern.pattern()) =>
        val listTypes = types.map { case listContentPattern(listType) => listType; case _ => "" }
        solveInfos.flatMap(info => signatures.outputVars.map(info.getTerm)).toList
      case _ =>
        solveInfos.map(_.getSolution)

  /**
   * Infers the return type based on the Scala method's return type and arguments.
   *
   * @return the solution using the inferred return type.
   */
  private def inferReturnType(solveInfos: Iterable[SolveInfo], method: Method, args: Array[AnyRef]) =
    val prologVarsFromArgs: List[String] = args.filterNot(arg => arg.isInstanceOf[Iterable[_]]).map(_.toString).filter(_.matches("^[A-Z].*")).toList

    if prologVarsFromArgs.isEmpty then
      if method.getReturnType == classOf[Boolean] then solveInfos.head.isSuccess else solveInfos.map(_.getSolution)
    else
      val returnTypeToTermFn: Map[Class[_], alice.tuprolog.Term => Any] = Map(
        classOf[Int] -> (_.castTo(classOf[alice.tuprolog.Int]).intValue()),
        classOf[Double] -> (_.castTo(classOf[alice.tuprolog.Double]).doubleValue()),
        classOf[Boolean] -> (_.isEqual(Term.createTerm("true"))),
        classOf[String] -> (_.toString),
      )

      returnTypeToTermFn.get(method.getReturnType) match
        case Some(termToValue) =>
          termToValue(solveInfos.head.getTerm(prologVarsFromArgs.head))
        case None =>
          solveInfos.flatMap { info =>
            prologVarsFromArgs.flatMap { varName =>
              info.getTerm(varName) match
                case term: alice.tuprolog.Int => Some(term.intValue())
                case term: alice.tuprolog.Double => Some(term.doubleValue())
                case term if term.isEqual(Term.createTerm("true")) => Some(true)
                case term if term.isEqual(Term.createTerm("false")) => Some(false)
                case term: Term ⇒ Some(term)
            }
          }
