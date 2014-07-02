package de.sql.tests;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mchange.v2.c3p0.ComboPooledDataSource;

public class RawJDBCLambdaExample {
    private static final Logger logger = LoggerFactory.getLogger(RawJDBCLambdaExample.class);

    /**
     * Carry out query – method returns a ResultSet object
     *
     */
    private interface ExecuteStatement<TResult> {
        TResult executeStatement(final Statement statement) throws SQLException;
    }

    private interface ExecuteResultSet<TResult> {
        void executeResultSet(final TResult resultSet) throws SQLException;
    }

    public static void main(final String[] args) throws PropertyVetoException, SQLException {

        // Just for fun, programmatic configuration.
        final DataSource dataSource = RawJDBCLambdaExample.getDataSource();
        try {
            // The database connection (taken from c3p0 pool)
            final Connection connection = dataSource.getConnection();

            RawJDBCLambdaExample.executeQuery(
                    connection,
                    statement -> statement.executeQuery("SELECT * FROM AD"),
                    answer ->
                    {
                        // Loop through ResultSet a row at a time
                        while (answer.next()) {
                            final int adID = answer.getInt("AD_ID");
                            final int adCode = answer.getInt("AD_CODE");
                            final String description = answer.getString("DESCRIPTION");
                            logger.info("AD_ID: " + adID + " AD_CODE: "
                                    + adCode + " DESCRIPTION: " + description);
                        }
                    }
                    );
        } finally {
            // Now we close the whole pool :)
            ((ComboPooledDataSource)dataSource).close();
        }
    }

    private static void executeQuery(final Connection connection,
            final ExecuteStatement<ResultSet> executeStatement,
            final ExecuteResultSet<ResultSet> executeResultSet) throws SQLException {

        try (final Statement statement = connection.createStatement();
                final ResultSet answer = executeStatement.executeStatement(statement)) {
            executeResultSet.executeResultSet(answer);
        }

        // Explicitly close the cursor and connection. NOTE: IT IS NOT THE SAME AS
        // "DECLARE CURSOR" OF SQL. This is a cursor in program memory not in DBMS!!!
        // answer.close(); // Cursor
    }

    /**
     * Just for fun, programmatic configuration.
     * @return
     * @throws PropertyVetoException
     */
    private static DataSource getDataSource() throws PropertyVetoException {
        final ComboPooledDataSource pool = new ComboPooledDataSource();

        pool.setUser("root");
        pool.setPassword("");
        // We are going to use JDBC driver
        pool.setDriverClass("com.mysql.jdbc.Driver");
        pool.setJdbcUrl("jdbc:mysql://127.0.0.1:3306/n2a?autoReconnect=true&amp;useUnicode=true&amp;characterEncoding=UTF-8");
        pool.setInitialPoolSize(5);
        pool.setMaxPoolSize(35);
        pool.setMinPoolSize(10);
        pool.setAcquireIncrement(1);
        pool.setAcquireRetryAttempts(5);
        pool.setAcquireRetryDelay(1000);
        pool.setAutomaticTestTable("con_test");
        pool.setCheckoutTimeout(5000);

        return pool;
    }
}
