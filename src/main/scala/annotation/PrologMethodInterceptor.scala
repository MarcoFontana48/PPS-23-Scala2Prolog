package pps.exam.application
package annotation

import org.apache.logging.log4j.scala.Logging

import java.lang.reflect.{InvocationHandler, Method, Proxy}

trait MethodInterceptor:
  def create[A](originalObject: A): A

object PrologMethodInterceptor extends Logging with MethodInterceptor:
  override def create[A](originalObject: A): A =
    logger.trace(s"creating a new PrologMethodHandler for the original object '$originalObject'...")
    val handler = new PrologMethodHandler(originalObject)
    logger.trace("creating a new Proxy instance...")
    Proxy.newProxyInstance(originalObject.getClass.getClassLoader, originalObject.getClass.getInterfaces, handler).asInstanceOf[A]

class PrologMethodHandler(originalObject: Any) extends InvocationHandler with Logging:
  override def invoke(proxy: Any, method: Method, args: Array[AnyRef]): AnyRef =
    logger.debug(s"invoking method '${method.getName}' on proxy of original object '$originalObject'...")
    if method.isAnnotationPresent(classOf[PrologMethod]) then
      logger.trace("method is annotated with @PrologMethod")
      val annotation = method.getAnnotation(classOf[PrologMethod])
      logger.trace(s"\nextracted annotation method field values:\n\t'predicate=${annotation.predicate()}';\n\t'types=${annotation.types()}';\n\t'clauses=${annotation.clauses()}';\n\t'signature=${annotation.signature()}'")
    else {
      logger.trace("method is not annotated with @PrologMethod")
    }

    logger.trace(s"invoking the original method ${method.getName} on the original object $originalObject...")
    val result = method.invoke(originalObject, args: _*)
    logger.trace(s"method invocation completed with result '$result', returning the result...")
    result
