package de.example.spring.kafka;

import javax.inject.Inject;

import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.messaging.support.MessageBuilder;

@EnableBinding(Source.class)
public class Sender {
  // Aquí podrías haber usado tu custom interface: InputOutputChannels :)
  private final Source source;

  @Inject
  public Sender(Source source) {
    this.source = source;
  }

  public void sendMessage(String message) {
	  Product product = new Product(message, "this is some description");
	  source.output().send(MessageBuilder.withPayload(product).build());
  }
}
