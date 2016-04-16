package de.spring.example.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.spring.example.annotation.beforeInitTransactional;
import de.spring.example.annotation.commitTransactional;
import de.spring.example.annotation.initTransactional;


public class Test {
	private static final Logger LOGGER = LoggerFactory.getLogger(Test.class);

    @initTransactional
    public int myMethod()
    {
    	LOGGER.info("The Advice should be run before.");

    	annotatedPrivateMethod();
    	
    	InnerTest innerTest = new InnerTest();
    	innerTest.innerMethod();
    	
        return 666;
    }

    public class InnerTest {
        @commitTransactional
        public void innerMethod() {
        	LOGGER.info("I am the inner class. The Advice should be run after."
        			+ " NO NEED OF DECLARING Spring BEANS WHEN WEAVING!!!!");
        }
    }
    
    // IT WORKS WHEN WEAVING!!!
    @initTransactional
    @beforeInitTransactional
    private void annotatedPrivateMethod() {
    	LOGGER.info("The Advice should be run before even with private methods because I AM WEAVING."
    			+ " IT WORKS EVEN CALLING FROM METHOD OF THE SAME CLASS. It doesn't when using proxies AOP.");
    }
}
