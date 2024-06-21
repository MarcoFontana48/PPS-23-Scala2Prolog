package pps.exam.application
package annotation

import org.apache.logging.log4j.scala.Logging

import java.lang.reflect.{InvocationHandler, Method, Proxy}

trait MethodInterceptor:
  def create[A](originalObject: A): A

trait PrologMethodUtils extends Logging:
  def extractSignature(prologMethod: PrologMethod): PrologMethodSignature =
    val signature = prologMethod.signature()
    logger.trace(s"extracted signature from @PrologMethod annotation: '$signature', extracting input and output variables...")
    val input_vars = signature.split("->").head.trim.stripPrefix("(").stripSuffix(")").split(",").map(_.trim)
    val output_vars = signature.split("->").last.trim.stripPrefix("{").stripSuffix("}").split(",").map(_.trim)
    logger.trace(s"extracted input and output variables from signature: 'input=${input_vars.mkString("Array(", ", ", ")")}', 'output=${output_vars.mkString("Array(", ", ", ")")}'")
    PrologMethodSignature(input_vars, output_vars)

case class PrologMethodSignature(inputVars: Array[String], outputVars: Array[String])

object PrologMethodInterceptor extends Logging with MethodInterceptor:
  override def create[A](originalObject: A): A =
    logger.trace(s"creating a new PrologMethodHandler for the original object '$originalObject'...")
    val handler = new PrologMethodHandler(originalObject)
    logger.trace("creating a new Proxy instance...")
    Proxy.newProxyInstance(originalObject.getClass.getClassLoader, originalObject.getClass.getInterfaces, handler).asInstanceOf[A]

class PrologMethodHandler(originalObject: Any) extends PrologMethodUtils with InvocationHandler:
  override def invoke(proxy: Any, method: Method, args: Array[AnyRef]): AnyRef =
    logger.debug(s"invoking method '${method.getName}' on proxy of original object '$originalObject'...")
    if method.isAnnotationPresent(classOf[PrologMethod]) then
      logger.trace("method is annotated with @PrologMethod")
      val annotation = method.getAnnotation(classOf[PrologMethod])
      val signatureValues = extractSignature(annotation)
      logger.trace(s"extracted signature values: '${signatureValues.inputVars}'->'${signatureValues.outputVars}'")
    else {
      logger.trace("method is not annotated with @PrologMethod, skipping annotation extraction...")
    }

    logger.trace(s"invoking the original method ${method.getName} on the original object $originalObject...")
    val result = method.invoke(originalObject, args: _*)
    logger.trace(s"method invocation completed with result '$result', returning the result...")
    result