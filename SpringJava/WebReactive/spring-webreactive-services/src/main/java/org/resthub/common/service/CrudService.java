package org.resthub.common.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.Serializable;
import java.util.Set;

/**
 * CRUD Service interface.
 *
 * @param <T>  Your resource POJO to manage, maybe an entity or DTO class
 * @param <ID> Resource id type, usually Long or String
 */
public interface CrudService<T, ID extends Serializable> {

    /**
     * Create new resource.
     *
     * @param resource Resource to create
     * @return new resource
     */
    Mono<T> create(T resource);

    /**
     * Update existing resource.
     *
     * @param resource Resource to update
     * @return resource updated
     */
    Mono<T> update(T resource);

    /**
     * Delete existing resource.
     *
     * @param resource Resource to delete
     */
    Mono<Void> delete(T resource);

    /**
     * Delete existing resource.
     *
     * @param id Resource id
     */
    Mono<Void> delete(ID id);

    /**
     * Delete all existing resource. Do not use cascade remove (not a choice -&gt; JPA specs)
     */
    Mono<Void> deleteAll();

    /**
     * Delete all existing resource, including linked entities with cascade delete
     */
    Mono<Void> deleteAllWithCascade();

    /**
     * Find resource by id.
     *
     * @param id Resource id
     * @return resource
     */
    Mono<T> findById(ID id);

    /**
     * Find resources by their ids.
     *
     * @param ids Resource ids
     * @return a list of retrieved resources, empty if no resource found
     */
    Flux<T> findByIds(Set<ID> ids);

    /**
     * Find all resources.
     *
     * @return a list of all resources.
     */
    Flux<T> findAll();

    /**
	 * Returns all entities sorted by the given options.
     *
     * @param sortRequest sort request.
	 * @return a list of retrieved resources, empty if no resource found
     */
    Flux<T> findAll(Sort sortRequest);

    /**
     * Count all resources.
     *
     * @return number of resources
     */
    Mono<Long> count();
}
