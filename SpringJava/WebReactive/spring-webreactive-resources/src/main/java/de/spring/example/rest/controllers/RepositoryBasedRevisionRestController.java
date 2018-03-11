package de.spring.example.rest.controllers;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.history.Revision;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

public abstract class RepositoryBasedRevisionRestController<T, ID extends Serializable, N extends Number & Comparable<N>, R extends RevisionRepository<T, ID, N>>
		implements RevisionRestController<T, ID, N> {
    protected R repository;

    protected Logger logger = LoggerFactory.getLogger(RepositoryBasedRevisionRestController.class);

    /**
     * You should override this setter in order to inject your repository with @Inject annotation
     *
     * @param repository The repository to be injected
     */
    public void setRepository(R repository) {
        this.repository = repository;
    }


    @Override
    public Page<Revision<N, T>> findRevisionsPaginated(@PathVariable ID id,
    							 @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                                 @RequestParam(value = "size", required = false, defaultValue = "10") Integer size,
                                 @RequestParam(value = "direction", required = false, defaultValue = "") String direction,
                                 @RequestParam(value = "properties", required = false) String properties) {
        Assert.isTrue(page > 0, "Page index must be greater than 0");
        Assert.isTrue(direction.isEmpty() || direction.equalsIgnoreCase(Sort.Direction.ASC.toString()) || direction.equalsIgnoreCase(Sort.Direction.DESC.toString()), "Direction should be ASC or DESC");
        if(direction.isEmpty()) {
        	return this.repository.findRevisions(id, new PageRequest(page - 1, size));
        } else {
            Assert.notNull(properties);
            return this.repository.findRevisions(id, new PageRequest(page - 1, size, new Sort(Sort.Direction.fromString(direction.toUpperCase()), properties.split(","))));
        }
    }
}
