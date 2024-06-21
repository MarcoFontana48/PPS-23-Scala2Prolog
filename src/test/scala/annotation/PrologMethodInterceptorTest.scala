package pps.exam.application
package annotation

import org.apache.logging.log4j.scala.Logging
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

trait TestPrologMethod {
  @PrologMethod(predicate = "test_predicate", types = Array("List<Int>", "List<Int>"), clauses = Array("test_clauses"))
  def test_annotated_method(a: Int): Int

  def test_not_annotated_method(a: Int): Int
}

class TestPrologClass extends Logging with TestPrologMethod {
  def test_annotated_method(a: Int): Int =
    logger.debug(s"arg: '$a'")
    a

  def test_not_annotated_method(a: Int): Int =
    logger.debug(s"arg: '$a'")
    a
}

class PrologMethodInterceptorTest extends AnyWordSpec with Matchers {
  val testPrologClass: TestPrologMethod = PrologMethodInterceptor.create(new TestPrologClass).asInstanceOf[TestPrologMethod]
  testPrologClass.test_annotated_method(1)
  testPrologClass.test_not_annotated_method(2)
}
