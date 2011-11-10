package de.spring.example.web;


public class Test {
	public int myMethod()
	{
		System.out.println("The Advice should be run before.");
		
		//This value will be caught by the Advice with the @AfterReturning annotation.
		return 666;
	}
}
