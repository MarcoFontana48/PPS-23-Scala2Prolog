package pps.exam.application.annotation;

import java.lang.annotation.*;

/**
 * Annotation for Prolog methods, to be used to link a method to a Prolog query
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PrologMethod {
    String predicate() default "";
    
    String signature() default "";
    
    String types() default "";
    
    String clauses() default "";
}
