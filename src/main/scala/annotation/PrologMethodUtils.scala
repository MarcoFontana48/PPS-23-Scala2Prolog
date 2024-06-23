package pps.exam.application
package annotation

import org.apache.logging.log4j.scala.Logging

/**
 * abstract class that represents the utility methods to extract and parse annotations.
 *
 * @tparam A the type of the annotation.
 * @tparam B the return type of each method, covariance is used to allow the return type to be a subtype of B.
 */
abstract class AnnotationUtils[A, +B]:
  /**
   * Method to extract and parse the fields of an annotation.
   *
   * @param annotation an annotation.
   * @return a generic type +B that contains the extracted and parsed fields of the annotation
   */
  def extractMethodFields(annotation: A): B

private type PrologMethodFields = Map[String, PrologMethodEntity]
/**
 * Utility object to extract and parse the fields of @PrologMethod annotations.
 */
object PrologMethodUtils extends AnnotationUtils[PrologMethod, PrologMethodFields] with Logging:
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
   * @return a 'Predicate' that contains the informations about the predicate method field
   */
  def extractPredicate(prologMethod: PrologMethod): Predicate =
    prologMethod.predicate() match
      case predicate if predicate.isEmpty =>
        logger.trace("predicate is empty, returning default predicate")
        Predicate(Array.empty, Array.empty)
      case predicate =>
        logger.trace(s"extracted predicate from @PrologMethod annotation: '$predicate', parsing its content...")
        val variables = extractPrologPredicateNotationVariables(predicate)
        logger.trace(s" - extracted variables with predicate notation symbol '+': ${variables("+").mkString("Array(", ", ", ")")}")
        logger.trace(s" - extracted variables with predicate notation symbol '-': ${variables("-").mkString("Array(", ", ", ")")}")
        Predicate(variables("+"), variables("-"))

  private type PrologPredicateNotationVariables = Map[String, Array[String]] // + -> [X1,X2,...,XN], - -> [Y1,Y2,...,YN], ...
  /**
   * Method to extract the variables from a predicate notation string.
   *
   * @param predicate a predicate notation string.
   * @return a Map that contains the extracted variables with their notation symbol
   */
  private def extractPrologPredicateNotationVariables(predicate: String): PrologPredicateNotationVariables = {
      val pattern = "([+-])(\\w+)".r
      val matches = pattern.findAllIn(predicate).matchData.toArray

      val inputVars = matches
        .filter(_.group(1) == "+")
        .map(_.group(2))
      val outputVars = matches
        .filter(_.group(1) == "-")
        .map(_.group(2))

      Map(
        "+" -> inputVars,
        "-" -> outputVars)
    }

  /**
   * Method to extract and parse the 'clauses' method field of @PrologMethod annotations.
   *
   * @param prologMethod a @PrologMethod annotation.
   * @return a 'Clauses' that contains the informations about the clauses method field
   */
  def extractClauses(prologMethod: PrologMethod): Clauses = Clauses(prologMethod.clauses())

  /**
   * Method to extract and parse the 'types' method field of @PrologMethod annotations.
   *
   * @param prologMethod a @PrologMethod annotation.
   * @return a 'Types' that contains the informations about the types method field
   */
  def extractTypes(prologMethod: PrologMethod): Types =
    prologMethod.types() match
      case types if types.isEmpty =>
        logger.trace("types is empty, returning default types...")
        Types(Array.empty)
      case types =>
        logger.trace(s"extracted types from @PrologMethod annotation: '${types.mkString("Array(", ", ", ")")}'")
        types.foreach { e => if !e.matches("(Int|Double|String|Boolean|List\\[\\s*(Int|Double|String|Boolean)\\s*])") then throw new IllegalArgumentException(s"Invalid type: '$e'. Valid types are 'Int', 'String', 'Boolean', 'List[Int]', 'List[String]', 'List[Boolean]'") }
        Types(types)

  /**
   * Method to extract and parse the 'signature' method field of @PrologMethod annotations.
   *
   * @param prologMethod a @PrologMethod annotation.
   * @return a 'Signature' that contains the informations about the signature method field
   */
  def extractSignature(prologMethod: PrologMethod): Signatures =
    prologMethod.signature() match
      case signature if signature.isEmpty =>
        logger.trace("signature is empty, returning default signature...")
        Signatures(Array.empty, Array.empty)
      case signature =>
        logger.trace(s"extracted signature from @PrologMethod annotation: '$signature', extracting input and output variables...")

        /* pattern: (X1,X2,..Xn) -> {Y1,Y2,..Yn} */
        val pattern = "\\(([A-Z]\\w*(,\\s*[A-Z]\\w*)*)\\)\\s*->\\s*\\{([A-Z]\\w*(,\\s*[A-Z]\\w*)*)}".r
        val matchOption = pattern.findFirstMatchIn(signature)

        matchOption match
          case Some(m) =>
            val inputVarsGroupIndex = 1
            val outputVarsGroupIndex = 3
            val inputVars = m.group(inputVarsGroupIndex).split(",").map(_.trim)
            val outputVars = m.group(outputVarsGroupIndex).split(",").map(_.trim)
            logger.trace(s"extracted input and output variables from signature: 'input=${inputVars.mkString("Array(", ", ", ")")}', 'output=${outputVars.mkString("Array(", ", ", ")")}'")
            Signatures(inputVars, outputVars)
          case None =>
            throw new IllegalArgumentException(s"Signature '$signature' is not formatted correctly")