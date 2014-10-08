package de.example.sql.deadlocks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.example.sql.deadlocks.example.CustomAnnotationExample;


public class Main {
	private static final Logger logger = LoggerFactory.getLogger(Main.class);
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		logger.info("Starting application");
		
		CustomAnnotationExample customAnnotation =
				(CustomAnnotationExample) SpringContextLocator.getInstance().getBean("customAnnotation");
		customAnnotation.doCustomAnnotationExample();
		logger.info("End application");
	}
}
