package de.example.spring.sns.service;

import java.util.Collections;

import javax.inject.Inject;

import org.springframework.cloud.aws.messaging.core.NotificationMessagingTemplate;
import org.springframework.cloud.aws.messaging.core.TopicMessageChannel;

import com.amazonaws.services.sns.AmazonSNS;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.example.spring.sns.service.dto.NotificationDTO;

public class SenderNotificationServiceImpl {
	private final NotificationMessagingTemplate notificationMessagingTemplate;
	private final TopicMessageChannel topic;
	private final ObjectMapper objectMapper;

	@Inject
	public SenderNotificationServiceImpl(AmazonSNS amazonSns, String topicArn, ObjectMapper objectMapper) {
		this.notificationMessagingTemplate = new NotificationMessagingTemplate(amazonSns);
		this.topic = new TopicMessageChannel(amazonSns, topicArn);
		this.objectMapper = objectMapper;
	}

	
	public void send(String subject, NotificationDTO notificationDTO) throws JsonProcessingException {
		String payload = objectMapper.writeValueAsString(notificationDTO);

		notificationMessagingTemplate
		.convertAndSend(topic, payload, Collections.<String, Object>singletonMap(TopicMessageChannel.NOTIFICATION_SUBJECT_HEADER, subject));
	}
}
