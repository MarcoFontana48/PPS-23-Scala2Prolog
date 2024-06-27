package pps.exam.application

import annotation.PrologInterceptor

import org.apache.logging.log4j.scala.Logging

/**
 * This object provides a method to set a theory and solve a goal in Prolog.
 */
object Scala2Prolog extends Logging:
  def newProxyInstanceOf[A](originalObject: A): A =
    PrologInterceptor.create(originalObject)