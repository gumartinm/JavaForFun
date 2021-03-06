package de.example.spring.sns.controller;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.aws.messaging.config.annotation.NotificationMessage;
import org.springframework.cloud.aws.messaging.config.annotation.NotificationSubject;
import org.springframework.cloud.aws.messaging.endpoint.NotificationStatus;
import org.springframework.cloud.aws.messaging.endpoint.annotation.NotificationMessageMapping;
import org.springframework.cloud.aws.messaging.endpoint.annotation.NotificationSubscriptionMapping;
import org.springframework.cloud.aws.messaging.endpoint.annotation.NotificationUnsubscribeConfirmationMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/gus-sns-topic")
public class NotificationController {
	private static final Logger LOGGER = LoggerFactory.getLogger(NotificationController.class);

	@NotificationSubscriptionMapping
	public void handleSubscriptionMessage(NotificationStatus status) throws IOException {
		LOGGER.info("We subscribe to start receive the message");
		
		
		// Connects to the SubscribeURL given by the SNS Topic when the SNS Topic is trying to Subscribe to
		// this SNS listener.
		status.confirmSubscription();
	}

	@NotificationMessageMapping
	public void handleNotificationMessage(@NotificationSubject String subject, @NotificationMessage String payload) {
		// TODO: Spring should convert String payload to NotificationDTO :(	
		
		LOGGER.info("Receiving data");
		LOGGER.info("Subject: " + subject);
		LOGGER.info("Message message: " + payload);
		LOGGER.info("End receiving data");

	}

	@NotificationUnsubscribeConfirmationMapping
	public void handleUnsubscribeMessage(NotificationStatus status) {
		LOGGER.info("The client has been unsubscribed and we want to re-subscribe");
		
		status.confirmSubscription();
	}
}
