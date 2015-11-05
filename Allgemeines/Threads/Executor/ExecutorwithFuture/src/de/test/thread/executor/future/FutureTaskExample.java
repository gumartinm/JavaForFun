package de.test.thread.executor.future;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


public class FutureTaskExample {
	
	public Car test() {
		Car carResult = null;
		FutureTask<Car> task = new FutureTask<>(() -> new Car(99));
//		FutureTask<Car> task = new FutureTask<>(new Callable<Car>() {
//
//			@Override
//			public Car call() throws Exception {
//				return new Car(99);
//			}
//
//		});

		new Thread(task).start();

		try {
			carResult = task.get(1000, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		} catch (ExecutionException e) {
			throw launderThrowable(e);
		} catch (TimeoutException e) {
			System.out.println("Timeout");
		} finally {
			task.cancel(true);
		}

		return carResult;
	}

	public static class Car {
		private final int id;

		public Car(int id) {
			this.id = id;
		}
		
		public int getId() {
			return id;
		}
	}

	private RuntimeException launderThrowable(final Throwable exception) {
		exception.printStackTrace();
		if (exception instanceof RuntimeException)
			return (RuntimeException)exception;
		else if (exception instanceof Error)
			throw (Error)exception;
		else
			throw new IllegalStateException("Not unchecked", exception);
	}

}
