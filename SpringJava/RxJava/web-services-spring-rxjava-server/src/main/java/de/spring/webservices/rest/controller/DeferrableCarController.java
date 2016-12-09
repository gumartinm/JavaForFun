package de.spring.webservices.rest.controller;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import de.spring.webservices.domain.Car;
import de.spring.webservices.rest.business.service.AwesomeBusinessLogic;
import de.spring.webservices.rest.controller.adapters.CompletableFutureAdapter;

@RestController
@RequestMapping("/api/deferrable/cars/")
public class DeferrableCarController {
	private static final Logger LOGGER = LoggerFactory.getLogger(DeferrableCarController.class);
	private static final int PAGE = 2;
	private static final int PAGE_SIZE = 10;
    	
	private final AwesomeBusinessLogic awesomeBusinessLogic;

	@Inject
    public DeferrableCarController(AwesomeBusinessLogic awesomeBusinessLogic) {
		this.awesomeBusinessLogic = awesomeBusinessLogic;
	}

	@RequestMapping(produces = { MediaType.APPLICATION_JSON_UTF8_VALUE }, method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public DeferredResult<Page<Car>> cars() {
		    	
    	return CompletableFutureAdapter.callAdapter(() ->
    		awesomeBusinessLogic.findAll(new PageRequest(PAGE, PAGE_SIZE)));
    }

    @RequestMapping(value = "{id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public DeferredResult<Car> car(@RequestHeader(value = "MY_HEADER", required = false) String specialHeader,
    			@PathVariable("id") long id,
    			@RequestParam Map<String, String> params,
    			@RequestParam(value = "wheel", required = false) String[] wheelParams) {
    	    	
    	if (specialHeader != null) {
    		LOGGER.info("SPECIAL HEADER: " + specialHeader);
    	}
    	 
    	if (params.get("mirror") != null) {
    		LOGGER.info("MIRROR: " + params.get("mirror"));	
    	}
    	
    	if (params.get("window") != null) {
    		LOGGER.info("WINDOW: " + params.get("window"));
    	}
    	
    	if (wheelParams != null) {
    		for(String wheel : wheelParams) {
    			LOGGER.info(wheel);
    		}
    	}
    	    	
    	return CompletableFutureAdapter.callAdapter(() -> awesomeBusinessLogic.findById(id));
    }
    
    @RequestMapping(consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
    		produces = MediaType.APPLICATION_JSON_UTF8_VALUE, method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
    public DeferredResult<ResponseEntity<Car>> create(@RequestBody Car car) {
    	    	
    	return CompletableFutureAdapter.callAdapter(() -> createResponseCar(car));
    }

    
    private ResponseEntity<Car> createResponseCar(Car car) {
		Car createdCar = awesomeBusinessLogic.create(car);
		
		HttpHeaders headers = new HttpHeaders();
	    headers.add(HttpHeaders.LOCATION, "/api/cars/" + createdCar.getId());
	    return new ResponseEntity<>(createdCar, headers, HttpStatus.CREATED);
    }
}
