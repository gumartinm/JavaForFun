package de.javapos.example.queue;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import jpos.events.JposEvent;

public class JposEventQueueImpl implements JposEventQueue {
	//§JLS Item 16: Favor composition over inheritance
	//Java Concurrency in Practice 4.4.2
	//Not sure if this may be called "Composition" LOL
	private final BlockingQueue<JposEvent> linkedBlockingQueue = new LinkedBlockingQueue<JposEvent>();

	@Override
	public void putEvent(JposEvent paramJposEvent) throws InterruptedException {
		this.linkedBlockingQueue.put(paramJposEvent);
	}

	@Override
	public void clearAllEvents() {
		this.linkedBlockingQueue.clear();
	}

	@Override
	public void clearInputEvents() {
		// TODO Auto-generated method stub
	}

	@Override
	public void clearOutputEvents() {
		// TODO Auto-generated method stub
	}

	@Override
	public int getNumberOfEvents() {
		return this.linkedBlockingQueue.size();
	}

	@Override
	public void checkEvents() {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean eventQueueIsFull() {
		//No seguro de esto :/
		return false;
	}
}
