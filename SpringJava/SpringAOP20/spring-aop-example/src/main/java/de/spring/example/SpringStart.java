package de.spring.example;

import de.spring.example.service.TestB;
import de.spring.example.web.TestA;

public class SpringStart {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("Starting application");
		SpringContextLocator.getInstance();
		
		TestA testA = (TestA) SpringContextLocator.getInstance().getBean("testA");
		testA.myMethod();
		
		TestB testB = (TestB) SpringContextLocator.getInstance().getBean("testB");
		testB.myMethod();
	}
}
