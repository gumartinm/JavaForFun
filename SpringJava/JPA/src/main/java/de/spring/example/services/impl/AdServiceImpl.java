package de.spring.example.services.impl;

import static org.springframework.data.jpa.domain.Specifications.where;

import javax.inject.Inject;

import org.resthub.common.service.CrudServiceImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import de.spring.example.persistence.domain.Ad;
import de.spring.example.persistence.domain.specifications.AdSpectifications;
import de.spring.example.persistence.repository.AdRepository;
import de.spring.example.services.AdService;

public class AdServiceImpl
	extends CrudServiceImpl<Ad, Long, AdRepository>
	implements AdService {
	
	@Override
	@Inject
    public void setRepository(AdRepository repository) {
        this.repository = repository;
    }

	/**
	 * Criteria using Specifications.
	 * 
	 * It is more complicated that when using Querydsl because I have to implement the
	 * Specifications. Querydsl is doing everything for me.
	 * 
	 * See: de.spring.example.services.impl.AdDescriptionServiceImpl
	 */
	@Override
	public Page<Ad> queryCriteriaExample(Pageable pageRequest) {
		return repository.findAll(
				where(AdSpectifications.createdToday()).and(AdSpectifications.mobileImage("picasso")),
				pageRequest);		
	}

}
