package de.spring.example.services.impl;

import javax.inject.Inject;
import javax.inject.Named;

import org.resthub.common.service.CrudServiceImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.history.Revision;

import com.querydsl.core.types.dsl.BooleanExpression;

import de.spring.example.persistence.domain.AdDescription;
import de.spring.example.persistence.domain.QAdDescription;
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
	
	
	/**
	 * Using Querydsl. Giving some business logic to this service :)
	 * 
	 * Querydsl: fluent interface done easy. There is no effort because it is already implemented.
	 * 
	 * Criteria using Specifications requires some effort.
	 * 
	 * See: de.spring.example.services.impl.AdServiceImpl
	 */
	@Override
	public Page<AdDescription> queryDslExample(Pageable pageRequest) {
		final QAdDescription adDescription = QAdDescription.adDescription1;
		final BooleanExpression adDescriptionHasAdLink = adDescription.adLink.contains("gumartinm");
		final BooleanExpression adDescriptionHasDescription = adDescription.adDescription.contains("babucha");
		/**
		 * https://github.com/spring-projects/spring-data-envers/pull/45
		 * return repository.findAll(adDescriptionHasAdLink.and(adDescriptionHasDescription), pageRequest);
		 */
		return null;
	}

	@Override
	public Page<Revision<Integer, AdDescription>> findRevisions(Long id, Pageable pageable) {
		return null;
	}
}
