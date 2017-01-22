package de.spring.example.rest.controllers;

import javax.inject.Inject;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.spring.example.persistence.domain.Ad;
import de.spring.example.persistence.repository.AdRepository;

@RestController
@RequestMapping("/ads/")
public class AdRevisionController extends
		RepositoryBasedRevisionRestController<Ad, Long, Integer, AdRepository> {

    @Override
    @Inject
    public void setRepository(AdRepository repository) {
        this.repository = repository;
    }
}
