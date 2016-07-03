package de.spring.example.services.impl;

import javax.inject.Inject;
import javax.inject.Named;

import org.resthub.common.service.CrudServiceImpl;

import de.spring.example.persistence.domain.AdDescription;
import de.spring.example.persistence.repository.AdDescriptionRepository;
import de.spring.example.services.AdDescriptionService;

@Named("adDescriptionService")
public class AdDescriptionServiceImpl
	extends CrudServiceImpl<AdDescription, Long, AdDescriptionRepository>
	implements AdDescriptionService {

	@Override
	@Inject
    public void setRepository(AdDescriptionRepository repository) {
        this.repository = repository;
    }

	// Extending CrudServiceImpl when we need some business logic. Otherwise we would be using
	// the JPA repositories and nothing else :)
	
	// In this case there is any business logic, but this is just an example.
}
