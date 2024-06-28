package pps.exam.application
package handler

import java.lang.reflect.Method

trait PrologExecutor extends S2PProcessor

/**
 * trait that gives the superclass the ability to execute the prolog annotation logic using tuProlog engine
 */
trait PrologAnnotationExecutor extends PrologExecutor:
  def executeAnnotation(method: Method, args: Array[AnyRef]): AnyRef

/**
 * trait that gives the superclass the ability to execute the annotated prolog method body
 */
trait PrologBodyMethodExecutor extends PrologExecutor:
  def executeMethodBody(originalObject: Any, method: Method, args: Array[AnyRef]): AnyRef =
    logger.trace(s"method's declaring class: ${method.getDeclaringClass}")
    method.invoke(originalObject, args: _*)
