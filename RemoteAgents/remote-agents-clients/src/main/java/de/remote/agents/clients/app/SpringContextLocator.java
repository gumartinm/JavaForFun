package de.remote.agents.clients.app;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringContextLocator {
    private static final Logger logger = Logger.getLogger(SpringContextLocator.class);

    // Singleton Pattern
    private static SpringContextLocator instance;

    // Spring ApplicationContext
    private static ApplicationContext context;

    // Spring Context
    private static final String SPRING_CONFIG_CONTEXT = "clients-remote-agents-spring.xml";

    /**
     * Private constructor. Singleton pattern.
     */
    private SpringContextLocator() {
        logger.info("Loading context files: " + SpringContextLocator.SPRING_CONFIG_CONTEXT);

        final String[] factoryFiles = new String[] { SPRING_CONFIG_CONTEXT };

        context = new ClassPathXmlApplicationContext(factoryFiles);

        logger.info("The context has been loaded successfully!! ");
    }

    /**
     * Singleton pattern not thread safety. To use SingletonHolder pattern as
     * the best approximation otherwise to use an Enum class (see Effective Java
     * Second Edition and ) if we need serialization.
     */
    public static SpringContextLocator getInstance() {
        if (instance == null) {
            instance = new SpringContextLocator();
        }
        return instance;
    }

    /**
     * Return bean from application context.
     */
    public Object getBean(final String name) {
        return context.getBean(name);
    }

}
