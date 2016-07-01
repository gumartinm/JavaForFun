package de.spring.persistence.example.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.repository.PagingAndSortingRepository;

import de.spring.persistence.example.domain.Ad;
import de.spring.persistence.example.domain.AdDescription;

public interface AdDescriptionRepository extends PagingAndSortingRepository<AdDescription, Long> {

	// Custom Query method
	Page<AdDescription> findByAd(Ad ad);
}
