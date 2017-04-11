package de.example.spring.sqs.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.context.config.annotation.EnableContextInstanceData;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.example.spring.sqs.service.SenderNotificationServiceImpl;

@Configuration
// Using instance metadata: http://cloud.spring.io/spring-cloud-aws/spring-cloud-aws.html#_using_instance_metadata
@EnableContextInstanceData
public class AWSConfiguration {
	private static final Logger LOGGER = LoggerFactory.getLogger(AWSConfiguration.class);
	
    @Value("${ami-id:N/A}")
    private String amiId;

    @Value("${hostname:N/A}")
    private String hostname;

    @Value("${instance-type:N/A}")
    private String instanceType;

    @Value("${services/domain:N/A}")
    private String serviceDomain;
    
	@Value("${app.aws.sqs.queue-url}")
	private String queueUrl;
	
    @Bean
    SenderNotificationServiceImpl senderNotificationServiceImpl(AmazonSQSAsync amazonSqsAsync, ObjectMapper objectMapper) {
    	LOGGER.info("amiId: " + amiId);
    	LOGGER.info("hostname: " + hostname);
    	LOGGER.info("instanceType: " + instanceType);
    	LOGGER.info("serviceDomain: " + serviceDomain);
    	LOGGER.info("queueUrl: " + queueUrl);
    	
		return new SenderNotificationServiceImpl(amazonSqsAsync, queueUrl, objectMapper);
    	
    }

}
