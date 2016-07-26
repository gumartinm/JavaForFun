package de.spring.example.services;

import java.io.Serializable;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.history.Revision;

public interface RevisionService<T, ID extends Serializable, N extends Number & Comparable<N>> {

	/**
	 * Returns a {@link Page} of revisions for the entity with the given id.
	 * 
	 * @param id must not be {@literal null}.
	 * @param pageable
	 * @return
	 */
	Page<Revision<N, T>> findRevisions(ID id, Pageable pageable);
}
