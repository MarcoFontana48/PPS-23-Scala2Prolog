package pps.exam.application
package scala2prolog

import scala2prolog.annotation.{PrologClass, PrologWrapper, PrologMethod}

import alice.tuprolog.Term
import org.apache.logging.log4j.scala.Logging

trait PrologParserTest extends Logging:
  @PrologMethod(clauses = Array(
    "expr(L,R) :- term(L,R).",
    "expr(L,R) :- term(L,[’+’|R2]), expr(R2,R).",
    "expr(L,R) :- term(L,[’-’|R2]), expr(R2,R)."
  ))
  def expr(x: List[String], y: List[String]): Iterable[Term] = null

@PrologWrapper
trait PrologParserWrapperTest extends PrologParserTest:

  def parse_expr(x: List[String], y: List[String]): Boolean =
    val proxy = Scala2Prolog.newProxyInstanceOf(this)
    val solution = proxy.expr(x, y)
    logger.trace(s"solution: '${solution.toList}', returning '${solution.nonEmpty}'...")
    solution.nonEmpty

@PrologClass(clauses = Array(
  "term(L,R) :- fact(L,R).",
  "term(L,R) :- fact(L,[’*’|R2]), term(R2,R).",
  "term(L,R) :- fact(L,[’/’|R2]), term(R2,R).",
  "fact(L,R):-num(L,R).",
  "fact([’(’ | E],R):-expr(E,[’)’|R]).",
  "num([L|R],R):-num_atom(_,L)."
))
class PrologParserImplTest extends PrologParserWrapperTest