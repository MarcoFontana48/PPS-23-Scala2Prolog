package pps.exam.application
package scala2prolog.annotation

import alice.tuprolog.Term
import alice.tuprolog.Term.createTerm

/**
 * Case class that represents the 'predicate' method field of the @PrologMethod annotation.
 *
 * @param term a compound term that represents the predicate.
 */
case class Predicate(term: Term) extends Entity

/**
 * Object companion that contains the methods to extract and parse the 'predicate' method field of the @PrologMethod annotation.
 */
object Predicate extends Entity:

  import Term.createTerm

  def apply(param: String): Option[Predicate] =
    isEmpty(param, {
      logger.trace("predicate is empty, returning None predicate...")
      None
    }, {
      logger.trace(s"extracted predicate from @Prolog* annotation: '$param', parsing its content...")

      // pattern: 'name(+Varname1, Varname2, ..., ?Varname3)' where in front of any Varname may appear one of those
      // symbols '+', '-', '?', '@' or none
      val pattern = "\\w+\\(\\s*([+-@?]?\\w+\\s*,\\s*)*[+-@?]?\\w+\\s*\\)".r
      val matchOption = pattern.findFirstIn(param)

      // if the pattern matches the parameter, then parse the predicate to remove any '?' and '@' occurrences because
      // they're inferred
      matchOption match
        case Some(matched) =>
          val cleanedParam = matched.replaceAll("[?@]", "")
          logger.trace(s"parsed predicate: '$cleanedParam'")
          Some(new Predicate(createTerm(cleanedParam)))
        case None =>
          logger.trace("given parameter does not match the expected pattern, returning None predicate...")
          None
    })
