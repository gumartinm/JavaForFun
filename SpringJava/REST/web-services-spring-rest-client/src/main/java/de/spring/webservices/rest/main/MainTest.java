package de.spring.webservices.rest.main;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import de.spring.webservices.rest.business.BusinessService;


/**
 * This class is used just like a nice example about how to write and run client
 * code which will send data to and from the Web Services.
 * 
 */
public class MainTest {	
    public ApplicationContext context;

    /**
     * @param args
     */
    public static void main(final String[] args) {
        final MainTest test = new MainTest();

        test.context = new ClassPathXmlApplicationContext(
                "classpath:spring-configuration/rest-config.xml");

        final BusinessService example =
        		(BusinessService) test.context.getBean("businessService");
        
        example.doSomethingWithCars();
        
        example.doSomethingWithCar(66L);
        
        example.createsNewCar();
    }
}
