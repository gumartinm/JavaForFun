package de.javapos.example.queue;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import jpos.events.JposEvent;
import jpos.services.EventCallbacks;

public class JposEventQueueImpl implements JposEventQueue {
	private final EventCallbacks callbacks;
	//§JLS Item 16: Favor composition over inheritance
	//Java Concurrency in Practice 4.4.2
	//Not sure if this may be called "Composition" LOL
	private final BlockingQueue<JposEvent> linkedBlockingQueue = new LinkedBlockingQueue<JposEvent>();
	
	public JposEventQueueImpl (EventCallbacks callbacks) {
		this.callbacks = callbacks;
	}
	
	@Override
	public void inputAvailable(JposEvent input) {
		try {
			this.linkedBlockingQueue.put(input);
		} catch (InterruptedException e) {
			//Java Concurrency in Practice 5.4: Restore the interrupt.
			//restore interrupted status. Because we do not know where this code is going to be used.
			//TODO: After reading Java Concurrency in Practice chapter 7 you must decide
			//if you should throw the interrupt exception or just restore the interrupt
			//as I am doing right now. In the meanwhile just restoring the interrupt.
			//EN MI OPIONION SERIA MEJOR NO COGER LA EXCEPCION AQUI Y PONER throws EN EL INTERFAZ
			//JposEventQueue
			Thread.currentThread().interrupt();
		}
		
	}

	@Override
	public void errorOccurred(JposEvent error) {
		try {
			this.linkedBlockingQueue.put(error);
		} catch (InterruptedException e) {
			//Java Concurrency in Practice 5.4: Restore the interrupt.
			//restore interrupted status. Because we do not know where this code is going to be used.
			//TODO: After reading Java Concurrency in Practice chapter 7 you must decide
			//if you should throw the interrupt exception or just restore the interrupt
			//as I am doing right now. In the meanwhile just restoring the interrupt.
			Thread.currentThread().interrupt();
		}
	}

	@Override
	public void statusUpdateOccurred(JposEvent status) {
		try {
			this.linkedBlockingQueue.put(status);
		} catch (InterruptedException e) {
			//Java Concurrency in Practice 5.4: Restore the interrupt.
			//restore interrupted status. Because we do not know where this code is going to be used.
			//TODO: After reading Java Concurrency in Practice chapter 7 you must decide
			//if you should throw the interrupt exception or just restore the interrupt
			//as I am doing right now. In the meanwhile just restoring the interrupt.
			Thread.currentThread().interrupt();
		}	
	}
}
