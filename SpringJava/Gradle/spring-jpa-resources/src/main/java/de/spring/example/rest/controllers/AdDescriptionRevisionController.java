package de.spring.example.rest.controllers;

import javax.inject.Inject;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.spring.example.persistence.domain.AdDescription;
import de.spring.example.services.AdDescriptionRevisionService;

@RestController
@RequestMapping("/ad-descriptions/")
public class AdDescriptionRevisionController
		extends ServiceBasedRevisionRestController<AdDescription, Long, Integer, AdDescriptionRevisionService> {
	
	@Override
	@Inject
    public void setService(AdDescriptionRevisionService adDescriptionRevisionService) {
        this.service = adDescriptionRevisionService;
    }

	// I do not have to do anything here because all I need is implemented by ServiceBasedRevisionRestController :)
	
}
