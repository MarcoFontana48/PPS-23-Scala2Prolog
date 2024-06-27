package pps.exam.application

import interceptor.PrologInterceptor

import org.apache.logging.log4j.scala.Logging

/**
 * This object is used to create a new S2P proxy instance of the original object passed as argument to handle the
 * annotated Prolog logic execution.
 */
object Scala2Prolog extends Logging:
  def newProxyInstanceOf[A](originalObject: A): A =
    PrologInterceptor.create(originalObject)