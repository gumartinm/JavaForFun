package de.spring.webservices.rest.configuration;

import org.springframework.boot.actuate.autoconfigure.ConditionalOnEnabledInfoContributor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import de.spring.webservices.rest.controller.info.CustomInfoContributor;

@Configuration
@ConditionalOnEnabledInfoContributor("custom")
public class InfoContributorConfiguration {

	  @Bean
	  CustomInfoContributor customInfoContributor() {
	    return new CustomInfoContributor();
	  }

}
