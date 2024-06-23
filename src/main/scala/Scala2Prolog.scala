package pps.exam.application

import alice.tuprolog.{Prolog, SolveInfo, Term, Theory}
import org.apache.logging.log4j.scala.Logging
import pps.exam.application.annotation.{Clauses, Predicate}
import scala.util.{Try, Success, Failure}

/**
 * This object provides a method to set a theory and solve a goal in Prolog.
 */
object Scala2Prolog extends Logging {
  /**
   * Sets a theory and solves a goal in Prolog.
   *
   * @param predicate the predicate to solve
   * @param clauses the clauses to set as theory
   * @return a LazyList that is computed only when needed, containing the result of the goal
   */
  def setTheoryAndSolveGoal(predicate: Predicate, clauses: Clauses): LazyList[Term] = {
    val engine = Prolog()
    val rules = clauses.values.mkString(" ")
    val goal = predicate.formatPredicate()  //TODO
    logger.trace(s"Setting theory: $rules")
    engine.setTheory(Theory(rules))
    logger.trace(s"Solving goal: $goal")
    val solveInfo = engine.solve(goal)
    getAllSolutions(engine, solveInfo)
  }

  private def getAllSolutions(engine: Prolog, initialSolveInfo: SolveInfo): LazyList[Term] = {
    LazyList.iterate(Try(initialSolveInfo)) {
      case Success(solveInfo) if solveInfo.hasOpenAlternatives => Try(engine.solveNext())
      case _ => Failure(new NoSuchElementException)
    }.takeWhile(_.isSuccess).collect {
      case Success(solveInfo) => solveInfo.getSolution
    }
  }
}