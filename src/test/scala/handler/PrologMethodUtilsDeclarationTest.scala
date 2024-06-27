package pps.exam.application
package handler

import annotation.PrologMethod

trait PrologMethodUtilsDeclarationTest:

  @PrologMethod()
  def testMethodSignature_default(): Unit = ()

  @PrologMethod(signature = "(X) -> {Y}")
  def testMethodSignature_XY(): Unit = ()

  @PrologMethod(signature = "(Var1, VAR2, X3) -> {Y1, Variabile2}")
  def testMethodSignature_XXXYY(): Unit = ()

  @PrologMethod(signature = "{X1, X2, X3} -> (Y1, Y2)")
  def testMethodSignature_incorrectFormatBrackets(): Unit = ()

  @PrologMethod(signature = "{X1, X2, X3} => (Y1, Y2)")
  def testMethodSignature_incorrectFormatArrow(): Unit = ()

  @PrologMethod(signature = "{X1, vARIABILE2, X3} -> (Y1, VAR2)")
  def testMethodSignature_incorrectFormatVars(): Unit = ()

  @PrologMethod()
  def testMethodTypes_default(): Unit = ()

  @PrologMethod(types = Array("Int", "Int"))
  def testMethodTypes_IntInt(): Unit = ()

  @PrologMethod(types = Array("List[Int]", "List[Boolean]"))
  def testMethodTypes_ListIntListBoolean(): Unit = ()

  @PrologMethod(types = Array("List[List[Int]]"))
  def testMethodTypes_ListListInt(): Unit = ()

  @PrologMethod(types = Array("Unit"))
  def testMethodTypes_Unit(): Unit = ()

  @PrologMethod()
  def testMethodPredicate_default(): Unit = ()

  @PrologMethod(predicate = "p(X1, X2, Y).")
  def testMethodPredicate_noSymbols(): Unit = ()

  @PrologMethod(predicate = "p(+X1, +X2, -Y).")
  def testMethodPredicate_IO(): Unit = ()

class PrologMethodUtilsDeclarationTestImpl extends PrologMethodUtilsDeclarationTest
