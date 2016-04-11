package de.spring.example.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestB {
	private static final Logger LOGGER = LoggerFactory.getLogger(TestB.class);

	public int myMethod()
	{
		LOGGER.info("TestB: The Advice should be run before.");
		
		//This value will be caught by the Advice with the @AfterReturning annotation.
		return 999;
	}
}
