package pps.exam.application.annotation;

import java.lang.annotation.*;

/**
 * Annotation to add clauses to already existing Prolog classes, to be used to define a common theory to
 * all @PrologMethod methods inside the same Prolog class
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface PrologAddClassClauses {
    String[] clauses() default {};
}
