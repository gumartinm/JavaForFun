package de.spring.example.persistence.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;

import de.spring.example.persistence.domain.Ad;
import de.spring.example.persistence.domain.AdDescription;

/**
 * By default <code>org.springframework.data.jpa.repository.support.SimpleJpaRepository</code>
 * will be the implementation for this interface.
 * 
 * Be careful with <code>@Transactional</code>. SimpleJpaRepository has annotated methods.
 *
 */
public interface AdDescriptionRepository extends
		ReactiveSortingRepository<AdDescription, Long>,
		/** https://github.com/spring-projects/spring-data-envers/pull/45 QueryDslPredicateExecutor<AdDescription>, **/
		RevisionRepository<AdDescription, Long, Integer> {

	// Custom Query method (useful when the offered methods by PagingAndSortingRepository are not enough)
	Page<AdDescription> findByAd(Ad ad, Pageable pageable);
}
