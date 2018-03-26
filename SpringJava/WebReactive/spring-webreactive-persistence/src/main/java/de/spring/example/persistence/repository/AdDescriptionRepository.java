package de.spring.example.persistence.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import de.spring.example.persistence.domain.Ad;
import de.spring.example.persistence.domain.AdDescription;
import reactor.core.publisher.Flux;

/**
 * will be the implementation for this interface.
 *
 */
public interface AdDescriptionRepository extends ReactiveMongoRepository<AdDescription, Long>
		/** https://github.com/spring-projects/spring-data-envers/pull/45 QueryDslPredicateExecutor<AdDescription>, **/ {

	// Custom Query method (useful when the offered methods by PagingAndSortingRepository are not enough)
	Flux<AdDescription> findByAd(Ad ad, Pageable pageable);
}
