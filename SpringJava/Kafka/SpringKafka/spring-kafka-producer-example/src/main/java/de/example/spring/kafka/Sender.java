package de.example.spring.kafka;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import javax.inject.Inject;

public class Sender {
  private static final Logger LOGGER = LoggerFactory.getLogger(Sender.class);

  private final KafkaTemplate<String, String> kafkaTemplate;

  @Inject
  public Sender(KafkaTemplate<String, String> kafkaTemplate) {
    this.kafkaTemplate = kafkaTemplate;
  }

  public void sendMessage(String topic, String message) {
    // the KafkaTemplate provides asynchronous send methods returning a
    // Future
    ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send(topic, message);

    // The same with MessageBuilder
    // ListenableFuture<SendResult<String, String>> future =
    //    kafkaTemplate.send(
    //        MessageBuilder.withPayload(message)
    //            .setHeader(KafkaHeaders.TOPIC, topic)
    //            .build());

    // you can register a callback with the listener to receive the result
    // of the send asynchronously
    future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {

          @Override
          public void onSuccess(SendResult<String, String> result) {
            RecordMetadata recordMetadata = result.getRecordMetadata();
            ProducerRecord producerRecord = result.getProducerRecord();

            LOGGER.info("sent message='{}'", message);
            LOGGER.info("RecordMetadata");
            LOGGER.info("with offset={}", recordMetadata.offset());
            LOGGER.info("with partition={}", recordMetadata.partition());
            LOGGER.info("with checksum={}", recordMetadata.checksum());
            LOGGER.info("with timestamp={}", recordMetadata.timestamp());
            LOGGER.info("with timestamp={}", recordMetadata.topic());

            LOGGER.info("ProducerRecord");
            LOGGER.info("with partition={}", producerRecord.partition());
            LOGGER.info("with value={}", producerRecord.value());
            LOGGER.info("with timestamp={}", producerRecord.timestamp());
            LOGGER.info("with topic={}", producerRecord.topic());
          }

          @Override
          public void onFailure(Throwable ex) {
            LOGGER.error("unable to send message='{}'", message, ex);
          }
        });

    // alternatively, to block the sending thread, to await the result,
    // invoke the future’s get() method
  }
}
