package de.test.thread.executor.execute;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadTest {
	private final ExecutorService exec = Executors.newFixedThreadPool(2);

	public void start () {
		
		//We have a pool with 2 threads. 
		//The third task will wait in a Queue until one thread of that pool is freed.
		for (int i = 0; i < 3 ; i++) {
			exec.execute(new ThreadExecutor(i));
		}
		
		try {
			synchronized(this) {
				wait(10000);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		//We have a pool with 2 threads. 
		//The third task will wait in a Queue until one thread of that pool is freed.
		for (int i = 0; i < 3 ; i++) {
			exec.execute(new ThreadExecutor(i));
		}
		
		try {
			synchronized(this) {
				wait(5000);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
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
			//With execute method from Executors you can not use ExceptionHandler.
			//The only way to catch unexpected exceptions is using try/catch RuntimeException in your code.
			//Another way is using submit method instead of execute method. There is an example with submite method
			//called ExecutorwithFuture
			//Thread.setDefaultUncaughtExceptionHandler(new MyUncaughtExceptionHandler());
			try {
				try {
					synchronized(this) {
						wait(2000);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				throw new RuntimeException("EXCEPTION: " + i);
			} catch (RuntimeException e) {
				//We catch unexpected exceptions with this code. 
				//From Effective Java Item 58: all of the unchecked throwables 
				//you implement should subclass RuntimeException (directly or indirectly).
				//Code throwing unexpected exceptions not being a RuntimeException subclass is a flawed code
				//or at least the API should have a nice Javadoc about that flaw.
				System.out.println(Thread.currentThread().getName() + "\n");
				e.printStackTrace();
			}
		}		
	}
}
