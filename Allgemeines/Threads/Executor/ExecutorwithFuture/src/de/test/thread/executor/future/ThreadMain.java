package de.test.thread.executor.future;

import de.test.thread.executor.future.FutureTaskExample.Car;


public class ThreadMain {
	
	public static void main(String[] args) {
//		ThreadTest test = new ThreadTest();
//		test.start();
		
		new CompletableFutureExample().doRun();
		
		Car car = new FutureTaskExample().test();
		System.out.println(car.getId());
	}
}
