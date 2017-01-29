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
}
