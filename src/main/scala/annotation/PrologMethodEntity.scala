package pps.exam.application
package annotation

trait PrologMethodEntity

case class Signatures(inputVars: Array[String], outputVars: Array[String]) extends PrologMethodEntity

case class Types(types: Array[String]) extends PrologMethodEntity

case class Clauses(clauses: Array[String]) extends PrologMethodEntity

case class Predicate(name: String, variables: Map[String,Array[String]]) extends PrologMethodEntity:
  def formatPredicate(inputValues: String*): String = inputValues match
    case values if values.nonEmpty => values.length match
        case valuesLength if valuesLength == variables("+").length => name + "(" + values.mkString(", ") + ", " + variables("-").mkString(", ") + ")."
        case _ => throw new IllegalArgumentException("Invalid number of input values")
    case _ => name + "(" + variables("-").mkString(", ") + ")."

