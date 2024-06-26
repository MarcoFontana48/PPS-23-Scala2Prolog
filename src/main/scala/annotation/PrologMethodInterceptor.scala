package pps.exam.application
package annotation

import org.apache.logging.log4j.scala.Logging

import java.lang.reflect.{InvocationHandler, Method, Proxy}

/**
 * Trait to mixin to give the extended class the property to create a Proxy of an object
 */
trait Interceptor:
  /**
   * Creates a Proxy instance of the original object passed as argument that is returned to the caller.
   * Each time the proxy instance is invoked, it will intercept the call and execute its logic.
   *
   * @param originalObject the original object to create a new PrologMethodHandler for
   * @tparam A the type of the original object
   * @return a new Proxy instance that intercepts method calls and executes its logic.
   */
  def create[A](originalObject: A): A

/**
 * Module that contains methods to intercept call of methods annotated with @PrologMethod an execute their logic
 */
object PrologMethodInterceptor extends Logging with Interceptor:
  /**
   * Creates a Proxy instance of the original object passed as argument that is returned to the caller.
   * Each time the proxy instance is invoked, it will intercept the method call and execute the annotated @PrologMethod
   * logic using the tuProlog engine, returning its result as the method result.
   * The body of the original method is ignored and the Prolog logic is executed instead.
   *
   * @param originalObject the original object to create a new PrologMethodHandler for
   * @tparam A the type of the original object
   * @return a new Proxy instance of the object passed as argument to this method
   */
  override def create[A](originalObject: A): A =
    logger.trace(s"creating a new PrologMethodHandler for the original object '$originalObject'...")
    val handler = PrologMethodHandler(originalObject)
    
    logger.trace("creating a new Proxy instance...")
    Proxy.newProxyInstance(originalObject.getClass.getClassLoader, originalObject.getClass.getInterfaces, handler).asInstanceOf[A]

/**
 * Handler for an object that intercepts any method call for methods annotated with @PrologMethod inside the
 * originalObject passed as argument and executes the logic expressed in the annotation instead of the originalObject's
 * annotated method body.
 *
 * @param originalObject the original object, methods calls of this object annotated with @PrologMethod are intercepted
 */
class PrologMethodHandler(originalObject: Any) extends InvocationHandler with Logging:
  /**
   * Intercepts the method call of the proxy instance and executes the logic of the annotated @PrologMethod method
   * instead of the original method body.
   *
   * @param proxy the proxy instance that intercepts the method call
   * @param method the method to invoke
   * @param args the arguments to pass to the method
   * @return the result of the method call
   */
  override def invoke(proxy: Any, method: Method, args: Array[AnyRef]): AnyRef =
    logger.debug(s"invoking method '${method.getName}' on proxy of original object '$originalObject'...")

    // if the method is annotated with @PrologMethod, execute the Prolog logic
    if method.isAnnotationPresent(classOf[PrologMethod]) then
      // extract the fields of the @PrologMethod annotation
      logger.trace("method is annotated with @PrologMethod, executing Prolog logic...")
      val prologMethodAnnotation = method.getAnnotation(classOf[PrologMethod])
      val fields = PrologMethodUtils.extractMethodFields(prologMethodAnnotation)

      // set the theory and solve the goal using tuProlog
      Scala2Prolog.setTheoryAndSolveGoal(fields, Option(args), method)

    // else, the method is not annotated with @PrologMethod, invoke the default method on the real object as if there was no proxy
    else
      logger.trace("method is not annotated with @PrologMethod, invoking the default method on the real object...")
      method.invoke(originalObject, args: _*)