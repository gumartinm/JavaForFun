package de.javapos.example.queue;

import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import jpos.events.DataEvent;
import jpos.events.ErrorEvent;
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
		//WIP sorry... :/
		Iterator<JposEvent> iterator = this.linkedBlockingQueue.iterator();
		while (iterator.hasNext()) {
			if (!(iterator.next() instanceof DataEvent) || !(iterator.next() instanceof ErrorEvent)) {

			}
		}
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
		 return this.linkedBlockingQueue.peek();
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
