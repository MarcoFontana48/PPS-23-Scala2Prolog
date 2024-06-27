package pps.exam.application
package handler

import annotation.*

/**
 * trait that represents annotation's method fields extractors.
 */
trait PrologEntityExtractor extends S2PHandler

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

/**
 * trait provides the property to extract the clauses from the annotation.
 *
 * @tparam A the type of the annotation.
 */
trait ClausesExtractor[A] extends PrologEntityExtractor:
  def extractClauses(annotation: A): Option[Clauses]

/**
 * trait provides the property to extract the predicate from the annotation.
 *
 * @tparam A the type of the annotation.
 */
trait PredicateExtractor[A] extends PrologEntityExtractor:
  def extractPredicate(annotation: A): Option[Predicate]

/**
 * trait provides the property to extract the signature from the annotation.
 *
 * @tparam A the type of the annotation.
 */
trait SignatureExtractor[A] extends PrologEntityExtractor:
  def extractSignature(annotation: A): Option[Signature]

/**
 * trait provides the property to extract the types from the annotation.
 *
 * @tparam A the type of the annotation.
 */
trait TypesExtractor[A] extends PrologEntityExtractor:
  def extractTypes(annotation: A): Option[Types]