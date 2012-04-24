package de.javapos.example.queue;

import jpos.events.JposEvent;

//Similar a WNBaseService  ¿mejor una clase o implementarlo en cada servicio :/?
//¿O mejor un servicio que extienda una clase que implementa este interfaz XD?
public interface JposEventQueue {
	
	public void putEvent(JposEvent paramJposEvent) throws InterruptedException;

	public JposEvent getEvent() throws InterruptedException;

	public void clearInputEvents();

	public void clearOutputEvents();

	public int getNumberOfEvents();

	public void checkEvents();

	public void removeAllEvents();

	public boolean removeEvent(JposEvent paramJposEvent);

	public JposEvent peekElement(int paramInt);

	public boolean isFull();

	public int getSize();
}
