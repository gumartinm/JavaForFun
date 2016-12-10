package de.spring.webservices.rest.business.service;

import java.util.concurrent.CompletableFuture;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import de.spring.webservices.domain.Car;


public interface CompletableFutureBusinessLogic {

	public CompletableFuture<Page<Car>> findAll(Pageable pageRequest);
	
	public CompletableFuture<Car> findById(long id);
	
	public CompletableFuture<Car> create(Car resource);
	
}
