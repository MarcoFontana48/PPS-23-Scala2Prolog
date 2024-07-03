package pps.exam.application
package scala2prolog.annotation

/**
 * Case class that represents the 'types' method field of the @PrologMethod annotation.
 *
 * @param values an array that contains the types of the method field.
 */
case class Types(values: Array[String]) extends Entity

/**
 * Object companion that contains the methods to extract and parse the 'types' method field of the @PrologMethod annotation.
 */
object Types extends Entity:
  def apply(param: Array[String]): Option[Types] =
    isEmpty(param.mkString, {
      logger.trace("types is empty, returning None types...")
      None
    }, {
      logger.trace(s"extracted types from @Prolog* annotation: '${param.mkString("Array(", ", ", ")")}'")
      param.foreach { e =>
        if !e.matches("(Int|Double|String|Boolean|List\\[\\s*(Int|Double|String|Boolean)\\s*])") then
          throw new IllegalArgumentException(s"Invalid type: '$e'. Valid types are 'Int', 'String', 'Boolean', 'List[Int]', 'List[String]', 'List[Boolean]'")
      }
      Some(new Types(param))
    })
