package pps.exam.application

import alice.tuprolog.Term

class Scala2PrologTest extends AbstractTest:
  "Scala2Prolog" should :
    "reuse the same clauses in multiple calls of method's annotated with @PrologMethod from a common class annotated " +
      "with @PrologClass" in :
      val scala2PrologDeclarationTest = Scala2PrologDeclarationTestANonEmptyClausesImpl().asInstanceOf[Scala2PrologDeclarationTestA]
      val proxy = Scala2Prolog.newProxyInstanceOf(scala2PrologDeclarationTest)
      val prologResultA = proxy.methodA("X")
      val prologResultB = proxy.methodB("X")

      assert((prologResultB, prologResultA) === (
        Iterable(
          Term.createTerm("methodB(c)"),
          Term.createTerm("methodB(b)")
        ),
        Iterable(
          Term.createTerm("methodA(c)"),
          Term.createTerm("methodA(a)")
        ))
      )

  "Scala2Prolog" should :
    "use only the clauses of the PrologClass if the PrologMethod's clauses are empty" in :
      val scala2PrologDeclarationTest = Scala2PrologDeclarationTestANonEmptyClausesImpl().asInstanceOf[Scala2PrologDeclarationTestA]
      val proxy = Scala2Prolog.newProxyInstanceOf(scala2PrologDeclarationTest)
      val prologResultC = proxy.methodC("X")

      assert(prologResultC === Iterable(Term.createTerm("methodC(c)")))

  "Scala2Prolog" should :
    "not consider @PrologClass's clauses if empty, and parse only @PrologMethod's clauses instead" in :
      val scala2PrologDeclarationTest = Scala2PrologDeclarationTestAEmptyClausesImpl().asInstanceOf[Scala2PrologDeclarationTestA]
      val proxy = Scala2Prolog.newProxyInstanceOf(scala2PrologDeclarationTest)
      val prologResultA = proxy.methodA("X")
      val prologResultB = proxy.methodB("X")

      assert((prologResultB, prologResultA) === (
        Iterable(
          Term.createTerm("methodB(b)")
        ),
        Iterable(
          Term.createTerm("methodA(a)")
        ))
      )