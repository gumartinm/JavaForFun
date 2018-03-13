package de.spring.example.persistence.repository;

import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;

import de.spring.example.persistence.domain.Ad;

/**
 * By default <code>org.springframework.data.jpa.repository.support.SimpleJpaRepository</code>
 * will be the implementation for this interface.
 * 
 * Be careful with <code>@Transactional</code>. SimpleJpaRepository has annotated methods.
 *
 */
public interface AdRepository extends
		ReactiveSortingRepository<Ad, Long>,
		RevisionRepository<Ad, Long, Integer> {
	
	// Named Native Query (using the native language of the store) It is not portable.
	// See de.spring.persistence.example.domain.Ad
	Ad findByIdNativeQuery(@Param("id") Long id);
	
	// Named Query (using JPL) It is portable.
	// See de.spring.persistence.example.domain.Ad
	Ad findByIdQuery(@Param("id") Long id);
}
