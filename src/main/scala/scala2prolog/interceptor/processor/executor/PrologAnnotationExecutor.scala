package pps.exam.application
package scala2prolog.interceptor.processor.executor

import java.lang.reflect.Method

/**
 * trait that gives the superclass the ability to execute the prolog annotation logic using tuProlog engine
 */
trait PrologAnnotationExecutor extends PrologExecutor:
  def executeAnnotation(method: Method, args: Array[AnyRef]): AnyRef
