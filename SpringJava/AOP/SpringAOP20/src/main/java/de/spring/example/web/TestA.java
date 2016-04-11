package de.spring.example.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestA {
	private static final Logger LOGGER = LoggerFactory.getLogger(TestA.class);

	public int myMethod()
	{
		LOGGER.info("TestA: The Advice should be run before.");
		
		//This value will be caught by the Advice with the @AfterReturning annotation.
		return 666;
	}
}
