package de.example.plugins.custom;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sonar.api.profiles.ProfileDefinition;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.rules.RuleFinder;
import org.sonar.api.utils.AnnotationUtils;
import org.sonar.api.utils.ValidationMessages;
import org.sonar.java.checks.CheckList;
import org.sonar.plugins.java.Java;
import org.sonar.plugins.java.JavaRulesDefinition;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.google.gson.Gson;

public class CustomProfile extends ProfileDefinition {

	  private final Gson gson = new Gson();
	  private final RuleFinder ruleFinder;
	  public CustomProfile(RuleFinder ruleFinder) {
	    this.ruleFinder = ruleFinder;
	  }

	  @Override
	  public RulesProfile createProfile(ValidationMessages messages) {
	    RulesProfile profile = RulesProfile.create("Sonar way", Java.KEY);
	    URL resource = JavaRulesDefinition.class.getResource("/org/sonar/l10n/java/rules/custom/Custom_profile.json");
	    Profile jsonProfile = gson.fromJson(readResource(resource), Profile.class);
	    Map<String, String> keys = legacyKeys();
	    for (String key : jsonProfile.ruleKeys) {
	      profile.activateRule(ruleFinder.findByKey(CheckList.REPOSITORY_KEY, keys.get(key)), null);
	    }
	    return profile;
	  }

	  private static String readResource(URL resource) {
	    try {
	      return Resources.toString(resource, Charsets.UTF_8);
	    } catch (IOException e) {
	      throw new IllegalStateException("Failed to read: " + resource, e);
	    }
	  }

	  private static Map<String, String> legacyKeys() {
	    Map<String, String> result = new HashMap<>();
	    for (Class checkClass : CheckList.getChecks()) {
	      org.sonar.check.Rule ruleAnnotation = AnnotationUtils.getAnnotation(checkClass, org.sonar.check.Rule.class);
	      String key = ruleAnnotation.key();
	      org.sonar.java.RspecKey rspecKeyAnnotation = AnnotationUtils.getAnnotation(checkClass, org.sonar.java.RspecKey.class);
	      String rspecKey = key;
	      if(rspecKeyAnnotation != null) {
	        rspecKey = rspecKeyAnnotation.value();
	      }
	      result.put(rspecKey, key);
	    }
	    return result;
	  }

	  private static class Profile {
	    String name;
	    List<String> ruleKeys;
	  }

	}
