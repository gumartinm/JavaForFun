package de.test.fork.join;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

/**
 * 
 * Explanation taken from:
 * {@link http://www.javacreed.com/java-fork-join-example/}
 * 
 * 
 * Ventajas respecto a Executors (esto es lo único que veo...):
 * Minimiza el número de colas y tamaño de colas que normalmente tendría que lanzar yo
 * vía Executors cuando queremos hacer algo recursivo. NO VEO OTRA VENTAJA A ESTO :/
 * 
 * Este ejemplo tendría que hacerlo lanzado ThreadPools a mano en cada subdirectorio y estos pools tendrían un
 * tamaño reservado para sus colas (mínimo, máximo y umbral) y por tanto estarían consumiendo
 * recursos que probablemente no se vayan a usar.
 * 
 * Con ForkJoinPool optimizo porque él se encarga de distribuir las tareas por los
 * pool de hilos que actualmente existen (sin necesidad de crear nuevos) Tendremos tantos hilos y pool de hilos como
 * CPUs tenga mi PC y no más.
 */
public class ForkJoinTaskExample {
	
	
	//	The common pool is by default constructed with default parameters, but these may be controlled by setting three system properties:
	//	
	//	java.util.concurrent.ForkJoinPool.common.parallelism - the parallelism level, a non-negative integer
	//	java.util.concurrent.ForkJoinPool.common.threadFactory - the class name of a ForkJoinPool.ForkJoinWorkerThreadFactory
	//	java.util.concurrent.ForkJoinPool.common.exceptionHandler - the class name of a Thread.UncaughtExceptionHandler
		
	// By default there is no value for java.util.concurrent.ForkJoinPool.common.exceptionHandler
	// SO BY DEFAULT, UNCAUGHT EXCEPTIONS ARE LOST FOREVER!!!! :/
	final ForkJoinPool pool = new ForkJoinPool();
	
	
	public void doRun(File file) {

		synchronous(file);
		
		// After pool.shutdown() and pool.shutdownNow() invocations to pool.invoke(task), pool.execute(task) and
		// pool.submit(task) throw RejectedExecutionException
		// synchronous(file);

		
		
		// asynchronous(file);
		
		// After pool.shutdown() and pool.shutdownNow() invocations to pool.invoke(task), pool.execute(task) and
		// pool.submit(task) throw RejectedExecutionException
		//asynchronous(file);
		
		
		
		// IF NO JOIN, ForkJoinTasks WILL BE KILLED BY THE JVM.
		
		// Unlike Executor, threads in ForkJoinPool are daemons and they are killed by the JVM when exiting.
		
		// Executor: NOT DAEMON THREADS. THEY NO DIE WHEN JVM EXITS. JVM MUST WAIT FOR THEM TO EXIT (we need a shutdown)
		// ForkJoinPool: DAEMON THREADS. THEY DIE WHEN JVM EXITS. JVM DO NOT WAIT FOR THEM TO EXIT (we do not need shutdown
		// and it also seems to do nothing :/ )
		
		// So, if not pool.join() program will exit and your ForkJoinTasks will die :(
	}
	
	/**
	 * synchronous execution
	 * 
	 */
	private void synchronous(File file) {
		SizeOfFileTask task = new SizeOfFileTask(file);
		try {
			
			Long size = pool.invoke(task);
			System.out.print("Synchronous result: " + size);
		
		} finally {
			// Only stops submitting tasks. It does not try to cancel threads.
			pool.shutdown();	
			// What means "stops submitting tasks"? task.fork() does not throw exception after pool.shutdown()
			// no idea what is pool.shutdown() for... It does not do anything at all... :/
		
		    // pool.shutdown() does not seem to do anything at all. No interruption of threads
			// (interrupted status does not change to interrupted), task.fork() and task.get() do not throw exceptions...
			// NO IDEA WHAT THE HECK IS IT FOR.
			
			// BUT AFTER pool.shutdown() AND pool.shutdownNow() INVOCATIONS TO pool.execute(task), pool.submit(task) and
			// pool.invoke(task) THROW RejectedExecutionException.
			// SO pool.shutdown() REALLY DOES SOMETHING :/
			// ANYWAY, ALWAYS CALL pool.shutdown() in finally block!!!
			
			
			
		
			// pool.shutdownNow() Performs cancellation of threads (interrupted status changes to interrupted) and
			// task.get() and task.join() throw CancellationException
			// pool.shutdownNow();
		}
	}
	
	/**
	 * asynchronous execution
	 * 
	 */
	private void asynchronous(File file) {
		SizeOfFileTask task = new SizeOfFileTask(file);
		try {
			
			// pool.execute() and pool.submit() do the same, but submit() returns the same task as a ForkJoinTask :/
			pool.execute(task);
			// If you want to do it synchronous (the same as pool.invoke())
			// task.join();
		
			// pool.submit(task) does not do anything special. Just returns task as a ForkJoinTask...
			// ForkJoinTask<Long> theSameTask = pool.submit(task);
			// If you want to do it synchronous (the same as invoke())
			// task.join() or theSameTask.join();
			
		} finally {
			// Only stops submitting tasks. It does not try to cancel threads.
			pool.shutdown();	
			// What means "stops submitting tasks"? task.fork() does not throw exception after pool.shutdown()
			// no idea what is pool.shutdown() for... It does not do anything at all... :/
			
		    // pool.shutdown() does not seem to do anything at all. No interruption of threads
			// (interrupted status does not change to interrupted), task.fork() and task.get() do not throw exceptions...
			// NO IDEA WHAT THE HECK IS IT FOR.
			
			// BUT AFTER pool.shutdown() AND pool.shutdownNow() INVOCATIONS TO pool.execute(task), pool.submit(task) and
			// pool.invoke(task) THROW RejectedExecutionException.
			// SO pool.shutdown() REALLY DOES SOMETHING :/
			// ANYWAY, ALWAYS CALL pool.shutdown() in finally block!!!
			
			
			
			
			// pool.shutdownNow() Performs cancellation of threads (interrupted status changes to interrupted) and
			// task.get() and task.join() throw CancellationException
			// pool.shutdownNow();
		}
	}
	
	private static class SizeOfFileTask extends RecursiveTask<Long> {
		private final File file;

		public SizeOfFileTask(final File file) {
			this.file = Objects.requireNonNull(file);
		}

		@Override
		protected Long compute() {
			System.out.println("Computing size of: " + file.getAbsolutePath());
			System.out.println("Thread name: " + Thread.currentThread().getName());

			if (file.isFile()) {
				return file.length();
			}

			final List<SizeOfFileTask> tasks = new ArrayList<>();
			final File[] children = file.listFiles();
			if (children != null) {
				for (final File child : children) {
					final SizeOfFileTask task = new SizeOfFileTask(child);
					
					// Current task puts new task on the queue of the thread
					// which is running the current task.
					// Every thread has one queue.
					// No exception from here neither when using pool.shutdown() nor pool.shutdownNow() :(
					task.fork();
					
					tasks.add(task);
				}
			}

			long size = 0;
			for (final SizeOfFileTask task : tasks) {
				
				// Current task makes space for the forked tasks above,
				// one task is run by the current thread the other ones (if there are)
				// will be stolen by other threads (if they are idle)
				
				// throws CancellationException after calling pool.shutdownNow()
				size += task.join();
				
				//size += get(task);
			}

			return size;
		}
		
		private Long get(SizeOfFileTask task) {
			long size = 0;
            try {
            	
				// Current task makes space for the forked tasks above,
				// one task is run by the current thread the other ones (if there are)
				// will be stolen by other threads (if they are idle)
            	size = task.get();
            	System.out.println(Thread.currentThread().getName() + " is interrupted?: " + Thread.currentThread().isInterrupted());
            	
            } catch (final CancellationException e) {
            	// When using pool.shutdownNow() in asynchronous mode, threads will be interrupted and there will be CancellationException
                e.printStackTrace();
            } catch (final InterruptedException e) {
    			Thread.currentThread().interrupt();
                e.printStackTrace();
            } catch (final ExecutionException e) {
                System.out.println("Exception from task " + task.file + Thread.currentThread().getName());
                e.printStackTrace();
                final Throwable cause = e.getCause();
                
                throw this.launderThrowable(cause);
            } finally {
            	task.cancel(true);
            }
            
            return size;
		}
		
		// THERE IS NO NEED OF CATCHING AND LAUNDER EXCEPTIONS BECAUSE BY DEFAULT UNCAUGHT EXCEPTIONS ARE LOST FOREVER!!!!
		// I AM IMPLEMENTING THIS METHOD HERE BUT IN REAL LIFE I DO NOT THINK THIS STUFF IS REQUIRED :/
		// The same about catching exceptions in get method (above) in real life I do not think I should catch any exception
		// (perhaps for logging them but nothing else)
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

}
