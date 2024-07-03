package pps.exam.application
package scala2prolog

import scala2prolog.interceptor.PrologInterceptor

import org.apache.logging.log4j.scala.Logging

/**
 * This object is used to create a new S2P proxy instance of the original object passed as argument to handle the
 * annotated Prolog logic execution.
 */
object Scala2Prolog extends Logging:
  /**
   * Method to create a new proxy instance of the original object.
   * The proxy instance will handle the annotated Prolog logic.
   *
   * @return a new proxy instance of the original object.
   */
  extension [A](originalObject: A) def asPrologProxy: A =
    PrologInterceptor.create(originalObject)