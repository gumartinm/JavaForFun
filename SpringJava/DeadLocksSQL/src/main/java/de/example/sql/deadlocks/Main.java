package de.example.sql.deadlocks;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.example.sql.deadlocks.example.FirstTransaction;
import de.example.sql.deadlocks.example.SecondTransaction;


public class Main {
	private static final Logger logger = LoggerFactory.getLogger(Main.class);
	

	public static void main(String[] args) {
		logger.info("Starting application");

		final FutureTask<Void> taskFirst = new FutureTask<Void>
		(
			new Runnable(){

				@Override
				public void run() {
					final FirstTransaction first = (FirstTransaction) SpringContextLocator.getInstance().getBean("firstTransaction");
					first.doTransaction();
				}
			},
			null
		);
		new Thread(taskFirst).start();

		final FutureTask<Void> taskSecond = new FutureTask<Void>
		(
			new Runnable(){

				@Override
				public void run() {
					final SecondTransaction second = (SecondTransaction) SpringContextLocator.getInstance().getBean("secondTransaction");
					second.doTransaction();
				}
			},
			null
		);
		new Thread(taskSecond).start();

		// Wait for end.
		try {
			taskFirst.get();
			taskSecond.get();
		} catch (final InterruptedException e) {
			logger.error("Error", e);
		} catch (final ExecutionException e) {
			logger.error("Error", e);
		}


		logger.info("End application");
	}
}
