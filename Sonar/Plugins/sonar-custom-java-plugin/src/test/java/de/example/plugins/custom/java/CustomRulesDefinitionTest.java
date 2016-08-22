package de.example.plugins.custom.java;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.check.Rule;
import org.sonar.plugins.java.Java;
import org.sonar.plugins.java.api.JavaCheck;

import de.example.custom.java.checks.CheckList;

public class CustomRulesDefinitionTest {

	@Test
	public void whenCreatingCustomJavaRulesDefinitionThenGenerateRulesDefinition() {
		RulesDefinition.Context context = new RulesDefinition.Context();
		CustomRulesDefinition rulesDefinition = new CustomRulesDefinition();
		
		rulesDefinition.define(context);
		
		RulesDefinition.Repository repository = context.repository(CheckList.REPOSITORY_KEY);
	    assertThat(repository.name(), is(CheckList.REPOSITORY_NAME));
	    assertThat(repository.language(), is(Java.KEY));
	    assertThat(repository.rules().size(), is(CheckList.getChecks().size()));
	}

	@Test(expected=IllegalArgumentException.class)
	public void shouldThrowIllegalArgumentExceptionWhenNoRuleAnnotationIsFound() {
	    RulesDefinition.Context context = new RulesDefinition.Context();
	    RulesDefinition.NewRepository newRepository = context.createRepository(CheckList.REPOSITORY_KEY, CheckList.REPOSITORY_NAME);
	    CustomRulesDefinition rulesDefinition = new CustomRulesDefinition();
	    
	    rulesDefinition.newRule(CheckWithNoAnnotation.class, newRepository);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldThrowIllegalArgumentExceptionWhenNoKeyIsFoundInRuleAnnotation() {
	    RulesDefinition.Context context = new RulesDefinition.Context();
	    RulesDefinition.NewRepository newRepository = context.createRepository(CheckList.REPOSITORY_KEY, CheckList.REPOSITORY_NAME);
	    CustomRulesDefinition rulesDefinition = new CustomRulesDefinition();
	    
	    rulesDefinition.newRule(EmptyRuleKey.class, newRepository);
	}
	
	@Test(expected=IllegalStateException.class)
	public void shouldThrowIllegalStateExceptionWhenNoKeyIsFoundInRuleAnnotation() {
	    RulesDefinition.Context context = new RulesDefinition.Context();
	    RulesDefinition.NewRepository newRepository = context.createRepository(CheckList.REPOSITORY_KEY, CheckList.REPOSITORY_NAME);
	    newRepository.createRule("myCardinality");
	    newRepository.createRule("correctRule");
	    CustomRulesDefinition rulesDefinition = new CustomRulesDefinition();
	    
	    rulesDefinition.newRule(UnregisteredRule.class, newRepository);
	}
	  
	private class CheckWithNoAnnotation implements JavaCheck {
	}

	@Rule(key = "")
	private class EmptyRuleKey implements JavaCheck {
	}

	@Rule(key = "myKey")
	private class UnregisteredRule implements JavaCheck {
	}

	@Rule(key = "correctRule")
	private class CorrectRule implements JavaCheck {
	}
}
