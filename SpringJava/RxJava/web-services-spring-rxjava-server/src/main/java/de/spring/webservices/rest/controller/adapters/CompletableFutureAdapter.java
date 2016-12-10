package de.spring.webservices.rest.controller.adapters;

import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.async.DeferredResult;

public class CompletableFutureAdapter {
	private static final Logger LOGGER = LoggerFactory.getLogger(CompletableFutureAdapter.class);

	// With no value, we depend on the Tomcat/Jboss/Jetty/etc timeout value for asynchronous requests.
	// Spring will answer after 60 secs with an empty response (by default) and HTTP 503 status (by default) when timeout.
	private static final long ASYNC_TIMEOUT = 60000;  /* milliseconds */

	
	@FunctionalInterface
	public interface DeferredCall<T> {
		
		public T doCall();
	}
	
	public static final <T> DeferredResult<T> deferredAdapter(CompletableFuture<T> completableFuture) {

    	DeferredResult<T> deferredResult = new DeferredResult<>(ASYNC_TIMEOUT);

    	completableFuture
    		.thenAcceptAsync(deferredResult::setResult)
	    	.exceptionally(exception -> {
    			Throwable realException = launderException(exception);

				LOGGER.error("error: ", realException);

				deferredResult.setErrorResult(exception);
				return null;
			});

        return deferredResult;	
	}
	
	private static final Throwable launderException(Throwable exception) {
		return exception.getCause() != null
			   ? exception.getCause()
			   : exception;
	}
}
