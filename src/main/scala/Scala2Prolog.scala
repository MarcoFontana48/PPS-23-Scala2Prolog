package pps.exam.application

import annotation.{Clauses, Predicate, PrologMethodEntity, PrologMethodFields, Signature, Types}

import alice.tuprolog.{Prolog, SolveInfo, Term, Theory}
import org.apache.logging.log4j.scala.Logging

import scala.annotation.tailrec
import scala.util.{Failure, Success, Try}

/**
 * This object provides a method to set a theory and solve a goal in Prolog.
 */
object Scala2Prolog extends Logging:
  /**
   * Sets a theory and solves a goal in Prolog.
   *
   * @param fields a map containing the extracted values of method fields of the @PrologMethod annotation.
   * @return an Iterable containing the result of the goal
   */
  def setTheoryAndSolveGoal(fields: PrologMethodFields, args: Option[Array[AnyRef]]): Iterable[AnyRef] =
    val rules = generateRules(fields)
    val goal = generateGoal(fields, args)
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
   * @return a Term containing the goal to solve.
   */
  private def generateGoal(fields: PrologMethodFields, args: Option[Array[AnyRef]]): Term =
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
    def getPredicateVariableNotationPattern(variable: String) = {
      s"'[-|+]'\\($variable\\)"
    }

    /**
     * Method to get the pattern of the predicate variable standard notation.
     * The predicate standard notation is represented as a variable that starts with an uppercase letter.
     *
     * @return a string that represents the pattern of the predicate variable standard notation.
     */
    def getPredicateVariableStandardPattern = {
      s"[A-Z]\\w*"
    }

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
      (vars, values) match
        case (varHead :: varTail, valueHead :: valueTail) =>  replaceTermsHelper(varTail)(valueTail)(acc.replaceFirst(getPredicateVariableNotationPattern(varHead), replaceVariableNotationPatternWithValue(varHead)(valueHead)))
        case (varHead :: varTail, Nil) =>                     replaceTermsHelper  (varTail)(values) (acc.replaceFirst(getPredicateVariableNotationPattern(varHead), varHead))
        case (Nil, valueHead :: valueTail) =>                 replaceTermsHelper  (vars)(valueTail) (acc.replaceFirst(getPredicateVariableStandardPattern, replaceVariableNotationPatternWithValue("Nil")(valueHead)))
        case (Nil, Nil) => acc

    // convert term to string
    val termStr = fields("predicate").getOrElse(Predicate("")).asInstanceOf[Predicate].term.toString
    logger.trace(s"predicate term as string: '$termStr'")

    // extract variables from term. The predicate notation is represented as concatenation of operator and term, since
    // the input variable has tuProlog type Term: i.e.: '+'(V) )
    val variablePattern = "'[-|+]?'\\(([A-Z]\\w*)\\)".r
    val variables = variablePattern.findAllMatchIn(termStr).toList.map(_.group(1))
    logger.trace(s"extracted variables from term: '${variables.mkString("List(", ", ", ")")}'")

    // replace variables with values
    val replacedTermStr = replaceTermsHelper(variables)(args.getOrElse(Array.empty[AnyRef]).toList)(termStr)
    logger.trace(s"replaced term: '$replacedTermStr'")
    Term.createTerm(replacedTermStr)

  /**
   * Computes all solutions of a goal in Prolog.
   *
   * @param rules a string containing the clauses to set as theory.
   * @param goal  a Term containing the goal to solve.
   * @return an Iterable containing all the results of the goal
   */
  private def computeAllSolutions(rules: String, goal: Term): Iterable[SolveInfo] =
    // Create a new Prolog engine
    val engine = Prolog()

    // Set the theory
    logger.debug(s"Setting theory: $rules")
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
      case (Some(types), Some(signatures)) =>
        val lastInputVarIndex = signatures.inputVars.length - 1

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