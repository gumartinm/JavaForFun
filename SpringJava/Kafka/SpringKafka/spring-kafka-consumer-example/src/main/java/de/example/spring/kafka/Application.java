package de.example.spring.kafka;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.support.KafkaNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.PollableChannel;
import org.springframework.messaging.support.GenericMessage;

@SpringBootApplication
public class Application {

  public static void main(String[] args) throws Exception {
    ConfigurableApplicationContext context
        = new SpringApplicationBuilder(Application.class)
        .web(false)
        .run(args);
    MessageChannel toKafka = context.getBean("toKafka", MessageChannel.class);
    for (int i = 0; i < 10; i++) {
      toKafka.send(new GenericMessage<>("foo" + i));
    }
    toKafka.send(new GenericMessage<>(KafkaNull.INSTANCE));
    PollableChannel fromKafka = context.getBean("received", PollableChannel.class);
    Message<?> received = fromKafka.receive(10000);
    while (received != null) {
      System.out.println(received);
      received = fromKafka.receive(10000);
    }
    context.close();
    System.exit(0);
  }
}
