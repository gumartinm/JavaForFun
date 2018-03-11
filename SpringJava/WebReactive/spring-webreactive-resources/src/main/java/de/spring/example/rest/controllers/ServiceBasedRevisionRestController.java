package de.spring.example.rest.controllers;

import java.io.Serializable;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.history.Revision;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import de.spring.example.services.RevisionService;

public abstract class ServiceBasedRevisionRestController<T, ID extends Serializable, N extends Number & Comparable<N>, S extends RevisionService<T, ID, N>>
		implements RevisionRestController<T, ID, N> {
	
    protected S service;

    /**
     * You should override this setter in order to inject your service with @Inject annotation
     *
     * @param service The service to be injected
     */
    public void setService(S service) {
        this.service = service;
    }


    /**
     * Returns a {@link Page} of revisions for the entity with the given id
     *
     * @param page       Page number starting from 0. default to 0
     * @param size       Number of resources by pages. default to 10
     * @param direction  Optional sort direction, could be "asc" or "desc"
     * @param properties Ordered list of comma separeted properies used for sorting resulats. At least one property should be provided if direction is specified
     * @return OK http status code if the request has been correctly processed, with the a paginated collection of all resource enclosed in the body.
     */
    @RequestMapping(value="{id}/revisions/", method = RequestMethod.GET)
    @ResponseBody
    public Page<Revision<N, T>> findRevisionsPaginated(@PathVariable ID id,
    							 @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                                 @RequestParam(value = "size", required = false, defaultValue = "10") Integer size,
                                 @RequestParam(value = "direction", required = false, defaultValue = "") String direction,
                                 @RequestParam(value = "properties", required = false) String properties) {
        Assert.isTrue(page > 0, "Page index must be greater than 0");
        Assert.isTrue(direction.isEmpty() || direction.equalsIgnoreCase(Sort.Direction.ASC.toString()) || direction.equalsIgnoreCase(Sort.Direction.DESC.toString()), "Direction should be ASC or DESC");
        if(direction.isEmpty()) {
        	return this.service.findRevisions(id, new PageRequest(page - 1, size));
        } else {
            Assert.notNull(properties);
            return this.service.findRevisions(id, new PageRequest(page - 1, size, new Sort(Sort.Direction.fromString(direction.toUpperCase()), properties.split(","))));
        }
    }
}
