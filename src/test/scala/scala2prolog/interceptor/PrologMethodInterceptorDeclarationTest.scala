package pps.exam.application
package scala2prolog.interceptor

import scala2prolog.annotation.PrologMethod

import alice.tuprolog.Term

import scala.collection.immutable.List

trait PrologMethodInterceptorDeclarationTest:

  def PrologMethodInterceptor_notAnnotatedMethod_Int(a: Int, b: Int): Int =
    a + b

  def PrologMethodInterceptor_notAnnotatedMethod_String(s: String): String =
    "Hello World!"

  @PrologMethod(predicate = "p(X).", clauses = Array("p(a)."))
  def testMethodPredicateClauses_Xa(s: String): Iterable[Term] = null

  @PrologMethod(predicate = "p(+X).", clauses = Array("p(a)."))
  def testMethodPredicateClauses_plusXa(s: String): Iterable[Term] = null

  @PrologMethod(predicate = "p(-X).", clauses = Array("p(a)."))
  def testMethodPredicateClauses_minusXa(s: String): Iterable[Term] = null

  @PrologMethod(predicate = "p(-X).", clauses = Array("p(a). p(b). p(c)."))
  def testMethodPredicateClauses_minusXabc(s: String): Iterable[Term] = null

  @PrologMethod(predicate = "p(+X, -Y).", clauses = Array("p(X, X)."))
  def testMethodPredicateClauses_A(list: List[Int]): Iterable[Term] = null

  @PrologMethod(predicate = "p(+X, -Y).", clauses = Array("p(X, X). p(A, A)."))
  def testMethodPredicateClauses_B(list: List[Int]): Iterable[Term] = null

  @PrologMethod(
    predicate = "permutation(+X,-Y)",
    clauses = Array(
      "any([X|Xs],X,Xs).",
      "any([X|Xs],E,[X|Ys]):-any(Xs,E,Ys).",
      "permutation([],[]).",
      "permutation(Xs,[X|Ys]):-any(Xs,X,Zs), permutation(Zs,Ys).")
  )
  def testMethodPredicatePermutations_A(list: List[Int]): Iterable[Term] = null

  @PrologMethod(
    predicate = "permutation(+X,-Y)",
    signature = "(X) -> {Y}",
    types = Array("List[Int]", "List[Int]"),
    clauses = Array(
      "any([X|Xs],X,Xs).",
      "any([X|Xs],E,[X|Ys]):-any(Xs,E,Ys).",
      "permutation([],[]).",
      "permutation(Xs,[X|Ys]):-any(Xs,X,Zs), permutation(Zs,Ys).")
  )
  def testMethodPredicatePermutations_B(list: List[Int]): Iterable[List[Int]] = null

  @PrologMethod(
    predicate = "permutation(+X,-Y)",
    signature = "(X) -> {Y}",
    types = Array("List[String]", "List[String]"),
    clauses = Array(
      "any([X|Xs],X,Xs).",
      "any([X|Xs],E,[X|Ys]):-any(Xs,E,Ys).",
      "permutation([],[]).",
      "permutation(Xs,[X|Ys]):-any(Xs,X,Zs), permutation(Zs,Ys).")
  )
  def testMethodPredicatePermutations_C(list: List[String]): Iterable[List[String]] = null

  @PrologMethod(
    predicate = "point(+X, +Y).",
    signature = "() -> {X_POS, Y_POS}",
    types = Array("Int", "Int"),
    clauses = Array(
      "point(3, 4).",
      "point(-1, 2).",
      "point(0, 0)."
    )
  )
  def testMethodPredicatePermutations_D(x: String, y: String): Iterable[List[Int]] = null

  @PrologMethod(
    predicate = "point(+X, +Y, +Z).",
    signature = "() -> {X_POS, Y_POS, Z_POS}",
    types = Array("Double", "Double", "Double"),
    clauses = Array(
      "point(3.14, 4.2, 1.0).",
      "point(-1.0, 2.67, 1.0).",
      "point(0.111, 0.23, 2.0)."
    )
  )
  def testMethodPredicatePermutations_E(x: String, y: String, z: String): Iterable[Term] = null

  @PrologMethod(
    predicate = "point(+X, +Y, +Z).",
    signature = "() -> {X_POS, Y_POS}",
    types = Array("Double", "Double"),
    clauses = Array(
      "point(3.14, 4.2, 1.0).",
      "point(-1.0, 2.67, 1.0).",
      "point(0.111, 0.23, 2.0)."
    )
  )
  def testMethodPredicatePermutations_F(x: String, y: String, z: Double): Iterable[List[Int]] = null

  @PrologMethod(
    clauses = Array(
      "point(3, 4).",
      "point(-1, 2).",
      "point(0, 0)."
    )
  )
  def point(x: String, y: String): Iterable[List[Int]] = null

  @PrologMethod(
    clauses = Array(
      "point(3, 4).",
      "point(-1, 2).",
      "point(0, 0)."
    )
  )
  def pointA(x: String, y: String): Iterable[List[Term]] = null

  @PrologMethod(
    clauses = Array(
      "point_B(3, 4).",
      "point_B(-1, 2).",
      "point_B(0, 0)."
    )
  )
  def point_B(x: String, y: Int): Iterable[Term] = null

  @PrologMethod(
    predicate = "sum(@X, ?Y)",
    clauses = Array(
      "sum([], 0).",
      "sum([H|T], S) :- sum(T, N), S is H + N."
    )
  )
  def sumElementsInList_A(x: List[Int], y: String): Iterable[Term] = null

  @PrologMethod(
    predicate = "sum(?X, ?Y)",
    clauses = Array(
      "sum([], 0).",
      "sum([H|T], S) :- sum(T, N), S is H + N."
    )
  )
  def sumElementsInList_B(x: List[Int], y: Int): Iterable[Term] = null

  @PrologMethod(
    clauses = Array(
      "lookup([H|T],H,zero,T).",
      "lookup([H|T],E,s(N),[H|T2]) :- lookup(T,E,N,T2)."
    )
  )
  def lookup(x: List[Int], y: Int, z: String, w: String): Iterable[Term] = null

class PrologMethodInterceptorDeclarationTestImpl extends PrologMethodInterceptorDeclarationTest



