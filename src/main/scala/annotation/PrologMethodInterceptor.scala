package pps.exam.application
package annotation

import alice.tuprolog.*
import alice.tuprolog.exceptions.InvalidTermException
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
  override def invoke(proxy: Any, method: Method, args: Array[AnyRef]): AnyRef =
    logger.debug(s"invoking method '${method.getName}' on proxy of original object '$originalObject'...")
    if method.isAnnotationPresent(classOf[PrologMethod]) then
      logger.trace("method is annotated with @PrologMethod")
      val annotation = method.getAnnotation(classOf[PrologMethod])
      import PrologMethodUtils.extractMethodFields
      val fields = extractMethodFields(annotation)
      val engine = Prolog()
      val predicateString = fields("predicate").asInstanceOf[Predicate].formatPredicate() //TODO: format args and pass them as input to getFormattedPredicate method, they have to replace the "+" variables in the predicate
      val signatureString = fields("signatures").asInstanceOf[Signatures].inputVars.mkString(", ")
      val typesString = fields("types").asInstanceOf[Types].types.mkString(" ")
      val clausesString = fields("clauses").asInstanceOf[Clauses].clauses.mkString(" ")
      logger.trace(s"pred, sign, types, clauses: '" + predicateString + "', '" + signatureString + "', '" + typesString + "', '" + clausesString + "'...")
      logger.trace(s"setting theory to the Prolog engine with clauses: '$clausesString'...")
      engine.setTheory(Theory(clausesString))
      logger.trace(s"querying the Prolog engine with predicate: '$predicateString'...")
      val solveInfo = engine.solve(predicateString)
      logger.trace(s"query completed with solveInfo result:\n$solveInfo")
      val solutionString = solveInfo.getSolution.toString
      logger.trace(s"returning result '$solutionString'...")
      solutionString
    else {
      logger.trace("method is not annotated with @PrologMethod, skipping annotation extraction and invoking original method instead...")
      logger.trace(s"invoking the original method ${method.getName} on the original object $originalObject...")
      val result = method.invoke(originalObject, args: _*)
      logger.trace(s"method invocation completed with result '$result', returning the result...")
      result
    }
