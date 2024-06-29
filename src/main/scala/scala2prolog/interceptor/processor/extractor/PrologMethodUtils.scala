package pps.exam.application
package scala2prolog.interceptor.processor.extractor

import scala2prolog.annotation.*
import scala2prolog.interceptor.processor.PrologAnnotationFields
import scala2prolog.interceptor.processor.extractor.entity.{ClausesExtractor, PredicateExtractor, SignatureExtractor, TypesExtractor}

/**
 * Utility object to extract and parse the fields of @PrologMethod annotations.
 */
abstract class PrologMethodUtils
  extends ExtractorUtils[PrologMethod, PrologAnnotationFields]
    with SignatureExtractor[PrologMethod]
    with PredicateExtractor[PrologMethod]
    with ClausesExtractor[PrologMethod]
    with TypesExtractor[PrologMethod]:
  /**
   * Method to extract and parse the fields of @PrologMethod annotations.
   *
   * @param prologMethod a @PrologMethod annotation.
   * @return a Map that contains the extracted and parsed method fields of the @PrologMethod annotation
   */
  override def extractMethodFields(prologMethod: PrologMethod): PrologAnnotationFields =
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
   * @return an Option of 'Predicate' that contains informations about the predicate method field
   */
  override def extractPredicate(prologMethod: PrologMethod): Option[Predicate] =
    Predicate(prologMethod.predicate())

  /**
   * Method to extract and parse the 'clauses' method field of @PrologMethod annotations.
   *
   * @param prologMethod a @PrologMethod annotation.
   * @return an Option of 'Clauses' that contains informations about the clauses method field
   */
  override def extractClauses(prologMethod: PrologMethod): Option[Clauses] =
    Clauses(prologMethod.clauses())

  /**
   * Method to extract and parse the 'types' method field of @PrologMethod annotations.
   *
   * @param prologMethod a @PrologMethod annotation.
   * @return an Option of 'Types' that contains informations about the types method field
   */
  override def extractTypes(prologMethod: PrologMethod): Option[Types] =
    Types(prologMethod.types())

  /**
   * Method to extract and parse the 'signature' method field of @PrologMethod annotations.
   *
   * @param prologMethod a @PrologMethod annotation.
   * @return an Option of 'Signature' that contains informations about the signature method field
   */
  override def extractSignature(prologMethod: PrologMethod): Option[Signature] =
    Signature(prologMethod.signature())

