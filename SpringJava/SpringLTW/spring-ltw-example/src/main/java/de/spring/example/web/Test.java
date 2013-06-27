package de.spring.example.web;

import de.spring.example.annotation.commitTransactional;
import de.spring.example.annotation.initTransactional;


public class Test {
    @initTransactional
    public int myMethod()
    {
        System.out.println("The Advice should be run before.");

        //This value will be caught by the Advice with the @AfterReturning annotation.
        return 666;
    }

    public class InnerTest {
        @commitTransactional
        public void innerMethod() {
            System.out.println("I am the inner class. The Advice should be run after. ");
        }
    }
}
