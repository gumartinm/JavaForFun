package de.example.spring.kafka;

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
 * 
 * By means of my custom converter we end up having:
 * 
 * contentType "application/json"{"name":"example message","description":"this is some description"}
 * 
 * About "application/json" and character encoding:
 * https://tools.ietf.org/html/rfc7158#section-8.1   "The default encoding is UTF-8"  :)
 * 
 * 
 * 
 */


// 
// You should set breakpoints in org.springframework.cloud.stream.binder.AbstractBinder.deserializePayload
// 							     org.springframework.cloud.stream.binder.AbstractBinder.serializePayloadIfNecessary
//                               org.springframework.cloud.stream.binder.AbstractMessageChannelBinder.SendingHandler.handleMessageInternal                              
// 
// The code in this Spring project is a bit messy (IMHO) for example this.embedHeaders in
// SendingHandler.handleMessageInternal: When the heck this.embedHeaders must do something? Depending on
// the content type embedHeaders will be doing something or not. This is weird. Also deserializePayload is
// always using UTF-8. It does not matter what character set I am receiving :/
// 
// 
// 
// 
// Be careful when using Spring Cloud Stream because there could be surprises when trying to connect to systems
// not using Spring (Spring is creating a mess with the headers...)
// 
//



// 
//  Kafka messages WITHOUT MyCustomMessageConverter:
//  headers:
//  	contentType "text/plain"   <--------------- perhaps this is important.
//  	originalContentType "application/json;charset=UTF-8"
//  payload:
//  	{"name":"example message","description":"this is some description"}
//
//
//  Kafka messages WITH MyCustomMessageConverter:
//  headers:
//  	contentType "application/json"
//  payload:
//  	{"name":"example message","description":"this is some description"}
// 


// YOU'D RATHER BETTER NOT USE THIS CONVERTER BECAUSE I DO NOT KNOW IF contentType "text/plain" IS IMPORTANT OR NOT :(
public class MyCustomMessageConverter extends MappingJackson2MessageConverter {

  public MyCustomMessageConverter() {
		super(new MimeType("application", "json"));
  }

}
