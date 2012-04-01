package de.javapos.example;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Declare classes as thread-safe. It is assumed that objects of these 
 * classes are thread-safe and can be shared between different threads. *
 */
@Target(value = ElementType.TYPE)
public @interface ThreadSafe {

}
