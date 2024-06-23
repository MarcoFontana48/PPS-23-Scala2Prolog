package pps.exam.application
package annotation

import alice.tuprolog.*
import alice.tuprolog.exceptions.InvalidTermException
import org.apache.logging.log4j.scala.Logging

import java.lang.reflect.{InvocationHandler, Method, Proxy}
import scala.util.{Failure, Success}

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
  override def invoke(proxy: Any, method: Method, args: Array[AnyRef]): AnyRef =
    logger.debug(s"invoking method '${method.getName}' on proxy of original object '$originalObject'...")
    if method.isAnnotationPresent(classOf[PrologMethod]) then
      logger.trace("method is annotated with @PrologMethod, executing Prolog logic...")
      val annotation = method.getAnnotation(classOf[PrologMethod])
      import PrologMethodUtils.*
      val fields = extractMethodFields(annotation)
      val predicate = fields("predicate").asInstanceOf[Predicate]
      val clauses = fields("clauses").asInstanceOf[Clauses]
      val terms = Scala2Prolog.setTheoryAndSolveGoal(predicate, clauses)
      terms
    else
      method.invoke(originalObject, args: _*)