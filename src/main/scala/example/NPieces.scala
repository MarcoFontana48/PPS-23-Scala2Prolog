package pps.exam.application
package example

import scala2prolog.annotation.{PrologClass, PrologMethod}

import alice.tuprolog.Term

trait NPieces:
  @PrologMethod(clauses = Array(
    "n_queens(N, Qs) :- range(1, N, Ns), permutation(Ns, Qs), safe(Qs).",
    "safe([]).",
    "safe([Q|Qs]) :- safe(Qs, Q, 1), safe(Qs).",
    "safe([], _, _).",
    "safe([Q|Qs], Q0, D0) :- Q0 =\\= Q + D0, Q0 =\\= Q - D0, D1 is D0 + 1, safe(Qs, Q0, D1)."
  ))
  def n_queens(N: Int, Qs: String): Iterable[Term] = null // this body is never executed
  
  @PrologMethod(clauses = Array(
    "n_rooks(N, Rs) :- range(1, N, Ns), permutation(Ns, Rs), safe(Rs).",
    "safe([]).",
    "safe([R|Rs]) :- not(member(R, Rs)), safe(Rs)."
  ))
  def n_rooks(N: Int, Rs: String): Iterable[Term] = null // this body is never executed

@PrologClass(clauses = Array(
  "range(N, N, [N]) :- !.",
  "range(M, N, [M|Ns]) :- M < N, M1 is M + 1, range(M1, N, Ns).",
  "permutation([], []).",
  "permutation(Qs, [Q|Qs1]) :- select(Q, Qs, Qs2), permutation(Qs2, Qs1).",
  "select(X, [X|Xs], Xs).",
  "select(X, [Y|Ys], [Y|Zs]) :- select(X, Ys, Zs)."
))
class NPiecesImpl extends NPieces
