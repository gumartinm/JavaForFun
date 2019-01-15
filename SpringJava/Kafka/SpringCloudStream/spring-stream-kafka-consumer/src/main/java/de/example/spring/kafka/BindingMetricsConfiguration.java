package de.example.spring.kafka;

import org.springframework.context.annotation.Configuration;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.config.EnableIntegrationManagement;

@Configuration
@EnableIntegration
@EnableIntegrationManagement(
        defaultLoggingEnabled = "false",
        defaultCountsEnabled = "false",
        defaultStatsEnabled = "false",
        statsEnabled = { "example.topic.examplegroup.errors" },
        countsEnabled = { "example.topic.examplegroup.errors" })
public class BindingMetricsConfiguration {

}
