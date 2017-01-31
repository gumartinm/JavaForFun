package de.example.spring.kafka;

import org.springframework.cloud.stream.messaging.Source;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SenderConfig {

  @Bean
  public Sender sender(Source source) {
    return new Sender(source);
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
