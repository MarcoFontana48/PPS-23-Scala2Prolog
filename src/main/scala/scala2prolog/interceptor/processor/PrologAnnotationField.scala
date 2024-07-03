package pps.exam.application
package scala2prolog.interceptor.processor

import scala2prolog.annotation.Entity

/**
 * Type alias that represents a Map of the fields of prolog annotations
 */
type PrologAnnotationFields = Map[String, Option[Entity]]