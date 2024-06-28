package pps.exam.application

import org.apache.logging.log4j.scala.Logging
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

abstract class AbstractTest
  extends AnyWordSpec 
  with Matchers
  with Logging