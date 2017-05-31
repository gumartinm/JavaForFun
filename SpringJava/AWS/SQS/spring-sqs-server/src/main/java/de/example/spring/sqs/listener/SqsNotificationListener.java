package de.example.spring.sqs.listener;

import java.io.IOException;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.example.spring.sqs.service.dto.NotificationDTO;

@Component
public class SqsNotificationListener {
	private static final Logger LOGGER = LoggerFactory.getLogger(SqsNotificationListener.class);
	private static final String UUID_HEADER = "UUID";

	private final ObjectMapper objectMapper;

	@Inject
	public SqsNotificationListener(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	@SqsListener(value = "${app.aws.sqs.queue-name}", deletionPolicy = SqsMessageDeletionPolicy.ON_SUCCESS)
	public void queueListener(String payload, @Header(name = UUID_HEADER, required = true) String uuid) {
		// TODO: Spring should convert String payload to NotificationDTO :(

		NotificationDTO notificationDto;
		try {
			notificationDto = objectMapper.readValue(payload, NotificationDTO.class);
		} catch (IOException ex) {
			LOGGER.error("ObjectMapper exception: ", ex);

			throw new RuntimeException(ex);
		}

		LOGGER.info("Received notification from GusQueue");
		LOGGER.info(payload);
		LOGGER.info(notificationDto.getName());
		LOGGER.info(notificationDto.getSurname());
		
		LOGGER.info("Header UUID");
		LOGGER.info(uuid);

		LOGGER.info("End of received notification from GusQueue");
	}
	
}
