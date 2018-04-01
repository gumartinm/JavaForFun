package de.spring.example.services;

import de.spring.example.persistence.domain.Ad;
import reactor.core.publisher.Flux;

public interface AdClientService {
	
	Flux<Ad> findAll();

}
