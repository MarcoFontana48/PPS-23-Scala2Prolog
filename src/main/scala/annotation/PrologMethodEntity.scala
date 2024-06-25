package pps.exam.application
package annotation

import alice.tuprolog.*
import org.apache.logging.log4j.scala.Logging

import scala.annotation.tailrec

/**
 * sealed trait that represents only the entities that can be extracted from the @PrologMethod annotation.
 */
sealed trait PrologMethodEntity extends Logging:
  /**
   * Method to check if the extracted field of the annotation is empty
   *
   * @param param   a string that represents the value of the method field of the @PrologMethod annotation.
   * @param default a default call-by-name parameter function (that is evaluated each time it is used within the method) to be
   *                returned if the method field is empty.
   * @param action  an action call-by-name parameter function (that is evaluated each time it is used within the method) to be
   *                executed if the method field is not empty.
   * @tparam T the return type of the method field.
   * @return a generic type T that contains the extracted and parsed field of the annotation
   */
  def isEmpty[T](param: String, default: => T, action: => T): T = param match
    case p if p.isEmpty =>
      default
    case _ =>
      action

/**
 * Case class that represents the 'predicate' method field of the @PrologMethod annotation.
 *
 * @param term a compound term that represents the predicate.
 */
case class Predicate(term: Term) extends PrologMethodEntity with Logging

/**
 * Object companion that contains the methods to extract and parse the 'predicate' method field of the @PrologMethod annotation.
 */
object Predicate extends PrologMethodEntity with Logging:

  import Term.createTerm

  def apply(param: String): Option[Predicate] =
    isEmpty(param, {
      logger.trace("predicate is empty, returning None predicate...")
      None
    }, {
      logger.trace(s"extracted predicate from @PrologMethod annotation: '$param', parsing its content...")
      Some(new Predicate(createTerm(param)))
    })

/**
 * Case class that represents the 'signature' method field of the @PrologMethod annotation.
 *
 * @param inputVars  an array that contains the input variables of the signature.
 * @param outputVars an array that contains the output variables of the signature.
 */
case class Signature(inputVars: Array[String], outputVars: Array[String]) extends PrologMethodEntity

/**
 * Object companion that contains the methods to extract and parse the 'signature' method field of the @PrologMethod annotation.
 */
object Signature extends PrologMethodEntity with Logging:
  def apply(param: String): Option[Signature] =
    isEmpty(param, {
      logger.trace("signature is empty, returning None signature...")
      None
    }, {
      logger.trace(s"extracted signature from @PrologMethod annotation: '$param', extracting input and output variables...")

      /* pattern: (X1,X2,..Xn) -> {Y1,Y2,..Yn} */
      val pattern = "\\(([A-Z]\\w*(,\\s*[A-Z]\\w*)*)\\)\\s*->\\s*\\{([A-Z]\\w*(,\\s*[A-Z]\\w*)*)}".r
      val matchOption = pattern.findFirstMatchIn(param)

      matchOption match
        case Some(m) =>
          // index group 1 is 'input' variables, 2 is the arrow symbol of the signature, 3 is 'output' variables
          val inputVars = m.group(1).split(",").map(_.trim)
          val outputVars = m.group(3).split(",").map(_.trim)
          logger.trace(s"extracted input and output variables from signature: 'input=${inputVars.mkString("Array(", ", ", ")")}', 'output=${outputVars.mkString("Array(", ", ", ")")}'")
          Some(new Signature(inputVars, outputVars))
        case None =>
          throw new IllegalArgumentException(s"Invalid signature format: '$param'. Signature must be formatted as '(X1,X2,..Xn) -> {Y1,Y2,..Yn}'")
    })

/**
 * Case class that represents the 'types' method field of the @PrologMethod annotation.
 *
 * @param values an array that contains the types of the method field.
 */
case class Types(values: Array[String]) extends PrologMethodEntity

/**
 * Object companion that contains the methods to extract and parse the 'types' method field of the @PrologMethod annotation.
 */
object Types extends PrologMethodEntity with Logging:
  def apply(param: Array[String]): Option[Types] =
    isEmpty(param.mkString, {
      logger.trace("types is empty, returning None types...")
      None
    }, {
      logger.trace(s"extracted types from @PrologMethod annotation: '${param.mkString("Array(", ", ", ")")}'")
      param.foreach { e =>
        if !e.matches("(Int|Double|String|Boolean|List\\[\\s*(Int|Double|String|Boolean)\\s*])") then
          throw new IllegalArgumentException(s"Invalid type: '$e'. Valid types are 'Int', 'String', 'Boolean', 'List[Int]', 'List[String]', 'List[Boolean]'")
      }
      Some(new Types(param))
    })

/**
 * Case class that represents the 'clauses' method field of the @PrologMethod annotation.
 *
 * @param value an array that contains the clauses of the method field.
 */
case class Clauses(value: Array[String]) extends PrologMethodEntity

/**
 * Object companion that contains the methods to extract and parse the 'clauses' method field of the @PrologMethod annotation.
 */
object Clauses extends PrologMethodEntity with Logging:
  def apply(param: Array[String]): Option[Clauses] =
    isEmpty(param.mkString, {
      logger.trace("clauses is empty, returning None clauses...")
      None
    }, {
      logger.trace(s"extracted clauses from @PrologMethod annotation: '${param.mkString("Array(", ", ", ")")}'")
      Some(new Clauses(param))
    })