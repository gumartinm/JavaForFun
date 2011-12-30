/**
 * 
 */
package de.test.thread.threadoldway;

import java.lang.Thread.UncaughtExceptionHandler;

/**
 * @author gusarapo
 *
 */
public class MyUncaughtExceptionHandler implements UncaughtExceptionHandler {

	@Override
	public void uncaughtException(Thread t, Throwable e) {
		//System.out.println("MyUncaughtExceptionHandler " + t.getName() + " " + e);
		e.printStackTrace();
	}
}
