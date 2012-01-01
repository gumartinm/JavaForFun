package de.spring.example;

import de.spring.example.web.Test;

public class SpringStart {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("Starting application");
		SpringContextLocator.getInstance();
		
		Test test = (Test) SpringContextLocator.getInstance().getBean("test");
		test.myMethod();
		test.anotherExample();
	}
}
