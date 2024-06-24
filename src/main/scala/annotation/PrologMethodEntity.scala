package pps.exam.application
package annotation

import annotation.PrologMethodUtils.logger

import org.apache.logging.log4j.scala.Logging

/**
 * sealed trait that represents only the entities that can be extracted from the @PrologMethod annotation.
 */
sealed trait PrologMethodEntity extends Logging:
  /**
   * Method to check if the extracted field of the annotation is empty
   *
   * @param param   a string that represents the value of the method field of the @PrologMethod annotation.
   * @param default a default call-by-name parameter (that is evaluated each time it is used within the method) to be
   *                returned if the method field is empty.
   * @param action  an action call-by-name parameter (that is evaluated each time it is used within the method) to be
   *                executed if the method field is not empty.
   * @tparam T the return type of the method field.
   * @return a generic type T that contains the extracted and parsed field of the annotation
   */
  def isEmpty[T](param: String, default: => T, action: => T): T = param match
    case p if p.isEmpty =>
      logger.trace(s"parameter is empty, returning default value")
      default
    case _ =>
      action

/**
 * Case class that represents the 'predicate' method field of the @PrologMethod annotation.
 *
 * @param name  the name of the predicate.
 * @param values a map that contains the values of the predicate
 */
case class Predicate(name: String, values: Map[String, Array[String]]) extends PrologMethodEntity:
  /**
   * Method to generate a goal from the predicate method field of a @PrologMethod annotation.
   *
   * @param inputValues an array that contains the input values of the goal.
   * @return a string that represents the goal.
   */
  def generateGoal(inputValues: String*): String = inputValues match
    case inputValues if inputValues.nonEmpty => inputValues.length match
      case inputValuesLength if inputValuesLength == values("+").length => name + "(" + inputValues.mkString(", ") + ", " + values("-").mkString(", ") + ")."
      case _ => throw new IllegalArgumentException("Invalid number of input values")
    case _ => name + "(" + values("-").mkString(", ") + ")."

/**
 * Object companion that contains the methods to extract and parse the 'predicate' method field of the @PrologMethod annotation.
 */
object Predicate extends PrologMethodEntity with Logging:
  opaque type PredicateMap = Map[String, Array[String]]

  def apply(param: String): Predicate = isEmpty(param, {
    logger.trace("predicate is empty, returning default predicate")
    new Predicate("", Map.empty)
  }, {
    logger.trace(s"extracted predicate from @PrologMethod annotation: '$param', parsing its content...")
    val splitPredicateResult = param.split("[()]")
    val predicateName = splitPredicateResult(0).trim
    val predicateArguments = splitPredicateResult(1).split(",").map(_.trim)

    val modifiedVariables = predicateArguments.zipWithIndex.map {
      case (variable, index) if variable.startsWith("+") || variable.startsWith("-") => variable
      case (variable, index) if index != predicateArguments.length - 1 => "+" + variable
      case (variable, _) => "-" + variable
    }

    val arrayInit = (Array.empty[String], Array.empty[String])
    val foldLeftCurry = modifiedVariables.foldLeft(arrayInit)
    val (inputVars, outputVars) = foldLeftCurry {
      case ((input, output), variable) if variable.startsWith("+") => (input :+ variable.substring(1), output)
      case ((input, output), variable) if variable.startsWith("-") => (input, output :+ variable.substring(1))
    }

    logger.trace(s"extracted variables with predicate notation symbol '+': ${inputVars.mkString("Array(", ", ", ")")}")
    logger.trace(s"extracted variables with predicate notation symbol '-': ${outputVars.mkString("Array(", ", ", ")")}")

    val predicateVarsMap: PredicateMap = Map(
      "+" -> inputVars,
      "-" -> outputVars
    )

    new Predicate(predicateName, predicateVarsMap)
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
  def apply(param: String): Signature = isEmpty(param, {
    logger.trace("signature is empty, returning default signature...")
    new Signature(Array.empty, Array.empty)
  }, {
    logger.trace(s"extracted signature from @PrologMethod annotation: '$param', extracting input and output variables...")

    /* pattern: (X1,X2,..Xn) -> {Y1,Y2,..Yn} */
    val pattern = "\\(([A-Z]\\w*(,\\s*[A-Z]\\w*)*)\\)\\s*->\\s*\\{([A-Z]\\w*(,\\s*[A-Z]\\w*)*)}".r
    val matchOption = pattern.findFirstMatchIn(param)

    matchOption match
      case Some(m) =>
        val inputVarsGroupIndex = 1
        val outputVarsGroupIndex = 3
        val inputVars = m.group(inputVarsGroupIndex).split(",").map(_.trim)
        val outputVars = m.group(outputVarsGroupIndex).split(",").map(_.trim)
        logger.trace(s"extracted input and output variables from signature: 'input=${inputVars.mkString("Array(", ", ", ")")}', 'output=${outputVars.mkString("Array(", ", ", ")")}'")
        new Signature(inputVars, outputVars)
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
  def apply(param: Array[String]): Types = isEmpty(param.mkString, {
    logger.trace("types is empty, returning default types...")
    new Types(Array.empty)
  }, {
    logger.trace(s"extracted types from @PrologMethod annotation: '${param.mkString("Array(", ", ", ")")}'")
    param.foreach { e =>
      if !e.matches("(Int|Double|String|Boolean|List\\[\\s*(Int|Double|String|Boolean)\\s*])") then
        throw new IllegalArgumentException(s"Invalid type: '$e'. Valid types are 'Int', 'String', 'Boolean', 'List[Int]', 'List[String]', 'List[Boolean]'")
    }
    new Types(param)
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
  def apply(param: Array[String]): Clauses = isEmpty(param.mkString, {
    logger.trace("clauses is empty, returning default clauses...")
    new Clauses(Array.empty)
  }, {
    logger.trace(s"extracted clauses from @PrologMethod annotation: '${param.mkString("Array(", ", ", ")")}'")
    new Clauses(param)
  })