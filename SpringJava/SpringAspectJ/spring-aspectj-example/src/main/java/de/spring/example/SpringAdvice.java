package de.spring.example;

import java.lang.reflect.Method;
import org.springframework.aop.MethodBeforeAdvice;

/**
 * 
 * We are using here an Advice of Spring 1.2
 * See: http://static.springsource.org/spring/docs/3.1.0.RC1/spring-framework-reference/html/aop-api.html#aop-api-advice-types
 *
 */
public class SpringAdvice implements MethodBeforeAdvice {

	public void before(Method m, Object[] args, Object target) throws Throwable {
		System.out.println("I am the SpringAdvice and I will be run before.");
	}
	
}
