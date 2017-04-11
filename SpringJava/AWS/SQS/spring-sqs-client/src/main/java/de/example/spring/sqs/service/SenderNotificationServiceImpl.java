package de.example.spring.sqs.service;

import javax.inject.Inject;

import org.springframework.cloud.aws.messaging.core.QueueMessageChannel;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;

import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.example.spring.sqs.service.dto.NotificationDTO;

public class SenderNotificationServiceImpl {
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

		queueMessagingTemplate.convertAndSend(queue, payload);
	}
}
