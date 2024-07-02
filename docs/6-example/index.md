# Esempio di utilizzo

Un esempio di applicazione è stato realizzato per mostrare come sia possibile utilizzare la libreria per integrare 
Prolog in un progetto Scala, implementando una variante del problema di scacchi delle N regine (NPieces):

``` scala
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
```

``` scala
@main
def main(): Unit =
  val proxy = newProxyInstanceOf(NPiecesImpl().asInstanceOf[NPieces])
  val n_queens_solution = proxy.n_queens(6, "Positions")
  val n_rooks_solution = proxy.n_rooks(2, "Positions")

  println(s"6 queens positions (lazy): $n_queens_solution")  // not computed (lazy)
  println(s"2 rooks positions (lazy): $n_rooks_solution")    // not computed (lazy)

  println(s"6 queens positions: ${n_queens_solution.toList}")  // computed: List([2,4,6,1,3,5], [3,6,2,5,1,4], [4,1,5,2,6,3], [5,3,1,6,4,2])
  println(s"2 rooks positions: ${n_rooks_solution.toList}")  // computed: List([1, 2], [2, 1])
```

In questo esempio, viene formulato il problema delle 'N regine' e una sua variante 'N torri', definendo due metodi
`n_queens` e `n_rooks` annotati con `@PrologMethod` che calcolano le posizioni delle regine e delle torri 
rispettivamente, in modo tale che non si attacchino tra di loro.
Le regole definite all'interno dei metodi annotati con `@PrologMethod` non sono condivise con altri metodi.

La classe `NPiecesImpl` è annotata con `@PrologClass` e contiene le clausole Prolog necessarie per calcolare il numero
di soluzioni possibili per il problema. Queste regole sono condivise tra tutti i metodi annotati con `@PrologMethod`
che appartengono alla stessa classe e permettono di calcolare il numero di soluzioni possibili per il problema,
considerando separatamente il problema delle regine e delle torri, in base al metodo che viene invocato.

I metodi `n_queens` e `n_rooks` definiscono solo le clausole, il nome del metodo Scala e gli argomenti definiscono il 
goal da utilizzare per ricavare le soluzioni.

\
Altri esempi sono disponibili nei test che sono stati utilizzati per verificare il corretto funzionamento della libreria.

[Torna al sommario](../index.md) |
[Capitolo precedente (implementazione)](../5-implementation/index.md) |
[Capitolo successivo (conclusione)](../7-conclusion/index.md)
