package de.spring.persistence.example.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import de.spring.persistence.example.domain.Ad;

public interface AdRepository extends PagingAndSortingRepository<Ad, Long> {
	
	// Named Native Query (using the native language of the store) It is not portable.
	// See de.spring.persistence.example.domain.Ad
	@Query(value="SELECT * FROM AD WHERE AD.ID = :id", nativeQuery=true)
	Ad findByIdNativeQuery(@Param("id") Long id);
	
	// Named Query (using JPL) It is portable.
	// See de.spring.persistence.example.domain.Ad
	@Query("SELECT * FROM AD WHERE AD.ID = :id")
	Ad findByIdQuery(@Param("id") Long id);
}
