package de.test.thread.executor.future;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ThreadTest {
    private final ExecutorService exec = Executors.newFixedThreadPool(2);

    public void start () {
        final Future<?> [] future = new Future<?> [3];

        Thread.currentThread().setName("main");
        //We have a pool with 2 threads.
        //The third task will wait in a Queue until one thread of that pool is freed.
        for (int i = 0; i < 3 ; i++) {
        	
        	// When using Runnable if you use the second parameter of ExecutorService.submit()
        	// when calling future[i].get() what you will ALWAYS have as returned value will be the string
        	// passed as input parameter to the submit() method.
        	// I have never used it, if I have something to return it will come from the
        	// Callable and in that case I will not be using a Runnable :/
        	
        	// this.exec.submit(new ThreadExecutor(i), "Returned string when calling future[i].get");
        	
            future[i] = this.exec.submit(new ThreadExecutor(i));
        }

        for (int i = 0; i < 3 ; i++) {
            try {
            	// We are using Runnable with submit() with one input parameter, so get() returns nothing :(
                future[i].get();
            } catch (final InterruptedException e) {
    			Thread.currentThread().interrupt();
                e.printStackTrace();
            } catch (final ExecutionException e) {
                //The exception thrown in the threads is caught by the main thread here.
                System.out.println("Exception from task " + i + ": "
                        + Thread.currentThread().getName() + "\n");
                final Throwable cause = e.getCause();
                // try { Uncomment this code in order to run the test. :(
                throw this.launderThrowable(cause);
                // } catch (final Throwable exception) {
                // exception.printStackTrace();
                // }
            } finally {
                future[i].cancel(true);
            }
        }

        try {
            Thread.sleep(4000);
        } catch (final InterruptedException e) {
			Thread.currentThread().interrupt();
            e.printStackTrace();
        }

        //We have a pool with 2 threads.
        //The third task will wait in a Queue until one thread of that pool is freed.
        for (int i = 0; i < 3 ; i++) {
            //Code without implementing the Future class. The exception from the threads is not treated.
            this.exec.submit(new ThreadExecutor(i));
        }

        this.exec.shutdown();

        // After exec.shutdown if we try to execute more tasks a RejectedExecutionException
        // exception will be thrown to the main thread.
        System.out.println("Going to receive a RejectedExecutionException ");
        for (int i = 0; i < 3; i++) {
            this.exec.execute(new ThreadExecutor(i));
        }
    }

    // Before calling launderThrowable, Preloader tests for the known checked
    // exceptions and rethrows
    // them. That leaves only unchecked exceptions, which Preloader handles by
    // calling launderThrowable and throwing the
    // result. If the Throwable passed to launderThrowable is an Error,
    // launderThrowable rethrows it directly; if it is not a
    // RuntimeException, it throws an IllegalStateException to indicate a logic
    // error. That leaves only RuntimeException,
    // which launderThrowable returns to its caller, and which the caller
    // generally rethrows

    /**
     * If the Throwable is an Error, throw it; if it is a RuntimeException
     * return it, otherwise throw IllegalStateException
     */
    private RuntimeException launderThrowable(final Throwable exception) {
        exception.printStackTrace();
        if (exception instanceof RuntimeException)
            return (RuntimeException) exception;
        else if (exception instanceof Error)
            throw (Error) exception;
        else
            throw new IllegalStateException("Not unchecked", exception);
    }


    private class ThreadExecutor implements Runnable {
        int i;

        private ThreadExecutor(final int i) {
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
                Thread.sleep(4000);
            } catch (final InterruptedException e) {
    			Thread.currentThread().interrupt();
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName() + " number: " + this.i + "\n");
            throw new RuntimeException("EXCEPTION: " + this.i);
        }
    }
}
