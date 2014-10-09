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
		final FutureTask<Void>[] tasks = new FutureTask[2];

		logger.info("Starting application");

		tasks[0] = new FutureTask<Void>
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
		tasks[1] = new FutureTask<Void>
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

		for (final FutureTask<Void> task : tasks) {
			new Thread(task).start();
		}

		// Wait for end.
		for (final FutureTask<Void> task : tasks) {
			try {
				task.get();
			} catch (final InterruptedException e) {
				logger.error("Error", e);
			} catch (final ExecutionException e) {
				logger.error("Error", e);
			} finally {
				task.cancel(true);
			}
		}

		SpringContextLocator.getInstance().close();
		logger.info("End application");
	}
}
