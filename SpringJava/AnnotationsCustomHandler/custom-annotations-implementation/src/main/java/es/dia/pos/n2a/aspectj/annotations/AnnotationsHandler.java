package de.spring.example;

import java.util.Map;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.generic.GenericBeanFactoryAccessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;


public class AnnotationsHandler implements ApplicationContextAware, InitializingBean {
	  private ApplicationContext applicationContext;
	   
	  @Override
	  public void afterPropertiesSet() {
		  GenericBeanFactoryAccessor genericBeanFactoryAccessor = new GenericBeanFactoryAccessor(applicationContext);
		  
		  final Map<String, Object> transactionalClass = genericBeanFactoryAccessor.getBeansWithAnnotation(TransactionalN2A.class);

		  for (final Object myFoo : transactionalClass.values()) {
			  final Class<? extends Object> fooClass = myFoo.getClass();
			  final TransactionalN2A annotation = fooClass.getAnnotation(TransactionalN2A.class);
			  System.out.println("Found 1 foo class: " + fooClass + ", with tags: ");
		  }
	  }

	  @Override
	  public void setApplicationContext(final ApplicationContext applicationContext)
	      throws BeansException {
	    this.applicationContext = applicationContext;
	  }
	}

