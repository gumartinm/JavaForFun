package de.example.plugins.custom.javascript;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import javax.annotation.Nullable;

import org.apache.commons.lang.StringUtils;
import org.sonar.api.rule.RuleStatus;
import org.sonar.api.server.debt.DebtRemediationFunction;
import org.sonar.api.server.rule.RulesDefinitionAnnotationLoader;
import org.sonar.api.utils.AnnotationUtils;
import org.sonar.plugins.javascript.JavaScriptLanguage;
import org.sonar.plugins.javascript.api.CustomJavaScriptRulesDefinition;
import org.sonar.squidbridge.annotations.RuleTemplate;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Charsets;
import com.google.common.collect.Iterables;
import com.google.common.io.Resources;
import com.google.gson.Gson;

import de.example.custom.javascript.checks.CheckList;


/**
 * This class will be injected (SonarQube is using PicoContainer) in
 * org.sonar.plugins.javascript.JavaScriptSquidSensor.
 * 
 * It seems like the SonarQube developers in charge of writing the JavaScript plugin tried to
 * make easy the creation of custom Java plugins.
 * 
 * So, JavaScriptSquidSensor will be the object that will run my rules (my Checks) whenever it finds JavaScript code.
 * I do not have to do anything else, what is great!
 *
 */
public class CustomRulesDefinition extends CustomJavaScriptRulesDefinition {
	private static final String RESOURCE_BASE_PATH = "/de/example/l10n/javascript/rules/custom";
	
	private final Gson gson = new Gson();
	
	  @Override
	  public String repositoryName() {
	    return CheckList.REPOSITORY_NAME;
	  }

	  @Override
	  public String repositoryKey() {
	    return CheckList.REPOSITORY_KEY;
	  }

	  @Override
	  public Class[] checkClasses() {
	    return CheckList.getChecks().toArray();
	  }
	
	  /**
	   * I do not want to use the define method implemented in org.sonar.plugins.javascript.api.CustomJavaScriptRulesDefinition.
	   */
	@Override
	public void define(Context context) {
		NewRepository repository = context
				.createRepository(repositoryKey(), JavaScriptLanguage.KEY)
				.setName(repositoryName());
		List<Class> checks = CheckList.getChecks();
		new RulesDefinitionAnnotationLoader().load(repository, Iterables.toArray(checks, Class.class));
		for (Class ruleClass : checks) {
			newRule(ruleClass, repository);
		}
		repository.done();
	}
  
  @VisibleForTesting
  protected void newRule(Class<?> ruleClass, NewRepository repository) {

    org.sonar.check.Rule ruleAnnotation = AnnotationUtils.getAnnotation(ruleClass, org.sonar.check.Rule.class);
    if (ruleAnnotation == null) {
      throw new IllegalArgumentException("No Rule annotation was found on " + ruleClass);
    }
    String ruleKey = ruleAnnotation.key();
    if (StringUtils.isEmpty(ruleKey)) {	
      throw new IllegalArgumentException("No key is defined in Rule annotation of " + ruleClass);
    }
    NewRule rule = repository.rule(ruleKey);
    if (rule == null) {
      throw new IllegalStateException("No rule was created for " + ruleClass + " in " + repository.key());
    }
    
    // Check whether it is a Rule Template.
    rule.setTemplate(AnnotationUtils.getAnnotation(ruleClass, RuleTemplate.class) != null);
    ruleMetadata(ruleClass, rule);
  }

  private void ruleMetadata(Class<?> ruleClass, NewRule rule) {
    String metadataKey = rule.key();
    addHtmlDescription(rule, metadataKey);
    addMetadata(rule, metadataKey);

  }

  private void addMetadata(NewRule rule, String metadataKey) {
    URL resource = CustomRulesDefinition.class.getResource(RESOURCE_BASE_PATH + "/" + metadataKey + "_javascript.json");
    if (resource != null) {
      RuleMetatada metatada = gson.fromJson(readResource(resource), RuleMetatada.class);
      rule.setSeverity(metatada.defaultSeverity.toUpperCase());
      rule.setName(metatada.title);
      rule.addTags(metatada.tags);
      rule.setStatus(RuleStatus.valueOf(metatada.status.toUpperCase()));
      if(metatada.remediation != null) {
        rule.setDebtRemediationFunction(metatada.remediation.remediationFunction(rule.debtRemediationFunctions()));
        rule.setGapDescription(metatada.remediation.linearDesc);
      }
    }
  }

  private void addHtmlDescription(NewRule rule, String metadataKey) {
    URL resource = CustomRulesDefinition.class.getResource(RESOURCE_BASE_PATH + "/" + metadataKey + "_javascript.html");
    if (resource != null) {
      rule.setHtmlDescription(readResource(resource));
    }
  }

  private String readResource(URL resource) {
    try {
      return Resources.toString(resource, Charsets.UTF_8);
    } catch (IOException e) {
      throw new IllegalStateException("Failed to read: " + resource, e);
    }
  }

  private static class RuleMetatada {
    String title;
    String status;
    @Nullable
    Remediation remediation;

    String[] tags;
    String defaultSeverity;
  }

  private static class Remediation {
    String func;
    String constantCost;
    String linearDesc;
    String linearOffset;
    String linearFactor;

    public DebtRemediationFunction remediationFunction(DebtRemediationFunctions drf) {
      if(func.startsWith("Constant")) {
        return drf.constantPerIssue(constantCost.replace("mn", "min"));
      }
      if("Linear".equals(func)) {
        return drf.linear(linearFactor.replace("mn", "min"));
      }
      return drf.linearWithOffset(linearFactor.replace("mn", "min"), linearOffset.replace("mn", "min"));
    }
  }
}
