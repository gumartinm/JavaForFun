package de.spring.example;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * 
 * We are using here the @AspectJ annotations to declare 
 * Proxies. If we want to use these kinds of proxies on the Spring framework
 * we have to use the <aop:aspectj-autoproxy/> annotation on the Spring xml file
 * (the Spring context file)
 */
@Aspect
public class SystemArchitecture {
	
	//Coonecting to the execution of any method defined in the 
	//package: de.spring.example.web
	//We are connecting the methods defined in that package with this
	//Pointcut. So, when executing any of those methods defined in that
	//package we will run the Advice related to this Pointcut (if there is an Advice)
	@Pointcut("execution(* de.spring.example.web.*.*(..))")
	public void pointCutMethod() 
	{
		System.out.println("I am the Pointcut and you will never see me.");
		//This is the PointCut. 
		//You can write code here, but it will be useless because while running
		//the methods connected to the Pointcut, this code will not be executed.
		//Just the advice will run!!!! :/
		//Is not this weird? We are here defining a method whose code 
		//will never be run. When the hell should we write code here?
		//This is a waste of time and code IMHO. Isn't it?
	}
	
	//NOTICE: YOU DO NOT NEED TO CREATE A SPECIAL CLASS FOR THE ADVICE
	//        YOU COULD USE THE SAME CLASS FOR THE POINTCUTS AND FOR THE
	//		  ADVICES. IN THIS CASE FOR EXAMPLE WE HAVE THE @AfterReturning
	//		  ADVICE IN THIS CLASS AND THE @Before ADVICE IN THE CLASS CALLED
	//		  MyAdvice
	//This advice is connected with the another Pointcut.
	//The returning value of every method connected to that Pointcut
	//will be caught by this method.
	@AfterReturning(pointcut="de.spring.example.SystemArchitecture.pointCutMethod())",
					returning="retVal")
	public void doAccessCheck(final Object retVal) {
		System.out.println("The returned value by the method " +
											"connected to the Pointcut: " + retVal);
	}
}