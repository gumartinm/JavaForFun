package de.example.plugins.custom.java;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.sonar.api.Plugin;
import org.sonar.api.SonarQubeVersion;

public class CustomPluginTest {

	@Test
	public void whenCreatingCustomJavaPluginThenRetrieveRightNumberOfExtensions() {
		CustomPlugin javaPlugin = new CustomPlugin();
		Plugin.Context context = new Plugin.Context(SonarQubeVersion.V5_6);
		
		javaPlugin.define(context);

		assertThat(context.getExtensions().size(), is(3));
	}

}
