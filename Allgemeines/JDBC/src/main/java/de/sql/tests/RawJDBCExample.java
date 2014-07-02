package de.sql.tests;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

import javax.sql.RowSetEvent;
import javax.sql.RowSetListener;
import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.FilteredRowSet;
import javax.sql.rowset.JdbcRowSet;
import javax.sql.rowset.JoinRowSet;
import javax.sql.rowset.RowSetFactory;
import javax.sql.rowset.RowSetProvider;
import javax.sql.rowset.WebRowSet;

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
            
            
            // 6. Execute some query: JDBC rowset
            logger.info("Execute JDBC rowset");
            JDBCrowSet(connection);
            logger.info("JDBC rowset executed successfully");
            
        } finally {
        	// It does not implement AutoCloseable
        	connection.close();
        }
    }
    
    private static void executeStatement(final Connection connection) throws SQLException {
    	/**
    	 * I MAY USE THE SAME Statement MORE THAN ONCE!!! :)
    	 */
    	
    	try (final Statement statement = connection.createStatement())
    	{
    		try (final ResultSet answer = statement.executeQuery("SELECT * FROM AD"))
    		{
        		// Loop through ResultSet a row at a time
        		while (answer.next()) {
        			final int adID = answer.getInt("AD_ID");
        			final int adCode = answer.getInt("AD_CODE");
        			final String description = answer.getString("DESCRIPTION");
        			logger.info("AD_ID: " + adID + " AD_CODE: " + adCode + " DESCRIPTION: " + description);
        		}
        	}
        	// Explicitly close the cursor and connection. NOTE: IT IS NOT THE SAME AS "DECLARE CURSOR" OF SQL
            // This is a cursor in program memory not in DBMS!!!
    		
    		try (final ResultSet answer = statement.executeQuery("SELECT * FROM AD"))
    		{
        		// Loop through ResultSet a row at a time
        		while (answer.next()) {
        			final int adID = answer.getInt("AD_ID");
        			final int adCode = answer.getInt("AD_CODE");
        			final String description = answer.getString("DESCRIPTION");
        			logger.info("AD_ID: " + adID + " AD_CODE: " + adCode + " DESCRIPTION: " + description);
        		}
        	}
        	// Explicitly close the cursor and connection. NOTE: IT IS NOT THE SAME AS "DECLARE CURSOR" OF SQL
            // This is a cursor in program memory not in DBMS!!!
    	}
    }
    
    private static void preparedStatement(final Connection connection) throws SQLException {
    	/**
    	 * I MAY USE THE SAME PreparedStatement MORE THAN ONCE!!! :)
    	 */
    	
    	try (final PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM AD"))
    	{
    		try (final ResultSet answer = preparedStatement.executeQuery())
        	{
        		// Loop through ResultSet a row at a time
        		while (answer.next()) {
        			final int adID = answer.getInt("AD_ID");
        			final int adCode = answer.getInt("AD_CODE");
        			final String description = answer.getString("DESCRIPTION");
        			logger.info("AD_ID: " + adID + " AD_CODE: " + adCode + " DESCRIPTION: " + description);
        		}
        	}
        	// Explicitly close the cursor and connection. NOTE: IT IS NOT THE SAME AS "DECLARE CURSOR" OF SQL
            // This is a cursor in program memory not in DBMS!!! (It is being closed by the try-catch-with-resources)
        	
        	
        	try (final ResultSet answer = preparedStatement.executeQuery())
        	{
        		// Loop through ResultSet a row at a time
        		while (answer.next()) {
        			final int adID = answer.getInt("AD_ID");
        			final int adCode = answer.getInt("AD_CODE");
        			final String description = answer.getString("DESCRIPTION");
        			logger.info("AD_ID: " + adID + " AD_CODE: " + adCode + " DESCRIPTION: " + description);
        		}
        	}
        	// Explicitly close the cursor and connection. NOTE: IT IS NOT THE SAME AS "DECLARE CURSOR" OF SQL
            // This is a cursor in program memory not in DBMS!!! (It is being closed by the try-catch-with-resources)	
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
    	// Stored procedure:
		final String str1 = "{CALL CHANGE_REGION(?, ?, ?)}";
		// Stored function:
		final String str2 = "{? = CALL GET_REGION(?)}";
    	

    	try(// Prepare the two statements
    		final CallableStatement cstmt1 = connection.prepareCall(str1);
    		final CallableStatement cstmt2 = connection.prepareCall(str2))
    	{
    		// Go ahead and execute the call to the stored procedure
    		cstmt1.executeUpdate();
    		final String adCode = cstmt1.getString("AD_CODE");
    		logger.info("AD_CODE: " + adCode);
        
    		cstmt2.setInt("AD_ID", 666);
    		cstmt2.setString("AD_DESCRIPTION", "My ad");
    		// returns a varchar parameter
    		cstmt2.registerOutParameter("AD_DESCRIPTION", Types.VARCHAR);
    		// Go ahead and execute the call to the stored function
    		cstmt2.executeUpdate();
    	}
    }
    
    private static void JDBCrowSet(final Connection connection) throws SQLException {
    	
//    	Java 1.6 way. With Java 1.8 IDE complains because JdbcRowSetImpl is not API :/
//    	try (final JdbcRowSet jdbcRowSet = new JdbcRowSetImpl(connection))
//    	{
//    		jdbcRowSet.setType(ResultSet.TYPE_SCROLL_INSENSITIVE);
//        	String queryString = "SELECT * FROM AD";
//    		jdbcRowSet.setCommand(queryString);
//    		jdbcRowSet.execute();
//    		// This could be a nice feature, I guess.
//    		jdbcRowSet.addRowSetListener(new ExampleListener());
//
//    		while (jdbcRowSet.next()) {
//    			// Generating cursor Moved event
//    			logger.info("AD_ID: " + jdbcRowSet.getString(1));
//    			logger.info("AD_CODE: " + jdbcRowSet.getString(2));
//    		}
//    	}
    	
    	// Java > 1.6
    	final RowSetFactory rowSetFactory = RowSetProvider.newFactory();
        try (final JdbcRowSet jdbcRowSet = rowSetFactory.createJdbcRowSet())
    	{
        	// With Java > 1.6 there is no way of using some Connection if
        	// we do not use new JdbcRowSetImpl(Connection) :(
        	jdbcRowSet.setUrl(DB_URL);
        	jdbcRowSet.setUsername("root");
        	jdbcRowSet.setPassword("");
    		jdbcRowSet.setType(ResultSet.TYPE_SCROLL_INSENSITIVE);
    		jdbcRowSet.setCommand("SELECT * FROM AD");
    		jdbcRowSet.execute();
    		// This could be a nice feature (I guess) I can not find the same
    		// feature in the "traditional" ResultSet
    		jdbcRowSet.addRowSetListener(new ExampleListener());

    		while (jdbcRowSet.next()) {
    			// Generating cursor Moved event
    			logger.info("AD_ID: " + jdbcRowSet.getString(1));
    			logger.info("AD_CODE: " + jdbcRowSet.getString(2));
    		}
    	}
        
        
        try (final CachedRowSet cachedRowSet = rowSetFactory.createCachedRowSet();
        	 final FilteredRowSet filteredRowSet = rowSetFactory.createFilteredRowSet();
        	 final JoinRowSet joinRowSet = rowSetFactory.createJoinRowSet();
        	 final WebRowSet webRowSet = rowSetFactory.createWebRowSet())
    	{
        	// I do not know what they are all for :(
    	}
    }
    
    /**
     * For JdbcRowSet.addRowSetListener
     */
    private static class ExampleListener implements RowSetListener {

    	@Override
    	public void cursorMoved(final RowSetEvent event) {
    		logger.info("Cursor Moved Listener");
    		logger.info(event.toString());
    	}

    	@Override
    	public void rowChanged(final RowSetEvent event) {
    		logger.info("Cursor Changed Listener");
    		logger.info(event.toString());
    	}

    	@Override
    	public void rowSetChanged(final RowSetEvent event) {
    		logger.info("RowSet changed Listener");
    		logger.info(event.toString());
    	}
    }
}
