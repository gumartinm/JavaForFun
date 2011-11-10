package de.spring.example.web;

import de.spring.example.annotation.commitTransactionalN2A;
import de.spring.example.annotation.initTransactionalN2A;


public class Test {
	@initTransactionalN2A
	public int myMethod()
	{
		System.out.println("The Advice should be run before.");
		
		//This value will be caught by the Advice with the @AfterReturning annotation.
		return 666;
	}
	
	public class InnerTest {
		@commitTransactionalN2A
		public void innerMethod() {
			System.out.println("I am the inner class. The Advice should be run after. ");
		}	
	}
}
