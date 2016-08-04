package de.example.plugins.helloworld;

import org.sonar.plugins.java.api.CheckRegistrar;

import de.example.helloworld.checks.CheckList;

public class HelloWorldRulesCheckRegistrar implements CheckRegistrar {

	@Override
	public void register(RegistrarContext registrarContext) {
	    registrarContext.registerClassesForRepository(CheckList.REPOSITORY_KEY, CheckList.getJavaChecks(), CheckList.getJavaTestChecks());	
	}

}
