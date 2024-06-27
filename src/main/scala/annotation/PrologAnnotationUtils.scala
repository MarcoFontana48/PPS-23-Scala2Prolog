package pps.exam.application
package annotation

/**
 * trait that represents a S2P utility class.
 */
trait PrologUtils

/**
 * abstract class that represents the utility methods to extract and parse annotations.
 *
 * @tparam A the type of the annotation.
 * @tparam B the return type of each method, covariance is used to allow the return type to be a subtype of B.
 */
abstract class PrologAnnotationUtils[A, +B] extends PrologUtils:
  /**
   * Method to extract and parse the fields of an annotation.
   *
   * @param annotation an annotation.
   * @return a generic type +B that contains the extracted and parsed fields of the annotation
   */
  def extractMethodFields(annotation: A): B
