package de.example.spring.kafka;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MessageConverter;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class ReceiverConfig {

  @Bean
  public Receiver receiver() {
    return new Receiver();
  }


  @Bean
  public MessageConverter customMessageConverter(ObjectMapper objectMapper) {
	  MyCustomMessageConverter converter = new MyCustomMessageConverter();
	  converter.setSerializedPayloadClass(String.class);
	  if (objectMapper != null) {
		  converter.setObjectMapper(objectMapper);
	  }
	  
	  return converter;
  }


//  @Bean
//  public MessageConverter avroMessageConverter() throws IOException {
//      AvroSchemaMessageConverter converter = new AvroSchemaMessageConverter(MimeType.valueOf("avro/bytes"));
//      //converter.setSchemaLocation(new ClassPathResource("schemas/User.avro"));
//      return converter;
//  }
}