package de.spring.webservices.rest.configuration;

import org.springframework.boot.actuate.autoconfigure.health.ConditionalOnEnabledHealthIndicator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import de.spring.webservices.rest.controller.health.CustomHealthIndicator;

@Configuration
@ConditionalOnEnabledHealthIndicator("custom")
public class HealthIndicatorConfiguration {

  @Bean
  CustomHealthIndicator customHealthIndicator() {
    return new CustomHealthIndicator();
  }

}