package de.example.sql.deadlocks.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import de.example.sql.deadlocks.annotation.DeadlockRetry;

//@Transactional
public class CustomAnnotationExample {
	private static final Logger logger = LoggerFactory.getLogger(CustomAnnotationExample.class);
	
	@DeadlockRetry(maxTries = 10, interval = 5000)
    public void doCustomAnnotationExample() {
		logger.info("Running doCustomAnnotationExample");
    } 
}
