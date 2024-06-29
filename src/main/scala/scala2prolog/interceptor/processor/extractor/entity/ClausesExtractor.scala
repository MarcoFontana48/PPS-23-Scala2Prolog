package pps.exam.application
package scala2prolog.interceptor.processor.extractor.entity

import scala2prolog.annotation.Clauses

/**
 * trait provides the property to extract the clauses from the annotation.
 *
 * @tparam A the type of the annotation.
 */
trait ClausesExtractor[A] extends EntityExtractor:
  def extractClauses(annotation: A): Option[Clauses]
