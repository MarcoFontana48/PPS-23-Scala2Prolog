package pps.exam.application
package scala2prolog.interceptor

import org.apache.logging.log4j.scala.Logging

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
