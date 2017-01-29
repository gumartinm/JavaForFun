package de.example.spring.kafka;

import java.nio.charset.Charset;

import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.util.MimeType;

/**
 * Working around problem created by org.springframework.cloud.stream.binder.AbstractBinder.JavaClassMimeTypeConversion.mimeTypeFromObject()
 *
 * This code:
 *			if (payload instanceof String) {
 *				return MimeTypeUtils.APPLICATION_JSON_VALUE.equals(originalContentType) ? MimeTypeUtils.APPLICATION_JSON
 *						: MimeTypeUtils.TEXT_PLAIN;
 *			}
 *
 * Changes messages from: 
 * contentType "application/json;charset=UTF-8"{"name":"example message","description":"this is some description"}
 * 
 * to:
 * contentType "text/plain" originalContentType "application/json;charset=UTF-8"{"name":"example message","description":"this is some description"}
 * 
 */
public class MyCustomMessageConverter extends MappingJackson2MessageConverter {

  public MyCustomMessageConverter() {
		super(new MimeType("application", "json"));
  }

}
