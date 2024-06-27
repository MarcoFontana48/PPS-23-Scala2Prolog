package pps.exam.application
package handler

import annotation.*

import org.apache.logging.log4j.scala.Logging

import java.lang.reflect.{InvocationHandler, Method}

/**
 * Trait that gives the superclass the property of being a prolog manager.
 */
trait PrologManager extends Logging

/**
 * Companion object that contains the method to create a new PrologHandler instance for the original object passed as
 * argument.
 * If the original object is annotated with @PrologClass, the clauses are extracted from the annotation and used as
 * common theory to each method annotated with @PrologMethod.
 * Otherwise only the clauses from the @PrologMethod annotation are used to execute the Prolog logic on each method
 * call.
 */
object PrologHandlerManager extends PrologManager:
  def apply(originalObject: Any): InvocationHandler =
    if originalObject.getClass.isAnnotationPresent(classOf[PrologClass]) then
      logger.trace(s"originalObject '$originalObject' is annotated with @PrologClass, extracting its clauses...")
      val maybeClauses = PrologClassHandler.extractClauses(originalObject.getClass.getAnnotation(classOf[PrologClass]))
      new PrologHandlerManager(maybeClauses, originalObject)
    else
      logger.trace(s"originalObject '$originalObject' is not annotated with @PrologClass, creating a new Prolog engine...")
      new PrologHandlerManager(Option.empty, originalObject)

/**
 * Handler for an object that intercepts any method call for methods annotated with @PrologMethod inside the
 * originalObject passed as argument and executes the logic expressed in the annotation instead of the originalObject's
 * annotated method body.
 *
 * @param originalObject the original object, methods calls of this object annotated with @PrologMethod are intercepted
 */
class PrologHandlerManager(classClauses: Option[Clauses], originalObject: Any)
  extends InvocationHandler
  with PrologManager:
  /**
   * Intercepts the method call of the proxy instance and executes the logic of the annotated @PrologMethod method
   * instead of the original method body.
   *
   * @param proxy  the proxy instance that intercepts the method call
   * @param method the method to invoke
   * @param args   the arguments to pass to the method
   * @return the result of the method call
   */
  override def invoke(proxy: Any, method: Method, args: Array[AnyRef]): AnyRef =
    logger.debug(s"invoking method '${method.getName}' on proxy of original object '$originalObject'...")

    if method.isAnnotationPresent(classOf[PrologMethod]) then
      logger.trace("method is annotated with @PrologMethod, executing Prolog logic...")
      PrologMethodHandler(classClauses).executeAnnotation(method, args)
    else
      logger.debug("method is not annotated with @PrologMethod, invoking the default method on the real object...")
      method.invoke(originalObject, args: _*)
