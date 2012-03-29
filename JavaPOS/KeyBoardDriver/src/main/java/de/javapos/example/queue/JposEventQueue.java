package de.javapos.example.queue;

import jpos.events.JposEvent;

public interface JposEventQueue {
	
	public void inputAvailable(JposEvent input);
	
	public void errorOccurred(JposEvent error);
	
	public void statusUpdateOccurred(JposEvent status);
}