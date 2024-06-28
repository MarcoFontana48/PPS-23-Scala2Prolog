package pps.exam.application

import annotation.{PrologAddSharedClauses, PrologClass, PrologMethod}

import alice.tuprolog.Term

trait Scala2PrologDeclarationTestA:
  @PrologMethod(clauses = Array("methodA(a)."))
  def methodA(prologVar: String): Iterable[Term] = null

  @PrologMethod(clauses = Array("methodB(b)."))
  def methodB(prologVar: String): Iterable[Term] = null

  @PrologMethod
  def methodC(prologVar: String): Iterable[Term] = null //use only PrologClass clauses

@PrologClass(clauses = Array("methodA(c).","methodB(c).","methodC(c)."))
class Scala2PrologDeclarationTestANonEmptyClausesImpl extends Scala2PrologDeclarationTestA

@PrologClass
class Scala2PrologDeclarationTestAEmptyClausesImpl extends Scala2PrologDeclarationTestA

trait Scala2PrologDeclarationTestB:
  @PrologAddSharedClauses(clauses = Array("methodA(a)."))
  def clausesAdder(): Unit = ()

  @PrologAddSharedClauses
  def clausesAdderEmptyAnnotation(): Unit = ()

  @PrologAddSharedClauses(clauses = Array("methodA(b)."))
  def clausesAdderNonEmptyBody(x: Int, y: Int): Int = x + y

  @PrologMethod
  def methodA(prologVar: String): Iterable[Term] = null

@PrologClass(clauses = Array("methodA(c).","methodB(c)."))
class Scala2PrologDeclarationTestBNonEmptyClausesImpl extends Scala2PrologDeclarationTestB

@PrologClass
class Scala2PrologDeclarationTestBEmptyClausesImpl extends Scala2PrologDeclarationTestB

class Scala2PrologDeclarationTestBNotAnnotatedClassImpl extends Scala2PrologDeclarationTestB