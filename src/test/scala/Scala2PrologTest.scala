package pps.exam.application

import alice.tuprolog.Term

class Scala2PrologTest extends AbstractTest {
  "Scala2Prolog" should {
    "reuse the same clauses in multiple calls of method's annotated with @PrologMethod from a common class annotated " +
      "with @PrologClass" in {
      val scala2PrologDeclarationTest = Scala2PrologDeclarationTestImpl().asInstanceOf[Scala2PrologDeclarationTest]
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
    }
  }
}
