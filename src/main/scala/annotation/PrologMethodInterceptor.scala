package pps.exam.application
package annotation

import org.apache.logging.log4j.scala.Logging
import alice.tuprolog.*

import java.lang.reflect.{InvocationHandler, Method, Proxy}

trait MethodInterceptor:
  /**
   * Creates a Proxy instance of the original object passed as argument that is returned to the caller.
   * Each time the proxy instance is invoked, it will intercept the method call and execute its logic.
   *
   * @param originalObject the original object to create a new PrologMethodHandler for
   * @tparam A the type of the original object
   * @return a new Proxy instance that intercepts method calls and executes its logic.
   */
  def create[A](originalObject: A): A

trait PrologMethodUtils extends Logging:

  def extractPredicate(prologMethod: PrologMethod): Predicate = Predicate(prologMethod.predicate())

  def extractClauses(prologMethod: PrologMethod): Clauses = Clauses(prologMethod.clauses())

  def extractTypes(prologMethod: PrologMethod): Types =
    prologMethod.types() match
      case types if types.isEmpty =>
        logger.trace("types is empty, returning default types...")
        Types(Array.empty)
      case types =>
        logger.trace(s"extracted types from @PrologMethod annotation: '${types.mkString("Array(", ", ", ")")}'")
        types.foreach { e => if !e.matches("(Int|Double|String|Boolean|List\\[\\s*(Int|Double|String|Boolean)\\s*])") then throw new IllegalArgumentException(s"Invalid type: '$e'. Valid types are 'Int', 'String', 'Boolean', 'List[Int]', 'List[String]', 'List[Boolean]'") }
        Types(types)

  def extractSignature(prologMethod: PrologMethod): Signatures =
    prologMethod.signature() match
      case signature if signature.isEmpty =>
        logger.trace("signature is empty, returning default signature...")
        Signatures(Array.empty, Array.empty)
      case signature =>
        logger.trace(s"extracted signature from @PrologMethod annotation: '$signature', extracting input and output variables...")

        /* pattern: (X1,X2,..Xn) -> {Y1,Y2,..Yn} */
        val pattern = "\\(([A-Z]\\w*(,\\s*[A-Z]\\w*)*)\\)\\s*->\\s*\\{([A-Z]\\w*(,\\s*[A-Z]\\w*)*)}".r
        val matchOption = pattern.findFirstMatchIn(signature)

        matchOption match
          case Some(m) =>
            val inputVarsGroupIndex = 1
            val outputVarsGroupIndex = 3
            val inputVars = m.group(inputVarsGroupIndex).split(",").map(_.trim)
            val outputVars = m.group(outputVarsGroupIndex).split(",").map(_.trim)
            logger.trace(s"extracted input and output variables from signature: 'input=${inputVars.mkString("Array(", ", ", ")")}', 'output=${outputVars.mkString("Array(", ", ", ")")}'")
            Signatures(inputVars, outputVars)
          case None =>
            throw new IllegalArgumentException(s"Signature '$signature' is not formatted correctly")

/**
 * Module that contains methods to intercept call of methods annotated with @PrologMethod an execute their logic
 */
object PrologMethodInterceptor extends Logging with MethodInterceptor:
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

class PrologMethodHandler(originalObject: Any) extends PrologMethodUtils with InvocationHandler:
  override def invoke(proxy: Any, method: Method, args: Array[AnyRef]): AnyRef =
    logger.debug(s"invoking method '${method.getName}' on proxy of original object '$originalObject'...")
    if method.isAnnotationPresent(classOf[PrologMethod]) then
      logger.trace("method is annotated with @PrologMethod")
      val annotation = method.getAnnotation(classOf[PrologMethod])
      val signatures = extractSignature(annotation)
      val predicate = extractPredicate(annotation)
      val clauses = extractClauses(annotation)
      val types = extractTypes(annotation)
      logger.trace("extracted @PrologMethod fields:" +
        s"\nextracted signatures: '${signatures.inputVars.mkString("Array(", ", ", ")")}'->'${signatures.outputVars.mkString("Array(", ", ", ")")}'" +
        s"\nextracted permutation: '${predicate.predicate}'" +
        s"\nextracted types: '${types.types.mkString("Array(", ", ", ")")}'" +
        s"\nextracted clauses: '${clauses.clauses.mkString("Array(", ", ", ")")}'"
      )
    else {
      logger.trace("method is not annotated with @PrologMethod, skipping annotation extraction...")
    }

    logger.trace(s"invoking the original method ${method.getName} on the original object $originalObject...")
    val result = method.invoke(originalObject, args: _*)
    logger.trace(s"method invocation completed with result '$result', returning the result...")
    result