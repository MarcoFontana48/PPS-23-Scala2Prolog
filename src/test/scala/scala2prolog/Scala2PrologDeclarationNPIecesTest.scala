package pps.exam.application
package scala2prolog

import scala2prolog.annotation.{PrologClass, PrologMethod}

import alice.tuprolog.Term
import org.apache.logging.log4j.scala.Logging

trait Scala2PrologDeclarationNPIecesTest extends Logging:
  /**
   * returns the solutions for the N-queens problem (N x N board), using the tuProlog engine.
   *
   * @param N number of queens
   * @param Qs solution
   * @return the solutions for the N-queens as Iterable of Term
   */
  @PrologMethod(clauses = Array(
    //n_queens is the main predicate that solves the N-Queens problem.
    //generates a permutation of the numbers 1 to N (Ns) and assigns it to Qs, then checks if Qs is a safe configuration.
    "n_queens(N, Qs) :- range(1, N, Ns), permutation(Ns, Qs), safe(Qs).",
    //base case for the safe predicate. An empty list is always safe.
    "safe([]).",
    //checks if the first queen Q is safe with respect to the rest of the queens Qs.
    //checks both the rest of the queens and the relative diagonal positions.
    "safe([Q|Qs]) :- safe(Qs, Q, 1), safe(Qs).",
    //base case for the safe predicate with three arguments. An empty list is always safe.
    "safe([], _, _).",
    //checks if the first queen Q is not in conflict with a specific queen Q0.
    //checks both the diagonal, up and down. If it's safe, it continues with the rest of the queens.
    "safe([Q|Qs], Q0, D0) :- Q0 =\\= Q + D0, Q0 =\\= Q - D0, D1 is D0 + 1, safe(Qs, Q0, D1)."
  ))
  def n_queens(N: Int, Qs: String): Iterable[Term] = null // this body is never executed
  
  @PrologMethod(clauses = Array(
    //n_rooks is the main predicate that solves the N-Rooks problem.
    //generates a permutation of the numbers 1 to N (Ns) and assigns it to Rs, then checks if Rs is a safe configuration.
    "n_rooks(N, Rs) :- range(1, N, Ns), permutation(Ns, Rs), safe(Rs).",
    //base case for the safe predicate. An empty list is always safe.
    "safe([]).",
    //checks if the first rook R is safe with respect to the rest of the rooks Rs.
    //checks if there is no rook in the same row or column for a specific rook, then checks the rest of the rooks.
    "safe([R|Rs]) :- not(member(R, Rs)), safe(Rs)."
  ))

  /**
   * returns the solutions for the N-rooks problem (N x N board), using the tuProlog engine.
   *
   * @param N number of rooks
   * @param Rs solution
   * @return the solutions for the N-rooks as Iterable of Term
   */
  def n_rooks(N: Int, Rs: String): Iterable[Term] = null // this body is never executed

@PrologClass(clauses = Array(
  //base case for the range predicate. If M equals N, then the range is just [N]. Uses the 'cut' operator to avoid
  //backtracking, computing only the first solution.
  "range(N, N, [N]) :- !.",
  //generates a list of numbers from M to N.
  "range(M, N, [M|Ns]) :- M < N, M1 is M + 1, range(M1, N, Ns).",
  //base case for the permutation predicate. The permutation of an empty list is an empty list.
  "permutation([], []).",
  //generates a permutation of Qs by selecting a queen Q from Qs and generating a permutation of the remaining queens Qs2.
  "permutation(Qs, [Q|Qs1]) :- select(Q, Qs, Qs2), permutation(Qs2, Qs1).",
  //selects an element X from the list, outputs the rest of the list.
  "select(X, [X|Xs], Xs).",
  //selects an element X from the rest of the list.
  "select(X, [Y|Ys], [Y|Zs]) :- select(X, Ys, Zs)."
))
class Scala2PrologDeclarationNPiecesTestImpl extends Scala2PrologDeclarationNPIecesTest
