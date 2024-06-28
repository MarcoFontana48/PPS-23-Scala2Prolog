package pps.exam.application

import annotation.{PrologAddSharedClauses, PrologClass, PrologMethod}

import alice.tuprolog.Term
import org.apache.logging.log4j.scala.Logging

trait Scala2PrologDeclarationPathFindingAlgorithmTest extends Logging:
  @PrologAddSharedClauses(clauses = Array("action(up, [X, Y], [X1, Y]) :- X1 is X - 1, X1 >= 1."))
  def addActionUp(activeActions: List[String]): List[String] =
    if !activeActions.contains("up") then
      logger.trace("Adding action up to active actions")
      activeActions.appended("up")
    else 
      logger.trace("Action up already in active actions, returning the same list...")
      activeActions

  @PrologAddSharedClauses(clauses = Array("action(down, [X, Y], [X1, Y]) :- X1 is X + 1, X1 =< 3."))
  def addActionDown(activeActions: List[String]): List[String] =
    if !activeActions.contains("down") then
      logger.trace("Adding action down to active actions")
      activeActions.appended("down")
    else 
      logger.trace("Action down already in active actions, returning the same list...")
      activeActions

  @PrologAddSharedClauses(clauses = Array("action(left, [X, Y], [X, Y1]) :- Y1 is Y - 1, Y1 >= 1."))
  def addActionLeft(activeActions: List[String]): List[String] =
    if !activeActions.contains("left") then
      logger.trace("Adding action left to active actions")
      activeActions.appended("left")
    else 
      logger.trace("Action left already in active actions, returning the same list...")
      activeActions

  @PrologAddSharedClauses(clauses = Array("action(right, [X, Y], [X, Y1]) :- Y1 is Y + 1, Y1 =< 3."))
  def addActionRight(activeActions: List[String]): List[String] =
    if !activeActions.contains("right") then
      logger.trace("Adding action right to active actions")
      activeActions.appended("right")
    else 
      logger.trace("Action right already in active actions, returning the same list...")
      activeActions

  @PrologMethod
  def find_plan(startingPos: List[Int], exitPos: List[Int], planVar: String): Iterable[Term] = null

@PrologClass(clauses = Array(
    "find_plan(Position, Goal, Plan) :- find_path(Position, Goal, [Position], Plan).",
    "find_path(Position, Position, _, []).",
    "find_path(Position, Goal, Visited, [Action|Plan]) :- action(Action, Position, NextPosition), \\+ member(NextPosition, Visited), find_path(NextPosition, Goal, [NextPosition|Visited], Plan)."))
class Scala2PrologDeclarationPathFindingAlgorithmTestImpl extends Scala2PrologDeclarationPathFindingAlgorithmTest
