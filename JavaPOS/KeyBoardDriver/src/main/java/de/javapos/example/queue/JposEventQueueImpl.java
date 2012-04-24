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
	public JposEvent getEvent() throws InterruptedException {
		return this.linkedBlockingQueue.take();
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
	public void removeAllEvents() {
		this.linkedBlockingQueue.clear();
	}

	@Override
	public boolean removeEvent(JposEvent paramJposEvent) {
		return this.linkedBlockingQueue.remove(paramJposEvent);
	}

	@Override
	public JposEvent peekElement(int paramInt) {
		 return null;
	}

	@Override
	public boolean isFull() {
		//No seguro de esto :/
		return false;
	}

	@Override
	public int getSize() {
		return this.linkedBlockingQueue.size();
	}
}
