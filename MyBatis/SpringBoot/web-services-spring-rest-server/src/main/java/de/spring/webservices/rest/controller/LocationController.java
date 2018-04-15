package de.spring.webservices.rest.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import de.spring.webservices.domain.Location;
import de.spring.webservices.rest.controller.apidocs.LocationControllerDocumentation;
import de.spring.webservices.rest.service.LocationService;

@RestController
@RequestMapping("/locations/")
public class LocationController implements LocationControllerDocumentation {
    private final LocationService locationService;

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @GetMapping(produces = { MediaType.APPLICATION_JSON_UTF8_VALUE })
    @ResponseStatus(HttpStatus.OK)
	@Override
    public List<Location> findAllLocationsPaginated() {
        return locationService.findAll();
    }

}
