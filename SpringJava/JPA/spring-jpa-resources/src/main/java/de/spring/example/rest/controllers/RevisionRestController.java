package de.spring.example.rest.controllers;

import java.io.Serializable;

import org.springframework.data.domain.Page;
import org.springframework.data.history.Revision;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

public interface RevisionRestController<T, ID extends Serializable, N extends Number & Comparable<N>> {

    /**
     * Returns a {@link Page} of revisions for the entity with the given id
     *
     * @param id         The identifier of the resource to find.
     * @param page       Page number starting from 0. default to 0
     * @param size       Number of resources by pages. default to 10
     * @param direction  Optional sort direction, could be "asc" or "desc"
     * @param properties Ordered list of comma separated properties used for sorting results. At least one property should be provided if direction is specified
     * @return OK http status code if the request has been correctly processed, with the a paginated collection of all resource enclosed in the body.
     */
    @RequestMapping(value="{id}/revisions/", method = RequestMethod.GET)
    @ResponseBody
    public Page<Revision<N, T>> findRevisionsPaginated(@PathVariable ID id,
    							 @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                                 @RequestParam(value = "size", required = false, defaultValue = "10") Integer size,
                                 @RequestParam(value = "direction", required = false, defaultValue = "") String direction,
                                 @RequestParam(value = "properties", required = false) String properties);
}
