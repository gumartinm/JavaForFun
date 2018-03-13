package org.resthub.web.controller;

import org.resthub.common.exception.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.Serializable;
import java.util.Set;

/**
 * REST controller interface
 *
 * @param <T>  Your resource POJO to manage, maybe an entity or DTO class
 * @param <ID> Primary resource identifier at webservice level, usually Long or String
 */
public interface RestController<T, ID extends Serializable> {

    /**
     * Create a new resource<br>
     * REST webservice published : <code>POST /</code>
     *
     * @param resource The resource to create
     * @return CREATED http status code if the request has been correctly processed, with updated resource enclosed in the body, usually with and additional identifier automatically created by the database
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    Mono<T> create(@RequestBody T resource);

    /**
     * Update an existing resource<br>
     * REST webservice published : <code>PUT /{id}</code>
     *
     * @param id       The identifier of the resource to update, usually a Long or String identifier. It is explicitely provided in order to handle cases where the identifier could be changed.
     * @param resource The resource to update
     * @return OK http status code if the request has been correctly processed, with the updated resource enclosed in the body
     * @throws NotFoundException when resource <code>id</code> does not exist.
     */
    @PutMapping(value = "{id}")
    @ResponseBody
    Mono<T> update(@PathVariable ID id, @RequestBody T resource);

    /**
     * Find all resources, and return the full collection (plain list not paginated)<br>
     * REST webservice published : <code>GET /?page=no</code>
     *
     * @return OK http status code if the request has been correctly processed, with the list of all resource enclosed in the body.
     * Be careful, this list should be big since it will return ALL resources. In this case, consider using paginated findAll method instead.
     */
    @GetMapping(params = "page=no")
    @ResponseBody
    Flux<T> findAll();

    /**
     * Find all resources, and return a paginated and optionaly sorted collection<br>
     * REST webservice published : <code>GET /search?page=0&amp;size=20 or GET /search?page=0&amp;size=20&amp;direction=desc&amp;properties=name</code>
     *
     * @param page       Page number starting from 0. default to 0
     * @param size       Number of resources by pages. default to 10
     * @param direction  Optional sort direction, could be "asc" or "desc"
     * @param properties Ordered list of comma separeted properies used for sorting resulats. At least one property should be provided if direction is specified
     * @return OK http status code if the request has been correctly processed, with the a paginated collection of all resource enclosed in the body.
     */
    @GetMapping
    @ResponseBody
    Flux<T> findPaginated(@RequestParam(value = "direction", required = false, defaultValue = "ASC") String direction,
                          @RequestParam(value = "properties", required = false) String properties);

    /**
     * Find a resource by its identifier<br>
     * REST webservice published : <code>GET /{id}</code>
     *
     * @param id The identifier of the resouce to find
     * @return OK http status code if the request has been correctly processed, with resource found enclosed in the body
     * @throws NotFoundException when resource <code>id</code> does not exist.
     */
    @GetMapping(value = "{id}")
    @ResponseBody
    Mono<T> findById(@PathVariable ID id);

    /**
     * Find multiple resources by their identifiers<br>
     * REST webservice published : <code>GET /?ids[]=</code>
     * <p>
     * example : <code>/?ids[]=1&amp;ids[]=2&amp;ids[]=3</code>
     * </p>
     *
     * @param ids List of ids to retrieve
     * @return OK http status code with list of retrieved resources. Not found resources are ignored:
     * no Exception thrown. List is empty if no resource found with any of the given ids.
     */
    @GetMapping(params = "ids[]")
    @ResponseBody
    Flux<T> findByIds(@RequestParam(value = "ids[]") Set<ID> ids);

    /**
     * Delete all resources<br>
     * REST webservice published : <code>DELETE /</code><br>
     * Return <code>No Content</code> http status code if the request has been correctly processed
     */
    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    Mono<Void> delete();

    /**
     * Delete a resource by its identifier<br>
     * REST webservice published : <code>DELETE /{id}</code><br>
     * Return No Content http status code if the request has been correctly processed
     *
     * @param id The identifier of the resource to delete
     * @throws NotFoundException when resource <code>id</code> does not exist.
     */
    @DeleteMapping(value = "{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    Mono<Void> delete(@PathVariable ID id);

}
