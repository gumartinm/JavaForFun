package de.example.plugins.custom;

import org.sonar.plugins.java.api.CheckRegistrar;

import de.example.custom.checks.CheckList;

public class CustomRulesCheckRegistrar implements CheckRegistrar {

	@Override
	public void register(RegistrarContext registrarContext) {
	    registrarContext.registerClassesForRepository(CheckList.REPOSITORY_KEY, CheckList.getJavaChecks(), CheckList.getJavaTestChecks());	
	}

}
