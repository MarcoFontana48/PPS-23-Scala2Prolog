package pps.exam.application
package annotation

import org.apache.logging.log4j.scala.Logging

import java.lang.reflect.{InvocationHandler, Method, Proxy}

trait MethodInterceptor:
  def create[A](originalObject: A): A

trait PrologMethodUtils extends Logging:

  def extractTypes(prologMethod: PrologMethod): Type =
    prologMethod.types() match
      case types if types.isEmpty =>
        logger.trace("types is empty, returning default types...")
        Type(Array.empty)
      case types =>
        logger.trace(s"extracted types from @PrologMethod annotation: '${types.mkString("Array(", ", ", ")")}'")
        types.foreach{ e => if !e.matches("(Int|String|Boolean|List\\[\\s*(Int|String|Boolean)\\s*])") then throw new IllegalArgumentException(s"Invalid type: '$e'. Valid types are 'Int', 'String', 'Boolean', 'List[Int]', 'List[String]', 'List[Boolean]'") }
        Type(types)
  
  def extractSignature(prologMethod: PrologMethod): Signature =
    prologMethod.signature() match
      case signature if signature.isEmpty =>
        logger.trace("signature is empty, returning default signature...")
        Signature(Array.empty, Array.empty)
      case signature =>
        logger.trace(s"extracted signature from @PrologMethod annotation: '$signature', extracting input and output variables...")

        /* pattern: (X1,X2,..Xn) -> {Y1,Y2,..Yn} */
        val pattern = "\\(([A-Z]\\w*(,\\s*[A-Z]\\w*)*)\\)\\s*->\\s*\\{([A-Z]\\w*(,\\s*[A-Z]\\w*)*)}".r
        val matchOption = pattern.findFirstMatchIn(signature)

        matchOption match
          case Some(m) =>
            val input_vars = m.group(1).split(",").map(_.trim)
            val output_vars = m.group(3).split(",").map(_.trim)
            logger.trace(s"extracted input and output variables from signature: 'input=${input_vars.mkString("Array(", ", ", ")")}', 'output=${output_vars.mkString("Array(", ", ", ")")}'")
            Signature(input_vars, output_vars)
          case None =>
            throw new IllegalArgumentException(s"Signature '$signature' is not formatted correctly")

object PrologMethodInterceptor extends Logging with MethodInterceptor:
  override def create[A](originalObject: A): A =
    logger.trace(s"creating a new PrologMethodHandler for the original object '$originalObject'...")
    val handler = PrologMethodHandler(originalObject)
    logger.trace("creating a new Proxy instance...")
    Proxy.newProxyInstance(originalObject.getClass.getClassLoader, originalObject.getClass.getInterfaces, handler).asInstanceOf[A]

class PrologMethodHandler(originalObject: Any) extends PrologMethodUtils with InvocationHandler:
  override def invoke(proxy: Any, method: Method, args: Array[AnyRef]): AnyRef =
    logger.debug(s"invoking method '${method.getName}' on proxy of original object '$originalObject'...")
    if method.isAnnotationPresent(classOf[PrologMethod]) then
      logger.trace("method is annotated with @PrologMethod")
      val annotation = method.getAnnotation(classOf[PrologMethod])
      val signatureValues = extractSignature(annotation)
      logger.trace(s"extracted signature values: '${signatureValues.inputVars.mkString("Array(", ", ", ")")}'->'${signatureValues.outputVars.mkString("Array(", ", ", ")")}'")
    else {
      logger.trace("method is not annotated with @PrologMethod, skipping annotation extraction...")
    }

    logger.trace(s"invoking the original method ${method.getName} on the original object $originalObject...")
    val result = method.invoke(originalObject, args: _*)
    logger.trace(s"method invocation completed with result '$result', returning the result...")
    result