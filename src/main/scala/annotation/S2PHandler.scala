package pps.exam.application
package annotation

import alice.tuprolog.Prolog

import java.lang.reflect.Method

/**
 * Trait that gives the superclass the property of a Scala2Prolog annotation handler
 */
trait S2PHandler

/**
 * trait that gives the superclass the ability to execute the prolog annotation logic
 */
trait PrologAnnotationHandler extends S2PHandler:
  def executeAnnotation(method: Method, args: Array[AnyRef]): AnyRef

/**
 * trait that gives the superclass the ability to execute the annotated prolog method body
 */
trait PrologBodyMethodHandler extends S2PHandler:
  def executeMethodBody(engine: Prolog, method: Method, args: Array[AnyRef]): AnyRef =
    method.invoke(method.getDeclaringClass, args: _*)