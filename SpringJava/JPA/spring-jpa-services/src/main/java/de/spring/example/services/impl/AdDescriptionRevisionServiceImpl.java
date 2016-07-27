package de.spring.example.services.impl;

import javax.inject.Inject;

import de.spring.example.persistence.domain.AdDescription;
import de.spring.example.persistence.repository.AdDescriptionRepository;
import de.spring.example.services.AdDescriptionRevisionService;

public class AdDescriptionRevisionServiceImpl
	extends RevisionServiceImpl<AdDescription, Long, Integer, AdDescriptionRepository>
	implements AdDescriptionRevisionService {

	@Override
	@Inject
    public void setRepository(AdDescriptionRepository repository) {
        this.repository = repository;
    }
}
