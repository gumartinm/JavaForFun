package de.spring.example.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Test {
	private static final Logger LOGGER = LoggerFactory.getLogger(Test.class);

	public int myMethod()
	{
		LOGGER.info("The Advice should be run before.");
		
		//This value will be caught by the Advice with the @AfterReturning annotation.
		return 666;
	}
	
	public int anotherExample()
	{
		LOGGER.info("The Advice should be run before and after.");
		return 666;
		
	}
	
    public class InnerTest {
        public void innerMethod() {
        	LOGGER.info("I am the inner class. The Advice should be run after."
        			+ " NO NEED OF DECLARING Spring BEANS WHEN WEAVING!!!!");
        }
    }
}
