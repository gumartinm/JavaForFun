package de.spring.example.rest.controllers;

import javax.inject.Inject;

import org.resthub.web.controller.ServiceBasedRestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.spring.example.persistence.domain.AdDescription;
import de.spring.example.services.AdDescriptionService;

@RestController
@RequestMapping("/ad-descriptions/")
public class AdDescriptionController extends ServiceBasedRestController<AdDescription, Long, AdDescriptionService> {
	
	@Override
	@Inject
    public void setService(AdDescriptionService adDescriptionService) {
        this.service = adDescriptionService;
    }

	// I do not have to do anything here because all I need is implemented by ServiceBasedRestController :)

}
