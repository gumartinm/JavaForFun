package de.spring.webservices.rest.service;

import java.util.List;

import de.spring.webservices.domain.Location;

public interface LocationService {

    List<Location> findAll();

}
