package de.example.plugins.custom.javascript;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Test;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.check.Rule;
import org.sonar.plugins.javascript.JavaScriptLanguage;
import org.sonar.plugins.javascript.api.JavaScriptCheck;
import org.sonar.plugins.javascript.api.tree.Tree;
import org.sonar.plugins.javascript.api.visitors.Issue;
import org.sonar.plugins.javascript.api.visitors.LineIssue;
import org.sonar.plugins.javascript.api.visitors.PreciseIssue;
import org.sonar.plugins.javascript.api.visitors.TreeVisitorContext;

import de.example.custom.javascript.checks.CheckList;

public class CustomRulesDefinitionTest {

	@Test
	public void whenCreatingCustomJavaRulesDefinitionThenGenerateRulesDefinition() {
		RulesDefinition.Context context = new RulesDefinition.Context();
		CustomRulesDefinition rulesDefinition = new CustomRulesDefinition();
		
		rulesDefinition.define(context);
		
		RulesDefinition.Repository repository = context.repository(CheckList.REPOSITORY_KEY);
	    assertThat(repository.name(), is(CheckList.REPOSITORY_NAME));
	    assertThat(repository.language(), is(JavaScriptLanguage.KEY));
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
	  
	private class CheckWithNoAnnotation implements JavaScriptCheck {

		@Override
		public <T extends Issue> T addIssue(T arg0) {
			return null;
		}

		@Override
		public PreciseIssue addIssue(Tree arg0, String arg1) {
			return null;
		}

		@Override
		public LineIssue addLineIssue(Tree arg0, String arg1) {
			return null;
		}

		@Override
		public List<Issue> scanFile(TreeVisitorContext arg0) {
			return null;
		}
	}

	@Rule(key = "")
	private class EmptyRuleKey implements JavaScriptCheck {

		@Override
		public <T extends Issue> T addIssue(T arg0) {
			return null;
		}

		@Override
		public PreciseIssue addIssue(Tree arg0, String arg1) {
			return null;
		}

		@Override
		public LineIssue addLineIssue(Tree arg0, String arg1) {
			return null;
		}

		@Override
		public List<Issue> scanFile(TreeVisitorContext arg0) {
			return null;
		}
	}

	@Rule(key = "myKey")
	private class UnregisteredRule implements JavaScriptCheck {

		@Override
		public <T extends Issue> T addIssue(T arg0) {
			return null;
		}

		@Override
		public PreciseIssue addIssue(Tree arg0, String arg1) {
			return null;
		}

		@Override
		public LineIssue addLineIssue(Tree arg0, String arg1) {
			return null;
		}

		@Override
		public List<Issue> scanFile(TreeVisitorContext arg0) {
			return null;
		}
	}

	@Rule(key = "correctRule")
	private class CorrectRule implements JavaScriptCheck {

		@Override
		public <T extends Issue> T addIssue(T arg0) {
			return null;
		}

		@Override
		public PreciseIssue addIssue(Tree arg0, String arg1) {
			return null;
		}

		@Override
		public LineIssue addLineIssue(Tree arg0, String arg1) {
			return null;
		}

		@Override
		public List<Issue> scanFile(TreeVisitorContext arg0) {
			return null;
		}
	}
}
