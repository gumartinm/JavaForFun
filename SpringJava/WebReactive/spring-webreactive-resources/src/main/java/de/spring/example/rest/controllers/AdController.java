package de.spring.example.rest.controllers;

import javax.inject.Inject;

import org.resthub.web.controller.RepositoryBasedRestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.spring.example.persistence.domain.Ad;
import de.spring.example.persistence.repository.AdRepository;

@RestController
@RequestMapping("/ads/")
public class AdController extends RepositoryBasedRestController<Ad, Long, AdRepository> {

    @Override
    @Inject
    public void setRepository(AdRepository repository) {
        this.repository = repository;
    }
    
	// I do not have to do anything here because all I need is implemented by RepositoryBasedRestController :)

    // @Transactional is implemented by org.springframework.data.jpa.repository.support.SimpleJpaRepository
    // By default, SimpleJpaRepository will be automatically implemented by my
    // Spring JPA repositories: AdRepository and AdDescriptionRepository.
    
}
