package de.sql.tests;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RawJDBCExample {
    private static final Logger logger = LoggerFactory.getLogger(RawJDBCExample.class);
    private static final String DB_URL =
            "jdbc:mysql://127.0.0.1:3306/n2a?autoReconnect=true&amp;useUnicode=true&amp;characterEncoding=UTF-8";

    public static void main(final String[] args) {
        Connection connection = null;
        Statement statement = null;
        ResultSet answer = null;
        PreparedStatement preparedStatement = null;
        try {
            // 1. Register JDBC driver
            //
            // Register JDBC driver (by itself) with the DriverManager!!!
            // (see static initializers in com.mysql.jdbc.Driver)
            //
            // otherwise, you could do it like this:
            // DriverManager.registerDriver(new Driver()); (it should be the same)
            //
            Class.forName("com.mysql.jdbc.Driver");

            // 2. Open a connection
            logger.info("Connecting to database");
            connection = DriverManager.getConnection(RawJDBCExample.DB_URL, "root", "");

            // 3. Execute some query: statement
            logger.info("Execute statement");
            statement = connection.createStatement();
            answer = statement.executeQuery("SELECT * FROM AD");
            // Loop through ResultSet a row at a time
            while (answer.next()) {
                final int adID = answer.getInt("AD_ID");
                final int adCode = answer.getInt("AD_CODE");
                final String description = answer.getString("DESCRIPTION");
                logger.info("AD_ID: " + adID + " AD_CODE: " + adCode + " DESCRIPTION: " + description);
            }
            logger.info("Statement executed successfully");

            // 4. Execute some query: prepared statement
            logger.info("Execute prepared statement");
            preparedStatement = connection.prepareStatement("SELECT * FROM AD");
            preparedStatement.executeQuery();
            // Loop through ResultSet a row at a time
            while (answer.next()) {
                final int adID = answer.getInt("AD_ID");
                final int adCode = answer.getInt("AD_CODE");
                final String description = answer.getString("DESCRIPTION");
                logger.info("AD_ID: " + adID + " AD_CODE: " + adCode + " DESCRIPTION: " + description);
            }
            logger.info("Prepared statement executed successfully");
        } catch (final ClassNotFoundException | SQLException e) {
            logger.error("Program error: ", e);
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
                // I think closing ResultSet should be enough...
                try {
                    statement.close();
                } catch (final SQLException e) {
                    logger.error("Error while closing statement: ", e);
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
                try {
                    connection.close();
                } catch (final SQLException e) {
                    logger.error("Error while closing connection: ", e);
                }
            }
        }
    }
}
