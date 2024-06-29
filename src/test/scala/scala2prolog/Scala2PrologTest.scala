package pps.exam.application
package scala2prolog

import alice.tuprolog.Term
import org.apache.logging.log4j.scala.Logging

class Scala2PrologTest extends AbstractTest with Logging:

  import scala2prolog.Scala2Prolog.newProxyInstanceOf

  "Scala2Prolog" should :
    "reuse the same clauses in multiple calls of method's annotated with @PrologMethod from a common class annotated " +
      "with @PrologClass" in :
      val scala2PrologDeclarationTest = Scala2PrologDeclarationTestANonEmptyClausesImpl().asInstanceOf[Scala2PrologDeclarationTestA]
      val proxy = newProxyInstanceOf(scala2PrologDeclarationTest)
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
      val proxy = newProxyInstanceOf(scala2PrologDeclarationTest)
      val prologResultC = proxy.methodC("X")

      assert(prologResultC === Iterable(Term.createTerm("methodC(c)")))

  "Scala2Prolog" should :
    "not consider @PrologClass' clauses if empty, and parse only @PrologMethod's clauses instead" in :
      val scala2PrologDeclarationTest = Scala2PrologDeclarationTestAEmptyClausesImpl().asInstanceOf[Scala2PrologDeclarationTestA]
      val proxy = newProxyInstanceOf(scala2PrologDeclarationTest)
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

  "Scala2Prolog" should :
    "return an empty Iterable of Term if no @PrologMethod nor @PrologClass clauses were defined, because the set" +
    "Theory in the tuProlog engine will be empty" in :
      val scala2PrologDeclarationTest = Scala2PrologDeclarationTestAEmptyClausesImpl().asInstanceOf[Scala2PrologDeclarationTestA]
      val proxy = newProxyInstanceOf(scala2PrologDeclarationTest)
      val prologResultC = proxy.methodC("X")

      assert(prologResultC === Iterable.empty)

  "Scala2Prolog" should :
    "add @PrologAddSharedClauses' clauses along with @PrologClass's clauses when invoking the method, plus " +
    "executing @PrologAddSharedClauses method's body" in :
      //@PrologClass clauses are 'Array("methodA(c).","methodB(c).")'
      val scala2PrologDeclarationTest = Scala2PrologDeclarationTestBNonEmptyClausesImpl().asInstanceOf[Scala2PrologDeclarationTestB]
      val proxy = newProxyInstanceOf(scala2PrologDeclarationTest)
      val prologResultA = proxy.methodA("X")                    // ?- methodA(X). CURRENT_THEORY: methodA(c)
      proxy.clausesAdder()                                      // ?-     /       UPDATED THEORY: methodA(c), methodA(a)              METHOD BODY IS EMPTY
      val prologResultB = proxy.methodA("X")                    // ?- methodA(X). CURRENT_THEORY: methodA(c), methodA(a)
      val prologResultC = proxy.clausesAdderNonEmptyBody(1, 2)  // ?-     /       UPDATED THEORY: methodA(c), methodA(a), methodA(b)  ALSO EXECUTES METHOD BODY
      val prologResultD = proxy.methodA("X")                    // ?- methodA(X). CURRENT_THEORY: methodA(c), methodA(a), methodA(b)

      assert((prologResultA, prologResultB, prologResultC, prologResultD) === (
        Iterable(Term.createTerm("methodA(c)")),
        Iterable(Term.createTerm("methodA(c)"), Term.createTerm("methodA(a)")),
        3,
        Iterable(Term.createTerm("methodA(c)"), Term.createTerm("methodA(a)"), Term.createTerm("methodA(b)"))
      ))

  "Scala2Prolog" should :
    "add @PrologAddSharedClauses' clauses adding @PrologClass's clauses from initial empty state when " +
      "invoking the method" in :
      //@PrologClass clauses are empty
      val scala2PrologDeclarationTest = Scala2PrologDeclarationTestBEmptyClausesImpl().asInstanceOf[Scala2PrologDeclarationTestB]
      val proxy = newProxyInstanceOf(scala2PrologDeclarationTest)
      val prologResultA = proxy.methodA("X")                    // ?- methodA(X). CURRENT_THEORY: *empty*
      proxy.clausesAdder()                                      // ?-     /       UPDATED THEORY: methodA(a)              METHOD BODY IS EMPTY
      val prologResultB = proxy.methodA("X")                    // ?- methodA(X). CURRENT_THEORY: methodA(a)
      val prologResultC = proxy.clausesAdderNonEmptyBody(1, 2)  // ?-     /       UPDATED THEORY: methodA(a), methodA(b)  ALSO EXECUTES METHOD BODY
      val prologResultD = proxy.methodA("X")                    // ?- methodA(X). CURRENT_THEORY: methodA(a), methodA(b)

      assert((prologResultA, prologResultB, prologResultC, prologResultD) === (
        Iterable.empty,
        Iterable(Term.createTerm("methodA(a)")),
        3,
        Iterable(Term.createTerm("methodA(a)"), Term.createTerm("methodA(b)"))
      ))

  "Scala2Prolog" should :
    "add @PrologAddSharedClauses' clauses as common clauses to all @PrologMethod's even when their class is not" +
      "annotated with @PrologClass" in :
      //@PrologClass annotation is not present (it's an optional annotation used only to define initial common clauses for each @PrologMethod)
      val scala2PrologDeclarationTest = Scala2PrologDeclarationTestBNotAnnotatedClassImpl().asInstanceOf[Scala2PrologDeclarationTestB]
      val proxy = newProxyInstanceOf(scala2PrologDeclarationTest)
      val prologResultA = proxy.methodA("X")                    // ?- methodA(X). CURRENT_THEORY: *empty*
      proxy.clausesAdder()                                      // ?-     /       UPDATED THEORY: methodA(a)              METHOD BODY IS EMPTY
      val prologResultB = proxy.methodA("X")                    // ?- methodA(X). CURRENT_THEORY: methodA(a)
      val prologResultC = proxy.clausesAdderNonEmptyBody(1, 2)  // ?-     /       UPDATED THEORY: methodA(a), methodA(b)  ALSO EXECUTES METHOD BODY
      val prologResultD = proxy.methodA("X")                    // ?- methodA(X). CURRENT_THEORY: methodA(a), methodA(b)

      assert((prologResultA, prologResultB, prologResultC, prologResultD) === (
        Iterable.empty,
        Iterable(Term.createTerm("methodA(a)")),
        3,
        Iterable(Term.createTerm("methodA(a)"), Term.createTerm("methodA(b)"))
      ))

  "Scala2Prolog" should :
    "throw an exception when invoking a method annotated with both @PrologMethod and @PrologAddSharedClauses" in :
      val scala2PrologDeclarationTest = Scala2PrologDeclarationTestBNonEmptyClausesImpl().asInstanceOf[Scala2PrologDeclarationTestB]
      val proxy = newProxyInstanceOf(scala2PrologDeclarationTest)
      assertThrows[IllegalArgumentException](proxy.methodException("X"))

  "Scala2Prolog" should :
    "find nested lists results" in :
      val scala2PrologDeclarationTest = Scala2PrologDeclarationTestCImpl().asInstanceOf[Scala2PrologDeclarationTestC]
      val proxy = newProxyInstanceOf(scala2PrologDeclarationTest)
      val prologResult = proxy.memberNested("a", List(List("a", "b"), List("c", "d")))
      assert(prologResult === Iterable(
        Term.createTerm("memberNested(a,[[a,b],[c,d]])")
      ))



  "The parser" should :
    "parse the argument using the tuProlog engine and return a boolean (true) when the argument is a valid " +
      "arithmetic expression" in :
      val scala2PrologDeclarationParserTest = PrologParserImplTest().asInstanceOf[PrologParserWrapperTest]
      val actual = scala2PrologDeclarationParserTest.parse_expr(List("'('","'3'","'+'","'4'","'*'","'2'","')'"), List.empty)  // (3+4*2)
      assert(actual === true)

  "The parser" should :
    "parse the argument using the tuProlog engine and return a boolean (false) when the argument is not a valid " +
      "arithmetic expression" in :
      val scala2PrologDeclarationParserTest = PrologParserImplTest().asInstanceOf[PrologParserWrapperTest]
      val actual = scala2PrologDeclarationParserTest.parse_expr(List("'('","'3'","'#'","'4'","'*'","'2'","')'"), List.empty)  // (3#4*2)
      assert(actual === false)



  "The path finding algorithm" should :
    "find all possible paths with the current active actions from the starting point to the exit point" in :
      val scala2PrologDeclarationPathFindingAlgorithmTest = Scala2PrologDeclarationPathFindingAlgorithmTestImpl().asInstanceOf[Scala2PrologDeclarationPathFindingAlgorithmTest]
      val pathFindingAlgorithmProxy = newProxyInstanceOf(scala2PrologDeclarationPathFindingAlgorithmTest)

      var activeActions: List[String] = List()
      logger.trace(s"currently active actions: $activeActions")
      activeActions = pathFindingAlgorithmProxy.addActionUp(activeActions)

      logger.trace(s"currently active actions: $activeActions")
      activeActions = pathFindingAlgorithmProxy.addActionRight(activeActions)
      val upRightActionsActiveActual = pathFindingAlgorithmProxy.find_plan(List(1, 1), List(2, 2), "Plan")
      val upRightActionsActiveExpected = Iterable.empty

      logger.trace(s"currently active actions: $activeActions")
      activeActions = pathFindingAlgorithmProxy.addActionDown(activeActions)
      val upRightDownActionsActiveActual = pathFindingAlgorithmProxy.find_plan(List(1, 1), List(2, 2), "Plan")
      val upRightDownActionsActiveExpected = Iterable(
        Term.createTerm("find_plan([1,1],[2,2],[right,down])"),
        Term.createTerm("find_plan([1,1],[2,2],[down,right])"),
        Term.createTerm("find_plan([1,1],[2,2],[down,down,right,up])")
      )

      logger.trace(s"currently active actions: $activeActions")
      activeActions = pathFindingAlgorithmProxy.addActionLeft(activeActions)

      logger.trace(s"currently active actions: $activeActions")
      val allActionsActiveActual = pathFindingAlgorithmProxy.find_plan(List(1, 1), List(2, 2), "Plan")
      val allActionsActiveExpected = Iterable(
        Term.createTerm("find_plan([1,1],[2,2],[right,right,down,down,left,up])"),
        Term.createTerm("find_plan([1,1],[2,2],[right,right,down,down,left,left,up,right])"),
        Term.createTerm("find_plan([1,1],[2,2],[right,right,down,left])"),
        Term.createTerm("find_plan([1,1],[2,2],[right,down])"),
        Term.createTerm("find_plan([1,1],[2,2],[down,right])"),
        Term.createTerm("find_plan([1,1],[2,2],[down,down,right,up])"),
        Term.createTerm("find_plan([1,1],[2,2],[down,down,right,right,up,up,left,down])"),
        Term.createTerm("find_plan([1,1],[2,2],[down,down,right,right,up,left])")
      )

      assert((upRightActionsActiveActual, upRightDownActionsActiveActual, allActionsActiveActual) === (upRightActionsActiveExpected, upRightDownActionsActiveExpected, allActionsActiveExpected))
