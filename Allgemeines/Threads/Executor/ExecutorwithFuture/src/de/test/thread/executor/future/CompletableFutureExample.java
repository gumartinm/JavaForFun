package de.test.thread.executor.future;

import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * AKA Promise in Java :)
 *
 */
public class CompletableFutureExample {
	

	public void doRun() {
				
        try {
			String someValue = doRunThrowableAsync();

//          STDOUT:			
//			ForkJoinPool.commonPool-worker-1
//			First promise
//			ForkJoinPool.commonPool-worker-1
//			Value from first promise: Hello World
//			ForkJoinPool.commonPool-worker-2
//			Second promise
//			ForkJoinPool.commonPool-worker-2
//			#1 First handleAsync value: Enchaining promises
//			ForkJoinPool.commonPool-worker-1
//			#2 First thenAcceptAsync value: First handleAsync value: Enchaining promises
//			ForkJoinPool.commonPool-worker-1
//			#3 Second handleAsync
//			ForkJoinPool.commonPool-worker-1
//			#4 Second thenAcceptAsync value: Second handleAsync
//			ForkJoinPool.commonPool-worker-1
//			#5 Third handleAsync
//			Third handleAsync
			
			
			// The same but running in the main thread.
			//String someValue = doRunThrowable();

//          STDOUT: 			
//			ForkJoinPool.commonPool-worker-1
//			First promise
//			main
//			Value from first promise: Hello World
//			ForkJoinPool.commonPool-worker-1
//			Second promise
//			main
//			#1 First handle value: Enchaining promises
//			main
//			#2 First thenAccept value: First handle value: Enchaining promises
//			main
//			#3 Second handle
//			main
//			#4 Second thenAccept value: Second handle
//			main
//			#5 Third handle
//			Third handle
			
			System.out.println(someValue);
        } catch (final InterruptedException e) {
			Thread.currentThread().interrupt();
			
            e.printStackTrace();
        } catch (final ExecutionException e) {
            final Throwable cause = e.getCause();
            
            throw this.launderThrowable(cause);
        }
	}
	
	private String doRunThrowableAsync() throws InterruptedException, ExecutionException, CancellationException {
		
		// All async methods without an explicit Executor argument are performed using the ForkJoinPool.commonPool()
		// (unless it does not support a parallelism level of at least two, in which case, a new Thread is created to run each task).
		
		
		// ForkJoinPool.makeCommonPool() tiene la lógica para calcular el parallelism. El parallelism indica el número de hilos
		// en el pool del ForkJoinPool común (compartido en una misma JVM)
		// Viene dado o por la propiedad del sistema java.util.concurrent.ForkJoinPool.common.parallelism
		// o por el número de CPUs que tenemos Runtime.getRuntime().availableProcessors()
		
		// El problema puede venir si no sabemos quién está usando el pool común :( Si alguien lo usa mucho mis promesas
		// puede que no se ejecuten nunca o tarden más en ejecutarse :(
		// Supongo que en ese caso podría usar Executors pasados como parámetro a las promesas :/
		
		
		// No using lambdas because I am learning this stuff.
		
		return CompletableFuture.supplyAsync(new Supplier<String>() {
			
			@Override
			public String get() {
				
				System.out.println(Thread.currentThread().getName());
				System.out.println("First promise");
				
				// If exception, we get into the rejected stage.
				// When exception the thenAcceptAsync does not run because it runs when no rejected stage.
				// throw new UnsupportedOperationException("getPage ERROR"); 
				
				return getPage();
			}
			
		} /* IF YOU DO NOT WANT TO USE THE commonPool YOU SHOULD BE PASSING HERE YOUR OWN Executor */ )
		
		 // consumes and returns new promise
		.thenComposeAsync(new Function<String, CompletionStage<String>>() {

			@Override
			public CompletionStage<String> apply(String page) {
				
				// If exception, we get into the rejected stage.
				//throw new UnsupportedOperationException();
				
				System.out.println(Thread.currentThread().getName());
				System.out.println("Value from first promise: " + page);
				
				return CompletableFuture.supplyAsync(() -> {                 // The same as above but with lambdas
					
					// If exception, we get into the rejected stage.
					//throw new UnsupportedOperationException();

					System.out.println(Thread.currentThread().getName());
					System.out.println("Second promise");
					
					return "Enchaining promises";
				});	
			}
			
		}) 
		 // runs callback whether the stage was resolved or rejected. IT ALWAYS RUNS even in rejected stage.
		 // I guess, it can be used as a FINALLY for promises.
		.handleAsync(new BiFunction<String, Throwable, String>() {

			@Override
			public String apply(String secondPromiseValue, /* the method before returns String */
								Throwable exception        /* not null if rejected stage I guess... */ ) {
				
				if (exception != null ) {
					// rejected stage I guess...
					
					System.out.println("#1 First handleAsync exception: ");
					exception.printStackTrace();
				} else {
					// resolved stage I guess...
					
					System.out.println(Thread.currentThread().getName());
					System.out.println("#1 First handleAsync value: " + secondPromiseValue);
				}
				
				// handleAsync runs always and enables us to return something
				return "First handleAsync value: " + secondPromiseValue;
				
				// If exception, we get into the rejected stage.
				// When exception the below thenAcceptAsync does not run because it runs when no rejected stage.
				// throw new UnsupportedOperationException("First handleAsync ERROR"); 
			}
			
		})
		 // consumes and returns nothing. IT RUNS IF NO REJECTED STAGE (if there was no errors)
		.thenAcceptAsync(new Consumer<String>() {
			
			@Override
			public void accept(String firstHandleAsyncValue) {
				
				System.out.println(Thread.currentThread().getName());
				System.out.println("#2 First thenAcceptAsync value: " + firstHandleAsyncValue);
				
				// If exception, we get into the rejected stage.
				// throw new UnsupportedOperationException("First thenAcceptAsync ERROR");
			}
			
		})
		 // runs callback whether the stage was resolved or rejected. IT ALWAYS RUNS even in rejected stage.
		 // I guess, it can be used as a FINALLY for promises.
		.handleAsync(new BiFunction<Void, Throwable, String>() {

			@Override
			public String apply(Void nothing,         /* the thenAcceptAsync before returns nothing */
					            Throwable exception   /* not null if rejected stage I guess... */ ) {
				
				if (exception != null ) {
					// rejected stage I guess...
					
					System.out.println("#3 Second handleAsync exception: ");
					exception.printStackTrace();
				} else {
					// resolved stage I guess...
					
					System.out.println(Thread.currentThread().getName());
					System.out.println("#3 Second handleAsync");
				}
							
				return "Second handleAsync";
			}
		})
		
		 // Because the above handleAsync does not throw exception this method will always run even if
		 // the first handleAsync threw exception. The second handleAsync (above) cancels the rejected stage.
		
		 // consumes and returns nothing. IT RUNS IF NO REJECTED STAGE (if there was no errors)
		.thenAcceptAsync(new Consumer<String>() {
			
			@Override
			public void accept(String secondHandleAsyncValue) {
				
				System.out.println(Thread.currentThread().getName());
				System.out.println("#4 Second thenAcceptAsync value: " + secondHandleAsyncValue);
				
				// If exception, we get into the rejected stage.
				// throw new UnsupportedOperationException("Second thenAcceptAsync ERROR");
			}
			
		})
		
		 // runs callback whether the stage was resolved or rejected. Using lambdas. IT ALWAYS RUNS even in rejected stage.
		 // I guess, it can be used as a FINALLY for promises.
		.handleAsync((nothing, exception) -> {
			
			if (exception != null ) {
				// rejected stage I guess...
				
				System.out.println("#5 Third handleAsync exception: ");
				exception.printStackTrace();
			} else {
				// resolved stage I guess...
				
				System.out.println(Thread.currentThread().getName());
				System.out.println("#5 Third handleAsync");
			}
						
			return "Third handleAsync";
		})
		
		// Synchronous point. So doRunThrowableAsync should be called doRunThrowable but who cares... :D
		.get();
		
//        No need of this because I am using get()
//		
//        try {
//            Thread.sleep(60000);
//        } catch (final InterruptedException e) {
//			Thread.currentThread().interrupt();
//            e.printStackTrace();
//        }
	
	}

	
	private String doRunThrowable() throws InterruptedException, ExecutionException, CancellationException {
		
		return CompletableFuture.supplyAsync(new Supplier<String>() {
			
			@Override
			public String get() {
				
				System.out.println(Thread.currentThread().getName());
				System.out.println("First promise");
				
				// If exception, we get into the rejected stage.
				// When exception the thenAccept does not run because it runs when no rejected stage.
				// throw new UnsupportedOperationException("getPage ERROR"); 
				
				return getPage();
			}
			
		} /* If you do not want to use the commonPool you should be passing here your own Executor */ )
		
		 // consumes and returns new promise
		.thenCompose(new Function<String, CompletionStage<String>>() {

			@Override
			public CompletionStage<String> apply(String page) {
				
				// If exception, we get into the rejected stage.
				//throw new UnsupportedOperationException();

				System.out.println(Thread.currentThread().getName());
				System.out.println("Value from first promise: " + page);
				
				return CompletableFuture.supplyAsync(() -> {                 // The same as above but with lambdas
					
					System.out.println(Thread.currentThread().getName());
					System.out.println("Second promise");
					
					return "Enchaining promises";
				});	
			}
			
		}) 
		 // runs callback whether the stage was resolved or rejected. IT ALWAYS RUNS even in rejected stage.
		 // I guess, it can be used as a FINALLY for promises.
		.handle(new BiFunction<String, Throwable, String>() {

			@Override
			public String apply(String secondPromiseValue, /* the method before returns String */
								Throwable exception        /* not null if rejected stage I guess... */ ) {
				
				if (exception != null ) {
					// rejected stage I guess...
					
					System.out.println("#1 First handle exception: ");
					exception.printStackTrace();
				} else {
					// resolved stage I guess...
					
					System.out.println(Thread.currentThread().getName());
					System.out.println("#1 First handle value: " + secondPromiseValue);
				}
				
				// handleAsync runs always and enables us to return something
				return "First handle value: " + secondPromiseValue;
				
				// If exception, we get into the rejected stage.
				// When exception the below thenAccept does not run because it runs when no rejected stage.
				// throw new UnsupportedOperationException("First handle ERROR"); 
			}
			
		})
		 // consumes and returns nothing. IT RUNS IF NO REJECTED STAGE (if there was no errors)
		.thenAccept(new Consumer<String>() {
			
			@Override
			public void accept(String firstHandleValue) {
				
				System.out.println(Thread.currentThread().getName());
				System.out.println("#2 First thenAccept value: " + firstHandleValue);
				
				// If exception, we get into the rejected stage.
				// throw new UnsupportedOperationException("First thenAccept ERROR");
			}
			
		})
		 // runs callback whether the stage was resolved or rejected. IT ALWAYS RUNS even in rejected stage.
		 // I guess, it can be used as a FINALLY for promises.
		.handle(new BiFunction<Void, Throwable, String>() {

			@Override
			public String apply(Void nothing,         /* the thenAcceptAsync before returns nothing */
					            Throwable exception   /* not null if rejected stage I guess... */ ) {
				
				if (exception != null ) {
					// rejected stage I guess...
					
					System.out.println("#3 Second handle exception: ");
					exception.printStackTrace();
				} else {
					// resolved stage I guess...
					
					System.out.println(Thread.currentThread().getName());
					System.out.println("#3 Second handle");
				}
							
				return "Second handle";
			}
		})
		
		 // Because the above handle does not throw exception this method will always run even if
		 // the first handle threw exception. The second handle (above) cancels the rejected stage.
		
		 // consumes and returns nothing. IT RUNS IF NO REJECTED STAGE (if there was no errors)
		.thenAccept(new Consumer<String>() {
			
			@Override
			public void accept(String secondHandleValue) {
				
				System.out.println(Thread.currentThread().getName());
				System.out.println("#4 Second thenAccept value: " + secondHandleValue);
				
				// If exception, we get into the rejected stage.
				// throw new UnsupportedOperationException("Second thenAccept ERROR");
			}
			
		})
		
		 // runs callback whether the stage was resolved or rejected. Using lambdas. IT ALWAYS RUNS even in rejected stage.
		 // I guess, it can be used as a FINALLY for promises.
		.handle((nothing, exception) -> {
			
			if (exception != null ) {
				// rejected stage I guess...
				
				System.out.println("#5 Third handle exception: ");
				exception.printStackTrace();
			} else {
				// resolved stage I guess...
				
				System.out.println(Thread.currentThread().getName());
				System.out.println("#5 Third handle");
			}
						
			return "Third handle";
		})
		
		.get();
		
		
//      No need of this because I am using ̶g̶̶e̶t̶(̶) synchronous methods. I am using the main thread :)
//		
//      try {
//          Thread.sleep(60000);
//      } catch (final InterruptedException e) {
//			Thread.currentThread().interrupt();
//          e.printStackTrace();
//      }
	}
	
	private String getPage() {
		
		return "Hello World";
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
