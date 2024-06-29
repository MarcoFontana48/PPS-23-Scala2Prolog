package pps.exam.application.scala2prolog.annotation;

import java.lang.annotation.*;

/**
 * optional annotation to identify a class as a wrapper for the one that the @PrologClass' class should have extended
 * meaning that its methods' body have a proxy that calls @PrologMethod's methods in the class it extends
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface PrologWrapper {}
