package de.example.spring.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.messaging.support.MessageBuilder;

import javax.inject.Inject;

public class Sender {
  private static final Logger LOGGER = LoggerFactory.getLogger(Sender.class);

  // Aquí podrías haber usado tu custom interface: InputOutputChannels :)
  private final Source source;

  @Inject
  public Sender(Source source) {
    this.source = source;
  }

  public void sendMessage(String message) {
    source.output().send(MessageBuilder.withPayload(message).build());
  }
}
