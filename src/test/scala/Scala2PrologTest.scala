package pps.exam.application

import alice.tuprolog.Term

class Scala2PrologTest extends AbstractTest {
  "Scala2Prolog" should {
    "reuse the same prolog engine in multiple method calls of a @PrologClass, resulting the same common class theory" +
      "but isolated method theory" in {
      val scala2PrologDeclarationTest = Scala2PrologDeclarationTestImpl().asInstanceOf[Scala2PrologDeclarationTest]
      val proxy = Scala2Prolog.newProxyInstanceOf(scala2PrologDeclarationTest)
      val prologResultA = proxy.methodA("X")
      val prologResultB = proxy.methodB("X")

      assert(prologResultB === Iterable(
        Term.createTerm("methodB(c)"),
        Term.createTerm("methodB(b)")
      ))

      assert(prologResultA === Iterable(
        Term.createTerm("methodA(c)"),
        Term.createTerm("methodA(a)")
      ))
    }
  }
}
