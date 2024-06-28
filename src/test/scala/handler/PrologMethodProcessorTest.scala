package pps.exam.application
package handler

import annotation.*

import org.apache.logging.log4j.scala.Logging
import org.scalatest.matchers.should.Matchers

class PrologMethodProcessorTest extends AbstractAnnotationTest with Matchers with Logging:
  
  /* @PrologMethod method field 'signature' tests */

  "PrologMethodHandler" should :
    "return an empty signature from a @PrologMethod if the default empty method field parsed" in :
      val prologMethod = classOf[PrologMethodUtilsDeclarationTest].getMethod("testMethodSignature_default")
      val annotation = prologMethod.getAnnotation(classOf[PrologMethod])
      val actualSignature = PrologMethodProcessor(Option.empty).extractSignature(annotation)

      assert(actualSignature.isEmpty)

  "PrologMethodHandler" should :
    "extract the correct signature from a @PrologMethod if a single input variable is set" in :
      val prologMethod = classOf[PrologMethodUtilsDeclarationTest].getMethod("testMethodSignature_XY")
      val annotation = prologMethod.getAnnotation(classOf[PrologMethod])
      val actualSignature = PrologMethodProcessor(Option.empty).extractSignature(annotation)
      val expectedSignature = Signature("(X) -> {Y}")

      assert(actualSignature.get.inputVars === expectedSignature.get.inputVars)

  "PrologMethodHandler" should :
    "extract the correct signature from a @PrologMethod if a single output variable is set" in :
      val prologMethod = classOf[PrologMethodUtilsDeclarationTest].getMethod("testMethodSignature_XY")
      val annotation = prologMethod.getAnnotation(classOf[PrologMethod])
      val actualSignature = PrologMethodProcessor(Option.empty).extractSignature(annotation)
      val expectedSignature = Signature("(X) -> {Y}")

      assert(actualSignature.get.outputVars === expectedSignature.get.outputVars)

  "PrologMethodHandler" should :
    "extract the correct signature from a @PrologMethod if multiple input variables are set" in :
      val prologMethod = classOf[PrologMethodUtilsDeclarationTest].getMethod("testMethodSignature_XXXYY")
      val annotation = prologMethod.getAnnotation(classOf[PrologMethod])
      val actualSignature = PrologMethodProcessor(Option.empty).extractSignature(annotation)
      val expectedSignature = Signature("(Var1, VAR2, X3) -> {Y1, Variabile2}")

      assert(actualSignature.get.inputVars === expectedSignature.get.inputVars)

  "PrologMethodHandler" should :
    "extract the correct signature from a @PrologMethod if multiple output variables are set" in :
      val prologMethod = classOf[PrologMethodUtilsDeclarationTest].getMethod("testMethodSignature_XXXYY")
      val annotation = prologMethod.getAnnotation(classOf[PrologMethod])
      val actualSignature = PrologMethodProcessor(Option.empty).extractSignature(annotation)
      val expectedSignature = Signature("(Var1, VAR2, X3) -> {Y1, Variabile2}")

      assert(actualSignature.get.outputVars === expectedSignature.get.outputVars)

  "PrologMethodHandler" should :
    "throw an IllegalArgumentException when extracting the signature from a @PrologMethod if brackets are formatted incorrectly" in :
      val prologMethod = classOf[PrologMethodUtilsDeclarationTest].getMethod("testMethodSignature_incorrectFormatBrackets")
      val annotation = prologMethod.getAnnotation(classOf[PrologMethod])
      assertThrows[IllegalArgumentException](PrologMethodProcessor(Option.empty).extractSignature(annotation))

  "PrologMethodHandler" should :
    "throw an IllegalArgumentException when extracting the signature from a @PrologMethod if the arrow is formatted incorrectly" in :
      val prologMethod = classOf[PrologMethodUtilsDeclarationTest].getMethod("testMethodSignature_incorrectFormatArrow")
      val annotation = prologMethod.getAnnotation(classOf[PrologMethod])
      assertThrows[IllegalArgumentException](PrologMethodProcessor(Option.empty).extractSignature(annotation))

  "PrologMethodHandler" should :
    "throw an IllegalArgumentException when extracting the signature from a @PrologMethod if variables do not start with uppercase character" in :
      val prologMethod = classOf[PrologMethodUtilsDeclarationTest].getMethod("testMethodSignature_incorrectFormatVars")
      val annotation = prologMethod.getAnnotation(classOf[PrologMethod])
      assertThrows[IllegalArgumentException](PrologMethodProcessor(Option.empty).extractSignature(annotation))

  /* @PrologMethod method field 'types' tests */

  "PrologMethodHandler" should :
    "return an empty type from a @PrologMethod if the default empty method field parsed" in :
      val prologMethod = classOf[PrologMethodUtilsDeclarationTest].getMethod("testMethodTypes_default")
      val annotation = prologMethod.getAnnotation(classOf[PrologMethod])
      val actualType = PrologMethodProcessor(Option.empty).extractTypes(annotation)

      assert(actualType.isEmpty)

  "PrologMethodHandler" should :
    "extract the correct type from a @PrologMethod if a single type is set" in :
      val prologMethod = classOf[PrologMethodUtilsDeclarationTest].getMethod("testMethodTypes_IntInt")
      val annotation = prologMethod.getAnnotation(classOf[PrologMethod])
      val actualType = PrologMethodProcessor(Option.empty).extractTypes(annotation)
      val expectedType = Types(Array("Int", "Int"))

      assert(actualType.get.values === expectedType.get.values)

  "PrologMethodHandler" should :
    "extract the correct type from a @PrologMethod if a list type is set" in :
      val prologMethod = classOf[PrologMethodUtilsDeclarationTest].getMethod("testMethodTypes_ListIntListBoolean")
      val annotation = prologMethod.getAnnotation(classOf[PrologMethod])
      val actualType = PrologMethodProcessor(Option.empty).extractTypes(annotation)
      val expectedType = Types(Array("List[Int]", "List[Boolean]"))

      assert(actualType.get.values === expectedType.get.values)

  "PrologMethodHandler" should :
    "throw an IllegalArgumentException when trying to extract the type from a @PrologMethod if non valid type (List[List[...]]) is set" in :
      val prologMethod = classOf[PrologMethodUtilsDeclarationTest].getMethod("testMethodTypes_ListListInt")
      val annotation = prologMethod.getAnnotation(classOf[PrologMethod])
      assertThrows[IllegalArgumentException](PrologMethodProcessor(Option.empty).extractTypes(annotation))

  "PrologMethodHandler" should :
    "throw an IllegalArgumentException when trying to extract the type from a @PrologMethod if non valid type (Unit) is set" in :
      val prologMethod = classOf[PrologMethodUtilsDeclarationTest].getMethod("testMethodTypes_Unit")
      val annotation = prologMethod.getAnnotation(classOf[PrologMethod])
      assertThrows[IllegalArgumentException](PrologMethodProcessor(Option.empty).extractTypes(annotation))

  /* @PrologMethod method field 'predicate' tests */

  "PrologMethodHandler" should :
    "extract empty predicate from a @PrologMethod if default predicate is present" in :
      val prologMethod = classOf[PrologMethodUtilsDeclarationTest].getMethod("testMethodPredicate_default")
      val annotation = prologMethod.getAnnotation(classOf[PrologMethod])
      val actualPredicate = PrologMethodProcessor(Option.empty).extractPredicate(annotation)

      assert(actualPredicate.isEmpty)

  "PrologMethodHandler" should :
    "extract the correct predicate from a @PrologMethod if no predicate notation symbols are present" in :
      val prologMethod = classOf[PrologMethodUtilsDeclarationTest].getMethod("testMethodPredicate_noSymbols")
      val annotation = prologMethod.getAnnotation(classOf[PrologMethod])
      val actualPredicate = PrologMethodProcessor(Option.empty).extractPredicate(annotation)
      val expectedPredicate = Predicate("p(X1, X2, Y).")

      assert(actualPredicate.get === expectedPredicate.get)

  "PrologMethodHandler" should :
    "extract the correct predicate from a @PrologMethod if predicate notation symbols are present" in :
      val prologMethod = classOf[PrologMethodUtilsDeclarationTest].getMethod("testMethodPredicate_IO")
      val annotation = prologMethod.getAnnotation(classOf[PrologMethod])
      val actualPredicate = PrologMethodProcessor(Option.empty).extractPredicate(annotation)
      val expectedPredicate = Predicate("p(+X1, +X2, -Y).")

      assert(actualPredicate.get === expectedPredicate.get)
