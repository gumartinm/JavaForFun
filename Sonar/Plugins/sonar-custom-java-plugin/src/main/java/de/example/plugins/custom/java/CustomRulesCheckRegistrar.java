package de.example.plugins.custom.java;

import org.sonar.plugins.java.api.CheckRegistrar;

import de.example.custom.java.checks.CheckList;

/**
 * This class will be called by org.sonar.java.SonarComponents (see constructor) and
 * the SonarComponents object will be injected (SonarQube is using PicoContainer) in
 * org.sonar.plugins.java.JavaSquidSensor.
 * 
 * It seems like the SonarQube developers in charge of writing the Java plugin tried to
 * make easy the creation of custom Java plugins.
 * 
 * So, JavaSquidSensor will be the object that will run my rules (my Checks) whenever it finds Java code.
 * I do not have to do anything else, what is great!
 *
 */
public class CustomRulesCheckRegistrar implements CheckRegistrar {

	@Override
	public void register(RegistrarContext registrarContext) {
	    registrarContext.registerClassesForRepository(CheckList.REPOSITORY_KEY, CheckList.getJavaChecks(), CheckList.getJavaTestChecks());	
	}

}
