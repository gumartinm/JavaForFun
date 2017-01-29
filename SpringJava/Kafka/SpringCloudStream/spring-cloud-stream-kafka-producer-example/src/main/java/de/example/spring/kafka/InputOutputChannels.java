package de.example.spring.kafka;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

/**
 * En lugar de esta definicion "custom" usaremos las dos que
 * vienen por defecto en:
 * org.springframework.cloud.stream.messaging.Sink
 * org.springframework.cloud.stream.messaging.Source
 *
 * Esta definicion custom se usaría igual que Sink y Source :)
 * Donde veas Sink y Source podrías haber puesto esto en su lugar ;)
 */
public interface InputOutputChannels {

  @Input("inputChannel")
  SubscribableChannel input();

  @Output("outputChannel")
  MessageChannel output();

}
