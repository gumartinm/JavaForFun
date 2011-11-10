package de.spring.example;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

@Aspect
public class MyAdvice {
	
	//This advice is connected with the Pointcut defined in SystemArchitecture.
	//So, every method connected to that Pointcut will be run after the
	//method defined in this Advice.
	@Before("de.spring.example.SystemArchitecture.pointCutMethod())")
	public void doAccessCheck() {
		System.out.println("I am the Advice and I will be run before.");
	}
}
