package de.spring.webservices.rest.controller;

import java.util.Map;
import java.util.concurrent.Callable;

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
import org.springframework.web.context.request.async.WebAsyncTask;

import de.spring.webservices.domain.Car;
import de.spring.webservices.rest.business.service.AwesomeBusinessLogic;

@RestController
@RequestMapping("/api/callable/cars/")
public class CallableCarController {
	private static final Logger LOGGER = LoggerFactory.getLogger(CallableCarController.class);
	private static final int PAGE = 2;
	private static final int PAGE_SIZE = 10;
    
	// With no value, we depend on the Tomcat/Jboss/Jetty/etc timeout value for asynchronous requests.
	// Spring will answer after 60 secs with an empty response (by default) and HTTP 503 status (by default) when timeout.
	private static final long ASYNC_TIMEOUT = 60000;  /* milliseconds */
	
	
	/**
	 * 
	 * WHEN EXCEPTION FROM CALLABLE, Spring WILL TRIGGER THE Spring Exception Handler AS YOU KNOW IT.
	 * See: https://spring.io/blog/2012/05/10/spring-mvc-3-2-preview-making-a-controller-method-asynchronous/
	 * 
	 * When an Exception is raised by a Callable, it is handled through the
	 * HandlerExceptionResolver mechanism just like exceptions raised by any
	 * other controller method. The more detailed explanation is that the
	 * exception is caught and saved, and the request is dispatched to the
	 * Servlet container where processing resumes and the
	 * HandlerExceptionResolver chain invoked. This also means
	 * that @ExceptionHandler methods will be invoked as usual.
	 * 
	 * 
	 * SO, YOU COULD HOOK UP THE HANDLER AND RETURN YOUR CUSTOM MESSAGESS (as
	 * usual)
	 * 
	 */
	
	
	private final AwesomeBusinessLogic awesomeBusinessLogic;
	
	@Inject
	public CallableCarController(AwesomeBusinessLogic awesomeBusinessLogic) {
		this.awesomeBusinessLogic = awesomeBusinessLogic;
	}

    @RequestMapping(produces = { MediaType.APPLICATION_JSON_UTF8_VALUE }, method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public Callable<Page<Car>> cars() {
    	
    	// Relying on the timeout given by Tomcat/Jboss/Jetty/etc. WebAsyncTask if you want to control your timeout (see below)
    	
//    	return new Callable<Page<Car>>() {
//			@Override
//			public Page<Car> call() throws Exception {
//				return awesomeBusinessLogic.findAll(new PageRequest(PAGE, PAGE_SIZE));
//			}
//		};
    	
    	// lambda way :)
    	return () -> awesomeBusinessLogic.findAll(new PageRequest(PAGE, PAGE_SIZE));
    	
    }

    @RequestMapping(value = "{id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public WebAsyncTask<Car> car(@RequestHeader(value = "MY_HEADER", required = false) String specialHeader,
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
    	
    	
    	// If you want to control stuff like timeout you must use WebAsyncTask + Callable :)
    	
//    	Callable<Car> callable = new Callable<Car>() {
//			@Override
//			public Car call() throws Exception {
//				return awesomeBusinessLogic.findById(id);
//			}
//		};
//		return new WebAsyncTask<>(ASYNC_TIMEOUT, callable);
    	
    	// lambda way :)
        return new WebAsyncTask<>(ASYNC_TIMEOUT, () -> awesomeBusinessLogic.findById(id));
    }
    
    @RequestMapping(consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
    		produces = MediaType.APPLICATION_JSON_UTF8_VALUE, method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
    public WebAsyncTask<ResponseEntity<Car>> create(@RequestBody Car car) {

    	// If you want to control stuff like timeout you must use WebAsyncTask + Callable :)

//    	Callable<ResponseEntity<Car>> callable = new Callable<ResponseEntity<Car>>() {
//			@Override
//			public ResponseEntity<Car> call() throws Exception {
//				Car createdCar = awesomeBusinessLogic.create(car);
//		        
//		        HttpHeaders headers = new HttpHeaders();
//		    	headers.add(HttpHeaders.LOCATION, "/api/cars/" + createdCar.getId());
//		    	return new ResponseEntity<>(createdCar, headers, HttpStatus.CREATED);
//			}
//		};
		
    	// lambda way
		Callable<ResponseEntity<Car>> callable = () -> {
			Car createdCar = awesomeBusinessLogic.create(car);
	        
	        HttpHeaders headers = new HttpHeaders();
	    	headers.add(HttpHeaders.LOCATION, "/api/callable/cars/" + createdCar.getId());
	    	return new ResponseEntity<>(createdCar, headers, HttpStatus.CREATED);
		};
		
		return new WebAsyncTask<>(ASYNC_TIMEOUT, callable);
    }

}
