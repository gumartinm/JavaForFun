package de.javapos.example;

import jpos.events.DataEvent;
import jpos.events.DirectIOEvent;
import jpos.events.ErrorEvent;
import jpos.events.JposEvent;
import jpos.events.OutputCompleteEvent;
import jpos.events.StatusUpdateEvent;
import jpos.services.EventCallbacks;
import de.javapos.example.queue.JposEventQueue;

public class ThreadFireEvent extends Thread {
	private final JposEventQueue jposEventQueue;
	private boolean isDataEventEnabled;
	private final EventCallbacks eventCallbacks;
	
	public ThreadFireEvent (JposEventQueue jposEventQueue, 
				boolean isDataEventEnabled, EventCallbacks  eventCallbacks) {
		this.jposEventQueue = jposEventQueue;
		this.isDataEventEnabled = isDataEventEnabled;
		this.eventCallbacks = eventCallbacks;
	}
	
	public void run () {
		JposEvent jposEvent = null;
		
		while (true) {
			if (this.jposEventQueue.getNumberOfEvents() != 0) {
				//PROBLEMA: si DataEventEnabled es false y lo unico que hay en la cola son eventos de datos
				//me meto en un bucle que va a dejar la CPU frita... :/
				if (this.isDataEventEnabled) {
					try {
						jposEvent = this.jposEventQueue.getEvent();
					} catch (InterruptedException e) {
						//restore interrupt status.
						Thread.currentThread().interrupt();
						//End of thread.
						break;
					}
				}
				else {
					//TODO: Buscar eventos que no sean DataEvent o ErrorEvent
				}
			}
			
			//TODO: Bloquearme hasta que freezeEvent sea false
			
			if (jposEvent instanceof DataEvent) {
				this.eventCallbacks.fireDataEvent((DataEvent)jposEvent);
				//TODO: synchronized?
				this.isDataEventEnabled = false;
			}
			if (jposEvent instanceof ErrorEvent) {
				this.eventCallbacks.fireErrorEvent((ErrorEvent)jposEvent);
			}
			if (jposEvent instanceof StatusUpdateEvent) {
				this.eventCallbacks.fireStatusUpdateEvent((StatusUpdateEvent)jposEvent);
			}
			if (jposEvent instanceof DirectIOEvent) {
				this.eventCallbacks.fireDirectIOEvent((DirectIOEvent)jposEvent);
			}
			if (jposEvent instanceof OutputCompleteEvent) {
				this.eventCallbacks.fireOutputCompleteEvent((OutputCompleteEvent)jposEvent);
			}
		}
	}
}
