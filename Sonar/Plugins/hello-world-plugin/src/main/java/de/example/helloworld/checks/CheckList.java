package de.example.helloworld.checks;

import java.util.List;

import org.sonar.api.internal.google.common.collect.ImmutableList;
import org.sonar.plugins.java.api.JavaCheck;

public final class CheckList {
	public static final String REPOSITORY_KEY = "gushelloworld";

	private CheckList() {
	}

	public static List<Class> getChecks() {
		return ImmutableList.<Class>builder().addAll(getJavaChecks())
				.build();
	}

	  public static List<Class<? extends JavaCheck>> getJavaChecks() {
		    return ImmutableList.<Class<? extends JavaCheck>>builder()
		      .add(HelloWorldCheck.class)
		      .build();
	  }

}
