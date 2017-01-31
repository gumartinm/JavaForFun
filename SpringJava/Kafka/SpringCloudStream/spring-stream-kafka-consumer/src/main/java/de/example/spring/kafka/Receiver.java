package de.example.spring.kafka;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;

@EnableBinding(Sink.class)
public class Receiver {
  private static final Logger LOGGER = LoggerFactory.getLogger(Receiver.class);
  
  private final DummyService dummyService;

  @Inject
  public Receiver(DummyService dummyService) {
	this.dummyService = dummyService;
  }

  @StreamListener(Sink.INPUT)
  public void handle(Product product) {
    LOGGER.info("product name='{}'", product.getName());
    LOGGER.info("product description='{}'", product.getDescription());
    
    dummyService.iAmVeryDummy(product.getName());
  }

}
