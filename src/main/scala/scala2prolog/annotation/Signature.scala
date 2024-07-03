package pps.exam.application
package scala2prolog.annotation

/**
 * Case class that represents the 'signature' method field of the @PrologMethod annotation.
 *
 * @param inputVars  an array that contains the input variables of the signature.
 * @param outputVars an array that contains the output variables of the signature.
 */
case class Signature(inputVars: Array[String], outputVars: Array[String]) extends Entity

/**
 * Object companion that contains the methods to extract and parse the 'signature' method field of the @PrologMethod annotation.
 */
object Signature extends Entity:
  def apply(param: String): Option[Signature] =
    isEmpty(param, {
      logger.trace("signature is empty, returning None signature...")
      None
    }, {
      logger.trace(s"extracted signature from @Prolog* annotation: '$param', extracting input and output variables...")

      /* pattern: (X1, *) -> {Y1, *} */
      val pattern = "\\(([A-Z]\\w*(,\\s*[A-Z]\\w*)*)*\\)\\s*->\\s*\\{([A-Z]\\w*(,\\s*[A-Z]\\w*)*)*}".r
      val matches = pattern.findAllMatchIn(param).toList

      matches.headOption.flatMap { m =>
        val inputVars = Option(m.group(1)).map(_.split(",").map(_.trim)).getOrElse(Array.empty[String])
        val outputVars = Option(m.group(3)).map(_.split(",").map(_.trim)).getOrElse(Array.empty[String])
        logger.trace(s"extracted input and output variables from signature: 'input=${inputVars.mkString("Array(", ", ", ")")}', 'output=${outputVars.mkString("Array(", ", ", ")")}'")
        Some(new Signature(inputVars, outputVars))
      }.orElse(throw new IllegalArgumentException(s"Invalid signature format: '$param'. Signature must be formatted as '(X1,X2,..Xn) -> {Y1,Y2,..Yn}'"))
    })
