package de.example.spring.kafka;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.schema.client.EnableSchemaRegistryClient;
import org.springframework.context.annotation.Bean;

/**
 * Using arguments with CommandLineRunner:
 *
 * Example: java -jar -Dtarget/spring-stream-kafka-producer-1.0-SNAPSHOT.jar "HELLO GUS"
 *
 */
@SpringBootApplication
@EnableSchemaRegistryClient
public class Application {

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

  @Bean
  CommandLineRunner lookup(Sender sender) {
    return args -> {
      String message = "example message";


      if (args.length > 0) {
        message = args[0];
      }

      sender.sendMessage(message);
    };
  }

}
