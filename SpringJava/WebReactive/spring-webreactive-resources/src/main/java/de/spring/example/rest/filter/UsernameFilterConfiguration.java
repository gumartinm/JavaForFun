package de.spring.example.rest.filter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UsernameFilterConfiguration {
	
    @Bean
    public UsernameFilter traceFilter() {
        return new UsernameFilter();
    }


}
