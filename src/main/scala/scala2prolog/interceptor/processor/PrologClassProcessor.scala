package pps.exam.application
package scala2prolog.interceptor.processor

import scala2prolog.interceptor.processor.extractor.PrologClassExtractorUtils

/**
 * handle the methods annotated with @PrologClass.
 */
object PrologClassProcessor
  extends PrologClassExtractorUtils
  with PrologProcessor