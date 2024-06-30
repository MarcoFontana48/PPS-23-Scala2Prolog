package pps.exam.application
package scala2prolog

import scala2prolog.annotation.{PrologAddSharedClauses, PrologClass, PrologMethod}

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

  @PrologAddSharedClauses(clauses = Array("methodA(a)."))
  @PrologMethod
  def methodException(prologVar: String): Iterable[Term] = null

@PrologClass(clauses = Array("methodA(c).","methodB(c)."))
class Scala2PrologDeclarationTestBNonEmptyClausesImpl extends Scala2PrologDeclarationTestB

@PrologClass
class Scala2PrologDeclarationTestBEmptyClausesImpl extends Scala2PrologDeclarationTestB

class Scala2PrologDeclarationTestBNotAnnotatedClassImpl extends Scala2PrologDeclarationTestB



trait Scala2PrologDeclarationTestC:
  @PrologMethod(clauses = Array(
    "memberNested(X, [X|_]).",
    "memberNested(X, [[X|_]|_]).",
    "memberNested(X, [[_|T]|_]) :- memberNested(X, [T]).",
    "memberNested(X, [_|T]) :- memberNested(X, T)."
  ))
  def memberNested(x: String, y: List[List[String]]): Iterable[Term] = null

class Scala2PrologDeclarationTestCImpl extends Scala2PrologDeclarationTestC



trait Scala2PrologDeclarationTestInference:

  @PrologMethod
  def num_int(x: String): Int = -1 // method will be intercepted and its body will be ignored

  @PrologMethod
  def num_double(x: String): Double = -1 // method will be intercepted and its body will be ignored

  @PrologMethod
  def bool(x: String): Boolean = false // method will be intercepted and its body will be ignored

  @PrologMethod
  def str(x: String): String = null // method will be intercepted and its body will be ignored

  @PrologMethod
  def list_num_int(x: String, y: String): Iterable[Int] = null // method will be intercepted and its body will be ignored

  @PrologMethod
  def list_num_double(x: String, y: String): Iterable[Double] = null // method will be intercepted and its body will be ignored

  @PrologMethod
  def list_bool(x: String, y: String): Iterable[Boolean] = null // method will be intercepted and its body will be ignored

  @PrologMethod
  def list_str(x: String, y: String): Iterable[String] = null // method will be intercepted and its body will be ignored

@PrologClass(clauses = Array(
  "num_int(1).",
  "num_double(1.23).",
  "str(abc).",
  "bool(true).",
  "list_num_int(1,2).",
  "list_num_double(1.23, 2.23).",
  "list_bool(true, false).",
  "list_str(abc, def).",
))
class Scala2PrologDeclarationTestInferenceImpl extends Scala2PrologDeclarationTestInference