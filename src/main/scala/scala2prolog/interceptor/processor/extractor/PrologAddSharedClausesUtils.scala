package pps.exam.application
package scala2prolog.interceptor.processor.extractor

import scala2prolog.annotation.{Clauses, PrologAddSharedClauses}
import scala2prolog.interceptor.processor.PrologAnnotationFields
import scala2prolog.interceptor.processor.extractor.entity.ClausesExtractor

/**
 * utility methods to extract and parse the fields of an annotation.
 */
abstract class PrologAddSharedClausesUtils
  extends ExtractorUtils[PrologAddSharedClauses, PrologAnnotationFields]
    with ClausesExtractor[PrologAddSharedClauses]:
  /**
   * Method to extract and parse the fields of a Scala2Prolog annotation.
   *
   * @param prologAddClassClauses a Scala2Prolog annotation.
   * @return a map that contains the extracted and parsed fields of the annotation
   */
  override def extractMethodFields(prologAddClassClauses: PrologAddSharedClauses): PrologAnnotationFields =
    Map(
      "clauses" -> extractClauses(prologAddClassClauses)
    )

  /**
   * Extract the clauses from the annotation.
   *
   * @param prologAddClassClauses a Scala2Prolog annotation.
   * @return the extracted and parsed clauses of the annotation
   */
  override def extractClauses(prologAddClassClauses: PrologAddSharedClauses): Option[Clauses] =
    Clauses(prologAddClassClauses.clauses())