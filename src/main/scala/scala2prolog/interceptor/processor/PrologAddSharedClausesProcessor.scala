package pps.exam.application
package scala2prolog.interceptor.processor

import scala2prolog.annotation.{Clauses, PrologAddSharedClauses}
import scala2prolog.interceptor.processor.executor.PrologBodyMethodExecutor
import scala2prolog.interceptor.processor.extractor.PrologAddSharedClausesExtractorUtils

import java.lang.reflect.Method

/**
 * handle the methods annotated with @PrologAddSharedClauses.
 */
object PrologAddSharedClausesProcessor:
  def apply(classClauses: Option[Clauses], originalObject: Any): PrologAddSharedClausesProcessor = new PrologAddSharedClausesProcessor(classClauses, originalObject)

case class PrologAddSharedClausesProcessor(classClauses: Option[Clauses], originalObject: Any)
  extends PrologAddSharedClausesExtractorUtils
  with PrologBodyMethodExecutor
  with PrologProcessor:

  def addClassClauses(method: Method): Option[Clauses] =
    val prologMethodAnnotation = method.getAnnotation(classOf[PrologAddSharedClauses])
    val extractedClauses = extractClauses(prologMethodAnnotation)

    extractedClauses match
      case Some(clauses) => classClauses match
        case Some(existingClauses) => Clauses(existingClauses.value ++ clauses.value)
        case None => extractedClauses
      case None => classClauses