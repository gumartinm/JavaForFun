package de.sql.tests.springtransaction;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


public class NestedTransactionExample {
    private static final Logger logger = LoggerFactory.getLogger(NestedTransactionExample.class);
    private DataSource dataSource;

    // Propagation.NESTED must be creating a SAVEPOINT to roll back when Exception
    // (at least in InnoDB)
    @Transactional(propagation=Propagation.NESTED)
    public void doExample()
    {
        logger.info("BEGIN: NestedTransactionExample");

        final JdbcTemplate jdbcTemplate = new JdbcTemplate(this.dataSource);
        jdbcTemplate.execute("INSERT INTO AD VALUES (16)");

        logger.info("END: NestedTransactionExample");
        throw new RuntimeException("GOING TO REOLLBACK NESTED TRANSACTION");
    }


    public void setDataSource (final DataSource dataSource) {
        this.dataSource = dataSource;
    }
}
