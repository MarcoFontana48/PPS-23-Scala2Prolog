package pps.exam.application.scala2prolog.annotation;

import java.lang.annotation.*;

/**
 * Annotation to add clauses to already existing Prolog classes, to be used to define a common theory to
 * all @PrologMethod methods inside the same Prolog class
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PrologAddSharedClauses {
    String[] clauses() default {};
}
