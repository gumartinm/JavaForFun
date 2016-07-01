package de.spring.rest.controllers;

import org.resthub.web.controller.RepositoryBasedRestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.spring.persistence.example.domain.Ad;
import de.spring.persistence.example.repository.AdRepository;

@RestController
@RequestMapping("/ads/")
public class AdController extends RepositoryBasedRestController<Ad, Long, AdRepository>{

	// I do not have to do anything here because all I need is implemented by RepositoryBasedRestController :)
}
