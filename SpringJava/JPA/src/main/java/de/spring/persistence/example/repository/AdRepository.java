package de.spring.persistence.example.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import de.spring.persistence.example.domain.Ad;

public interface AdRepository extends PagingAndSortingRepository<Ad, Long> {

}
