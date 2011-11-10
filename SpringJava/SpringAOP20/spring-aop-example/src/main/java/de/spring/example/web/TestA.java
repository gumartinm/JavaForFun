package de.spring.example.web;


public class TestA {
	public int myMethod()
	{
		System.out.println("TestA: The Advice should be run before.");
		
		//This value will be caught by the Advice with the @AfterReturning annotation.
		return 666;
	}
}
