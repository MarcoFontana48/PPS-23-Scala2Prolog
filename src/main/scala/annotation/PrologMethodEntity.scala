package pps.exam.application
package annotation

trait PrologMethodEntity

case class Signature(inputVars: Array[String], outputVars: Array[String]) extends PrologMethodEntity

case class Type(types: Array[String]) extends PrologMethodEntity
