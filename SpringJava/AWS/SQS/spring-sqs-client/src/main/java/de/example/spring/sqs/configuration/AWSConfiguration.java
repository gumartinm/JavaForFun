package de.example.spring.sqs.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.context.config.annotation.EnableContextInstanceData;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.example.spring.sqs.service.SenderNotificationServiceImpl;

import javax.annotation.PostConstruct;

@Configuration
public class AWSConfiguration {
	private static final Logger LOGGER = LoggerFactory.getLogger(AWSConfiguration.class);
	
	@Value("${app.aws.sqs.queue-url}")
	private String queueUrl;
	
    @Bean
    SenderNotificationServiceImpl senderNotificationServiceImpl(AmazonSQSAsync amazonSqsAsync, ObjectMapper objectMapper) {
    	LOGGER.info("queueUrl: " + queueUrl);
    	
		return new SenderNotificationServiceImpl(amazonSqsAsync, queueUrl, objectMapper);
    	
    }

    @Configuration
    // When running from developer's PC: -Dspring.profiles.active=dev  (there is not access to http://169.254.169.254/ )
    @Profile(value = "!dev")
    // Using instance metadata: http://cloud.spring.io/spring-cloud-aws/spring-cloud-aws.html#_using_instance_metadata
    @EnableContextInstanceData
    public static class InstanceData {
        @Value("${ami-id:N/A}")
        private String amiId;

        @Value("${hostname:N/A}")
        private String hostname;

        @Value("${instance-type:N/A}")
        private String instanceType;

        @Value("${services/domain:N/A}")
        private String serviceDomain;

        @Value("${placement/availability-zone:N/A}")
        private String region;

        
        @PostConstruct
        private void init() {
          LOGGER.info("AWS ami id: " + amiId);
          LOGGER.info("AWS hostname: " + hostname);
          LOGGER.info("AWS instance type: " + instanceType);
          LOGGER.info("AWS service domain: " + serviceDomain);
          LOGGER.info("AWS region: " + region);
        }
    }

}
