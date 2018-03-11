package de.spring.example.context;

import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

/**
 * JPA Entity classes do not allow you to inject beans.
 * I tried to use this class for injecting beans in them but it did not either work :(
 * No way of injecting beans in JPA Entity classes :( 
 */
@Named("staticContextHolder")
public class StaticContextHolder implements BeanFactoryAware {
	private static final Logger LOGGER = LoggerFactory.getLogger(StaticContextHolder.class);
	
	private static BeanFactory CONTEXT;
	
	public StaticContextHolder() {
		
	}
	
	public static Object getBean(String bean) {
		return CONTEXT.getBean(bean);
	}
	
	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		if (CONTEXT != null) {
			LOGGER.warn("CONTEXT is not null!!!");
		}

		CONTEXT = beanFactory;
	}

}
