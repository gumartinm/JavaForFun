package de.spring.webservices.rest.controller.adapters;

import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.async.DeferredResult;

public class CompletableFutureAdapter {
	private static final Logger LOGGER = LoggerFactory.getLogger(CompletableFutureAdapter.class);

	/**
	 * 
	 * WHEN EXCEPTION IN setErrorResult, Spring WILL TRIGGER THE Spring Exception Handler AS YOU KNOW IT (I HOPE)
	 * SO, YOU COULD HOOK UP THE HANDLER AND RETURN YOUR CUSTOM MESSAGESS (as usual)
	 * 
	 */
	
	// With no value, we depend on the Tomcat/Jboss/Jetty/etc timeout value for asynchronous requests.
	// Spring will answer after 60 secs with an empty response (by default) and HTTP 503 status (by default) when timeout.
	private static final long ASYNC_TIMEOUT = 60000;  /* milliseconds */

	
	@FunctionalInterface
	public interface DeferredCall<T> {
		
		public T doCall();
	}
	
	
	public static final <T> DeferredResult<T> callAdapter(DeferredCall<T> deferredCall) {
		
    	DeferredResult<T> deferredResult = new DeferredResult<>(ASYNC_TIMEOUT);
    	CompletableFuture
    		.supplyAsync(deferredCall::doCall)
    		.thenAcceptAsync(deferredResult::setResult)
    		.exceptionally(exception -> {
    			
    			LOGGER.error("findById error: ", exception);
    			
    			deferredResult.setErrorResult(exception);
    			
    			return null;
    		});
    	
        return deferredResult;	
	}
	
}
