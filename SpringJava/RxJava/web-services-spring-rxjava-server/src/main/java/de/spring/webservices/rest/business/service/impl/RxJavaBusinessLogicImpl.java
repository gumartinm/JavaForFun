package de.spring.webservices.rest.business.service.impl;

import javax.inject.Inject;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import de.spring.webservices.domain.Car;
import de.spring.webservices.rest.business.service.AwesomeBusinessLogic;
import de.spring.webservices.rest.business.service.RxJavaBusinessLogic;
import rx.Observable;

/**
 * 
 * 
 * TODO: WHAT ABOUT EXCEPTIONS FROM awesomeBusinessLogic? RuntimeExceptions for example
 * I guess they will be caught by my adapter in controller layer but I must try it.
 *
 */


@Service("rxJavaBusinessLogic")
public class RxJavaBusinessLogicImpl implements RxJavaBusinessLogic {
    private final AwesomeBusinessLogic awesomeBusinessLogic;
    
    @Inject
	public RxJavaBusinessLogicImpl(AwesomeBusinessLogic awesomeBusinessLogic) {
		this.awesomeBusinessLogic = awesomeBusinessLogic;
	}

	@Override
	public Observable<Page<Car>> findAll(Pageable pageRequest) {
    	return Observable.create(observer -> observer.onNext( awesomeBusinessLogic.findAll(pageRequest)));

	}

	@Override
	public Observable<Car> findById(long id) {
    	return Observable.create(observer -> observer.onNext( awesomeBusinessLogic.findById(id)));
	}

	@Override
	public Observable<Car> create(Car car) {	
		return Observable.create(observer -> observer.onNext(awesomeBusinessLogic.create(car)));
	}
}
