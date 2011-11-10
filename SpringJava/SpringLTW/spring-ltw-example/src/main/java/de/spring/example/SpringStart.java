package de.spring.example;

import de.spring.example.web.Test;

public class SpringStart {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("Starting application");
		SpringContextLocator.getInstance();
		
		Test test = (Test) SpringContextLocator.getInstance().getBean("testOuter");
		test.myMethod();
		
		Test.InnerTest testInner = (Test.InnerTest) SpringContextLocator.getInstance().getBean("testInner");
		testInner.innerMethod();
	}
}
