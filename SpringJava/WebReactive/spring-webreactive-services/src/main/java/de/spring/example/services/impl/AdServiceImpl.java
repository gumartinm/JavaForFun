package de.spring.example.services.impl;

import javax.inject.Inject;
import javax.inject.Named;

import org.resthub.common.service.CrudServiceImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import de.spring.example.persistence.domain.Ad;
import de.spring.example.persistence.repository.AdRepository;
import de.spring.example.services.AdService;

@Named("adService")
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
	 * It is more complicated than when using Querydsl because I have to implement the
	 * Specifications. Querydsl is doing everything for me.
	 * 
	 * See: de.spring.example.services.impl.AdDescriptionServiceImpl
	 */
	@Override
	public Page<Ad> queryCriteriaExample(Pageable pageRequest) {
		// TODO: to be implemented with Querydsl. We can not use Specifications with MonogoDB :(
		return null;
	}

}
