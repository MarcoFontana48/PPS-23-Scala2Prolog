package pps.exam.application
package annotation

import alice.tuprolog.Prolog
import org.apache.logging.log4j.scala.Logging

import java.lang.reflect.{InvocationHandler, Method}

/**
 * Trait that gives the superclass the property of being a prolog manager.
 */
trait PrologManager

/**
 * Companion object that contains the method to create a new PrologHandler instance for the original object passed as argument.
 */
object PrologHandlerManager extends PrologManager:
  def apply(originalObject: Any): InvocationHandler = new PrologHandlerManager(new Prolog(), originalObject)

/**
 * Handler for an object that intercepts any method call for methods annotated with @PrologMethod inside the
 * originalObject passed as argument and executes the logic expressed in the annotation instead of the originalObject's
 * annotated method body.
 *
 * @param originalObject the original object, methods calls of this object annotated with @PrologMethod are intercepted
 */
class PrologHandlerManager(engine: Prolog, originalObject: Any) extends Logging with InvocationHandler with PrologManager:
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
      PrologMethodHandler(engine).executeAnnotation(method, args)

    // if the method is not annotated with @PrologMethod, invoke the default method on the real object as if there was no proxy
    else
      logger.debug("method is not annotated with @PrologMethod, invoking the default method on the real object...")
      // !!PERSONAL REMINDER: DO NOT EXTEND this class with PrologBodyMethodHandler, since it's NOT an handler, the
      // method invocation inside this else statement does NOT have anything to do with PROLOG METHODS, because if it
      // reaches this else statement, it means that the method is NOT annotated with @PrologMethod, so it should just
      // invoke the original method body as if there was no proxy.
      method.invoke(originalObject, args: _*)
