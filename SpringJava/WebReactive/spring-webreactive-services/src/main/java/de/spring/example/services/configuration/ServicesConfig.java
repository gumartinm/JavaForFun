package de.spring.example.services.configuration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan({ "de.spring.example.services", "org.resthub.common.service" })
public class ServicesConfig {

}
