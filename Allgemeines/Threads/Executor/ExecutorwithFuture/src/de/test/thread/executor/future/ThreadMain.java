package de.test.thread.executor.future;

import de.test.thread.executor.future.FutureTaskExample.Car;


public class ThreadMain {
	
	public static void main(String[] args) {
//		ThreadTest test = new ThreadTest();
//
//		test.start();
		
		FutureTaskExample lol = new FutureTaskExample();
		Car jeje = lol.test();
		
		System.out.println(jeje.getId());
	}
}
