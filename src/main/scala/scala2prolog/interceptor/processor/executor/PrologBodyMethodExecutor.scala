package pps.exam.application
package scala2prolog.interceptor.processor.executor

import java.lang.reflect.Method

/**
 * trait that gives the superclass the ability to execute the annotated prolog method body
 */
trait PrologBodyMethodExecutor extends PrologExecutor:
  def executeMethodBody(originalObject: Any, method: Method, args: Array[AnyRef]): AnyRef =
    logger.trace(s"method's declaring class: ${method.getDeclaringClass}")
    method.invoke(originalObject, args: _*)
