package pps.exam.application
package scala2prolog.annotation

import alice.tuprolog.*

/**
 * Case class that represents the 'clauses' method field of the @PrologMethod annotation.
 *
 * @param value an array that contains the clauses of the method field.
 */
case class Clauses(value: Array[String]) extends Entity

/**
 * Object companion that contains the methods to extract and parse the 'clauses' method field of the @PrologMethod annotation.
 */
object Clauses extends Entity:
  def apply(param: Array[String]): Option[Clauses] =
    isEmpty(param.mkString, {
      logger.trace("clauses is empty, returning None clauses...")
      None
    }, {
      logger.trace(s"extracted clauses from @Prolog* annotation: '${param.mkString("Array(", ", ", ")")}'")
      Some(new Clauses(param))
    })