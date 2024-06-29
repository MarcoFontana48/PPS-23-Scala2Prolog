package pps.exam.application
package scala2prolog.annotation

import org.apache.logging.log4j.scala.Logging

/**
 * trait that represents only the fields that can be extracted from any the Scala2Prolog annotation.
 */
trait Entity extends Logging:
  /**
   * Method to check if the extracted field of the annotation is empty
   *
   * @param param   a string that represents the value of the method field of the @PrologMethod annotation.
   * @param default a default call-by-name parameter function (that is evaluated each time it is used within the method) to be
   *                returned if the method field is empty.
   * @param action  an action call-by-name parameter function (that is evaluated each time it is used within the method) to be
   *                executed if the method field is not empty.
   * @tparam T the return type of the method field.
   * @return a generic type T that contains the extracted and parsed field of the annotation
   */
  def isEmpty[T](param: String, default: => T, action: => T): T = param match
    case p if p.isEmpty =>
      default
    case _ =>
      action
