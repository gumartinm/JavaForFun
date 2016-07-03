package de.spring.example.persistence.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import de.spring.example.persistence.domain.Ad;

/**
 * By default <code>org.springframework.data.jpa.repository.support.SimpleJpaRepository<code>
 * will be the implementation for this interface.
 * 
 * Be careful with <code>@Transactional</code>. SimpleJpaRepository has annotated methods.
 *
 */
public interface AdRepository extends PagingAndSortingRepository<Ad, Long> {
	
	// Named Native Query (using the native language of the store) It is not portable.
	// See de.spring.persistence.example.domain.Ad
	@Query(value="SELECT * FROM ad WHERE ad.id = :id", nativeQuery=true)
	Ad findByIdNativeQuery(@Param("id") Long id);
	
	// Named Query (using JPL) It is portable.
	// See de.spring.persistence.example.domain.Ad
	@Query("select a from Ad a where a.id = :id")
	Ad findByIdQuery(@Param("id") Long id);
}
