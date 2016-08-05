package de.example.plugins.helloworld;

import org.sonar.api.Plugin;

import com.google.common.collect.ImmutableList;


public class HelloWorldPlugin implements Plugin {

  @Override
  public void define(Context context) {    
    
    ImmutableList.Builder<Object> builder = ImmutableList.builder();
    builder.add(
    		HelloWorldRulesDefinition.class,
    		HelloWorldRulesCheckRegistrar.class);
    
    context.addExtensions(builder.build());
  }
}
