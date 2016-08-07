package de.example.plugins.custom.javascript;

import org.sonar.api.Plugin;

import com.google.common.collect.ImmutableList;

public class CustomPlugin implements Plugin {

	@Override
	public void define(Context context) {
	    ImmutableList.Builder<Object> builder = ImmutableList.builder();
	    builder.add(
	    		);
	    
	    context.addExtensions(builder.build());
	}

}
