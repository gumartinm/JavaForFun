package de.example.plugins.custom.java;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.sonar.plugins.java.api.CheckRegistrar;
import org.sonar.plugins.java.api.CheckRegistrar.RegistrarContext;
import org.sonar.plugins.java.api.JavaCheck;

import com.google.common.collect.Lists;

import de.example.custom.java.checks.CheckList;

public class CustomRulesCheckRegistrarTest {

	@Test
	public void whenCreatingCustomJavaCheckRegistrarThenGenerateClassWithSuccess() {
		CustomRulesCheckRegistrar registrar = new CustomRulesCheckRegistrar();
		RegistrarContext registrarContext = new CheckRegistrar.RegistrarContext();
		
		registrar.register(registrarContext);
		
		List<Class<? extends JavaCheck>> checkClasses =  Lists.newArrayList(registrarContext.checkClasses());
		List<Class<? extends JavaCheck>> testCheckClasses =  Lists.newArrayList(registrarContext.testCheckClasses());
		assertThat(checkClasses.size(), is(2));
		assertThat(testCheckClasses.size(), is(0));
		assertThat(registrarContext.repositoryKey(), is(CheckList.REPOSITORY_KEY));
	}

}
