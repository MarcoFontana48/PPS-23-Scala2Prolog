package pps.exam.application
package scala2prolog.interceptor.processor.extractor.entity

import scala2prolog.annotation.Types

/**
 * trait provides the property to extract the types from the annotation.
 *
 * @tparam A the type of the annotation.
 */
trait TypesExtractor[A] extends EntityExtractor:
  def extractTypes(annotation: A): Option[Types]
