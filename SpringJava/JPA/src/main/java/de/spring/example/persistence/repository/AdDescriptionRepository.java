package de.spring.example.persistence.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import de.spring.example.persistence.domain.Ad;
import de.spring.example.persistence.domain.AdDescription;

public interface AdDescriptionRepository extends PagingAndSortingRepository<AdDescription, Long> {

	// Custom Query method
	Page<AdDescription> findByAd(Ad ad, Pageable pageable);
}
