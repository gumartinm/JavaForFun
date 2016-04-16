package de.spring.example.aspects;


import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Aspect
//Higher values has lower priority
//@Order(1) Just works when using Spring AOP proxies
//When weaving order is given by @DeclarePrecedence annotation, see: MyAspectsOrder
public class BeforeMyAspect {
	private static final Logger LOGGER = LoggerFactory.getLogger(BeforeMyAspect.class);

	// With execution we avoid double weaving (when call and when execution)
    @Before("@annotation(de.spring.example.annotation.beforeInitTransactional) && execution(* *(..))")
    public void beforeInitTransactional() {
    	LOGGER.info("I am the Advice beforeInitTransaction.");
    }
}
