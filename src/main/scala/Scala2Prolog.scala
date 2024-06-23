package pps.exam.application

import alice.tuprolog.{Prolog, SolveInfo, Term, Theory}
import org.apache.logging.log4j.scala.Logging
import pps.exam.application.annotation.{Clauses, Predicate, PrologMethodFields}

import scala.util.{Failure, Success, Try}

/**
 * This object provides a method to set a theory and solve a goal in Prolog.
 */
object Scala2Prolog extends Logging {
  /**
   * Sets a theory and solves a goal in Prolog.
   *
   * @param fields a map containing the extracted values of method fields of the @PrologMethod annotation.
   * @return a LazyList that is computed only when needed, containing the result of the goal
   */
  def setTheoryAndSolveGoal(fields: PrologMethodFields, args: AnyRef): LazyList[Term] = {
    val engine = Prolog()
    val rules = fields("clauses").asInstanceOf[Clauses].values.mkString(" ")
    val goal = fields("predicate").asInstanceOf[Predicate].generateGoal() //TODO pass args of the annotated method
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