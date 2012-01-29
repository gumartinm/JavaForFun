package de.test.thread.executor.future;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ThreadTest {
	private final ExecutorService exec = Executors.newFixedThreadPool(2);

	public void start () {
		Future<?> [] future = new Future<?> [3];

		Thread.currentThread().setName("main");
		//We have a pool with 2 threads. 
		//The third task will wait in a Queue until one thread of that pool is freed.
		for (int i = 0; i < 3 ; i++) {
			future[i] = exec.submit(new ThreadExecutor(i));
		}
		
		for (int i = 0; i < 3 ; i++) {
			try {
				future[i].get();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
				//The exception thrown in the threads is caught by the main thread here.
				System.out.println(Thread.currentThread().getName() + "\n");
			}
		}
		
		try {
			synchronized(this) {
				wait(4000);
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//We have a pool with 2 threads. 
		//The third task will wait in a Queue until one thread of that pool is freed.
		for (int i = 0; i < 3 ; i++) {
			//Code without implementing the Future class. The exception from the threads is not treated.
			exec.submit(new ThreadExecutor(i));
		}
		
		exec.shutdown();
	}
	
	private class ThreadExecutor implements Runnable {
		int i;

		private ThreadExecutor(int i) {
			this.i=i;
		}
		@Override
		public void run() {
			//With submit method from Executors you can not use ExceptionHandler.
			//Anyway you do not need it, because using the submit method instead of execute method the exception
			//from the threads is caught by the Executors implementation and if you have a Future Task you can 
			//treat it in you main thread.
			//Thread.setDefaultUncaughtExceptionHandler(new MyUncaughtExceptionHandler());
			try {
				synchronized(this) {
					wait(4000);
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println(Thread.currentThread().getName() + " number: " + i + "\n");
			throw new RuntimeException("EXCEPTION: " + i);
		}		
	}
}