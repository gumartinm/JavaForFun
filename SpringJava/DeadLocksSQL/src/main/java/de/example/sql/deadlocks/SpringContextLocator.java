package de.example.sql.deadlocks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public final class SpringContextLocator {
	private static final Logger logger = LoggerFactory.getLogger(SpringContextLocator.class);
	// Spring Context
	private static final String SPRING_CONFIG_CONTEXT="/spring-config.xml";
	// Spring ApplicationContext
	private final ApplicationContext context;
	
	
	private SpringContextLocator() {
		logger.info("Loading context files: " + SpringContextLocator.SPRING_CONFIG_CONTEXT);
		
		final String[] factoryFiles = new String[] { SPRING_CONFIG_CONTEXT };
		context = new ClassPathXmlApplicationContext(factoryFiles);
		
		logger.info("The context has been loaded successfully!! ");	
	}
	
	private static class SingletonHolder {
		private static final SpringContextLocator INSTANCE = new SpringContextLocator();
	}
	
	public static SpringContextLocator getInstance() {
		return SingletonHolder.INSTANCE;
	}
	
	public Object getBean(final String name) {
		return context.getBean(name);
	}
}
