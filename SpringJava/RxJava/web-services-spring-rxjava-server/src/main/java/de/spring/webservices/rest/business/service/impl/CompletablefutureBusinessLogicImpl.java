package de.spring.webservices.rest.business.service.impl;

import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import de.spring.webservices.domain.Car;
import de.spring.webservices.rest.business.service.AwesomeBusinessLogic;
import de.spring.webservices.rest.business.service.CompletableFutureBusinessLogic;

@Service("completableFutureBusinessLogic")
public class CompletablefutureBusinessLogicImpl implements CompletableFutureBusinessLogic {
    private final AwesomeBusinessLogic awesomeBusinessLogic;
    
    @Inject
	public CompletablefutureBusinessLogicImpl(AwesomeBusinessLogic awesomeBusinessLogic) {
		this.awesomeBusinessLogic = awesomeBusinessLogic;
	}

	@Override
	public CompletableFuture<Page<Car>> findAll(Pageable pageRequest) {
    	return CompletableFuture.supplyAsync(() -> awesomeBusinessLogic.findAll(pageRequest));
	}

	@Override
	public CompletableFuture<Car> findById(long id) {
    	return CompletableFuture.supplyAsync(() -> awesomeBusinessLogic.findById(id));
	}

	@Override
	public CompletableFuture<Car> create(Car car) {
		return CompletableFuture.supplyAsync(() -> awesomeBusinessLogic.create(car));
	}
}
