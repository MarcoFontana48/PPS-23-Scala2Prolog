package pps.exam.application
package annotation

import org.scalatest.matchers.should.Matchers

trait TestPrologMethod:
  @PrologMethod()
  def testMethodSignature_default(): Unit

  @PrologMethod(signature = "(X) -> {Y}")
  def testMethodSignature_XY(): Unit

  @PrologMethod(signature = "(Var1, VAR2, X3) -> {Y1, Variabile2}")
  def testMethodSignature_XXXYY(): Unit

  @PrologMethod(signature = "{X1, X2, X3} -> (Y1, Y2)")
  def testMethodSignature_incorrectFormatBrackets(): Unit

  @PrologMethod(signature = "{X1, X2, X3} => (Y1, Y2)")
  def testMethodSignature_incorrectFormatArrow(): Unit

  @PrologMethod(signature = "{X1, vARIABILE2, X3} -> (Y1, VAR2)")
  def testMethodSignature_incorrectFormatVars(): Unit

class PrologMethodUtilsTest extends AbstractAnnotationTest with Matchers:
  "PrologMethodUtils" should :
    "return an empty signature from a @PrologMethod if the default empty method field parsed" in :
      val prologMethod = classOf[TestPrologMethod].getMethod("testMethodSignature_default")
      val annotation = prologMethod.getAnnotation(classOf[PrologMethod])
      val actualSignature = extractSignature(annotation)
      val expectedSignature = PrologMethodSignature(Array.empty, Array.empty)

      assert(actualSignature.inputVars === expectedSignature.inputVars)
      assert(actualSignature.outputVars === expectedSignature.outputVars)

  "PrologMethodUtils" should :
    "extract the correct signature from a @PrologMethod if a single input and output variables are set" in :
      val prologMethod = classOf[TestPrologMethod].getMethod("testMethodSignature_XY")
      val annotation = prologMethod.getAnnotation(classOf[PrologMethod])
      val actualSignature = extractSignature(annotation)
      val expectedSignature = PrologMethodSignature(Array("X"), Array("Y"))

      assert(actualSignature.inputVars === expectedSignature.inputVars)
      assert(actualSignature.outputVars === expectedSignature.outputVars)

  "PrologMethodUtils" should :
    "extract the correct signature from a @PrologMethod if multiple input and output variables are set" in :
      val prologMethod = classOf[TestPrologMethod].getMethod("testMethodSignature_XXXYY")
      val annotation = prologMethod.getAnnotation(classOf[PrologMethod])
      val actualSignature = extractSignature(annotation)
      val expectedSignature = PrologMethodSignature(Array("Var1", "VAR2", "X3"), Array("Y1", "Variabile2"))

      assert(actualSignature.inputVars === expectedSignature.inputVars)
      assert(actualSignature.outputVars === expectedSignature.outputVars)

  "PrologMethodUtils" should :
    "throw an IllegalArgumentException when extracting the signature from a @PrologMethod if brackets are formatted incorrectly" in :
      val prologMethod = classOf[TestPrologMethod].getMethod("testMethodSignature_incorrectFormatBrackets")
      val annotation = prologMethod.getAnnotation(classOf[PrologMethod])
      assertThrows[IllegalArgumentException](extractSignature(annotation))

  "PrologMethodUtils" should :
    "throw an IllegalArgumentException when extracting the signature from a @PrologMethod if the arrow is formatted incorrectly" in :
      val prologMethod = classOf[TestPrologMethod].getMethod("testMethodSignature_incorrectFormatArrow")
      val annotation = prologMethod.getAnnotation(classOf[PrologMethod])
      assertThrows[IllegalArgumentException](extractSignature(annotation))

  "PrologMethodUtils" should :
    "throw an IllegalArgumentException when extracting the signature from a @PrologMethod if variables do not start with uppercase character" in :
      val prologMethod = classOf[TestPrologMethod].getMethod("testMethodSignature_incorrectFormatVars")
      val annotation = prologMethod.getAnnotation(classOf[PrologMethod])
      assertThrows[IllegalArgumentException](extractSignature(annotation))



