package de.test.thread.executor.future;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;


public class TimerSchedulerExample {
	ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

	public void test() {
		ScheduledFuture<Integer> future = executor.schedule(() -> 666, 2000, TimeUnit.MILLISECONDS);
//		ScheduledFuture<Integer> future = executor.schedule(new Callable<Integer>() {
//
//			@Override
//			public Integer call() throws Exception {
//				return 666;
//			}
//
//		}, 2000, TimeUnit.MILLISECONDS);

		long delay;
		while ((delay = future.getDelay(TimeUnit.MILLISECONDS)) > 0) {
			System.out.println("Delay: " + delay);
		}

		try {
			Integer result = future.get();
			System.out.println(result);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		} catch (ExecutionException e) {
			throw launderThrowable(e);
		} finally {
			future.cancel(true);
		}
	}
	
	
    private RuntimeException launderThrowable(final Throwable exception) {
        exception.printStackTrace();
        if (exception instanceof RuntimeException)
            return (RuntimeException) exception;
        else if (exception instanceof Error)
            throw (Error) exception;
        else
            throw new IllegalStateException("Not unchecked", exception);
    }
}
