package de.spring.example;

import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * Using the @Aspect annotation you could use this class as an Aspect without
 * using a schema based declaration (without using <aop:aspect in the Spring xml file)
 * What means, you could use this class as an Aspect without the xml Spring declaration.
 *
 */
public class SystemArchitecture {
	private static final Logger LOGGER = LoggerFactory.getLogger(SystemArchitecture.class);

	
	//Coonecting to the execution of any method defined in the 
	//package: de.spring.example.web
	//We are connecting the methods defined in that package with this
	//Pointcut. So, when executing any of those methods defined in that
	//package we will run the Advice related to this Pointcut (if there is an Advice)
	//NOTICE:
	//			WE ARE NOT USING THE @Aspect ANNOTATION, SO WE CAN USE THIS CLASS AS
	//          AN ASPECT AND THIS METHOD AS A POINTCUT JUST USING A SCHEMA BASED DECLARATION
	@Pointcut("execution(* de.spring.example.web.*.*(..))")
	public void pointCutMethod() 
	{
		LOGGER.info("I am the Pointcut and you will never see me.");
		//This is the PointCut. 
		//You can write code here, but it will be useless because while running
		//the methods connected to the Pointcut, this code will not be executed.
		//Just the advice will run!!!! :/
		//Is not this weird? We are here defining a method whose code 
		//will never be run. When the hell should we write code here?
		//This is a waste of time and code IMHO. Isn't it?
	}
	
	public void monitor()
	{
		LOGGER.info("I am the Advice monitor for TestA and I will be run before.");
	}
}