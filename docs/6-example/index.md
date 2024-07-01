# Esempio di applicazione

Un esempio di applicazione è stato realizzato per mostrare come sia possibile utilizzare la libreria per integrare 
Prolog in un progetto Scala, implementando una variante del problema di scacchi delle N regine (NPieces):

```scala 3
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
```
``` scala 3
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
che appartengono alla stessa classe e permettono di calcolare il numero di soluzioni possibili per il problema
considerando separatamente il problema delle regine e delle torri, in base al metodo che viene invocato.

I metodi `n_queens` e `n_rooks` definiscono solo le clausole, il nome del metodo e gli argomenti che vengono passati
tramite Scala definiscono il goal da utilizzare per ricavare le soluzioni.

Altri esempi sono disponibili nei test che sono stati utilizzati per verificare il corretto funzionamento della libreria.

[Torna al sommario](../index.md) |
[Capitolo precedente (design)](../5-implementation/index.md) |
[Capitolo successivo (esempio di utilizzo)](../7-conclusion/index.md)
