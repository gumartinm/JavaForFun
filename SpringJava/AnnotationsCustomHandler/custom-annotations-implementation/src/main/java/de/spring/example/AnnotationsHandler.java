package de.spring.example;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.generic.GenericBeanFactoryAccessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import de.spring.example.annotation.CustomTransactional;
import de.spring.example.annotation.TestAnnotation;


public class AnnotationsHandler implements ApplicationContextAware, InitializingBean {
	  private ApplicationContext applicationContext;
	   
	  @Override
	  public void afterPropertiesSet() {
		  GenericBeanFactoryAccessor genericBeanFactoryAccessor = new GenericBeanFactoryAccessor(applicationContext);
		  
		  //Spring searched the annotations configured in spring-config.xml file. 
		  //With this code we can retrieve the Spring beans with those annotations.
		  final Map<String, Object> transactionalClass = genericBeanFactoryAccessor.getBeansWithAnnotation(CustomTransactional.class);

		  for (final Object annotationObject : transactionalClass.values()) 
		  {
			  final Class<? extends Object> annotationClass = annotationObject.getClass();
			  final CustomTransactional annotation = annotationClass.getAnnotation(CustomTransactional.class);
			  System.out.println("Found foo class: " + annotationClass + ", with annotation: " + annotation);
			  for (Method m : annotationClass.getDeclaredMethods())
			  {
				  //Another annotation just to show the nice thing about using annotations in Java.
				  //It requires Java reflection.
				  if (m.isAnnotationPresent(TestAnnotation.class))
				  {
					try {
						m.invoke(null);
					//First of all we catch exception related to Java reflection.
					} catch (IllegalArgumentException e) {
						System.out.println(m + " failed: " + e.getCause());
					} catch (IllegalAccessException e) {
						System.out.println(m + " failed: " + e.getCause());
					} catch (InvocationTargetException e) {
						System.out.println(m + " failed: " + e.getCause());
					} catch (Exception e)
					{
						//If the invoke method throws an exception we can catch it here 
						//and write a nice message.
						System.out.println("INVALID method: " + m + ", something went wrong: " + e);
					}
				  }
			  }
		  }
	  }

	  @Override
	  public void setApplicationContext(final ApplicationContext applicationContext)
	      throws BeansException {
	    this.applicationContext = applicationContext;
	  }
	}