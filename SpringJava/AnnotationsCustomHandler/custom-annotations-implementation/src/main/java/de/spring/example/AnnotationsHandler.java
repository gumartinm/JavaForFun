package de.spring.example;

import java.util.Map;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.generic.GenericBeanFactoryAccessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import de.spring.example.annotation.CustomTransactional;


public class AnnotationsHandler implements ApplicationContextAware, InitializingBean {
	  private ApplicationContext applicationContext;
	   
	  @Override
	  public void afterPropertiesSet() {
		  GenericBeanFactoryAccessor genericBeanFactoryAccessor = new GenericBeanFactoryAccessor(applicationContext);
		  
		  final Map<String, Object> transactionalClass = genericBeanFactoryAccessor.getBeansWithAnnotation(CustomTransactional.class);

		  for (final Object myFoo : transactionalClass.values()) {
			  final Class<? extends Object> fooClass = myFoo.getClass();
			  final CustomTransactional annotation = fooClass.getAnnotation(CustomTransactional.class);
			  System.out.println("Found foo class: " + fooClass + ", with tags: ");
		  }
	  }

	  @Override
	  public void setApplicationContext(final ApplicationContext applicationContext)
	      throws BeansException {
	    this.applicationContext = applicationContext;
	  }
	}

