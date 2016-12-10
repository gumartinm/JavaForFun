package de.spring.webservices.rest.business.service;

import java.io.IOException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import de.spring.webservices.domain.Car;


public interface AwesomeBusinessLogic {

	public Page<Car> findAll(Pageable pageRequest);
	
	public Car findById(long id);
	
	public Car create(Car car);
	
	public Car createThrowable(Car car) throws IOException;
	
}
