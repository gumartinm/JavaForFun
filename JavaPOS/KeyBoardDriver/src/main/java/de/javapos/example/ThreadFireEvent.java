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
    private boolean isDataEventEnabled = true;
    private final JposEventQueue jposEventQueue;
    private final EventCallbacks eventCallbacks;
    private final ThreadGate freezeEventsGate = new ThreadGate();

    public ThreadFireEvent(JposEventQueue jposEventQueue,
            boolean isDataEventEnabled, EventCallbacks eventCallbacks) {
        this.jposEventQueue = jposEventQueue;
        this.eventCallbacks = eventCallbacks;
    }

    @Override
    public void run() {

        while (!this.isInterrupted()) {

            JposEvent event;

            try {
                event = this.jposEventQueue.peekEvent();
            } catch (InterruptedException e) {
                // We are implementing the interruption policy in this class
                // for this thread. So, this is permitted.
                // End of thread.
                break;
            }

            try {
                this.freezeEventsGate.await();
            } catch (InterruptedException e) {
                // We are implementing the interruption policy in this class
                // for this thread. So, this is permitted.
                // End of thread.
                break;
            }

            // PROBLEMA: si DataEventEnabled es false y lo unico que hay en la
            // cola son eventos de datos
            // me meto en un bucle que va a dejar la CPU frita... :/
            if (!this.isDataEventEnabled) {
                if (((event instanceof DataEvent))
                        || (((event instanceof ErrorEvent)) && (((ErrorEvent) event)
                                .getErrorLocus() != 1))) {
                    // TODO: Buscar eventos que no sean DataEvent o ErrorEvent
                }
            }

            fireEvent(event);
        }
    }

    private void fireEvent(JposEvent jposEvent) {
        try {
            jposEvent = this.jposEventQueue.getEvent();
        } catch (InterruptedException e) {
            // restore interrupt status.
            this.interrupt();
            return;
        }

        if (jposEvent instanceof DataEvent) {
            this.eventCallbacks.fireDataEvent((DataEvent) jposEvent);
            // TODO: synchronized?
            this.isDataEventEnabled = false;
        }
        if (jposEvent instanceof ErrorEvent) {
            this.eventCallbacks.fireErrorEvent((ErrorEvent) jposEvent);
        }
        if (jposEvent instanceof StatusUpdateEvent) {
            this.eventCallbacks
                    .fireStatusUpdateEvent((StatusUpdateEvent) jposEvent);
        }
        if (jposEvent instanceof DirectIOEvent) {
            this.eventCallbacks.fireDirectIOEvent((DirectIOEvent) jposEvent);
        }
        if (jposEvent instanceof OutputCompleteEvent) {
            this.eventCallbacks
                    .fireOutputCompleteEvent((OutputCompleteEvent) jposEvent);
        }
    }

    public void setFreezeEvents(boolean freezeEvents) {
        if (freezeEvents) {
            this.freezeEventsGate.close();
        } else {
            this.freezeEventsGate.open();
        }
    }

    public void setDataEventEnabled(boolean dataEventEnabled) {
        this.isDataEventEnabled = dataEventEnabled;
    }
}
