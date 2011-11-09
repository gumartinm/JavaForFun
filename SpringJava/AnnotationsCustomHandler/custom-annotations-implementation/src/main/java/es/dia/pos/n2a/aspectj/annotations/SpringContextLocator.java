package de.spring.example;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


/**
 * Localizador de beans para de los dispositivos
 *
 * @author rvp001es
 */
public final class SpringContextLocator {


	// Singleton Pattern
	private static SpringContextLocator instance;

	// Spring ApplicationContext
	private static ApplicationContext context;

	// Dispositivos logicos
	private static final String SPRING_CONFIG_CONTEXT="spring-config.xml";
	//private static final String DATABASE_CONFIG="database-config.xml";

	
	/**
	 * Private constructor. Singleton pattern.
	 */
	private SpringContextLocator() {
		String[] factoryFiles = null;
		System.out.println("Loading files context " + 
										SpringContextLocator.SPRING_CONFIG_CONTEXT);

		factoryFiles = new String[] { SPRING_CONFIG_CONTEXT };
		
		SpringContextLocator.context = new ClassPathXmlApplicationContext(factoryFiles);

		System.out.println("The N2A devices context and test " +
										"context has been loaded successfully!! ");
	}

	/**
	 * Singleton pattern. GetInstance()
	 */
	public synchronized static SpringContextLocator getInstance() {
		if (SpringContextLocator.instance == null) {
			SpringContextLocator.instance = new SpringContextLocator();
		}
		return SpringContextLocator.instance;
	}

	/**
	 * Return a bean in application context.
	 */
	public Object getBean(final String name) {
		return SpringContextLocator.context.getBean(name);
	}
}
