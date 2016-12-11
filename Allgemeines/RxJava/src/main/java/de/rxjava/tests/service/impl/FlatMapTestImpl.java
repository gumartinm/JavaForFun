package de.rxjava.tests.service.impl;

import java.util.Arrays;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

public class FlatMapTestImpl {

	
	public void getWords() {
		
		getWordsAsync()
		.flatMap(words -> {                // flatmap method enables us to return one Observable. The subscribe method does not have such feature.
			return Observable.fromArray(words.toArray());
		})
		.subscribe(word -> {               //Unlike JavaScript Promises, we can call many times the same promise without resolving it.
			                               //This stuff would be impossible in JavaScript :)
			System.out.println(word);
		}, exception -> exception.printStackTrace());
		
		
		// The same with method references!!!
		getWordsAsync()
		.flatMap((List<String> words) -> {
			return Observable.fromArray(words.toArray());
		})
		.subscribe(System.out::println, Throwable::printStackTrace);
		
		
		System.out.println("FlatMapTestImpl: YOU SEE ME FIRST!!!!");
		
    	try {	
			Thread.sleep(30000);
		} catch (InterruptedException exception) {
			// Do not forget good patterns when dealing with InterruptedException :(
			Thread.currentThread().interrupt();
		}

	}
	
	private Observable<List<String>> getWordsAsync() {
        return getWordsSync()
        		.subscribeOn(Schedulers.io());  // Creates a pool of threads for us which will run the code implemented below :)
                                                // THIS METHOD DOES NOT START TO RUN MY CODE!!! IT IS DONE BY subscribe METHOD!!!
    }
	
	private Observable<List<String>> getWordsSync() {
        return Observable.create(observer -> {
        	System.out.println(Thread.currentThread().getName());
        	
        	String[] words = { "gus", "stuff", "given", "no", "way" };
        	try {
				// Making it slower as if having a bad connection :)
				Thread.sleep(2000);
			} catch (InterruptedException exception) {
				// Do not forget good patterns when dealing with InterruptedException :(
				Thread.currentThread().interrupt();
				
				observer.onError(exception);
			}

        	// When do you use this stuff?
        	// observer.onCompleted();
        	
            observer.onNext(Arrays.asList(words));    
        });
    }
}
