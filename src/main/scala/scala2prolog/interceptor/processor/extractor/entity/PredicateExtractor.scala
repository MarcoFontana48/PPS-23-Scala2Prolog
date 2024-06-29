package pps.exam.application
package scala2prolog.interceptor.processor.extractor.entity

import scala2prolog.annotation.Predicate

/**
 * trait provides the property to extract the predicate from the annotation.
 *
 * @tparam A the type of the annotation.
 */
trait PredicateExtractor[A] extends EntityExtractor:
  def extractPredicate(annotation: A): Option[Predicate]
