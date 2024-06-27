package pps.exam.application.annotation;

import java.lang.annotation.*;

/**
 * Annotation for Prolog classes, to be used to define a common theory to all @PrologMethod methods inside the same
 * class
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface PrologClass {
    String[] clauses() default {};
}
