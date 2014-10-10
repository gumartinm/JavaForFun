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
public class SecondTransaction {
	private static final Logger logger = LoggerFactory.getLogger(SecondTransaction.class);
	private DataSource dataSource;
	private ThreadGate trx2Gate;
	private ThreadGate trx1Gate;
	private boolean isFirstTry = true;

	@DeadlockRetry(maxTries = 10, interval = 5000)
    public void doSecondStep() {
		if (isFirstTry) {
			isFirstTry = false;
			this.doSecondStepWithGate();
		} else {
			// Retry after roll back.
			this.doSecondStepWithoutGate();
		}
    }

    private void doSecondStepWithGate() {
		logger.info("Start doSecondStepWithGate");

		try {
            this.trx2Gate.await();
        } catch (InterruptedException e) {
            logger.warn("interrupt error", e);

        	Thread.currentThread().interrupt();
        }
		this.trx2Gate.close();

		logger.info("doSecondStepWithGate UPDATING");
		final JdbcOperations jdbcTemplate = new JdbcTemplate(dataSource);
	    jdbcTemplate.execute("UPDATE parents SET name='Frodo' WHERE id='2'");
	    jdbcTemplate.execute("SELECT * FROM children WHERE id='2' LOCK IN SHARE MODE");

	    // trx1 continue
	    trx1Gate.open();

	    this.doFourthStep();

        logger.info("End doSecondStepWithGate");
    }

    private void doSecondStepWithoutGate() {
		logger.info("Start doSecondStepWithoutGate");

		logger.info("doSecondStepWithoutGate UPDATING");
		final JdbcOperations jdbcTemplate = new JdbcTemplate(dataSource);
	    jdbcTemplate.execute("UPDATE parents SET name='Frodo' WHERE id='2'");
	    jdbcTemplate.execute("SELECT * FROM children WHERE id='2' LOCK IN SHARE MODE");

	    this.doFourthStep();

        logger.info("End doSecondStepWithoutGate");
    }

    private void doFourthStep() {
		logger.info("Start doFourthStep");
		
		logger.info("doFourthStep UPDATING");
		final JdbcOperations jdbcTemplate = new JdbcTemplate(dataSource);
		jdbcTemplate.execute("UPDATE children SET name='Sauron', parent_id='2' WHERE id='1'");
        
        logger.info("End doFourthStep");
    }

	public void setDataSource(final DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void setThreadGateTrx1(final ThreadGate trx1Gate) {
		this.trx1Gate = trx1Gate;
	}

	public void setThreadGateTrx2(final ThreadGate trx2Gate) {
		this.trx2Gate = trx2Gate;
	}
}
