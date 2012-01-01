package de.spring.example;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
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
	
	
	//NOTICE: YOU DO NOT NEED TO CREATE A SPECIAL CLASS FOR POINTCUTS
	//        YOU COULD DEFINE AN ADVICE WITHOUT A POINTCUT
	//This advice has a PointCut defined like execution(* de.spring.example.web.Test.anotherExample())
	//right here wihout a special PointCut method. This advice has itself the PointCut
	@Around("execution(* de.spring.example.web.Test.anotherExample())")
	public Object doAround(ProceedingJoinPoint pjp) {
		System.out.println("I am the Advice and I will be run before and after. BEFORE");
		// start stopwatch
		// This local variable will store the returned value from the method anotherExample()
	    Object retVal=null;
		try {
			//Calling the real method
			retVal = pjp.proceed();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	    // stop stopwatch
	    System.out.println("I am the Advice and I will be run before and after. AFTER " + retVal);
	    return retVal;
	}
}
