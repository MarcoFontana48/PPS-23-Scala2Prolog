package pps.exam.application
package annotation

import alice.tuprolog.Term
import org.apache.logging.log4j.scala.Logging
import org.scalatest.matchers.should.Matchers

trait PrologMethodInterceptorDeclarationTest:
  
  def PrologMethodInterceptor_notAnnotatedMethod_Int(a:Int, b:Int): Int
  
  def PrologMethodInterceptor_notAnnotatedMethod_String(s: String): String

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

class PrologMethodInterceptorDeclarationTestImpl extends PrologMethodInterceptorDeclarationTest:

  def PrologMethodInterceptor_notAnnotatedMethod_Int(a:Int, b:Int): Int =
    a + b

  def PrologMethodInterceptor_notAnnotatedMethod_String(s: String): String =
    "Hello World!"

 /*
  * those methods will be intercepted by the 'PrologMethodInterceptor' and their body will be ignored, the declared
  * logic of the @PrologMethod annotation will be executed and returned instead.
  */

  def testMethodPredicateClauses_Xa(s: String): LazyList[Term] = null

  def testMethodPredicateClauses_plusXa(s: String): List[Term] = null 

  def testMethodPredicateClauses_minusXa(s: String): Seq[Term] = null  

  def testMethodPredicateClauses_minusXabc(s: String): Iterable[Term] = null  

  def testMethodPredicateClauses_A(list: List[Int]): Iterable[Term] = null  

  def testMethodPredicateClauses_B(list: List[Int]): Iterable[Term] = null  

  def testMethodPredicatePermutations_A(list: List[Int]): Iterable[Term] = null  

  def testMethodPredicatePermutations_B(list: List[Int]): Iterable[List[Int]] = null  

  def testMethodPredicatePermutations_C(list: List[String]): Iterable[List[String]] = null  

class PrologMethodInterceptorTest extends AbstractAnnotationTest with Matchers with Logging:
  
  "PrologMethodInterceptor" should :
    "run methods not annotated with @PrologMethod as usual" in :
      val proxy = PrologMethodInterceptor.create(PrologMethodInterceptorDeclarationTestImpl().asInstanceOf[PrologMethodInterceptorDeclarationTest])

      assert((proxy.PrologMethodInterceptor_notAnnotatedMethod_Int(1, 2), proxy.PrologMethodInterceptor_notAnnotatedMethod_String("Hello World!")) === (3, "Hello World!"))

  "PrologMethodInterceptor" should :
    "intercept the @PrologMethod annotation and execute its logic producing the correct result when:" +
      "predicate = p(X)., clauses = Array(p(a).)" in :
      val proxy = PrologMethodInterceptor.create(PrologMethodInterceptorDeclarationTestImpl().asInstanceOf[PrologMethodInterceptorDeclarationTest])
      assert(proxy.testMethodPredicateClauses_Xa("X") === Iterable(Term.createTerm("p(a)")))

  "PrologMethodInterceptor" should :
    "intercept the @PrologMethod annotation and execute its logic producing the correct result when:" +
      "predicate = p(+X)., clauses = Array(p(a).)" in :
      val proxy = PrologMethodInterceptor.create(PrologMethodInterceptorDeclarationTestImpl().asInstanceOf[PrologMethodInterceptorDeclarationTest])
      assert(proxy.testMethodPredicateClauses_minusXa("X") === Iterable(Term.createTerm("p(a)")))

  "PrologMethodInterceptor" should :
    "intercept the @PrologMethod annotation and execute its logic producing the correct result when:" +
      "predicate = p(-X)., clauses = Array(p(a).)" in :
      val proxy = PrologMethodInterceptor.create(PrologMethodInterceptorDeclarationTestImpl().asInstanceOf[PrologMethodInterceptorDeclarationTest])
      assert(proxy.testMethodPredicateClauses_plusXa("X") === Iterable(Term.createTerm("p(a)")))

  "PrologMethodInterceptor" should :
    "intercept the @PrologMethod annotation and execute its logic producing the correct result when:" +
      "p(-X)., clauses = Array(p(a). p(b). p(c).)" in :
      val proxy = PrologMethodInterceptor.create(PrologMethodInterceptorDeclarationTestImpl().asInstanceOf[PrologMethodInterceptorDeclarationTest])
      assert(proxy.testMethodPredicateClauses_minusXabc("X") === Iterable(Term.createTerm("p(a)"), Term.createTerm("p(b)"), Term.createTerm("p(c)")))

  "PrologMethodInterceptor" should :
    "intercept the @PrologMethod annotation and execute its logic producing the correct result when:" +
      "predicate = p(+X, -Y)., clauses = Array(p(X, X).)" in :
      val proxy = PrologMethodInterceptor.create(PrologMethodInterceptorDeclarationTestImpl().asInstanceOf[PrologMethodInterceptorDeclarationTest])
      assert(proxy.testMethodPredicateClauses_A(List(1, 2, 3)) === Iterable(Term.createTerm("p([1,2,3],[1,2,3])")))

  "PrologMethodInterceptor" should :
    "intercept the @PrologMethod annotation and execute its logic producing the correct result when:" +
      "predicate = p(+X, -Y)., clauses = Array(p(X, X). p(A, A).)" in :
      val proxy = PrologMethodInterceptor.create(PrologMethodInterceptorDeclarationTestImpl().asInstanceOf[PrologMethodInterceptorDeclarationTest])
      assert(proxy.testMethodPredicateClauses_B(List(1, 2, 3)) === Iterable(Term.createTerm("p([1,2,3],[1,2,3])"), Term.createTerm("p([1,2,3],[1,2,3])")))

  "PrologMethodInterceptor" should :
    "intercept the @PrologMethod annotation and execute its logic producing the correct result when:" +
      "predicate = permutation(+X,-Y)," +
      "clauses = Array(any([X|Xs],X,Xs).,any([X|Xs],E,[X|Ys]):-any(Xs,E,Ys).,permutation([],[]).,permutation(Xs,[X|Ys]):-any(Xs,X,Zs), permutation(Zs,Ys).\"" in :
      val proxy = PrologMethodInterceptor.create(PrologMethodInterceptorDeclarationTestImpl().asInstanceOf[PrologMethodInterceptorDeclarationTest])
      assert(proxy.testMethodPredicatePermutations_A(List(1, 2, 3)) === Iterable(
        Term.createTerm("permutation([1,2,3],[1,2,3])"),
        Term.createTerm("permutation([1,2,3],[1,3,2])"),
        Term.createTerm("permutation([1,2,3],[2,1,3])"),
        Term.createTerm("permutation([1,2,3],[2,3,1])"),
        Term.createTerm("permutation([1,2,3],[3,1,2])"),
        Term.createTerm("permutation([1,2,3],[3,2,1])")
      ))

  "PrologMethodInterceptor" should :
    "intercept the @PrologMethod annotation and execute its logic producing the correct result when:" +
      "predicate = permutation(+X,-Y)," +
      "signature = (X) -> {Y}," +
      "types = Array(List[Int],List[Int])," +
      "clauses = Array(any([X|Xs],X,Xs).,any([X|Xs],E,[X|Ys]):-any(Xs,E,Ys).,permutation([],[]).,permutation(Xs,[X|Ys]):-any(Xs,X,Zs), permutation(Zs,Ys).)" in :
      val proxy = PrologMethodInterceptor.create(PrologMethodInterceptorDeclarationTestImpl().asInstanceOf[PrologMethodInterceptorDeclarationTest])
      assert(proxy.testMethodPredicatePermutations_B(List(1, 2, 3)) === List(
        Term.createTerm("[1,2,3]"),
        Term.createTerm("[1,3,2]"),
        Term.createTerm("[2,1,3]"),
        Term.createTerm("[2,3,1]"),
        Term.createTerm("[3,1,2]"),
        Term.createTerm("[3,2,1]")
      ))

  "PrologMethodInterceptor" should :
    "intercept the @PrologMethod annotation and execute its logic producing the correct result when:" +
      "predicate = permutation(+X,-Y)," +
      "signature = (X) -> {Y}," +
      "types = Array(List[String],List[String])," +
      "clauses = Array(any([X|Xs],X,Xs).,any([X|Xs],E,[X|Ys]):-any(Xs,E,Ys).,permutation([],[]).,permutation(Xs,[X|Ys]):-any(Xs,X,Zs), permutation(Zs,Ys).)" in :
      val proxy = PrologMethodInterceptor.create(PrologMethodInterceptorDeclarationTestImpl().asInstanceOf[PrologMethodInterceptorDeclarationTest])
      assert(proxy.testMethodPredicatePermutations_C(List("a","b","c")) === List(
        Term.createTerm("[a,b,c]"),
        Term.createTerm("[a,c,b]"),
        Term.createTerm("[b,a,c]"),
        Term.createTerm("[b,c,a]"),
        Term.createTerm("[c,a,b]"),
        Term.createTerm("[c,b,a]")
      ))
