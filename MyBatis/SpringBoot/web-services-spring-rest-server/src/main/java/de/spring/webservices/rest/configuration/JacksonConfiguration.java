package de.spring.webservices.rest.configuration;

import javax.inject.Inject;

import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Configuration
public class JacksonConfiguration {

  @Inject
  public void configureJackson(ObjectMapper jackson2ObjectMapper) {
	  jackson2ObjectMapper
	  	.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        .disable(MapperFeature.DEFAULT_VIEW_INCLUSION)
        .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
        .enable(SerializationFeature.INDENT_OUTPUT)
        .registerModule(new JavaTimeModule());
  }
  
}
