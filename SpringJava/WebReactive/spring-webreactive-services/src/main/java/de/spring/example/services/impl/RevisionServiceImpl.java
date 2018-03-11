package de.spring.example.services.impl;

import java.io.Serializable;

import javax.inject.Named;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.history.Revision;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import de.spring.example.services.RevisionService;

@Named("revisionService")
public class RevisionServiceImpl<T, ID extends Serializable, N extends Number & Comparable<N>, R extends RevisionRepository<T, ID, N>>
	implements RevisionService<T, ID, N> {

    protected R repository;

    /**
     * @param repository the repository to set
     */
    public void setRepository(R repository) {
        this.repository = repository;
    }
    
	@Override
	@Transactional
	public Page<Revision<N, T>> findRevisions(ID id, Pageable pageable) {
		Assert.notNull(pageable, "page request can't be null");
		Assert.notNull(id, "Resource ID can't be null");
		
		return this.repository.findRevisions(id, pageable);
	}

}
