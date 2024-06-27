package pps.exam.application
package annotation

import org.apache.logging.log4j.scala.Logging

trait PrologEntityExtractor extends Logging

/**
 * abstract class that represents the utility methods to extract and parse annotations.
 *
 * @tparam A the type of the annotation.
 * @tparam B the return type of each method, covariance is used to allow the return type to be a subtype of B.
 */
abstract class PrologExtractorUtils[A, +B] extends PrologEntityExtractor:
  /**
   * Method to extract and parse the fields of an annotation.
   *
   * @param annotation an annotation.
   * @return a generic type +B that contains the extracted and parsed fields of the annotation
   */
  def extractMethodFields(annotation: A): B
  
trait ClausesExtractor[A] extends PrologEntityExtractor:
  def extractClauses(annotation: A): Option[Clauses]

trait PredicateExtractor[A] extends PrologEntityExtractor:
  def extractPredicate(annotation: A): Option[Predicate]

trait SignatureExtractor[A] extends PrologEntityExtractor:
  def extractSignature(annotation: A): Option[Signature]

trait TypesExtractor[A] extends PrologEntityExtractor:
  def extractTypes(annotation: A): Option[Types]