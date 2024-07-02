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

  /**
   * returns the solutions for the N-rooks problem (N x N board), using the tuProlog engine.
   *
   * @param N number of rooks
   * @param Rs solution
   * @return the solutions for the N-rooks as Iterable of Term
   */
  def n_rooks(N: Int, Rs: String): Iterable[Term] = null // this body is never executed

@PrologClass(clauses = Array(
  "range(N, N, [N]) :- !.",
  "range(M, N, [M|Ns]) :- M < N, M1 is M + 1, range(M1, N, Ns).",
  "permutation([], []).",
  "permutation(Qs, [Q|Qs1]) :- select(Q, Qs, Qs2), permutation(Qs2, Qs1).",
  "select(X, [X|Xs], Xs).",
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

  println(s"6 queens positions (lazy): $n_queens_solution")     // not computed (lazy)
  println(s"2 rooks positions (lazy): $n_rooks_solution")       // not computed (lazy)

  println(s"6 queens positions: ${n_queens_solution.toList}")   // computed: List([2,4,6,1,3,5], [3,6,2,5,1,4], [4,1,5,2,6,3], [5,3,1,6,4,2])
  println(s"2 rooks positions: ${n_rooks_solution.toList}")     // computed: List([1, 2], [2, 1])
```

In questo esempio, viene formulato il problema delle 'N regine' e una sua variante 'N torri', definendo due metodi
`n_queens` e `n_rooks` annotati con `@PrologMethod` che calcolano le posizioni delle regine e delle torri 
rispettivamente, in modo tale che non si attacchino tra di loro.\
Si noti che le regole definite all'interno dei metodi annotati con `@PrologMethod` non sono condivise con altri metodi.

La classe `NPiecesImpl` è annotata con `@PrologClass` e contiene le clausole Prolog generali (che sono utilizzate nello
specifico, dai metodi annotati) per calcolare le soluzioni del problema delle N regine ed N torri.\
Queste regole sono condivise tra tutti i metodi annotati con `@PrologMethod` che appartengono alla stessa classe.

I metodi `n_queens` e `n_rooks` definiscono solo le clausole, il nome del metodo Scala e gli argomenti definiscono il 
goal da utilizzare per ricavare le soluzioni.

Regole definite all'interno della classe:
- `range`: genera una lista di numeri da M a N (inclusi). 
  Se M è minore di N, incrementa M di 1 (M1) e chiama ricorsivamente la funzione `range` con: M incrementato, N e il 
  resto della lista.
  Quando è raggiunto il caso base in cui M è uguale a N, mette in uscita una lista '[N]', da questo punto comincia 
  il backtracking degli step generati precedentemente dalla ricorsione che effettua 'prepend' alla lista del valore M a 
  quello step.
- `select`: seleziona un elemento X dalla lista.
  Controlla se l'elemento X è in cima alla lista (head), se non lo è applica la ricorsione con il resto della lista
  fino a quando non diventa head.
  Quando è raggiunto il caso base in cui X è in cima alla lista, mette in uscita il resto della lista, da questo punto
  comincia il backtracking degli step generati precedentemente dalla ricorsione che effettua 'prepend' alla lista del
  valore X (valore che era in cima alla lista a quello step).
- `permutation`: genera una permutazione della lista passata in ingresso. Applica la funzione select per selezionare un 
  elemento Q da Qs e lo rimuove dalla lista, ottenendo così Qs2. Poi chiama ricorsivamente la funzione permutation con 
  la lista Qs2 e il resto della lista iniziale Qs1. A ogni step, ripete select passando il valore nella cima della lista 
  e il resto della lista. Il numero di elementi della lista Qs1 si riduce di uno a ogni step fino a raggiungere il caso
  base. Quando è raggiunto il caso base in cui la lista è vuota, restituisce una lista vuota. Da questo punto, comincia
  il backtracking degli step generati precedentemente dalla ricorsione. Durante il backtracking, effettua ‘prepend’ del
  valore Q selezionato a quel passo alla lista Qs1, che inizia come una lista vuota nel caso base e viene costruita man
  mano che il backtracking procede.

Regole definite nel metodo `n_queens`:
- `safe`: verifica se la regina Q0 è in una posizione sicura rispetto alla regina Q.\
  Le regine sono considerate in conflitto se sono sulla stessa riga o diagonale (le colonne saranno diverse perchè la
  posizione di ogni regina è identificata dalla colonna e la permutazione di queste, che sono calcolate prima di questo, 
  esclude elementi uguali).\
  Questo è verificato da Q0 =\= Q + D0 e Q0 =\= Q - D0, che verifica se due regine si trovano nella stessa diagonale: se
  la somma (nel caso di diagonali da alto-sinistra a basso-destra) tra le loro righe e colonne è la stessa, si trovano 
  nella stessa diagonale, se la differenza (nel caso di diagonali da basso-sinistra ad alto-destra) tra le loro righe e
  colonne è la stessa, si trovano nella stessa diagonale. In questo caso l'operatore =\\= è utilizzato per verificare che
  le somme e le differenze siano diverse (regine non in conflitto).\
  Se non sono in conflitto, il predicato è chiamato ricorsivamente passando la lista delle regine ancora da verificare,
  contro la stessa regina Q0, incrementando la riga di 1: D1 is D0 + 1.\
  Tutto questo è ripetuto ricorsivamente per ogni regina nella lista di partenza '[Q|Qs]' in 
  'safe([Q|Qs]):- safe(Qs, Q, 1), safe(Qs).', se il risultato restituito è 'si', nessuna regina è in conflitto con le 
  altre.
- `n_queens`: è il predicato principale che risolve il problema delle N regine. Genera una permutazione dei numeri da 1
  a N (Ns) e assegna il risultato a Qs (posizione delle colonne delle regine), e chiama per ogni permutazione generata
  il predicato 'safe(Qs)' che verifica come descritto in precedenza se le regine non sono in conflitto tra loro.

Regole definite nel metodo `n_rooks`:
- `safe`: il funzionamento è lo stesso del precedente ma semplificato, verifica se la torre R è in una posizione sicura
  rispetto alle altre torri Rs.\
  Le torri sono considerate in conflitto se si trovano sulla stessa riga (la colonna non può essere la stessa perchè
  le permutazioni, che vengono calcolate prima di questo, non ammettono elementi doppi).\
  Questo è verificato da not(member(R, Rs)), che verifica se la torre R non è presente nella lista delle torri Rs (lista
  della posizione delle righe delle torri).\
  Se non sono in conflitto, il predicato è chiamato ricorsivamente passando la lista delle torri ancora da verificare.\
  Questo è ripetuto ricorsivamente per ogni torre nella lista di partenza '[R|Rs]' in 'safe([R|Rs]):- not(member(R, Rs)), safe(Rs).'.
- `n_rooks`: è il predicato principale che risolve il problema delle N torri. Genera una permutazione dei numeri da 1
  a N (Ns) e assegna il risultato a Rs (posizione delle colonne delle torri), e chiama per ogni permutazione generata
  il predicato 'safe(Rs)' che verifica mediante 'not(member(R, Rs))' se le torri non sono in conflitto tra loro, 
  verificando se ci sono righe (R) con lo stesso valore delle colonne (Rs), se non ci sono elementi in comune tra righe
  e colonne, nessuna le torre è sono in conflitto con le altre.

\
Altri esempi sono disponibili nei test che sono stati utilizzati per verificare il corretto funzionamento della libreria.

[Torna al sommario](../index.md) |
[Capitolo precedente (implementazione)](../5-implementation/index.md) |
[Capitolo successivo (conclusione)](../7-conclusion/index.md)
