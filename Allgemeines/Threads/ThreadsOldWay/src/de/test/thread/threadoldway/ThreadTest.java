package de.test.thread.threadoldway;


public class ThreadTest implements Runnable {
	Thread thread1 = new Thread(this, "thread1");
	Thread main;
	volatile boolean key;

	public void start () {
		this.main = Thread.currentThread();
		main.setName("main");
		
		//What is going to happen when for example RuntimeException is thrown from a thread?
		//Usually you will use the default method (the JVM shows the exception and the stack)
		//but, what about if you want to catch any kinds of unexpected exceptions from your thread
		//because for example you want to write on a log file those exceptions.
		//In that case you can use the uncaught exceptions handlers.
		
		//You can use any of those two methods (setDefault and setUncaught). See getUncaughtExceptionHandler in Thread class
		//and uncaughtException in ThreadGroup to see the differences. For me they are more than less
		//pretty the same.
		//UncaughtException->
		//           getUncaughtExceptionHandler 
		//                 1. If in that thread there is not an exception handler -> 
		//													uncaughtException search exception handler in parent->
		//																						run exception handler defined in parent
		//																			             
		//                 2. If in that thread there is an exception handler -> 
		//														     run exception handler
		// IF THERE IS NO ONE, A DEFAULT EXCEPTION HANDLER IS RUN BY THE JVM. Typically it will be enough for your requirements.
		//
		//In my example I am in the first case where the handler is in the parent code. It is in the parent code but 
		//the code is executed by the sibling. What means thread1 will run el manejador de excepcion que fue definido en el hilo main.
		Thread.setDefaultUncaughtExceptionHandler(new MyUncaughtExceptionHandler()); 
		//So, in my example the ExceptionHandler is in the parent thread. When thread1 throws RuntimeException
		//the thread1 will search an exception handler finding this one in the parent. See the Thread constructor,
		//it has a ThreadGroup with relations between parents and siblings. See uncaughtException in ThreadGroup
		//it is a recursive searching by a parent.
		
		//You could use this one where the exception handler is directly in thread1 and when calling getUncaughtExceptionHandler
		//in Thread class we obtain the handlers without having to search for the parents using uncaughtException method
		//from ThreadGroup class
		//thread1.setUncaughtExceptionHandler(new MyUncaughtExceptionHandler());

		
		thread1.start();
					
		//thread1 will have the interrupted status. If it is or when it reaches the wait method
		//or other ones (see Javadoc from interrupt method) it will see the interrupted status
		//and thread1 will catch the interrupted exception. The interrupted exception is just thrown when
		//the thread1 is in (for example) wait and the thread has the interrupted status.
		thread1.interrupt();
		//After this instruction thread1 will have the interrupted status. When it reaches the wait method
		//it will see the interrupted status (not before reaching that code it will see that status) and the
		//InterruptedException will be thrown.
		
		try {
			synchronized(this) {
				wait(5000);
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//After a putfield volatile field (Java bytecode) there is a StoreLoad instruction. See http://g.oswego.edu/dl/jmm/cookbook.html
		//In this moment (no real time) thread will stop on the wait method, and it will see the interrupted state and the InterruptedException
		//is thrown.
		key = true;
		
	}
	
	
	@Override
	public void run() {
		try {
			while(true)
			{
				if (key == true) {
					synchronized(this) {
						this.wait();
					}
				}
			}	
		} catch (InterruptedException e) {
			//This exception will be caught by the UncaughtExceptionHandler. The one defined in parent or in this thread.
			//If there is no uncaught exception handler, the JVM runs one by default.
			throw new RuntimeException(e);
		}
	}
}
