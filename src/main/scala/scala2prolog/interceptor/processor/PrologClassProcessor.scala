package pps.exam.application
package scala2prolog.interceptor.processor

import scala2prolog.interceptor.processor.extractor.PrologClassUtils

/**
 * handle the methods annotated with @PrologClass.
 */
object PrologClassProcessor
  extends PrologClassUtils
  with PrologProcessor