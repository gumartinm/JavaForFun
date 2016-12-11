package de.rxjava.tests.service.impl;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.atomic.AtomicLong;

import de.rxjava.tests.httpclient.CustomHTTPClient;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

public class AsyncHTTPClient {

	public void getPages() {
	    AtomicLong streamCounter = new AtomicLong();
	    
				
		getDataStreamAsync("https://github.com/gumartinm")
			// fancy Java way of using lambdas. Called method reference :)
			.subscribe(System.out::println,		//The subscribe method starts to run the code implemented in getDataSync
					Throwable::printStackTrace);//subscribeOn just declares who is going to run my code (a pool of threads)
												//subscribe is the guy who starts to run my code!!!
												//JavaScript does the same with Promises but in a cleaner way (IMHO), it does not
												//need a subscribe method for starting the machinery (the machinery is underneath
												//implemented by the Web Browser with its asynchronous callbacks)
											 	 
		
		getDataStreamAsync("http://www.google.de").
			subscribe(page -> {                               // It will be called on success :)
				System.out.println("STREAM, counter " + streamCounter.getAndIncrement());

				System.out.println("Another way, no so cool (with lambdas)");
				System.out.println(Thread.currentThread().getName());
				System.out.println(page);
				
			}, exception -> exception.printStackTrace());     // It will be called on error. :)
		
		
		// The same with method reference :)
		getDataStreamAsync("http://www.google.es").
		subscribe(System.out::println,             // It will be called on success :)
				  Throwable::printStackTrace);     // It will be called on error. :)
		
		
		System.out.println("AsyncHTTPClient: YOU SEE ME FIRST!!!!");
		
		
    	try {	
			Thread.sleep(30000);
		} catch (InterruptedException exception) {
			// Do not forget good patterns when dealing with InterruptedException :(
			Thread.currentThread().interrupt();
		}

	}
	
	private Observable<String> getDataStreamAsync(String uri) {
        return getDataStreamSync(uri)
        		.subscribeOn(Schedulers.io());  // Creates a pool of threads for us which will run the code implemented below :)
                                                // THIS METHOD DOES NOT START TO RUN MY CODE!!! IT IS DONE BY subscribe METHOD!!!
    }
	
	private Observable<String> getDataStreamSync(String uri) {
        return Observable.create(observer -> {
        	System.out.println(Thread.currentThread().getName());
        	try {
        		observer.onNext(CustomHTTPClient.newInstance("RxJavaTest").retrieveDataAsString(new URL(uri)));
        		
        		// WE ARE EMITTING MORE THAN ONE VALUE :)
        		observer.onNext(CustomHTTPClient.newInstance("RxJavaTest").retrieveDataAsString(new URL(uri)));
				
				// Making it slower as if I had a bad connection :)
				Thread.sleep(2000);
			} catch (InterruptedException exception) {
				// Do not forget good patterns when dealing with InterruptedException :(
				Thread.currentThread().interrupt();
				
				observer.onError(exception);
			} catch (IOException exception) {
				observer.onError(exception);
			}

        	// When do you use this stuff?
        	observer.onComplete();
        });
    }
}
