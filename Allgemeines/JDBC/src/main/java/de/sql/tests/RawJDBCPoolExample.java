package de.sql.tests;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mchange.v2.c3p0.ComboPooledDataSource;

public class RawJDBCPoolExample {
    private static final Logger logger = LoggerFactory.getLogger(RawJDBCPoolExample.class);

    public static void main(final String[] args) throws PropertyVetoException {
        // Just for fun, programmatic configuration.
        final DataSource dataSource = getDataSource();

        // 1. Using Statement
        Connection connection = null;
        Statement statement = null;
        ResultSet answer = null;
        try {
            // The database connection (taken from c3p0 pool)
            connection = dataSource.getConnection();
            // Create a statement object for executing the query
            statement = connection.createStatement();
            // Carry out query – method returns a ResultSet object
            answer = statement.executeQuery("SELECT * FROM AD");
            // Loop through ResultSet a row at a time
            while (answer.next()) {
                final int adID = answer.getInt("AD_ID");
                final int adCode = answer.getInt("AD_CODE");
                final String description = answer.getString("DESCRIPTION");
                logger.info("AD_ID: " + adID + " AD_CODE: " + adCode + " DESCRIPTION: " + description);
            }

        } catch (final SQLException e) {
            logger.error("Using Statement: ", e);
        } finally {
            if (answer != null) {
                // Explicitly close the cursor and connection. NOTE: IT IS NOT THE SAME AS "DECLARE CURSOR" OF SQL
                // This is a cursor in program memory not in DBMS!!!
                try {
                    answer.close(); // Cursor
                } catch (final SQLException e) {
                    logger.error("Error while closing cursor: ", e);
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (final SQLException e) {
                    logger.error("Error while closing statement: ", e);
                }
            }
            if (connection != null) {
                // try {
                //     connection.close(); final I do not think, I should do it final when having a connection pool :/
                // } catch (final SQLException e) {
                //     logger.error("Error while closing connection: ", e);
                // }
            }
        }


        // 2. Using PreparedStatement
        PreparedStatement preparedStatement = null;
        answer = null;
        try {
            // The database connection (taken from c3p0 pool)
            connection = dataSource.getConnection();
            // Create a statement object for executing the query
            preparedStatement = connection.prepareStatement("SELECT * FROM AD");
            // Carry out query – method returns a ResultSet object
            answer = preparedStatement.executeQuery();
            // Loop through ResultSet a row at a time
            while (answer.next()) {
                final int adID = answer.getInt("AD_ID");
                final int adCode = answer.getInt("AD_CODE");
                final String description = answer.getString("DESCRIPTION");
                logger.info("AD_ID: " + adID + " AD_CODE: " + adCode + " DESCRIPTION: " + description);
            }
        } catch (final SQLException e) {
            logger.error("Using PreparedStatement: ", e);
        } finally {
            if (answer != null) {
                // Explicitly close the cursor and connection. NOTE: IT IS NOT THE SAME AS "DECLARE CURSOR" OF SQL
                // This is a cursor in program memory not in DBMS!!!
                try {
                    answer.close(); // Cursor
                } catch (final SQLException e) {
                    logger.error("Error while closing cursor: ", e);
                }
            }
            if (preparedStatement != null) {
                // I think closing ResultSet should be enough...
                try {
                    preparedStatement.close();
                } catch (final SQLException e) {
                    logger.error("Error while closing prepared statement: ", e);
                }
            }
            if (connection != null) {
                // try {
                //     connection.close(); final I do not think, I should do it final when having a connection pool :/
                // } catch (final SQLException e) {
                //     logger.error("Error while closing connection: ", e);
                // }
            }
        }

        // Now we close the whole pool :)
        ((ComboPooledDataSource)dataSource).close();
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
