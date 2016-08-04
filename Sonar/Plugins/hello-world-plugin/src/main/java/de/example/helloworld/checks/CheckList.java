package de.example.helloworld.checks;

import java.util.List;

import org.sonar.plugins.java.api.JavaCheck;

import com.google.common.collect.ImmutableList;

public final class CheckList {
	public static final String REPOSITORY_KEY = "helloworld";
	public static final String REPOSITORY_NAME = "Hello World";

	private CheckList() {
	}

	public static List<Class> getChecks() {
		return ImmutableList.<Class>builder()
				.addAll(getJavaChecks())
				.addAll(getJavaTestChecks())
				.addAll(getXmlChecks())
				.build();
	}

	public static List<Class<? extends JavaCheck>> getJavaChecks() {
		return ImmutableList.<Class<? extends JavaCheck>>builder()
				.add(HelloWorldCheck.class)
				.build();
	}

	public static List<Class<? extends JavaCheck>> getJavaTestChecks() {
		return ImmutableList.<Class<? extends JavaCheck>>builder()
				.build();
	}

	public static List<Class<? extends JavaCheck>> getXmlChecks() {
		return ImmutableList.<Class<? extends JavaCheck>>builder()
				.build();
	}

	private static List<Class<? extends JavaCheck>> getMavenChecks() {
		return ImmutableList.<Class<? extends JavaCheck>>builder()
				.build();
	}
}
