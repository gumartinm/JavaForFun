package de.example.plugins.custom.javascript;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.Test;
import org.sonar.api.Plugin;
import org.sonar.api.SonarQubeVersion;

public class CustomPluginTest {

	@Test
	public void whenCreatingCustomJavaScriptPluginThenRetrieveRightNumberOfExtensions() {
		CustomPlugin javaPlugin = new CustomPlugin();
		Plugin.Context context = new Plugin.Context(SonarQubeVersion.V5_6);
		
		javaPlugin.define(context);

		assertThat(context.getExtensions().size(), is(1));
	}

}
