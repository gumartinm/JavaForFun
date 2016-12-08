package de.spring.webservices.rest.business.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import de.spring.webservices.domain.Car;
import de.spring.webservices.rest.business.service.AwesomeBusinessLogic;

@Service("awesomeBusinessLogic")
public class AwesomeBusinessLogicImpl implements AwesomeBusinessLogic {
	private static final String TEMPLATE = "Car: %s";
	
    private final AtomicLong counter = new AtomicLong();
	
	@Override
	public Page<Car> findAll(Pageable pageRequest) {
        final List<Car> cars = new ArrayList<>();
        cars.add(new Car(counter.incrementAndGet(), String.format(TEMPLATE, 1)));
        cars.add(new Car(counter.incrementAndGet(), String.format(TEMPLATE, 2)));
        cars.add(new Car(counter.incrementAndGet(), String.format(TEMPLATE, 3)));
        
        try {
            Thread.sleep(300000);
        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
		
		return new PageImpl<>(cars);	
	}

	@Override
	public Car findById(long id) {
		
        try {
            Thread.sleep(300000);
        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
		
		
		return new Car(counter.incrementAndGet(), String.format(TEMPLATE, id));
	}

	@Override
	public Car create(Car resource) {
		long count = counter.incrementAndGet();
		
        try {
            Thread.sleep(300000);
        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
        
		return new Car(count, String.format(TEMPLATE, count));
	}
}
