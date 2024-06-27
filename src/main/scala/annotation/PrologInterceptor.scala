package pps.exam.application
package annotation

import org.apache.logging.log4j.scala.Logging

import java.lang.reflect.Proxy

/**
 * Trait to mixin to give the extended class the property to create a Proxy of an object to intercept method calls
 */
trait Interceptor extends Logging:
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
object PrologInterceptor extends Interceptor:
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
    logger.trace(s"creating a new handler for the original object '$originalObject'...")
    val handler = PrologHandlerManager(originalObject)

    logger.trace("creating a new Proxy instance...")
    Proxy.newProxyInstance(originalObject.getClass.getClassLoader, originalObject.getClass.getInterfaces, handler).asInstanceOf[A]