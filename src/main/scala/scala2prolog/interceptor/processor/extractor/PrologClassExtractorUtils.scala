package pps.exam.application
package scala2prolog.interceptor.processor.extractor

import scala2prolog.annotation.{Clauses, PrologClass}
import scala2prolog.interceptor.processor.PrologAnnotationFields
import scala2prolog.interceptor.processor.extractor.entity.ClausesExtractor

/**
 * utility methods to extract and parse the fields of an annotation.
 */
abstract class PrologClassExtractorUtils
  extends ExtractorUtils[PrologClass, PrologAnnotationFields]
    with ClausesExtractor[PrologClass]:
  /**
   * Method to extract and parse the fields of a Scala2Prolog annotation.
   *
   * @param prologClass a Scala2Prolog annotation.
   * @return a map that contains the extracted and parsed fields of the annotation
   */
  override def extractMethodFields(prologClass: PrologClass): PrologAnnotationFields =
    Map(
      "clauses" -> extractClauses(prologClass)
    )

  /**
   * Extract the clauses from the annotation.
   *
   * @param prologClass a Scala2Prolog annotation.
   * @return the extracted and parsed clauses of the annotation
   */
  override def extractClauses(prologClass: PrologClass): Option[Clauses] =
    Clauses(prologClass.clauses())

