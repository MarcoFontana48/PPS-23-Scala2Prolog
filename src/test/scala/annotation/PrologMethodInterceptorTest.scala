package pps.exam.application
package annotation

import org.scalatest.matchers.should.Matchers

trait TestPrologMethod:
  @PrologMethod(signature = "(X) -> {Y}")
  def testMethodSignature_XY(): Unit

  @PrologMethod(signature = "(  X1,   X2,X3 )   -> {Y1  , Y2}")
  def testMethodSignature_XXY(): Unit


class PrologMethodUtilsTest extends AbstractAnnotationTest with Matchers:
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
      val prologMethod = classOf[TestPrologMethod].getMethod("testMethodSignature_XXY")
      val annotation = prologMethod.getAnnotation(classOf[PrologMethod])
      val actualSignature = extractSignature(annotation)
      val expectedSignature = PrologMethodSignature(Array("X1","X2","X3"), Array("Y1","Y2"))

      assert(actualSignature.inputVars === expectedSignature.inputVars)
      assert(actualSignature.outputVars === expectedSignature.outputVars)


