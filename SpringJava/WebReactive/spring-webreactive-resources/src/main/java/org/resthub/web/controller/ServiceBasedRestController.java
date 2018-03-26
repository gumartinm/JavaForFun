package org.resthub.web.controller;

import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

import org.resthub.common.exception.NotFoundException;
import org.resthub.common.service.CrudService;
import org.springframework.data.domain.Sort;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Abstract REST controller using a service implementation
 * 
 * <p>
 * You should extend this class when you want to use a 3 layers pattern : Repository, Service and Controller
 * If you don't have a real service (also called business layer), consider using RepositoryBasedRestController
 * </p>
 * <p>
 * Default implementation uses "id" field (usually a Long) in order to identify resources in web request.
 * If your want to identity resources by a slug (human readable identifier), your should override findById() method with for example :
 * </p>
 * <pre>
 * <code>
 * {@literal @}Override
 * public Sample findById({@literal @}PathVariable String id) {
 * Sample sample = this.service.findByName(id);
 * if (sample == null) {
 * throw new NotFoundException();
 * }
 * return sample;
 * }
 * </code>
 * </pre>
 *
 * @param <T>  Your resource class to manage, maybe an entity or DTO class
 * @param <ID> Resource id type, usually Long or String
 * @param <S>  The service class
 * @see RepositoryBasedRestController
 */
public abstract class ServiceBasedRestController<T, ID extends Serializable, S extends CrudService<T, ID>> implements
        RestController<T, ID> {

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
     * {@inheritDoc}
     */
    @Override
    public Mono<T> create(@RequestBody T resource) {
        return (Mono<T>) this.service.create(resource);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<T> update(@PathVariable ID id, @RequestBody T resource) {
        Assert.notNull(id, "id cannot be null");

        Mono<T> retreivedResource = this.findById(id);
        if (retreivedResource == null) {
            throw new NotFoundException();
        }

        return (Mono<T>) this.service.update(resource);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Flux<T> findAll() {
        return service.findAll();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Flux<T> findPaginated(@RequestParam(value = "direction", required = false, defaultValue = "") String direction,
                                 @RequestParam(value = "properties", required = false) String properties) {
        Assert.isTrue(direction.isEmpty() || direction.equalsIgnoreCase(Sort.Direction.ASC.toString()) || direction.equalsIgnoreCase(Sort.Direction.DESC.toString()), "Direction should be ASC or DESC");
        if (direction.isEmpty()) {
            return this.service.findAll(Sort.unsorted());
        } else {
        	Objects.requireNonNull(properties, "properties param, required value");
            return this.service.findAll(new Sort(Sort.Direction.fromString(direction.toUpperCase()), properties.split(",")));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<T> findById(@PathVariable ID id) {
    	Mono<T> resource = (Mono<T>) this.service.findById(id);
        if (resource == null) {
            throw new NotFoundException();
        }

        return resource;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Flux<T> findByIds(@RequestParam(value = "ids[]") Set<ID> ids) {
        Assert.notNull(ids, "ids list cannot be null");
        return this.service.findByIds(ids);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<Void> delete() {
        return this.service.deleteAllWithCascade();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<Void> delete(@PathVariable ID id) {
        return this.service.delete(id);
    }
}

