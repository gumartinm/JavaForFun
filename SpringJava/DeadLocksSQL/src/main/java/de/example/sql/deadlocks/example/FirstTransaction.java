package de.example.sql.deadlocks.example;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import de.example.sql.deadlocks.annotation.DeadlockRetry;

@Transactional
public class FirstTransaction {
	private static final Logger logger = LoggerFactory.getLogger(FirstTransaction.class);
	private DataSource dataSource;

	@DeadlockRetry(maxTries = 10, interval = 5000)
    public void doTransaction() {
		logger.info("Running doTransaction");

		final JdbcOperations jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.execute("UPDATE children SET name='Bilbo', parent_id='2' WHERE id='1'");
        jdbcTemplate.execute("UPDATE parents SET name='Smith' WHERE id='1'");

        try {
			Thread.sleep(100000);
		} catch (final InterruptedException e) {
			logger.warn("First transaction thread interrupt");

			// Restore interrupt status.
			Thread.currentThread().interrupt();
		}

        logger.info("Running endTransaction");
    }

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
}
