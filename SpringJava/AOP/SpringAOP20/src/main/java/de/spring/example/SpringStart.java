package de.spring.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.spring.example.service.TestB;
import de.spring.example.web.TestA;

public class SpringStart {
	private static final Logger LOGGER = LoggerFactory.getLogger(SpringStart.class);

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		LOGGER.info("Starting application");
		SpringContextLocator.getInstance();
		
		TestA testA = (TestA) SpringContextLocator.getInstance().getBean("testA");
		testA.myMethod();
		
		TestB testB = (TestB) SpringContextLocator.getInstance().getBean("testB");
		testB.myMethod();
	}
}
