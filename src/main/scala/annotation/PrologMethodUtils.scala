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

type PrologMethodFields = Map[String, Option[PrologMethodEntity]]

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
  def extractPredicate(prologMethod: PrologMethod): Option[Predicate] =
    Predicate(prologMethod.predicate())

  /**
   * Method to extract and parse the 'clauses' method field of @PrologMethod annotations.
   *
   * @param prologMethod a @PrologMethod annotation.
   * @return a 'Clauses' that contains the informations about the clauses method field
   */
  def extractClauses(prologMethod: PrologMethod): Option[Clauses] =
    Clauses(prologMethod.clauses())

  /**
   * Method to extract and parse the 'types' method field of @PrologMethod annotations.
   *
   * @param prologMethod a @PrologMethod annotation.
   * @return a 'Types' that contains the informations about the types method field
   */
  def extractTypes(prologMethod: PrologMethod): Option[Types] =
    Types(prologMethod.types())

  /**
   * Method to extract and parse the 'signature' method field of @PrologMethod annotations.
   *
   * @param prologMethod a @PrologMethod annotation.
   * @return a 'Signature' that contains the informations about the signature method field
   */
  def extractSignature(prologMethod: PrologMethod): Option[Signature] =
    Signature(prologMethod.signature())