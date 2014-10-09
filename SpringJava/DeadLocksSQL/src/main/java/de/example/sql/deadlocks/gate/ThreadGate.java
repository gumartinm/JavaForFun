package de.example.sql.deadlocks.gate;

import de.example.sql.deadlocks.annotation.GuardedBy;
import de.example.sql.deadlocks.annotation.ThreadSafe;



/**
 * See: §Java Concurrency in practice 14.2.6
 * @author
 *
 */
@ThreadSafe
public class ThreadGate {
    //CONDITION-PREDICATE: opened-since(n) (isOpen || generation>n)
    @GuardedBy("this") private boolean isOpen;
    @GuardedBy("this") private int generation;


    public synchronized void close() {
        isOpen = false;
    }

    public synchronized void open() {
        ++generation;
        isOpen = true;
        notifyAll();
    }

    //BLOCKS-UNTIL: opened-since(generation on entry)
    public synchronized void await() throws InterruptedException  {
        int arrivalGeneration = generation;
        while (!isOpen && arrivalGeneration == generation)
            wait();
    }
}

