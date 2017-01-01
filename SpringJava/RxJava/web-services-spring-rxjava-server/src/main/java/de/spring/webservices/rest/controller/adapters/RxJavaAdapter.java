package de.spring.webservices.rest.controller.adapters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.async.DeferredResult;

import rx.Observable;
import rx.Single;

/**
 * 
 * Instead of using this class, you could create you own implementation of
 * org.springframework.web.servlet.mvc.method.annotation.DeferredResultAdapter
 * for Observable and Single.
 *
 * spring netflix is already doing this stuff for me.
 *
 * I DO NOT THINK THIS IS THE RIGHT WAY FOR DOING IT. I MEAN I AM USING subscribe()
 * AND spring netflix DOES NOT. SO, I GUESS, CALLING subscribe() IS NOT THE RIGHT WAY OF DOING THIS STUFF :(
 *
 * YOU'D BETTER USE THE spring netflix IMPLEMENTATION INSTEAD OF THIS ONE :(
 */
public class RxJavaAdapter {
	private static final Logger LOGGER = LoggerFactory.getLogger(RxJavaAdapter.class);
	
	// With no value, we depend on the Tomcat/Jboss/Jetty/etc timeout value for asynchronous requests.
	// Spring will answer after 60 secs with an empty response (by default) and HTTP 503 status (by default) when timeout.

	// You'd rather better rely on the server values. IMHO this is something to be controlled
	// by the server administrator no the developer.
	// private static final long ASYNC_TIMEOUT = 60000;  /* milliseconds */

	private RxJavaAdapter() {
		
	}
	
	public static final <T> DeferredResult<T> deferredAdapter(Observable<T> observable) {

    	DeferredResult<T> deferredResult = new DeferredResult<>(/** ASYNC_TIMEOUT **/);
    	
    	observable
    		.subscribe(deferredResult::setResult, exception -> {
    			Throwable realException = launderException(exception);

    			LOGGER.error("error: ", realException);

    			deferredResult.setErrorResult(realException);
			});

        return deferredResult;	
	}
	
	public static final <T> DeferredResult<T> deferredAdapter(Single<T> single) {

    	DeferredResult<T> deferredResult = new DeferredResult<>(/** ASYNC_TIMEOUT **/);

    	single
    		.subscribe(deferredResult::setResult, exception -> {
				Throwable realException = launderException(exception);
	
				LOGGER.error("error: ", realException);

			deferredResult.setErrorResult(realException);
		});

        return deferredResult;
	}
	
	private static final Throwable launderException(Throwable exception) {
		return exception.getCause() != null
			   ? exception.getCause()
			   : exception;
	}
}
