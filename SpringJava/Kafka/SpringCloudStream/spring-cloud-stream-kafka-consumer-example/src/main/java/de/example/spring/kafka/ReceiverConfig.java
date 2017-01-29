package de.example.spring.kafka;

import org.springframework.cloud.stream.schema.avro.AvroSchemaMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.util.MimeType;

import java.io.IOException;

@Configuration
public class ReceiverConfig {

  @Bean
  public Receiver receiver() {
    return new Receiver();
  }


  @Bean
  public MessageConverter customMessageConverter() {
    return new MyCustomMessageConverter();
  }

  @Bean
  public MessageConverter avroMessageConverter() throws IOException {
      AvroSchemaMessageConverter converter = new AvroSchemaMessageConverter(MimeType.valueOf("avro/bytes"));
      //converter.setSchemaLocation(new ClassPathResource("schemas/User.avro"));
      return converter;
    }
}
