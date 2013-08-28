package de.example.mybatis.spring;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


/**
 * Spring context locator.
 * 
 */
public final class SpringContextLocator {
    private static final Logger logger = Logger.getLogger(SpringContextLocator.class);

    /** Spring ApplicationContext **/
    private final ApplicationContext context;

    /** Spring Context **/
    private static final String SPRING_CONFIG_CONTEXT="spring-config.xml";


    /**
     * Private constructor. Singleton pattern.
     */
    private SpringContextLocator() {
        final String[] factoryFiles = new String[] { SPRING_CONFIG_CONTEXT };

        logger.info("Loading context files " + SpringContextLocator.SPRING_CONFIG_CONTEXT);

        this.context = new ClassPathXmlApplicationContext(factoryFiles);

        logger.info("The context has been loaded successfully!! ");
    }

    /**
     * SingletonHolder Thread-safety. To use an Enum class (see Effective Java
     * Second Edition) if we need serialization and thread-safety.
     */
    private static class SingletonHolder {
        public static final SpringContextLocator INSTANCE = new SpringContextLocator();
    }

    /**
     * Return singleton instance. Thread-safety.
     * 
     * @return Singleton instance.
     */
    public static SpringContextLocator getInstance() {
        return SingletonHolder.INSTANCE;
    }

    /**
     * Return bean from application context.
     * 
     * @param beanId
     *            Bean's id.
     * @return The bean instance.
     */
    public Object getBean(final String beanId) {
        return this.context.getBean(beanId);
    }
}
