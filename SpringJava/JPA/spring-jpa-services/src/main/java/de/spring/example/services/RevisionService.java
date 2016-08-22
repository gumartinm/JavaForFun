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
	 * @param pageable page request.
	 * @return a new {@link Page} with the content of the current one mapped by the given {@link Pageable}.
	 */
	Page<Revision<N, T>> findRevisions(ID id, Pageable pageable);
}
