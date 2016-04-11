package de.spring.example;

import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Aspect
public class MyAdvice {
	private static final Logger LOGGER = LoggerFactory.getLogger(MyAdvice.class);

	// With execution we avoid double weaving (when call and when execution)
    @Before("@annotation(de.spring.example.annotation.initTransactional) && execution(* *(..))")
    public void initTransactional()
    {
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
