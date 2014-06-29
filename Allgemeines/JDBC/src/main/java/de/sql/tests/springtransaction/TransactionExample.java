package de.sql.tests.springtransaction;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;


public class TransactionExample {
    private static final Logger logger = LoggerFactory.getLogger(TransactionExample.class);
    private DataSource dataSource;
    private NestedTransactionExample nestedTransactionExample;

    @Transactional
    public void doExample()
    {
        final JdbcTemplate jdbcTemplate = new JdbcTemplate(this.dataSource);
        jdbcTemplate.execute("INSERT INTO AD VALUES (16)");

        try {
            this.nestedTransactionExample.doExample();
        } catch(final RuntimeException e) {
            logger.error("Nested transaction performed roll back: ", e);
        }
    }

    public void setDataSource (final DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void setNestedTransactionExample (
            final NestedTransactionExample nestedTransactionExample) {
        this.nestedTransactionExample = nestedTransactionExample;
    }
}
