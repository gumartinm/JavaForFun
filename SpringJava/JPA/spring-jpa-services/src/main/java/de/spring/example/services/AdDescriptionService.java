package de.spring.example.services;

import org.resthub.common.service.CrudService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.history.Revision;

import de.spring.example.persistence.domain.AdDescription;

public interface AdDescriptionService extends CrudService<AdDescription, Long> {

	public Page<AdDescription> queryDslExample(Pageable pageRequest);
	
	/**
	 * Returns a {@link Page} of revisions for the entity with the given id.
	 * 
	 * @param id must not be {@literal null}.
	 * @param pageable page request.
	 * @return a new {@link Page} with the content of the current one mapped by the given {@link Pageable}.
	 */
	Page<Revision<Integer, AdDescription>> findRevisions(Long id, Pageable pageable);
}
