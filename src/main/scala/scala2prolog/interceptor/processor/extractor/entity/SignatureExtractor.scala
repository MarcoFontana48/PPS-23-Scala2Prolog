package pps.exam.application
package scala2prolog.interceptor.processor.extractor.entity

import scala2prolog.annotation.Signature

/**
 * trait provides the property to extract the signature from the annotation.
 *
 * @tparam A the type of the annotation.
 */
trait SignatureExtractor[A] extends EntityExtractor:
  def extractSignature(annotation: A): Option[Signature]
