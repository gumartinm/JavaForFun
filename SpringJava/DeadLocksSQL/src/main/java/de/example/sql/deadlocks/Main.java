package de.example.sql.deadlocks;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.example.sql.deadlocks.example.FirstTransaction;
import de.example.sql.deadlocks.example.SecondTransaction;
import de.example.sql.deadlocks.gate.ThreadGate;


public class Main {
	private static final Logger logger = LoggerFactory.getLogger(Main.class);

	public static void main(String[] args) {
		final ThreadGate trx1Gate = new ThreadGate();
		final ThreadGate trx2Gate = new ThreadGate();

		logger.info("Starting application");

		final FutureTask<Void> taskFirst = new FutureTask<Void>
		(
			new Runnable(){

				@Override
				public void run() {
					final FirstTransaction first = (FirstTransaction) SpringContextLocator.getInstance().getBean("firstTransaction");
					first.setThreadGateTrx1(trx1Gate);
					first.setThreadGateTrx2(trx2Gate);
					first.doFirstStep();
				}
			},
			null
		);
		final FutureTask<Void> taskSecond = new FutureTask<Void>
		(
			new Runnable(){

				@Override
				public void run() {
					final SecondTransaction second = (SecondTransaction) SpringContextLocator.getInstance().getBean("secondTransaction");
					second.setThreadGateTrx1(trx1Gate);
					second.setThreadGateTrx2(trx2Gate);
					second.doSecondStep();
				}
			},
			null
		);

		new Thread(taskFirst).start();
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
