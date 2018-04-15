package de.spring.webservices.rest.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import de.spring.webservices.rest.persistence.repository.LocationRepository;
import de.spring.webservices.rest.service.LocationService;
import de.spring.webservices.rest.service.impl.LocationServiceImpl;

@Configuration
public class ServiceConfiguration {

    @Bean
    public LocationService locationService(LocationRepository locationRepository) {
        return new LocationServiceImpl(locationRepository);
    }

}
