package pps.exam.application

import annotation.{Clauses, Predicate, PrologMethodFields}

import alice.tuprolog.{Prolog, SolveInfo, Term, Theory}
import org.apache.logging.log4j.scala.Logging

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
  def setTheoryAndSolveGoal(fields: PrologMethodFields, args: Option[Array[AnyRef]]): Iterable[Term] =
    val rules = generateRules(fields)
    val goal = generateGoal(fields, args)
    computeAllSolutions(rules, goal)

  /**
   * Computes all solutions of a goal in Prolog.
   *
   * @param rules a string containing the clauses to set as theory.
   * @param goal  a Term containing the goal to solve.
   * @return an Iterable containing all the results of the goal
   */
  private def computeAllSolutions(rules: String, goal: Term): Iterable[Term] =
    val engine = Prolog()
    logger.debug(s"Setting theory: $rules")
    engine.setTheory(Theory(rules))
    logger.debug(s"Solving goal: $goal")
    val solveInfo = engine.solve(goal)
    logger.trace(s"Solution found: ${solveInfo.getSolution}")
    LazyList.iterate(Try(solveInfo)) {
      case Success(solveInfo) if solveInfo.hasOpenAlternatives => Try(engine.solveNext())
      case _ => Failure(new NoSuchElementException)
    }.takeWhile(_.isSuccess).collect {
      case Success(solveInfo) =>
        logger.trace(s"Solution found: ${solveInfo.getSolution}")
        solveInfo.getSolution
    }

  /**
   * Generates a goal from the extracted fields of the @PrologMethod annotation.
   *
   * @param fields a map containing the extracted values of method fields of the @PrologMethod annotation.
   * @param args   an optional array containing the arguments of the method annotated with @PrologMethod.
   * @return a Term containing the goal to solve.
   */
  private def generateGoal(fields: PrologMethodFields, args: Option[Array[AnyRef]]): Term =
    fields.get("predicate").flatten match
      case Some(predicate: Predicate) =>
        args match
          case Some(values) => predicate.generateGoal(values)
          case None => predicate.generateGoal(Array.empty)
      case _ => throw new Exception("Failed to extract predicate or predicate is not of type Predicate")

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
