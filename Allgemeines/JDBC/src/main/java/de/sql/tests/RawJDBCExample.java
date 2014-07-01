package de.sql.tests;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RawJDBCExample {
    private static final Logger logger = LoggerFactory.getLogger(RawJDBCExample.class);
    private static final String DB_URL =
            "jdbc:mysql://127.0.0.1:3306/n2a?autoReconnect=true&amp;useUnicode=true&amp;characterEncoding=UTF-8";

    public static void main(final String[] args) throws ClassNotFoundException, SQLException {
    	
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
        final Connection connection = DriverManager.getConnection(RawJDBCExample.DB_URL, "root", "");
        try {
            
            // 3. Execute some query: statement
            logger.info("Execute statement");
            executeStatement(connection);
            logger.info("Statement executed successfully");

            
            // 4. Execute some query: prepared statement
            logger.info("Execute prepared statement");
            preparedStatement(connection);
            logger.info("Prepared statement executed successfully");
            
            
            // 5. Execute some query: callable statement
            // JDBC also supports the execution of stored
            // procedures and stored functions through a third type of statement object,
            // the CallableStatement object created by the prepareCall() method.
            logger.info("Execute callable statement");
            callableStatement(connection);
            logger.info("Callable statement executed successfully");          
            
        } finally {
        	connection.close();
        }
    }
    
    private static void executeStatement(final Connection connection) throws SQLException {
    	/**
    	 * I MAY USE THE SAME Statement MORE THAN ONCE!!! :)
    	 */
    	
    	Statement statement = null;
    	ResultSet answer = null;
    	try {
    		statement = connection.createStatement();
    		answer = statement.executeQuery("SELECT * FROM AD");
    		// Loop through ResultSet a row at a time
    		while (answer.next()) {
    			final int adID = answer.getInt("AD_ID");
    			final int adCode = answer.getInt("AD_CODE");
    			final String description = answer.getString("DESCRIPTION");
    			logger.info("AD_ID: " + adID + " AD_CODE: " + adCode + " DESCRIPTION: " + description);
    		}
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
    	}
    	
    	
    	try {
    		answer = statement.executeQuery("SELECT * FROM AD");
    		// Loop through ResultSet a row at a time
    		while (answer.next()) {
    			final int adID = answer.getInt("AD_ID");
    			final int adCode = answer.getInt("AD_CODE");
    			final String description = answer.getString("DESCRIPTION");
    			logger.info("AD_ID: " + adID + " AD_CODE: " + adCode + " DESCRIPTION: " + description);
    		}
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
    	}
    }
    
    private static void preparedStatement(final Connection connection) throws SQLException {
    	/**
    	 * I MAY USE THE SAME PreparedStatement MORE THAN ONCE!!! :)
    	 */
    	
    	PreparedStatement preparedStatement = null;
    	ResultSet answer = null;
    	try {
    		preparedStatement = connection.prepareStatement("SELECT * FROM AD");
    		answer = preparedStatement.executeQuery();
    		// Loop through ResultSet a row at a time
    		while (answer.next()) {
    			final int adID = answer.getInt("AD_ID");
    			final int adCode = answer.getInt("AD_CODE");
    			final String description = answer.getString("DESCRIPTION");
    			logger.info("AD_ID: " + adID + " AD_CODE: " + adCode + " DESCRIPTION: " + description);
    		}
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
    	}
    	
    	
    	try {
    		answer = preparedStatement.executeQuery();
    		// Loop through ResultSet a row at a time
    		while (answer.next()) {
    			final int adID = answer.getInt("AD_ID");
    			final int adCode = answer.getInt("AD_CODE");
    			final String description = answer.getString("DESCRIPTION");
    			logger.info("AD_ID: " + adID + " AD_CODE: " + adCode + " DESCRIPTION: " + description);
    		}
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
                try {
                	preparedStatement.close();
                } catch (final SQLException e) {
                    logger.error("Error while closing statement: ", e);
                }
            }
    	}
    }
    
    private static void callableStatement(final Connection connection) throws SQLException {
    	/**
         * CREATE PROCEDURE CHANGE_REGION
         *     (IN OFFICE INTEGER,
         *      OUT OLD_REG VARCHAR(10),
         *      IN NEW_REG VARCHAR(10))
         *
         * CREATE FUNCTION GET_REGION
         *    (IN OFFICE INTEGER)
         *        RETURNS VARCHAR(10)
         */
    	
    	CallableStatement cstmt1 = null;
    	CallableStatement cstmt2 = null;
    	try {
    		// Stored procedure:
    		final String str1 = "{CALL CHANGE_REGION(?, ?, ?)}";
    		// Stored function:
    		final String str2 = "{? = CALL GET_REGION(?)}";
        
    		// Prepare the two statements
    		cstmt1 = connection.prepareCall(str1);
    		cstmt2 = connection.prepareCall(str2);
        
    		// Go ahead and execute the call to the stored procedure
    		cstmt1.executeUpdate();
    		final String adCode = cstmt1.getString("AD_CODE");
        
    		cstmt2.setInt("AD_ID", 666);
    		cstmt2.setString("AD_DESCRIPTION", "My ad");
    		// returns a varchar parameter
    		cstmt2.registerOutParameter("AD_DESCRIPTION", Types.VARCHAR);
    		// Go ahead and execute the call to the stored function
    		cstmt2.executeUpdate();
    	} finally {
            try {
            	cstmt1.close();
            } catch (final SQLException e) {
                logger.error("Error while closing statement: ", e);
            }
            try {
            	cstmt2.close();
            } catch (final SQLException e) {
                logger.error("Error while closing statement: ", e);
            }
    	}
    }
}
