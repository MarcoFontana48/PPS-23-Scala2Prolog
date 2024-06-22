package pps.exam.application
package annotation

trait PrologMethodEntity

case class Signatures(inputVars: Array[String], outputVars: Array[String]) extends PrologMethodEntity

case class Types(types: Array[String]) extends PrologMethodEntity

case class Clauses(clauses: Array[String]) extends PrologMethodEntity

case class Predicate(predicate: String) extends PrologMethodEntity