package de.spring.webservices.rest.service.impl;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import de.spring.webservices.domain.Location;
import de.spring.webservices.rest.configuration.DatabaseConfiguration;
import de.spring.webservices.rest.persistence.repository.LocationRepository;
import de.spring.webservices.rest.service.LocationService;

@Transactional(transactionManager = DatabaseConfiguration.TRX_MANAGER_LOCATIONS, readOnly = true)
public class LocationServiceImpl implements LocationService {
    private final LocationRepository locationRepository;

    public LocationServiceImpl(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    @Override
    public List<Location> findAll() {
        return locationRepository.findAll();
    }

}
