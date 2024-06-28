package pps.exam.application
package handler

import annotation.{PrologAddClassClauses, PrologMethod}

import java.lang.reflect.Method


/**
 * handle the methods annotated with @PrologAddClassClauses.
 */
object PrologAddClassClausesProcessor:
  def apply(classClauses: Option[Clauses], originalObject: Any): PrologAddClassClausesProcessor = new PrologAddClassClausesProcessor(classClauses, originalObject)

/**
 * utility methods to extract and parse the fields of an annotation.
 */
abstract class PrologAddClassClausesUtils
  extends PrologExtractorUtils[PrologAddClassClauses, PrologAnnotationFields]
  with ClausesExtractor[PrologAddClassClauses]:
  /**
   * Method to extract and parse the fields of a Scala2Prolog annotation.
   *
   * @param prologAddClassClauses a Scala2Prolog annotation.
   * @return a map that contains the extracted and parsed fields of the annotation
   */
  override def extractMethodFields(prologAddClassClauses: PrologAddClassClauses): PrologAnnotationFields =
    Map(
      "clauses" -> extractClauses(prologAddClassClauses)
    )

  /**
   * Extract the clauses from the annotation.
   *
   * @param prologClass a Scala2Prolog annotation.
   * @return the extracted and parsed clauses of the annotation
   */
  override def extractClauses(prologAddClassClauses: PrologAddClassClauses): Option[Clauses] =
    Clauses(prologAddClassClauses.clauses())

case class PrologAddClassClausesProcessor(classClauses: Option[Clauses], originalObject: Any)
  extends PrologAddClassClausesUtils
    with PrologBodyMethodExecutor:

  def addClassClauses(method: Method): Option[Clauses] =
    val prologMethodAnnotation = method.getAnnotation(classOf[PrologAddClassClauses])
    val extractedClauses = extractClauses(prologMethodAnnotation)

    extractedClauses match
      case Some(clauses) => classClauses match
        case Some(existingClauses) => Clauses(existingClauses.value ++ clauses.value)
        case None => extractedClauses
      case None => classClauses