package de.example.spring.sqs.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.stereotype.Component;

@Component
public class SqsNotificationListener {
	private static final Logger LOGGER = LoggerFactory.getLogger(SqsNotificationListener.class);
	
	@SqsListener(value = "${app.aws.sqs.queue-name}", deletionPolicy = SqsMessageDeletionPolicy.ON_SUCCESS)
	public void queueListener(String payload) {
		// TODO: Spring should convert String payload to NotificationDTO :(
		
		LOGGER.info("Received notification from GusQueue");
		LOGGER.info(payload);
		LOGGER.info("End of received notification from GusQueue");
	}
	
}
