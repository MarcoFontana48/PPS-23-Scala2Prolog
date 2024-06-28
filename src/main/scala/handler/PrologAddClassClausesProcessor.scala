package pps.exam.application
package handler

import annotation.{PrologAddSharedClauses, PrologMethod}

import java.lang.reflect.Method


/**
 * handle the methods annotated with @PrologAddSharedClauses.
 */
object PrologAddClassClausesProcessor:
  def apply(classClauses: Option[Clauses], originalObject: Any): PrologAddClassClausesProcessor = new PrologAddClassClausesProcessor(classClauses, originalObject)

/**
 * utility methods to extract and parse the fields of an annotation.
 */
abstract class PrologAddClassClausesUtils
  extends PrologExtractorUtils[PrologAddSharedClauses, PrologAnnotationFields]
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
   * @param prologClass a Scala2Prolog annotation.
   * @return the extracted and parsed clauses of the annotation
   */
  override def extractClauses(prologAddClassClauses: PrologAddSharedClauses): Option[Clauses] =
    Clauses(prologAddClassClauses.clauses())

case class PrologAddClassClausesProcessor(classClauses: Option[Clauses], originalObject: Any)
  extends PrologAddClassClausesUtils
    with PrologBodyMethodExecutor:

  def addClassClauses(method: Method): Option[Clauses] =
    val prologMethodAnnotation = method.getAnnotation(classOf[PrologAddSharedClauses])
    val extractedClauses = extractClauses(prologMethodAnnotation)

    extractedClauses match
      case Some(clauses) => classClauses match
        case Some(existingClauses) => Clauses(existingClauses.value ++ clauses.value)
        case None => extractedClauses
      case None => classClauses