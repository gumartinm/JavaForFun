package de.example.plugins.custom.java;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.sonar.api.profiles.ProfileDefinition;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.rules.Rule;
import org.sonar.api.utils.ValidationMessages;
import org.sonar.plugins.java.Java;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.google.gson.Gson;

import de.example.custom.java.checks.CheckList;

public class CustomProfile extends ProfileDefinition {
	private final Gson gson = new Gson();

	@Override
	public RulesProfile createProfile(ValidationMessages messages) {
		URL resource = CustomRulesDefinition.class.getResource("/de/example/l10n/java/rules/custom/Custom_profile.json");
		Profile jsonProfile = gson.fromJson(readResource(resource), Profile.class);
		RulesProfile profile = RulesProfile.create(jsonProfile.getName(), Java.KEY);
		
		for (String key : jsonProfile.getRuleKeys()) {
			Rule rule = Rule.create(CheckList.REPOSITORY_KEY, key);
			profile.activateRule(rule, null);
		}
		
		return profile;
	}

	private String readResource(URL resource) {
		try {
			return Resources.toString(resource, Charsets.UTF_8);
		} catch (IOException e) {
			throw new IllegalStateException("Failed to read: " + resource, e);
		}
	}

	private static class Profile {
		private String name;
		private List<String> ruleKeys;
		
		public String getName() {
			return name;
		}
		
		public List<String> getRuleKeys() {
			return ruleKeys;
		}
	}

}
