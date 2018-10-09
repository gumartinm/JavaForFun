package de.spring.example.rest.controllers;

import javax.inject.Inject;

import org.resthub.web.controller.RepositoryBasedRestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.spring.example.context.UsernameContext;
import de.spring.example.persistence.domain.Ad;
import de.spring.example.persistence.repository.AdRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/ads/")
public class AdController extends RepositoryBasedRestController<Ad, Long, AdRepository> {
	private static final Logger LOGGER = LoggerFactory.getLogger(AdController.class);

    @Override
    @Inject
    public void setRepository(AdRepository repository) {
        this.repository = repository;
    }
    
    @Override
    public Flux<Ad> findAll() {
		Flux<Ad> entities = repository.findAll();
		return entities.flatMap(ad -> {
			// throw new RuntimeException("Some horrible error");
			return Mono.subscriberContext().map(context -> {
				UsernameContext usernameContext = context.get(UsernameContext.class);
				LOGGER.info("IT WORKS {}", usernameContext.getValue());
				return ad;
			});
			});
    }
	// I do not have to do anything here because all I need is implemented by RepositoryBasedRestController :)

    // @Transactional is implemented by org.springframework.data.jpa.repository.support.SimpleJpaRepository
    // By default, SimpleJpaRepository will be automatically implemented by my
    // Spring JPA repositories: AdRepository and AdDescriptionRepository.
    
}
