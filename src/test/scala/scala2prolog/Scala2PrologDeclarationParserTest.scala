package pps.exam.application
package scala2prolog

import scala2prolog.annotation.{PrologClass, PrologWrapper, PrologMethod}

import alice.tuprolog.Term
import org.apache.logging.log4j.scala.Logging

trait PrologParserTest extends Logging:
  @PrologMethod(clauses = Array(
    "expr(L,R) :- term(L,R).",                    // an expression can be a term.
    "expr(L,R) :- term(L,['+'|R2]), expr(R2,R).", // an expression can be a term followed by a '+' and another expression.
    "expr(L,R) :- term(L,['-'|R2]), expr(R2,R)."  // an expression can be a term followed by a '-' and another expression.
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
  "term(L,R) :- fact(L,R).",                    // a term can be a fact.
  "term(L,R) :- fact(L,['*'|R2]), term(R2,R).", // a term can be a fact followed by a '*' and another term.
  "term(L,R) :- fact(L,['/'|R2]), term(R2,R).", // a term can be a fact followed by a '/' and another term.
  "fact(L,R):-num(L,R).",                       // a fact can be a number.
  "fact(['(' | E],R) :- expr(E,[')'|R]).",      // a fact can be an expression enclosed in parentheses.
  "num([L|R],R) :- num_atom(_,L)."              // a number can be a number atom.
))
class PrologParserImplTest extends PrologParserWrapperTest