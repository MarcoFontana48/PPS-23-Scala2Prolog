package pps.exam.application

import org.apache.logging.log4j.LogManager

object main:
  private val logger = LogManager.getLogger(this.getClass)

  def main(args: Array[String]): Unit =
    logger.trace("Hello world!")
