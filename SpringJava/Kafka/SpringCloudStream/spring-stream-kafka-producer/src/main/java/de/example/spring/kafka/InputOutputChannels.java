package de.example.spring.kafka;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

/**
 * Instead of this "custom" definition we will use the ones
 * already implemented by Spring:
 * org.springframework.cloud.stream.messaging.Sink
 * org.springframework.cloud.stream.messaging.Source
 *
 * This "custom" definition would be used in the same way as Sink and Source :)
 * Wherever you see Sink and Source you could replace them by this interface ;)
 */
public interface InputOutputChannels {

  @Input("inputChannel")
  SubscribableChannel input();

  @Output("outputChannel")
  MessageChannel output();

}
