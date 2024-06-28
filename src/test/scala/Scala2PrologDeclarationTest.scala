package pps.exam.application

import annotation.{PrologClass, PrologMethod}

import alice.tuprolog.Term

trait Scala2PrologDeclarationTest:
  @PrologMethod(clauses = Array("methodA(a)."))
  def methodA(prologVar: String): Iterable[Term] = null

  @PrologMethod(clauses = Array("methodB(b)."))
  def methodB(prologVar: String): Iterable[Term] = null

@PrologClass(clauses = Array("methodA(c).","methodB(c)."))
class Scala2PrologDeclarationTestNonEmptyClausesImpl extends Scala2PrologDeclarationTest

@PrologClass()
class Scala2PrologDeclarationTestEmptyClausesImpl extends Scala2PrologDeclarationTest
