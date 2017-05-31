package de.example.spring.sqs.service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.aws.messaging.core.QueueMessageChannel;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;

import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.example.spring.sqs.service.dto.NotificationDTO;

public class SenderNotificationServiceImpl {
	private static final Logger LOGGER = LoggerFactory.getLogger(SenderNotificationServiceImpl.class);
	private static final String UUID_HEADER = "UUID";
	  
	private final QueueMessagingTemplate queueMessagingTemplate;
	private final QueueMessageChannel queue;
	private final ObjectMapper objectMapper;

	@Inject
	public SenderNotificationServiceImpl(AmazonSQSAsync amazonSqsAsync, String queueUrl, ObjectMapper objectMapper) {
		this.queueMessagingTemplate = new QueueMessagingTemplate(amazonSqsAsync);
		this.queue = new QueueMessageChannel(amazonSqsAsync, queueUrl);
		this.objectMapper = objectMapper;
	}

	
	public void send(NotificationDTO notificationDTO) throws JsonProcessingException {
		String payload = objectMapper.writeValueAsString(notificationDTO);
		
		String uuid = UUID.randomUUID().toString();
		Map<String, Object> headers = new HashMap<>();
		headers.put(UUID_HEADER, uuid);
		
		LOGGER.info("Header UUID");
		LOGGER.info(uuid);
		
		queueMessagingTemplate.convertAndSend(queue, payload, headers);
	}
}
