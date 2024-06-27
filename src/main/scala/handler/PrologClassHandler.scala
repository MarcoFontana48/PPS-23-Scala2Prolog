package pps.exam.application
package handler

import annotation.PrologClass

/**
 * handle the methods annotated with @PrologClass.
 */
object PrologClassHandler extends PrologClassUtils

/**
 * utility methods to extract and parse the fields of an annotation.
 */
abstract class PrologClassUtils
  extends PrologExtractorUtils[PrologClass, PrologAnnotationFields]
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
