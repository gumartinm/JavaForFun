package org.resthub.web.controller;

import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

import org.resthub.common.exception.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Abstract REST controller using a repository implementation
 * <p>
 * You should extend this class when you want to use a 2 layers pattern : Repository and Controller. This is the default
 * controller implementation to use if you have no service (also called business) layer. You will be able to transform
 * it to a ServiceBasedRestController later easily if needed.
 * </p>
 *
 * <p>Default implementation uses "id" field (usually a Long) in order to identify resources in web request.
 * If your want to identity resources by a slug (human readable identifier), your should override findById() method with for example :
 * </p>
 * <pre>
 * <code>
   {@literal @}Override
   public Sample findById({@literal @}PathVariable String id) {
        Sample sample = this.repository.findByName(id);
        if (sample == null) {
            throw new NotFoundException();
        }
        return sample;
   }
   </code>
 * </pre>
 *
 *
 * @param <T>  Your resource class to manage, maybe an entity or DTO class
 * @param <ID> Resource id type, usually Long or String
 * @param <R>  The repository class
 * @see ServiceBasedRestController
 */
public abstract class RepositoryBasedRestController<T, ID extends Serializable, R extends ReactiveMongoRepository<T, ID>>
        implements RestController<T, ID> {

    protected R repository;

    protected Logger logger = LoggerFactory.getLogger(RepositoryBasedRestController.class);

    /**
     * You should override this setter in order to inject your repository with @Inject annotation
     *
     * @param repository The repository to be injected
     */
    public void setRepository(R repository) {
        this.repository = repository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<T> create(@RequestBody T resource) {
        return (Mono<T>)this.repository.save(resource);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<T> update(@PathVariable ID id, @RequestBody T resource) {
        Assert.notNull(id, "id cannot be null");

        Mono<T> retrievedResource = this.findById(id);
        if (retrievedResource == null) {
            throw new NotFoundException();
        }

        return (Mono<T>)this.repository.save(resource);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Flux<T> findAll() {
        return repository.findAll();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Flux<T> findPaginated(@RequestParam(value = "direction", required = false, defaultValue = "") String direction,
                                 @RequestParam(value = "properties", required = false) String properties) {
        Assert.isTrue(direction.isEmpty() || direction.equalsIgnoreCase(Sort.Direction.ASC.toString()) || direction.equalsIgnoreCase(Sort.Direction.DESC.toString()), "Direction should be ASC or DESC");
        if(direction.isEmpty()) {
            return this.repository.findAll(Sort.unsorted());
        } else {
        	Objects.requireNonNull(properties, "properties param, required value");
            return this.repository.findAll(new Sort(Sort.Direction.fromString(direction.toUpperCase()), properties.split(",")));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<T> findById(@PathVariable ID id) {
    	Mono<T> entity = this.repository.findById(id);
        if (entity == null) {
            throw new NotFoundException();
        }

        return entity;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Flux<T> findByIds(@RequestParam(value="ids[]") Set<ID> ids){
        Assert.notNull(ids, "ids list cannot be null");
        return this.repository.findAllById(ids);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<Void> delete() {
    	Flux<T> list = repository.findAll();
    	list.toStream().map(entity -> repository.delete(entity));
        return Mono.empty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<Void> delete(@PathVariable ID id) {
        return this.repository.deleteById(id);
    }

}
