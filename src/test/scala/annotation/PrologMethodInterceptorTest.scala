package pps.exam.application
package annotation

import alice.tuprolog.Term
import org.scalatest.matchers.should.Matchers

import java.lang.reflect.UndeclaredThrowableException

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

  @PrologMethod()
  def testMethodTypes_default(): Unit

  @PrologMethod(types = Array("Int", "Int"))
  def testMethodTypes_IntInt(): Unit

  @PrologMethod(types = Array("List[Int]", "List[Boolean]"))
  def testMethodTypes_ListIntListBoolean(): Unit

  @PrologMethod(types = Array("List[List[Int]]"))
  def testMethodTypes_ListListInt(): Unit

  @PrologMethod(types = Array("Unit"))
  def testMethodTypes_Unit(): Unit

  @PrologMethod()
  def testMethodPredicate_default(): Unit

  @PrologMethod(predicate = "p(X1, X2, Y).")
  def testMethodPredicate_noSymbols(): Unit

  @PrologMethod(predicate = "p(+X1, +X2, -Y).")
  def testMethodPredicate_IO(): Unit

  @PrologMethod(predicate = "p(X).", clauses = Array("p(a)."))
  def testMethodPredicateClauses_Xa(): LazyList[Term]

  @PrologMethod(predicate = "p(+X).", clauses = Array("p(a)."))
  def testMethodPredicateClauses_plusXa(): LazyList[Term]

  @PrologMethod(predicate = "p(-X).", clauses = Array("p(a)."))
  def testMethodPredicateClauses_minusXa(): LazyList[Term]

  @PrologMethod(predicate = "p(-X).", clauses = Array("p(a). p(b). p(c)."))
  def testMethodPredicateClauses_minusXabc(): LazyList[Term]

class TestPrologMethodImpl extends TestPrologMethod:
  def testMethodSignature_default(): Unit = ()

  def testMethodSignature_XY(): Unit = ()

  def testMethodSignature_XXXYY(): Unit = ()

  def testMethodSignature_incorrectFormatBrackets(): Unit = ()

  def testMethodSignature_incorrectFormatArrow(): Unit = ()

  def testMethodSignature_incorrectFormatVars(): Unit = ()

  def testMethodTypes_default(): Unit = ()

  def testMethodTypes_IntInt(): Unit = ()

  def testMethodTypes_ListIntListBoolean(): Unit = ()

  def testMethodTypes_ListListInt(): Unit = ()

  def testMethodTypes_Unit(): Unit = ()

  def testMethodPredicate_default(): Unit = ()

  def testMethodPredicate_noSymbols(): Unit = ()

  def testMethodPredicate_IO(): Unit = ()

  def testMethodPredicateClauses_Xa(): LazyList[Term] = null

  def testMethodPredicateClauses_plusXa(): LazyList[Term] = null

  def testMethodPredicateClauses_minusXa(): LazyList[Term] = null

  def testMethodPredicateClauses_minusXabc(): LazyList[Term] = null

class PrologMethodUtilsTest extends AbstractAnnotationTest with Matchers:

  import PrologMethodUtils.*

  /* @PrologMethod method field 'signature' tests */

  "PrologMethodUtils" should :
    "return an empty signature from a @PrologMethod if the default empty method field parsed" in :
      val prologMethod = classOf[TestPrologMethod].getMethod("testMethodSignature_default")
      val annotation = prologMethod.getAnnotation(classOf[PrologMethod])
      val actualSignature = extractSignature(annotation)
      val expectedSignature = Signatures(Array.empty, Array.empty)

      assert(actualSignature.inputVars === expectedSignature.inputVars)
      assert(actualSignature.outputVars === expectedSignature.outputVars)

  "PrologMethodUtils" should :
    "extract the correct signature from a @PrologMethod if a single input and output variables are set" in :
      val prologMethod = classOf[TestPrologMethod].getMethod("testMethodSignature_XY")
      val annotation = prologMethod.getAnnotation(classOf[PrologMethod])
      val actualSignature = extractSignature(annotation)
      val expectedSignature = Signatures(Array("X"), Array("Y"))

      assert(actualSignature.inputVars === expectedSignature.inputVars)
      assert(actualSignature.outputVars === expectedSignature.outputVars)

  "PrologMethodUtils" should :
    "extract the correct signature from a @PrologMethod if multiple input and output variables are set" in :
      val prologMethod = classOf[TestPrologMethod].getMethod("testMethodSignature_XXXYY")
      val annotation = prologMethod.getAnnotation(classOf[PrologMethod])
      val actualSignature = extractSignature(annotation)
      val expectedSignature = Signatures(Array("Var1", "VAR2", "X3"), Array("Y1", "Variabile2"))

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

  /* @PrologMethod method field 'types' tests */

  "PrologMethodUtils" should :
    "return an empty type from a @PrologMethod if the default empty method field parsed" in :
      val prologMethod = classOf[TestPrologMethod].getMethod("testMethodTypes_default")
      val annotation = prologMethod.getAnnotation(classOf[PrologMethod])
      val actualType = extractTypes(annotation)
      val expectedType = Types(Array.empty)

      assert(actualType.values === expectedType.values)

  "PrologMethodUtils" should :
    "extract the correct type from a @PrologMethod if a single type is set" in :
      val prologMethod = classOf[TestPrologMethod].getMethod("testMethodTypes_IntInt")
      val annotation = prologMethod.getAnnotation(classOf[PrologMethod])
      val actualType = extractTypes(annotation)
      val expectedType = Types(Array("Int", "Int"))

      assert(actualType.values === expectedType.values)

  "PrologMethodUtils" should :
    "extract the correct type from a @PrologMethod if a list type is set" in :
      val prologMethod = classOf[TestPrologMethod].getMethod("testMethodTypes_ListIntListBoolean")
      val annotation = prologMethod.getAnnotation(classOf[PrologMethod])
      val actualType = extractTypes(annotation)
      val expectedType = Types(Array("List[Int]", "List[Boolean]"))

      assert(actualType.values === expectedType.values)

  "PrologMethodUtils" should :
    "throw an IllegalArgumentException when trying to extract the type from a @PrologMethod if non valid type (List[List[...]]) is set" in :
      val prologMethod = classOf[TestPrologMethod].getMethod("testMethodTypes_ListListInt")
      val annotation = prologMethod.getAnnotation(classOf[PrologMethod])
      assertThrows[IllegalArgumentException](extractTypes(annotation))

  "PrologMethodUtils" should :
    "throw an IllegalArgumentException when trying to extract the type from a @PrologMethod if non valid type (Unit) is set" in :
      val prologMethod = classOf[TestPrologMethod].getMethod("testMethodTypes_Unit")
      val annotation = prologMethod.getAnnotation(classOf[PrologMethod])
      assertThrows[IllegalArgumentException](extractTypes(annotation))

  /* @PrologMethod method field 'predicate' tests */

  "PrologMethodUtils" should :
    "extract empty predicate from a @PrologMethod if default predicate is present" in :
      val prologMethod = classOf[TestPrologMethod].getMethod("testMethodPredicate_default")
      val annotation = prologMethod.getAnnotation(classOf[PrologMethod])
      val actualPredicate = extractPredicate(annotation)
      val expectedPredicate = Predicate("", Map.empty)

      assert(actualPredicate.name === expectedPredicate.name)
      assert(actualPredicate.values === expectedPredicate.values)

  "PrologMethodUtils" should :
    "extract the correct predicate from a @PrologMethod if no predicate notation symbols are present" in :
      val prologMethod = classOf[TestPrologMethod].getMethod("testMethodPredicate_noSymbols")
      val annotation = prologMethod.getAnnotation(classOf[PrologMethod])
      val actualPredicate = extractPredicate(annotation)
      val expectedPredicate = Predicate("p", Map("+" -> Array("X1", "X2"), "-" -> Array("Y")))

      assert(actualPredicate.name === expectedPredicate.name)
      assert(actualPredicate.values("+") === expectedPredicate.values("+"))
      assert(actualPredicate.values("-") === expectedPredicate.values("-"))

  "PrologMethodUtils" should :
    "extract the correct predicate from a @PrologMethod if predicate notation symbols are present" in :
      val prologMethod = classOf[TestPrologMethod].getMethod("testMethodPredicate_IO")
      val annotation = prologMethod.getAnnotation(classOf[PrologMethod])
      val actualPredicate = extractPredicate(annotation)
      val expectedPredicate = Predicate("p", Map("+" -> Array("X1", "X2"), "-" -> Array("Y")))

      assert(actualPredicate.name === expectedPredicate.name)
      assert(actualPredicate.values("+") === expectedPredicate.values("+"))
      assert(actualPredicate.values("-") === expectedPredicate.values("-"))

  "PrologMethodUtils" should :
    "evaluate correctly the prolog goal 'p(X).' against theory 'p(a).'" in :
      val proxy = PrologMethodInterceptor.create(TestPrologMethodImpl().asInstanceOf[TestPrologMethod])
      assert(proxy.testMethodPredicateClauses_Xa() === Iterable(Term.createTerm("p(a)")))

  "PrologMethodUtils" should :
    "evaluate correctly the prolog goal 'p(-X).' against theory 'p(a).'" in :
      val proxy = PrologMethodInterceptor.create(TestPrologMethodImpl().asInstanceOf[TestPrologMethod])
      assert(proxy.testMethodPredicateClauses_minusXa() === Iterable(Term.createTerm("p(a)")))

  "PrologMethodUtils" should :
    "throw an UndeclaredThrowableException when trying to solve the goal 'p(+X).' against theory 'p(a).'" in :
      val proxy = PrologMethodInterceptor.create(TestPrologMethodImpl().asInstanceOf[TestPrologMethod])
      assertThrows[UndeclaredThrowableException](proxy.testMethodPredicateClauses_plusXa())

  "PrologMethodUtils" should :
    "evaluate correctly the prolog goal 'p(-X).' against theory 'p(a). p(b). p(c).'" in :
      val proxy = PrologMethodInterceptor.create(TestPrologMethodImpl().asInstanceOf[TestPrologMethod])
      assert(proxy.testMethodPredicateClauses_minusXabc() === Iterable(Term.createTerm("p(a)"), Term.createTerm("p(b)"), Term.createTerm("p(c)")))