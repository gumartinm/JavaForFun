package de.example.sql.deadlocks.example;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import de.example.sql.deadlocks.annotation.DeadlockRetry;
import de.example.sql.deadlocks.gate.ThreadGate;

@Transactional
public class FirstTransaction {
	private static final Logger logger = LoggerFactory.getLogger(FirstTransaction.class);
	private DataSource dataSource;
	private ThreadGate trx2Gate;
	private ThreadGate trx1Gate;
	private boolean isFirstTry = true;

	@DeadlockRetry(maxTries = 10, interval = 5000)
    public void doFirstStep() {
		if (isFirstTry) {
			isFirstTry = false;
			this.doFirstStepWithGate();
		} else {
			// Retry after roll back.
			this.doFirstStepWithoutGate();
		}
    }

    private void doFirstStepWithGate() {
		logger.info("Start doFirstStepWithGate");

		logger.info("doFirstStepWithGate UPDATING");
		final JdbcOperations jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.execute("UPDATE parents SET name='Smith' WHERE id='1'");
        jdbcTemplate.execute("SELECT * FROM children WHERE id='1' LOCK IN SHARE MODE");

        trx2Gate.open();

		try {
            this.trx1Gate.await();
        } catch (final InterruptedException e) {
            logger.warn("interrupt error", e);

        	Thread.currentThread().interrupt();
        }
		this.trx1Gate.close();

		trx2Gate.open();

        this.doThirdStep();

        logger.info("End doFirstStepWithGate");
    }

    private void doFirstStepWithoutGate() {
		logger.info("Start doFirstStepWithoutGate");

		logger.info("doFirstStepWithoutGate UPDATING");
		final JdbcOperations jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.execute("UPDATE parents SET name='Smith' WHERE id='1'");
        jdbcTemplate.execute("SELECT * FROM children WHERE id='1' LOCK IN SHARE MODE");

        this.doThirdStep();

        logger.info("End doFirstStepWithoutGate");
    }


    private void doThirdStep() {
		logger.info("Start doThirdStep");

		logger.info("doThirdStep UPDATING");
		final JdbcOperations jdbcTemplate = new JdbcTemplate(dataSource);
		jdbcTemplate.execute("UPDATE children SET name='Bob', parent_id='1' WHERE id='2'");

        logger.info("End doThirdStep");
    }

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void setThreadGateTrx1(final ThreadGate trx1Gate) {
		this.trx1Gate = trx1Gate;
	}

	public void setThreadGateTrx2(final ThreadGate trx2Gate) {
		this.trx2Gate = trx2Gate;
	}
}
