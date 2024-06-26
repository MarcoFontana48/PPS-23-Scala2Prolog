package pps.exam.application
package annotation

import alice.tuprolog.Term
import org.apache.logging.log4j.scala.Logging
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
  def testMethodPredicateClauses_Xa(s: String): Iterable[Term]

  @PrologMethod(predicate = "p(+X).", clauses = Array("p(a)."))
  def testMethodPredicateClauses_plusXa(s: String): Iterable[Term]

  @PrologMethod(predicate = "p(-X).", clauses = Array("p(a)."))
  def testMethodPredicateClauses_minusXa(s: String): Iterable[Term]

  @PrologMethod(predicate = "p(-X).", clauses = Array("p(a). p(b). p(c)."))
  def testMethodPredicateClauses_minusXabc(s: String): Iterable[Term]

  @PrologMethod(predicate = "p(+X, -Y).", clauses = Array("p(X, X)."))
  def testMethodPredicateClauses_A(list: List[Int]): Iterable[Term]

  @PrologMethod(predicate = "p(+X, -Y).", clauses = Array("p(X, X). p(A, A)."))
  def testMethodPredicateClauses_B(list: List[Int]): Iterable[Term]

  @PrologMethod(
    predicate = "permutation(+X,-Y)",
    clauses = Array(
      "any([X|Xs],X,Xs).",
      "any([X|Xs],E,[X|Ys]):-any(Xs,E,Ys).",
      "permutation([],[]).",
      "permutation(Xs,[X|Ys]):-any(Xs,X,Zs), permutation(Zs,Ys).")
  )
  def testMethodPredicatePermutations_A(list: List[Int]): Iterable[Term]

  @PrologMethod(
    predicate = "permutation(+X,-Y)",
    signature = "(X) -> {Y}",
    types = Array("List[Int]","List[Int]"),
    clauses = Array(
      "any([X|Xs],X,Xs).",
      "any([X|Xs],E,[X|Ys]):-any(Xs,E,Ys).",
      "permutation([],[]).",
      "permutation(Xs,[X|Ys]):-any(Xs,X,Zs), permutation(Zs,Ys).")
  )
  def testMethodPredicatePermutations_B(list: List[Int]): Iterable[List[Int]]

  @PrologMethod(
    predicate = "permutation(+X,-Y)",
    signature = "(X) -> {Y}",
    types = Array("List[String]","List[String]"),
    clauses = Array(
      "any([X|Xs],X,Xs).",
      "any([X|Xs],E,[X|Ys]):-any(Xs,E,Ys).",
      "permutation([],[]).",
      "permutation(Xs,[X|Ys]):-any(Xs,X,Zs), permutation(Zs,Ys).")
  )
  def testMethodPredicatePermutations_C(list: List[String]): Iterable[List[String]]

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

  def testMethodPredicateClauses_Xa(s: String): LazyList[Term] = null // body of this method won't be used

  def testMethodPredicateClauses_plusXa(s: String): List[Term] = null // body of this method won't be used

  def testMethodPredicateClauses_minusXa(s: String): Seq[Term] = null  // body of this method won't be used

  def testMethodPredicateClauses_minusXabc(s: String): Iterable[Term] = null  // body of this method won't be used

  def testMethodPredicateClauses_A(list: List[Int]): Iterable[Term] = null  // body of this method won't be used

  def testMethodPredicateClauses_B(list: List[Int]): Iterable[Term] = null  // body of this method won't be used

  def testMethodPredicatePermutations_A(list: List[Int]): Iterable[Term] = null  // body of this method won't be used

  def testMethodPredicatePermutations_B(list: List[Int]): Iterable[List[Int]] = null  // body of this method won't be used

  def testMethodPredicatePermutations_C(list: List[String]): Iterable[List[String]] = null  // body of this method won't be used

class PrologMethodUtilsTest extends AbstractAnnotationTest with Matchers with Logging:

  import PrologMethodUtils.*

  /* @PrologMethod method field 'signature' tests */

  "PrologMethodUtils" should :
    "return an empty signature from a @PrologMethod if the default empty method field parsed" in :
      val prologMethod = classOf[TestPrologMethod].getMethod("testMethodSignature_default")
      val annotation = prologMethod.getAnnotation(classOf[PrologMethod])
      val actualSignature = extractSignature(annotation)

      assert(actualSignature.isEmpty)

  "PrologMethodUtils" should :
    "extract the correct signature from a @PrologMethod if a single input and output variables are set" in :
      val prologMethod = classOf[TestPrologMethod].getMethod("testMethodSignature_XY")
      val annotation = prologMethod.getAnnotation(classOf[PrologMethod])
      val actualSignature = extractSignature(annotation)
      val expectedSignature = Signature("(X) -> {Y}")

      (actualSignature.get.inputVars, actualSignature.get.outputVars) === (expectedSignature.get.inputVars, expectedSignature.get.outputVars)

  "PrologMethodUtils" should :
    "extract the correct signature from a @PrologMethod if multiple input and output variables are set" in :
      val prologMethod = classOf[TestPrologMethod].getMethod("testMethodSignature_XXXYY")
      val annotation = prologMethod.getAnnotation(classOf[PrologMethod])
      val actualSignature = extractSignature(annotation)
      val expectedSignature = Signature("(Var1, VAR2, X3) -> {Y1, Variabile2}")

      (actualSignature.get.inputVars, actualSignature.get.outputVars) === (expectedSignature.get.inputVars, expectedSignature.get.outputVars)

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

      assert(actualType.isEmpty)

  "PrologMethodUtils" should :
    "extract the correct type from a @PrologMethod if a single type is set" in :
      val prologMethod = classOf[TestPrologMethod].getMethod("testMethodTypes_IntInt")
      val annotation = prologMethod.getAnnotation(classOf[PrologMethod])
      val actualType = extractTypes(annotation)
      val expectedType = Types(Array("Int", "Int"))

      assert(actualType.get.values === expectedType.get.values)

  "PrologMethodUtils" should :
    "extract the correct type from a @PrologMethod if a list type is set" in :
      val prologMethod = classOf[TestPrologMethod].getMethod("testMethodTypes_ListIntListBoolean")
      val annotation = prologMethod.getAnnotation(classOf[PrologMethod])
      val actualType = extractTypes(annotation)
      val expectedType = Types(Array("List[Int]", "List[Boolean]"))

      assert(actualType.get.values === expectedType.get.values)

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

      assert(actualPredicate.isEmpty)

  "PrologMethodUtils" should :
    "extract the correct predicate from a @PrologMethod if no predicate notation symbols are present" in :
      val prologMethod = classOf[TestPrologMethod].getMethod("testMethodPredicate_noSymbols")
      val annotation = prologMethod.getAnnotation(classOf[PrologMethod])
      val actualPredicate = extractPredicate(annotation)
      val expectedPredicate = Predicate("p(X1, X2, Y).")

      assert(actualPredicate.get === expectedPredicate.get)

  "PrologMethodUtils" should :
    "extract the correct predicate from a @PrologMethod if predicate notation symbols are present" in :
      val prologMethod = classOf[TestPrologMethod].getMethod("testMethodPredicate_IO")
      val annotation = prologMethod.getAnnotation(classOf[PrologMethod])
      val actualPredicate = extractPredicate(annotation)
      val expectedPredicate = Predicate("p(+X1, +X2, -Y).")

      assert(actualPredicate.get === expectedPredicate.get)

  /* prolog method interceptor */

  "PrologMethodInterceptor" should :
    "evaluate correctly the prolog predicate 'p(X).', generate the right goal 'p(X)'and unify when compared " +
      "against the theory: 'p(a).' generating 1 solution 'p(a).'" in :
      val proxy = PrologMethodInterceptor.create(TestPrologMethodImpl().asInstanceOf[TestPrologMethod])
      assert(proxy.testMethodPredicateClauses_Xa("X") === Iterable(Term.createTerm("p(a)")))

  "PrologMethodInterceptor" should :
    "evaluate correctly the prolog predicate 'p(-X).', generate the right goal 'p(X)'and unify when compared " +
      "against the theory: 'p(a).' generating 1 solution 'p(a).'" in :
      val proxy = PrologMethodInterceptor.create(TestPrologMethodImpl().asInstanceOf[TestPrologMethod])
      assert(proxy.testMethodPredicateClauses_minusXa("X") === Iterable(Term.createTerm("p(a)")))

  "PrologMethodInterceptor" should :
    "evaluate correctly the prolog predicate 'p(+X).', generate the right goal 'p(X)' and unify when compared " +
      "against the theory: 'p(a).' generating 1 solution 'p(a).'" in :
      val proxy = PrologMethodInterceptor.create(TestPrologMethodImpl().asInstanceOf[TestPrologMethod])
      assert(proxy.testMethodPredicateClauses_plusXa("X") === Iterable(Term.createTerm("p(a)")))

  "PrologMethodInterceptor" should :
    "evaluate correctly the prolog predicate 'p(-X).' generate the right goal 'p(X)' and unify when compared " +
      "against the theory 'p(a). p(b). p(c).' generating 3 solutions 'p(a). p(b). p(c)." in :
      val proxy = PrologMethodInterceptor.create(TestPrologMethodImpl().asInstanceOf[TestPrologMethod])
      assert(proxy.testMethodPredicateClauses_minusXabc("X") === Iterable(Term.createTerm("p(a)"), Term.createTerm("p(b)"), Term.createTerm("p(c)")))

  "PrologMethodInterceptor" should :
    "evaluate correctly the prolog predicate 'p(+X, -Y).' generate the right goal 'p([1,2,3],Y)' and unify when compared " +
      "against theory 'p(X, X).' generating 1 solution p([1,2,3],[1,2,3])" in :
      val proxy = PrologMethodInterceptor.create(TestPrologMethodImpl().asInstanceOf[TestPrologMethod])
      assert(proxy.testMethodPredicateClauses_A(List(1, 2, 3)) === Iterable(Term.createTerm("p([1,2,3],[1,2,3])")))

  "PrologMethodInterceptor" should :
    "evaluate correctly the prolog predicate 'p(+X, -Y).' generate the right goal 'p([1,2,3],Y)' and unify when compared " +
      "against theory 'p(X, X). p(A,A)' generating 2 solutions p([1,2,3],[1,2,3]). p([1,2,3],[1,2,3])." in :
      val proxy = PrologMethodInterceptor.create(TestPrologMethodImpl().asInstanceOf[TestPrologMethod])
      assert(proxy.testMethodPredicateClauses_B(List(1, 2, 3)) === Iterable(Term.createTerm("p([1,2,3],[1,2,3])"), Term.createTerm("p([1,2,3],[1,2,3])")))

  "PrologMethodInterceptor" should :
    "evaluate correctly the prolog predicate 'permutation(@X,-!Y)' generate the right goal 'permutation([1,2,3],Y)' and unify when compared " +
      "against theory 'any([X|Xs],X,Xs). any([X|Xs],E,[X|Ys]):-any(Xs,E,Ys). permutation([],[]). permutation(Xs,[X|Ys]):-any(Xs,X,Zs), permutation(Zs,Ys).' " +
      "generating 6 solutions of type Term: permutation([1,2,3],[1,2,3]), permutation([1,2,3],[1,3,2]), permutation([1,2,3],[2,1,3]), permutation([1,2,3],[2,3,1]), permutation([1,2,3],[3,1,2]), permutation([1,2,3],[3,2,1])" in :
      val proxy = PrologMethodInterceptor.create(TestPrologMethodImpl().asInstanceOf[TestPrologMethod])
      assert(proxy.testMethodPredicatePermutations_A(List(1, 2, 3)) === Iterable(
        Term.createTerm("permutation([1,2,3],[1,2,3])"),
        Term.createTerm("permutation([1,2,3],[1,3,2])"),
        Term.createTerm("permutation([1,2,3],[2,1,3])"),
        Term.createTerm("permutation([1,2,3],[2,3,1])"),
        Term.createTerm("permutation([1,2,3],[3,1,2])"),
        Term.createTerm("permutation([1,2,3],[3,2,1])")
      ))

  "PrologMethodInterceptor" should :
    "evaluate correctly the prolog predicate 'permutation(@X,-!Y)' generate the right goal 'permutation([1,2,3],Y)' and unify when compared " +
      "against theory 'any([X|Xs],X,Xs). any([X|Xs],E,[X|Ys]):-any(Xs,E,Ys). permutation([],[]). permutation(Xs,[X|Ys]):-any(Xs,X,Zs), permutation(Zs,Ys).' " +
      "generating 6 solutions of type List[Int]: permutation([1,2,3],[1,2,3]), permutation([1,2,3],[1,3,2]), permutation([1,2,3],[2,1,3]), permutation([1,2,3],[2,3,1]), permutation([1,2,3],[3,1,2]), permutation([1,2,3],[3,2,1])" in :
      val proxy = PrologMethodInterceptor.create(TestPrologMethodImpl().asInstanceOf[TestPrologMethod])
      assert(proxy.testMethodPredicatePermutations_B(List(1, 2, 3)) === List(
        Term.createTerm("[1,2,3]"),
        Term.createTerm("[1,3,2]"),
        Term.createTerm("[2,1,3]"),
        Term.createTerm("[2,3,1]"),
        Term.createTerm("[3,1,2]"),
        Term.createTerm("[3,2,1]")
      ))

  "PrologMethodInterceptor" should :
    "1evaluate correctly the prolog predicate 'permutation(@X,-!Y)' generate the right goal 'permutation([1,2,3],Y)' and unify when compared " +
      "against theory 'any([X|Xs],X,Xs). any([X|Xs],E,[X|Ys]):-any(Xs,E,Ys). permutation([],[]). permutation(Xs,[X|Ys]):-any(Xs,X,Zs), permutation(Zs,Ys).' " +
      "generating 6 solutions of type List[String]: permutation([1,2,3],[1,2,3]), permutation([1,2,3],[1,3,2]), permutation([1,2,3],[2,1,3]), permutation([1,2,3],[2,3,1]), permutation([1,2,3],[3,1,2]), permutation([1,2,3],[3,2,1])" in :
      val proxy = PrologMethodInterceptor.create(TestPrologMethodImpl().asInstanceOf[TestPrologMethod])
      assert(proxy.testMethodPredicatePermutations_C(List("a","b","c")) === List(
        Term.createTerm("[a,b,c]"),
        Term.createTerm("[a,c,b]"),
        Term.createTerm("[b,a,c]"),
        Term.createTerm("[b,c,a]"),
        Term.createTerm("[c,a,b]"),
        Term.createTerm("[c,b,a]")
      ))