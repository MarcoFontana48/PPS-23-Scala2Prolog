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
  def executeMethodBody(method: Method, args: Array[AnyRef]): AnyRef =
    method.invoke(method.getDeclaringClass, args: _*)
