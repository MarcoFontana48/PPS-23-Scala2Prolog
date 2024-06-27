package pps.exam.application
package annotation

object PrologClassHandler extends PrologClassUtils with S2PHandler

abstract class PrologClassUtils extends PrologExtractorUtils[PrologClass, PrologAnnotationFields] with ClausesExtractor[PrologClass]:
  override def extractMethodFields(prologClass: PrologClass): PrologAnnotationFields =
    Map(
      "clauses" -> extractClauses(prologClass)
    )

  override def extractClauses(prologClass: PrologClass): Option[Clauses] =
    Clauses(prologClass.clauses())
