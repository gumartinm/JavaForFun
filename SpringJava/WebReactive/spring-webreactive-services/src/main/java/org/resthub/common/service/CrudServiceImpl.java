package org.resthub.common.service;

import java.io.Serializable;
import java.util.Set;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.util.Assert;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * CRUD service that uses a {@link org.springframework.data.repository.reactive.ReactiveSortingRepository} Spring Data repository implementation
 *
 * You should extend it and inject your Repository bean by overriding {@link #setRepository(org.springframework.data.repository.reactive.ReactiveSortingRepository)}
 *
 * @param <T> Your resource class to manage, usually an entity class
 * @param <ID> Resource id type, usually Long or String
 * @param <R> The repository class
 */
public class CrudServiceImpl<T, ID extends Serializable, R extends ReactiveMongoRepository<T, ID>> implements
        CrudService<T, ID> {

    protected R repository;

    /**
     * @param repository the repository to set
     */
    public void setRepository(R repository) {
        this.repository = repository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<T> create(T resource) {
        Assert.notNull(resource, "Resource can't be null");
        return repository.save(resource);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<T> update(T resource) {
        Assert.notNull(resource, "Resource can't be null");
        return repository.save(resource);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<Void> delete(T resource) {
        Assert.notNull(resource, "Resource can't be null");
        return repository.delete(resource);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<Void> delete(ID id) {
        Assert.notNull(id, "Resource ID can't be null");
        return repository.deleteById(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<Void> deleteAll() {
        return repository.deleteAll();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<Void> deleteAllWithCascade() {
        Flux<T> list = repository.findAll();
        list.toStream().map(entity -> repository.delete(entity));
        return Mono.empty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<T> findById(ID id) {
        Assert.notNull(id, "Resource ID can't be null");
        return repository.findById(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Flux<T> findByIds(Set<ID> ids) {
        Assert.notNull(ids, "Resource ids can't be null");
        return repository.findAllById(ids);
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
    public Flux<T> findAll(Sort sortRequest) {
        Assert.notNull(sortRequest, "sort request can't be null");
        return repository.findAll(sortRequest);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<Long> count() {
        return repository.count();
    }
}
