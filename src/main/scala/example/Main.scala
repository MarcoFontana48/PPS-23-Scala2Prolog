package pps.exam.application
package example

import scala2prolog.Scala2Prolog.newProxyInstanceOf

@main
def main(): Unit =
  val proxy = newProxyInstanceOf(NPiecesImpl().asInstanceOf[NPieces])
  val n_queens_solution = proxy.n_queens(6, "Positions")
  val n_rooks_solution = proxy.n_rooks(2, "Positions")

  println(s"6 queens positions (lazy): $n_queens_solution")  // not computed (lazy)
  println(s"2 rooks positions (lazy): $n_rooks_solution")    // not computed (lazy)

  println(s"6 queens positions: ${n_queens_solution.toList}")  // computed: List([2,4,6,1,3,5], [3,6,2,5,1,4], [4,1,5,2,6,3], [5,3,1,6,4,2])
  println(s"2 rooks positions: ${n_rooks_solution.toList}")  // computed: List([1, 2], [2, 1])
