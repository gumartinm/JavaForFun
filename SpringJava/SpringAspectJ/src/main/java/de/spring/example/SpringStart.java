package de.spring.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.spring.example.web.Test;

public class SpringStart {
	private static final Logger LOGGER = LoggerFactory.getLogger(SpringStart.class);

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		LOGGER.info("Starting application");
		SpringContextLocator.getInstance();
		
		Test test = (Test) SpringContextLocator.getInstance().getBean("test");
		test.myMethod();
		test.anotherExample();
	}
}
