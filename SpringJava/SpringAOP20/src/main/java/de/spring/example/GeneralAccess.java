package de.spring.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * This class is not using the AspectJ annotations, so we could use it on JDK 1.4 and below.
 * If we want to use it as an Aspect we may JUST do it using a schema based declaration.
 * What means, you can use this class as an Aspect JUST using an xml Spring declaration. 
 *
 */
public class GeneralAccess {
	private static final Logger LOGGER = LoggerFactory.getLogger(GeneralAccess.class);

		
	public void monitor()
	{
		LOGGER.info("I am the Advice monitor for TestB and I will be run before.");
	}
}
