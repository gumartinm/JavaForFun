package de.spring.example.aspects;


import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.spring.example.TransactionManager;

@Aspect
// Higher values has lower priority
// @Order(2) Just works when using Spring AOP proxies
//When weaving order is given by @DeclarePrecedence annotation, see: MyAspectsOrder
public class MyAspect {
	private static final Logger LOGGER = LoggerFactory.getLogger(MyAspect.class);

	// With execution we avoid double weaving (when call and when execution)
    @Before("@annotation(de.spring.example.annotation.initTransactional) && execution(* *(..))")
    public void initTransactional() {
    	LOGGER.info("I am the Advice initTransaction.");
        TransactionManager.getInstance().initTransaction();
    }


	// With execution we avoid double weaving (when call and when execution)
    @After("@annotation(de.spring.example.annotation.commitTransactional) && execution(* *(..))")
    public void commitTransactional() {
    	LOGGER.info("I am the Advice commitTransaction.");
        TransactionManager.getInstance().commitTransaction();
    }
}
