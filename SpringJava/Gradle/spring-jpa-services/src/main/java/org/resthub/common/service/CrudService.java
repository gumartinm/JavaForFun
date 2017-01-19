package org.resthub.common.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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
    T create(T resource);

    /**
     * Update existing resource.
     *
     * @param resource Resource to update
     * @return resource updated
     */
    T update(T resource);

    /**
     * Delete existing resource.
     *
     * @param resource Resource to delete
     */
    void delete(T resource);

    /**
     * Delete existing resource.
     *
     * @param id Resource id
     */
    void delete(ID id);

    /**
     * Delete all existing resource. Do not use cascade remove (not a choice -&gt; JPA specs)
     */
    void deleteAll();

    /**
     * Delete all existing resource, including linked entities with cascade delete
     */
    void deleteAllWithCascade();

    /**
     * Find resource by id.
     *
     * @param id Resource id
     * @return resource
     */
    T findById(ID id);

    /**
     * Find resources by their ids.
     *
     * @param ids Resource ids
     * @return a list of retrieved resources, empty if no resource found
     */
    Iterable<T> findByIds(Set<ID> ids);

    /**
     * Find all resources.
     *
     * @return a list of all resources.
     */
    Iterable<T> findAll();

    /**
     * Find all resources (pageable).
     *
     * @param pageRequest page request.
	 * @return a new {@link Page} with the content of the current one mapped by the given {@link Pageable}.
     */
    Page<T> findAll(Pageable pageRequest);

    /**
     * Count all resources.
     *
     * @return number of resources
     */
    Long count();
}
