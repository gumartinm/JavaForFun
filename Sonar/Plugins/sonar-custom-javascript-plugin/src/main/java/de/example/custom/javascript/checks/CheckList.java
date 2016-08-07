package de.example.custom.javascript.checks;

import java.util.List;

import org.sonar.plugins.javascript.api.JavaScriptCheck;

import com.google.common.collect.ImmutableList;

public final class CheckList {
	public static final String REPOSITORY_KEY = "customjavascript";
	public static final String REPOSITORY_NAME = "Custom JavaScript";

	private CheckList() {
	}

	public static List<Class> getChecks() {
		return ImmutableList.<Class>builder()
				.addAll(getJavaScriptChecks())
				.build();
	}

	public static List<Class<? extends JavaScriptCheck>> getJavaScriptChecks() {
		return ImmutableList.<Class<? extends JavaScriptCheck>>builder()
				.add(AngularJSRootOnEventSubscriptionCheck.class)
				.build();
	}
}
