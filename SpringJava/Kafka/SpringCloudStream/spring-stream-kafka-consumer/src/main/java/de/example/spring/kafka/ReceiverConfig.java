package de.example.spring.kafka;

import java.io.IOException;

import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.config.GlobalChannelInterceptor;
import org.springframework.messaging.support.ChannelInterceptor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;

import de.example.spring.kafka.interceptor.JSONSchemaChannelInterceptor;

@Configuration
public class ReceiverConfig {

  @Bean
  public Receiver receiver(DummyService dummyService) {
    return new Receiver(dummyService);
  }

	@GlobalChannelInterceptor(patterns = Sink.INPUT)
	@Bean
	public ChannelInterceptor jSONSchemaChannelInterceptor(ObjectMapper objectMapper, JsonSchema jsonSchema) {
		return new JSONSchemaChannelInterceptor(objectMapper, jsonSchema);
	}

	@Bean
	public JsonSchema jsonSchema(ObjectMapper objectMapper) throws ProcessingException, IOException {
		JsonNode schemaJson = objectMapper
		        .readTree(this.getClass().getClassLoader().getResourceAsStream("schemas/product.json"));

		return JsonSchemaFactory.byDefault().getJsonSchema(schemaJson);
	}

//  @Bean
//  public MessageConverter customMessageConverter(ObjectMapper objectMapper) {
//	  MyCustomMessageConverter converter = new MyCustomMessageConverter();
//	  converter.setSerializedPayloadClass(String.class);
//	  if (objectMapper != null) {
//		  converter.setObjectMapper(objectMapper);
//	  }
//	  
//	  return converter;
//  }


//  @Bean
//  public MessageConverter avroMessageConverter() throws IOException {
//      AvroSchemaMessageConverter converter = new AvroSchemaMessageConverter(MimeType.valueOf("avro/bytes"));
//      //converter.setSchemaLocation(new ClassPathResource("schemas/User.avro"));
//      return converter;
//  }
}
