package de.example.plugins.custom.java;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.rules.ActiveRule;
import org.sonar.plugins.java.Java;

import de.example.custom.java.checks.CheckList;

public class CustomProfileTest {

	@Test
	public void whenCreatingJavaCustomProfileThenGenerateProfileWithRules() {
		CustomProfile profile = new CustomProfile();
		
		RulesProfile rulesProfile = profile.createProfile(null);
		
		List<ActiveRule> activeRules = rulesProfile.getActiveRulesByRepository(CheckList.REPOSITORY_KEY);
		Set<String> ruleKeys = getRuleKeys(activeRules);
		assertThat(ruleKeys, hasItem("GUJ0002"));
		assertThat(ruleKeys, hasItem("GUJ0002"));
		assertThat(ruleKeys.size(), is(2));
		assertThat(rulesProfile.getLanguage(), is(Java.KEY));
		assertThat(rulesProfile.getName(), is("Custom Java Profile"));
	}

	private Set<String> getRuleKeys(List<ActiveRule> activeRules) {
		Set<String> keys = new HashSet<>();
		for (ActiveRule activeRule : activeRules) {
			keys.add(activeRule.getRuleKey());
		}
		
		return keys;
	}
}
