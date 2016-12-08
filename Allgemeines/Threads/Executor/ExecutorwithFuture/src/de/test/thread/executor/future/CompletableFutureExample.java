package de.test.thread.executor.future;

import java.util.Optional;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.function.BiConsumer;
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
        	
        	// Using ForkJoinPool
			String someValue = doRunThrowableAsync();
			System.out.println(someValue);

//          STDOUT:			
//			ForkJoinPool.commonPool-worker-1
//			First promise
//			ForkJoinPool.commonPool-worker-1
//			Value from first promise: Hello World
//			ForkJoinPool.commonPool-worker-2
//			Second promise
//			ForkJoinPool.commonPool-worker-2
//			#1 First handleAsync value: Enchaining promises
//			ForkJoinPool.commonPool-worker-2
//			#2 First whenCompleteAsync value: First handleAsync value: Enchaining promises
//			ForkJoinPool.commonPool-worker-2
//			#3 First thenAcceptAsync value: First handleAsync value: Enchaining promises
//			ForkJoinPool.commonPool-worker-2
//			#4 Second handleAsync
//			ForkJoinPool.commonPool-worker-2
//			#5 Second thenAcceptAsync value: Second handleAsync
//			ForkJoinPool.commonPool-worker-2
//			#6 Third handleAsync
//			Third handleAsync
			

			// The same but running everything in the main thread.
			System.out.println();
			System.out.println("#################################################### doRunThrowable ####################################################");
			System.out.println();
			someValue = doRunThrowable();
			System.out.println(someValue);
			
//          STDOUT: 			
//			ForkJoinPool.commonPool-worker-2
//			First promise
//			main
//			Value from first promise: Hello World
//			ForkJoinPool.commonPool-worker-2
//			Second promise
//			main
//			#1 First handle value: Enchaining promises
//			main
//			#2 First whenComplete value: First handle value: Enchaining promises
//			main
//			#3 First thenAccept value: First handle value: Enchaining promises
//			main
//			#4 Second handle
//			main
//			#5 Second thenAccept value: Second handle
//			main
//			#6 Third handle
//			Third handle
			
				
			System.out.println();
			System.out.println("#################################################### doRunThrowableSimpleAsync ####################################################");
			System.out.println();
			doRunThrowableSimpleAsync();
			
			
			System.out.println();
			System.out.println("#################################################### doRunThrowableComplexAsync ####################################################");
			System.out.println();
			doRunThrowableComplexAsync().ifPresent(value -> System.out.println("doRunThrowableComplexAsync value: " + value));
			
			
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
		
		 // returns new promise
		 // IT DOES NOT RUN IN REJECTED STAGE (if there was exception)
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
		
		 // Creates a new CompletableFuture with the returned value FROM HERE (unlike whenCompleteAsync)
		 // runs callback whether the stage was resolved or rejected. IT ALWAYS RUNS even in rejected stage.
		 // Could be used as a complex finally.
		.handleAsync(new BiFunction<String, Throwable, String>() {

			@Override
			public String apply(String secondPromiseValue, /* the method before returns String */
								Throwable exception        /* not null if rejected stage I guess... */ ) {
				
				if (exception != null ) {
					// rejected stage I guess...
					
					// Because we do not rethrow exception we are canceling the rejected stage :(
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
		
		 // Creates a new CompletableFuture with the value returned FROM handleAsync or thenComposeAsync
		 // (depending on what was run before) THAT IS THE DIFERENCE BETWEEN handleAsync AND whenCompleteAsync
		 // IT DOES NOT CREATE A NEW CompletableFuture with a different value, it creates a new CompletableFuture
		 // with the same value as before.
		
		 // Runs callback whether the stage was resolved or rejected. IT ALWAYS RUNS even in rejected stage.
		 // Could be used as a finally neither as complex as handleAsync nor as simple as exceptionally method, 
		.whenCompleteAsync(new BiConsumer<String, Throwable>() {

			@Override
			public void accept(String firstHandleAsyncValue,   /* the method before returns String */
							   Throwable exception             /* not null if rejected stage I guess... */) {
				
				
				if (exception != null ) {
					// rejected stage I guess...
					
					// Because we do not rethrow exception we are canceling the rejected stage :(
					System.out.println("#2 First whenCompleteAsync exception: ");
					exception.printStackTrace();
				} else {
					// resolved stage I guess...
					
					System.out.println(Thread.currentThread().getName());
					System.out.println("#2 First whenCompleteAsync value: " + firstHandleAsyncValue);
				}
				
				// If exception, we get into the rejected stage.
				// throw new UnsupportedOperationException();     
			}
		})
		
		 // Creates a new CompletableFuture with Void value.
		 // IT RUNS IF NO REJECTED STAGE (if there was no errors)
		.thenAcceptAsync(new Consumer<String>() {
			
			@Override
			public void accept(String firstHandleAsyncValue) {
				
				System.out.println(Thread.currentThread().getName());
				System.out.println("#3 First thenAcceptAsync value: " + firstHandleAsyncValue);
				
				// If exception, we get into the rejected stage.
				// throw new UnsupportedOperationException("First thenAcceptAsync ERROR");
			}
			
		})
		
		 // Creates a new CompletableFuture with the returned value FROM HERE (unlike whenCompleteAsync)
		 // runs callback whether the stage was resolved or rejected. IT ALWAYS RUNS even in rejected stage.
		 // Could be used as a complex finally.
		.handleAsync(new BiFunction<Void, Throwable, String>() {

			@Override
			public String apply(Void nothing,         /* the thenAcceptAsync before returns nothing */
					            Throwable exception   /* not null if rejected stage I guess... */ ) {
				
				if (exception != null ) {
					// rejected stage I guess...
					
					// Because we do not rethrow exception we are canceling the rejected stage :(
					System.out.println("#4 Second handleAsync exception: ");
					exception.printStackTrace();
				} else {
					// resolved stage I guess...
					
					System.out.println(Thread.currentThread().getName());
					System.out.println("#4 Second handleAsync");
				}
							
				return "Second handleAsync";
			}
		})
		
		 // Because the above handleAsync does not throw exception this method will always run even if
		 // the first handleAsync threw exception. The second handleAsync (above) cancels the rejected stage.
		
		 // Creates a new CompletableFuture with Void value.
		 // IT RUNS IF NO REJECTED STAGE (if there was no errors)
		.thenAcceptAsync(new Consumer<String>() {
			
			@Override
			public void accept(String secondHandleAsyncValue) {
				
				System.out.println(Thread.currentThread().getName());
				System.out.println("#5 Second thenAcceptAsync value: " + secondHandleAsyncValue);
				
				// If exception, we get into the rejected stage.
				// throw new UnsupportedOperationException("Second thenAcceptAsync ERROR");
			}
			
		})
		
		 // Creates a new CompletableFuture with the returned value FROM HERE (unlike whenCompleteAsync)
		 // runs callback whether the stage was resolved or rejected. IT ALWAYS RUNS even in rejected stage.
		 // Could be used as a complex finally.
		.handleAsync((nothing, exception) -> {
			
			if (exception != null ) {
				// rejected stage I guess...
				
				// Because we do not rethrow exception we are canceling the rejected stage :(
				System.out.println("#6 Third handleAsync exception: ");
				exception.printStackTrace();
			} else {
				// resolved stage I guess...
				
				System.out.println(Thread.currentThread().getName());
				System.out.println("#6 Third handleAsync");
			}
						
			return "Third handleAsync";
		})
		
		 // IF NOT REJECTED STAGE: creates a new CompletableFuture with the value returned FROM whatever was run before.
		 // IF REJECTED STAGE: creates a new CompletableFuture with the value returned from exceptionally.
		 // It is almost like handleAsync but with less functionality because we have no access to the value returned by the stage before.
		
		 // IT ONLY RUNS IN REJECTED STAGE!!
		 // Could be used as a simple finally
		.exceptionally(new Function<Throwable, String>() {

			@Override
			public String apply(Throwable exception) {
					
				// Because we do not rethrow exception we are canceling the rejected stage :(
				System.out.println(Thread.currentThread().getName());
				System.out.println("#7 You only see me if rejected stage.");
				exception.printStackTrace();
				
				return "You will not see me if not rejected stage";
			}
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

	
	private void doRunThrowableSimpleAsync() throws InterruptedException, ExecutionException, CancellationException {

		Void nothing = // There is nothing because of thenAcceptAsync
				
			CompletableFuture.supplyAsync(() -> {
			
			System.out.println(Thread.currentThread().getName());
			System.out.println("First promise");

			// If exception, we get into the rejected stage.
			// When exception the thenAcceptAsync does not run because it runs
			// when no rejected stage.
			// throw new UnsupportedOperationException("getPage ERROR");

			return getPage();
		})
		
		.thenAcceptAsync(page -> {
			
			System.out.println(Thread.currentThread().getName());
			System.out.println("Page: " + page);
			
		})
		
		.exceptionally(exception -> {

			System.out.println(Thread.currentThread().getName());
			System.out.println("exceptionally");
			exception.printStackTrace();
			
			return null;
		})
		
		// Synchronization for the sake of this example.
		// RETURNS VOID BECAUSE thenAcceptAsync creates a new CompletableFuture with Void value :(
		.get();

	}
	
	
	private Optional<Long> doRunThrowableComplexAsync() throws InterruptedException, ExecutionException, CancellationException {

		return CompletableFuture.supplyAsync(() -> {
			
			System.out.println(Thread.currentThread().getName());
			System.out.println("First promise");

			// If exception, we get into the rejected stage.
			// When exception the thenAcceptAsync does not run because it runs
			// when no rejected stage.
			// throw new UnsupportedOperationException("getPage ERROR");

			return Optional.ofNullable(getPage());
		})
				
		// Changing the returned value from String to Long :)
		// Remember, if we do not rethrow exception in handleAsync the rejected stage is cancelled :(
		.handleAsync((page, exception) -> Optional.of(999666L))
		
		.exceptionally(exception -> {
			
			// Because we do not rethrow exception we are canceling the rejected stage :(
			
			// This code will never be run because the handleAsync before is cancelling exceptions :(
			System.out.println(Thread.currentThread().getName());
			System.out.println("exceptionally");
			exception.printStackTrace();
			
			// null sucks :)
			return Optional.empty();
		})
		
		// For the sake of this example.
		// RETURNS Optional<Long> BECAUSE handleAsync creates a new CompletableFuture.
		// IT WILL RETURN Optional.of(999666L) IF EVERYTHING WENT OK OR Optional.empty() IF ERROR
		.get();	
	}
	
	private String doRunThrowable() throws InterruptedException, ExecutionException, CancellationException {
		
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
		
		 // returns new promise
		 // IT DOES NOT RUN IN REJECTED STAGE (if there was exception)
		.thenCompose(new Function<String, CompletionStage<String>>() {

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
		
		 // Creates a new CompletableFuture with the returned value FROM HERE (unlike whenComplete)
		 // runs callback whether the stage was resolved or rejected. IT ALWAYS RUNS even in rejected stage.
		 // Could be used as a complex finally.
		.handle(new BiFunction<String, Throwable, String>() {

			@Override
			public String apply(String secondPromiseValue, /* the method before returns String */
								Throwable exception        /* not null if rejected stage I guess... */ ) {
				
				if (exception != null ) {
					// rejected stage I guess...
					
					// Because we do not rethrow exception we are canceling the rejected stage :(
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
		
		 // Creates a new CompletableFuture with the value returned FROM handle or thenCompose
		 // (depending on what was run before) THAT IS THE DIFERENCE BETWEEN handle AND whenComplete
		 // IT DOES NOT CREATE A NEW CompletableFuture with a different value, it creates a new CompletableFuture
		 // with the same value as before.
		
		 // Runs callback whether the stage was resolved or rejected. IT ALWAYS RUNS even in rejected stage.
		 // Could be used as a finally neither as complex as handle nor as simple as exceptionally method, 
		.whenComplete(new BiConsumer<String, Throwable>() {

			@Override
			public void accept(String firstHandleValue,   /* the method before returns String */
							   Throwable exception             /* not null if rejected stage I guess... */) {
				
				
				if (exception != null ) {
					// rejected stage I guess...
					
					// Because we do not rethrow exception we are canceling the rejected stage :(
					System.out.println("#2 First whenComplete exception: ");
					exception.printStackTrace();
				} else {
					// resolved stage I guess...
					
					System.out.println(Thread.currentThread().getName());
					System.out.println("#2 First whenComplete value: " + firstHandleValue);
				}
				
				// If exception, we get into the rejected stage.
				// throw new UnsupportedOperationException();     
			}
		})
		
		 // Creates a new CompletableFuture with Void value.
		 // IT RUNS IF NO REJECTED STAGE (if there was no errors)
		.thenAccept(new Consumer<String>() {
			
			@Override
			public void accept(String firstHandleValue) {
				
				System.out.println(Thread.currentThread().getName());
				System.out.println("#3 First thenAccept value: " + firstHandleValue);
				
				// If exception, we get into the rejected stage.
				// throw new UnsupportedOperationException("First thenAcceptAsync ERROR");
			}
			
		})
		
		 // Creates a new CompletableFuture with the returned value FROM HERE (unlike whenComplete)
		 // runs callback whether the stage was resolved or rejected. IT ALWAYS RUNS even in rejected stage.
		 // Could be used as a complex finally.
		.handle(new BiFunction<Void, Throwable, String>() {

			@Override
			public String apply(Void nothing,         /* the thenAcceptAsync before returns nothing */
					            Throwable exception   /* not null if rejected stage I guess... */ ) {
				
				if (exception != null ) {
					// rejected stage I guess...
					
					// Because we do not rethrow exception we are canceling the rejected stage :(
					System.out.println("#4 Second handle exception: ");
					exception.printStackTrace();
				} else {
					// resolved stage I guess...
					
					System.out.println(Thread.currentThread().getName());
					System.out.println("#4 Second handle");
				}
							
				return "Second handle";
			}
		})
		
		 // Because the above handleAsync does not throw exception this method will always run even if
		 // the first handle threw exception. The second handle (above) cancels the rejected stage.
		
		 // Creates a new CompletableFuture with Void value.
		 // IT RUNS IF NO REJECTED STAGE (if there was no errors)
		.thenAccept(new Consumer<String>() {
			
			@Override
			public void accept(String secondHandleValue) {
				
				System.out.println(Thread.currentThread().getName());
				System.out.println("#5 Second thenAccept value: " + secondHandleValue);
				
				// If exception, we get into the rejected stage.
				// throw new UnsupportedOperationException("Second thenAccept ERROR");
			}
			
		})
		
		 // Creates a new CompletableFuture with the returned value FROM HERE (unlike whenComplete)
		 // runs callback whether the stage was resolved or rejected. IT ALWAYS RUNS even in rejected stage.
		 // Could be used as a complex finally.
		.handle((nothing, exception) -> {
			
			if (exception != null ) {
				// rejected stage I guess...
				
				// Because we do not rethrow exception we are canceling the rejected stage :(
				System.out.println("#6 Third handle exception: ");
				exception.printStackTrace();
			} else {
				// resolved stage I guess...
				
				System.out.println(Thread.currentThread().getName());
				System.out.println("#6 Third handle");
			}
						
			return "Third handle";
		})
		
		 // IF NOT REJECTED STAGE: creates a new CompletableFuture with the value returned FROM whatever was run before.
		 // IF REJECTED STAGE: creates a new CompletableFuture with the value returned from exceptionally.
		 // It is almost like handle but with less functionality because we have no access to the value returned by the stage before.
		
		 // IT ONLY RUNS IN REJECTED STAGE!!
		 // Could be used as a simple finally
		.exceptionally(new Function<Throwable, String>() {

			@Override
			public String apply(Throwable exception) {
					
				// Because we do not rethrow exception we are canceling the rejected stage :(
				System.out.println(Thread.currentThread().getName());
				System.out.println("#7 You only see me if rejected stage.");
				exception.printStackTrace();
				
				return "You will not see me if not rejected stage";
			}
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
