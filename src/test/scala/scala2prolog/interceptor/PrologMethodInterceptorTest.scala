package pps.exam.application
package scala2prolog.interceptor

import scala2prolog.interceptor.processor.AbstractProcessorTest

import alice.tuprolog.Term
import org.apache.logging.log4j.scala.Logging
import org.scalatest.matchers.should.Matchers

class PrologMethodInterceptorTest extends AbstractProcessorTest with Matchers with Logging:

  import scala2prolog.interceptor.PrologInterceptor.create

  import Term.createTerm

  "PrologMethodInterceptor" should :
    "run methods not annotated with @PrologMethod as usual" in :
      val proxy = create(PrologMethodInterceptorDeclarationTestImpl().asInstanceOf[PrologMethodInterceptorDeclarationTest])

      assert((proxy.PrologMethodInterceptor_notAnnotatedMethod_Int(1, 2), proxy.PrologMethodInterceptor_notAnnotatedMethod_String("Hello World!")) === (3, "Hello World!"))

  "PrologMethodInterceptor" should :
    "intercept the @PrologMethod annotation and execute its logic producing the correct result when:" +
      "predicate = p(X)., clauses = Array(p(a).)" in :
      val proxy = create(PrologMethodInterceptorDeclarationTestImpl().asInstanceOf[PrologMethodInterceptorDeclarationTest])
      assert(proxy.testMethodPredicateClauses_Xa("X") === Iterable(createTerm("a")))

  "PrologMethodInterceptor" should :
    "intercept the @PrologMethod annotation and execute its logic producing the correct result when:" +
      "predicate = p(+X)., clauses = Array(p(a).)" in :
      val proxy = create(PrologMethodInterceptorDeclarationTestImpl().asInstanceOf[PrologMethodInterceptorDeclarationTest])
      assert(proxy.testMethodPredicateClauses_minusXa("X") === Iterable(createTerm("a")))

  "PrologMethodInterceptor" should :
    "intercept the @PrologMethod annotation and execute its logic producing the correct result when:" +
      "predicate = p(-X)., clauses = Array(p(a).)" in :
      val proxy = create(PrologMethodInterceptorDeclarationTestImpl().asInstanceOf[PrologMethodInterceptorDeclarationTest])
      assert(proxy.testMethodPredicateClauses_plusXa("X") === Iterable(createTerm("a")))

  "PrologMethodInterceptor" should :
    "intercept the @PrologMethod annotation and execute its logic producing the correct result when:" +
      "p(-X)., clauses = Array(p(a). p(b). p(c).)" in :
      val proxy = create(PrologMethodInterceptorDeclarationTestImpl().asInstanceOf[PrologMethodInterceptorDeclarationTest])
      assert(proxy.testMethodPredicateClauses_minusXabc("X") === Iterable(createTerm("a"), createTerm("b"), createTerm("c")))

  "PrologMethodInterceptor" should :
    "intercept the @PrologMethod annotation and execute its logic producing the correct result when:" +
      "predicate = p(+X, -Y)., clauses = Array(p(X, X).)" in :
      val proxy = create(PrologMethodInterceptorDeclarationTestImpl().asInstanceOf[PrologMethodInterceptorDeclarationTest])
      assert(proxy.testMethodPredicateClauses_A(List(1, 2, 3)) === Iterable(createTerm("p([1,2,3],[1,2,3])")))

  "PrologMethodInterceptor" should :
    "intercept the @PrologMethod annotation and execute its logic producing the correct result when:" +
      "predicate = p(+X, -Y)., clauses = Array(p(X, X). p(A, A).)" in :
      val proxy = create(PrologMethodInterceptorDeclarationTestImpl().asInstanceOf[PrologMethodInterceptorDeclarationTest])
      assert(proxy.testMethodPredicateClauses_B(List(1, 2, 3)) === Iterable(createTerm("p([1,2,3],[1,2,3])"), createTerm("p([1,2,3],[1,2,3])")))

  "PrologMethodInterceptor" should :
    "intercept the @PrologMethod annotation and execute its logic producing the correct result when:" +
      "predicate = permutation(+X,-Y)," +
      "clauses = Array(any([X|Xs],X,Xs).,any([X|Xs],E,[X|Ys]):-any(Xs,E,Ys).,permutation([],[]).,permutation(Xs,[X|Ys]):-any(Xs,X,Zs), permutation(Zs,Ys).\"" in :
      val proxy = create(PrologMethodInterceptorDeclarationTestImpl().asInstanceOf[PrologMethodInterceptorDeclarationTest])
      assert(proxy.testMethodPredicatePermutations_A(List(1, 2, 3)) === Iterable(
        createTerm("permutation([1,2,3],[1,2,3])"),
        createTerm("permutation([1,2,3],[1,3,2])"),
        createTerm("permutation([1,2,3],[2,1,3])"),
        createTerm("permutation([1,2,3],[2,3,1])"),
        createTerm("permutation([1,2,3],[3,1,2])"),
        createTerm("permutation([1,2,3],[3,2,1])")
      ))

  "PrologMethodInterceptor" should :

    "intercept the @PrologMethod annotation and execute its logic producing the correct result when:" +
      "predicate = permutation(+X,-Y)," +
      "signature = (X) -> {Y}," +
      "types = Array(List[Int],List[Int])," +
      "clauses = Array(any([X|Xs],X,Xs).,any([X|Xs],E,[X|Ys]):-any(Xs,E,Ys).,permutation([],[]).,permutation(Xs,[X|Ys]):-any(Xs,X,Zs), permutation(Zs,Ys).)" in :
      val proxy = create(PrologMethodInterceptorDeclarationTestImpl().asInstanceOf[PrologMethodInterceptorDeclarationTest])
      assert(proxy.testMethodPredicatePermutations_B(List(1, 2, 3)) === List(
        List(1,2,3),
        List(1,3,2),
        List(2,1,3),
        List(2,3,1),
        List(3,1,2),
        List(3,2,1)
      ))

  "PrologMethodInterceptor" should :
    "intercept the @PrologMethod annotation and execute its logic producing the correct result when:" +
      "predicate = permutation(+X,-Y)," +
      "signature = (X) -> {Y}," +
      "types = Array(List[String],List[String])," +
      "clauses = Array(any([X|Xs],X,Xs).,any([X|Xs],E,[X|Ys]):-any(Xs,E,Ys).,permutation([],[]).,permutation(Xs,[X|Ys]):-any(Xs,X,Zs), permutation(Zs,Ys).)" in :
      val proxy = create(PrologMethodInterceptorDeclarationTestImpl().asInstanceOf[PrologMethodInterceptorDeclarationTest])
      assert(proxy.testMethodPredicatePermutations_C(List("a","b","c")) === List(
        List("a","b","c"),
        List("a","c","b"),
        List("b","a","c"),
        List("b","c","a"),
        List("c","a","b"),
        List("c","b","a")
      ))

  "PrologMethodInterceptor" should :
    "intercept the @PrologMethod annotation and execute its logic producing the correct result when:" +
      "predicate = point(+X, +Y).," +
      "signature = () -> {X_POS, Y_POS}," +
      "types = Array(Int, Int)," +
      "clauses = Array(point(3, 4).,point(-1, 2).,point(0, 0).)" in :
      val proxy = create(PrologMethodInterceptorDeclarationTestImpl().asInstanceOf[PrologMethodInterceptorDeclarationTest])
      assert(proxy.testMethodPredicatePermutations_D("X","Y") === List(
        createTerm("point(3,4)"),
        createTerm("point(-1,2)"),
        createTerm("point(0,0)")
      ))

  "PrologMethodInterceptor" should :
    "intercept the @PrologMethod annotation and execute its logic producing the correct result when:" +
      "predicate = point(+X, +Y, +Z).," +
      "signature = () -> {X_POS, Y_POS, Z_POS}," +
      "types = Array(Double, Double, Double)," +
      "clauses = Array(point(3.14, 4.2, 1.0).,point(-1.0, 2.67, 1.0).,point(0.111, 0.23, 2.0).)\n  " in :
      val proxy = create(PrologMethodInterceptorDeclarationTestImpl().asInstanceOf[PrologMethodInterceptorDeclarationTest])
      assert(proxy.testMethodPredicatePermutations_E("X", "Y", "Z") === Iterable(
        createTerm("point(3.14,4.2,1.0)"),
        createTerm("point(-1.0,2.67,1.0)"),
        createTerm("point(0.111,0.23,2.0)")
      ))

  "PrologMethodInterceptor" should :
    "intercept the @PrologMethod annotation and execute its logic producing the correct result when:" +
      "predicate = point(+X, +Y, +Z).," +
      "signature = () -> {X_POS, Y_POS}," +
      "types = Array(Double, Double)," +
      "clauses = Array(point(3.14, 4.2, 1.0).,point(-1.0, 2.67, 1.0).,point(0.111, 0.23, 2.0).)\n  " in :
      val proxy = create(PrologMethodInterceptorDeclarationTestImpl().asInstanceOf[PrologMethodInterceptorDeclarationTest])
      assert(proxy.testMethodPredicatePermutations_F("X", "Y", 1.0) === List(
        createTerm("point(3.14,4.2,1.0)"),
        createTerm("point(-1.0,2.67,1.0)")
      ))

  "PrologMethodInterceptor" should :
    "intercept the @PrologMethod annotation and execute its logic, guessing and inferring missing fields and types " +
      "correctly if not present when only clauses are present:" +
      "clauses = Array(point(3, 4).,point(-1, 2).,point(0, 0).)" in :
      val proxy = create(PrologMethodInterceptorDeclarationTestImpl().asInstanceOf[PrologMethodInterceptorDeclarationTest])
      assert(proxy.point("X", "Y") === List(
        3,4,-1,2,0,0  //the output of the method is a List[Int], so it puts the X and Y results as pairs in a single list (X1,Y1,X2,Y2,...)
      ))

  "PrologMethodInterceptor" should :
    "intercept the @PrologMethod annotation and execute its logic, guessing and inferring missing fields and types " +
      "correctly if not present when:" +
      "clauses = Array(point_B(3, 4).,point_B(-1, 2).,point_B(0, 0).)" in :
      val proxy = create(PrologMethodInterceptorDeclarationTestImpl().asInstanceOf[PrologMethodInterceptorDeclarationTest])
      assert(proxy.point_B("X", 4) === Iterable(
        alice.tuprolog.Int.of(3).intValue()
      ))

  "PrologMethodInterceptor" should :
    "intercept the @PrologMethod annotation and execute its logic, guessing and inferring missing fields and types " +
      "correctly if not present when:" +
      "predicate = sum(@X, ?Y)," +
      "clauses = Array(sum([], 0).,sum([H|T], S) :- sum(T, N), S is H + N.)" in :
      val proxy = create(PrologMethodInterceptorDeclarationTestImpl().asInstanceOf[PrologMethodInterceptorDeclarationTest])
      assert(proxy.sumElementsInList_A(List(1,2,3,4), "X") === Iterable(
        alice.tuprolog.Int.of(10).intValue()
      ))

  "PrologMethodInterceptor" should :
    "intercept the @PrologMethod annotation and execute its logic, guessing and inferring missing fields and types " +
      "correctly if not present when:" +
      "predicate = sum(?X, ?Y)," +
      "clauses = Array(sum([], 0).,sum([H|T], S) :- sum(T, N), S is H + N.)" in :
      val proxy = create(PrologMethodInterceptorDeclarationTestImpl().asInstanceOf[PrologMethodInterceptorDeclarationTest])
      assert(proxy.sumElementsInList_B(List(1, 2, 3, 4, 5), 15) === Iterable(
        createTerm("sum([1,2,3,4,5],15)")
      ))

  "PrologMethodInterceptor" should :
    "intercept the @PrologMethod annotation and execute its logic, guessing and inferring missing fields and types " +
      "correctly if not present and retuning empty Iterable if no solutions were found:" +
      "predicate = sum(?X, ?Y)," +
      "clauses = Array(sum([], 0).,sum([H|T], S) :- sum(T, N), S is H + N.)" in :
      val proxy = create(PrologMethodInterceptorDeclarationTestImpl().asInstanceOf[PrologMethodInterceptorDeclarationTest])
      assert(proxy.sumElementsInList_B(List(1, 2, 3, 4, 5), 10) === Iterable.empty)

  "PrologMethodInterceptor" should :
    "intercept the @PrologMethod annotation and execute its logic, guessing and inferring missing fields and types " +
      "correctly if not present when only clauses are present:" +
      "clauses = Array(lookup([H|T],H,zero ,T).,lookup([H|T],E,s(N) ,[H|T2 ]):- lookup (T,E,N,T2).)" in :
      val proxy = create(PrologMethodInterceptorDeclarationTestImpl().asInstanceOf[PrologMethodInterceptorDeclarationTest])
      val actual = proxy.lookup(List(1, 2, 3, 4, 5), 3, "X", "Y")
      val expected: Iterable[Term] = List(createTerm("s(s(zero))"), createTerm("[1,2,4,5]"))
      assert((actual.head, actual.last) === (expected.head, expected.last))