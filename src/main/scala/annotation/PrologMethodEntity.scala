package pps.exam.application
package annotation

trait PrologMethodEntity

case class Signatures(inputVars: Array[String], outputVars: Array[String]) extends PrologMethodEntity

case class Types(values: Array[String]) extends PrologMethodEntity

case class Clauses(values: Array[String]) extends PrologMethodEntity

case class Predicate(name: String, values: Map[String,Array[String]]) extends PrologMethodEntity:
  def generateGoal(inputValues: String*): String = inputValues match
    case inputValues if inputValues.nonEmpty => inputValues.length match
        case inputValuesLength if inputValuesLength == values("+").length => name + "(" + inputValues.mkString(", ") + ", " + values("-").mkString(", ") + ")."
        case _ => throw new IllegalArgumentException("Invalid number of input values")
    case _ => name + "(" + values("-").mkString(", ") + ")."

