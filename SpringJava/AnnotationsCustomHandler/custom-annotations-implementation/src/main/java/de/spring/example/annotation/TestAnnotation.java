/**
 * 
 */
package de.spring.example.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * We us this annotation to show how to invoke methods with annotations
 * using Java reflection.
 * This annotation will not be searched by Spring.
 *
 */

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface TestAnnotation {

}
