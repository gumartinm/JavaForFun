/**
 * 
 */
package de.spring.example.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.springframework.stereotype.Component;


@Retention(RetentionPolicy.RUNTIME)
public @interface CustomTransactional {
}
