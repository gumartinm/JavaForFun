package de.spring.example.service;

public class TestB {
	public int myMethod()
	{
		System.out.println("TestB: The Advice should be run before.");
		
		//This value will be caught by the Advice with the @AfterReturning annotation.
		return 999;
	}
}
