package de.spring.example.persistence.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import de.spring.example.persistence.domain.Ad;

/**
 * will be the implementation for this interface.
 *
 */
public interface AdRepository extends ReactiveMongoRepository<Ad, Long> {

}
