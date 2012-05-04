package de.android.mobiads.provider;

import java.util.HashMap;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.database.sqlite.SQLiteReadOnlyDatabaseException;
import android.net.Uri;
import android.text.TextUtils;


/**
 *
 */
public class IndexerProvider extends ContentProvider {
    // Creates a UriMatcher object.
    private static final UriMatcher sUriMatcher;
    
    /**
     * A projection map used to select columns from the database
     */
    private static HashMap<String, String> sIndexerProjectionMap;
    
    // Handle to a new DatabaseHelper.
    private IndexerOpenHelper mOpenHelper;
    
    // The incoming URI matches the Notes URI pattern
    private static final int INDEXER = 1;
    
    // The incoming URI matches the Note ID URI pattern
    private static final int INDEXER_ID = 2;
    
    private static final int INDEXER_IDAD = 3;
    
    static {
    	
    	/*
         * Creates and initializes the URI matcher
         */
        // Create a new instance
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        
        // Add a pattern that routes URIs terminated with "indexer" to a INDEXER operation
        sUriMatcher.addURI("de.android.mobiads.provider", Indexer.Index.TABLE_NAME, INDEXER);
        
        // Add a pattern that routes URIs terminated with "indexer" plus an integer
        // to a index ID operation
        sUriMatcher.addURI("de.android.mobiads.provider", Indexer.Index.TABLE_NAME + "/#", INDEXER_ID);
        
        sUriMatcher.addURI("de.android.mobiads.provider", Indexer.Index.TABLE_NAME + "/" + Indexer.Index.COLUMN_NAME_ID_AD + "/#", INDEXER_IDAD);
        
        
        /*
         * Creates and initializes a projection map that returns all columns
         */

        // Creates a new projection map instance. The map returns a column name
        // given a string. The two are usually equal.
        sIndexerProjectionMap = new HashMap<String, String>();

        // Maps the string "_ID" to the column name "_ID"
        sIndexerProjectionMap.put(Indexer.Index._ID, Indexer.Index._ID);

        // Maps "idad" to "idad"
        sIndexerProjectionMap.put(Indexer.Index.COLUMN_NAME_ID_AD, Indexer.Index.COLUMN_NAME_ID_AD);

        // Maps "path" to "path"
        sIndexerProjectionMap.put(Indexer.Index.COLUMN_NAME_PATH, Indexer.Index.COLUMN_NAME_PATH);
        
        sIndexerProjectionMap.put(Indexer.Index.COLUMN_NAME_TEXT, Indexer.Index.COLUMN_NAME_TEXT);
        
        sIndexerProjectionMap.put(Indexer.Index.COLUMN_NAME_URL, Indexer.Index.COLUMN_NAME_URL);
    }
 
    
    
    /**
	 *
	 * Initializes the provider by creating a new DatabaseHelper. onCreate() is called
	 * automatically when Android creates the provider in response to a resolver request from a
	 * client.
	 */
	@Override
	public boolean onCreate() {
      // Creates a new helper object. Note that the database itself isn't opened until
      // something tries to access it, and it's only created if it doesn't already exist.
      mOpenHelper = new IndexerOpenHelper(getContext());

      // Assumes that any failures will be reported by a thrown exception.
      return true;
	}
    
	
    /**
     * This is called when a client calls
     * {@link android.content.ContentResolver#delete(Uri, String, String[])}.
     * Deletes records from the database. If the incoming URI matches the note ID URI pattern,
     * this method deletes the one record specified by the ID in the URI. Otherwise, it deletes a
     * a set of records. The record or records must also match the input selection criteria
     * specified by where and whereArgs.
     *
     * If rows were deleted, then listeners are notified of the change.
     * @return If a "where" clause is used, the number of rows affected is returned, otherwise
     * 0 is returned. To delete all rows and get a row count, use "1" as the where clause.
     * @throws IllegalArgumentException if the incoming URI pattern is invalid.
     */
    @Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
    	// Opens the database object in "write" mode.
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
    	
        String finalWhere;

        int count;

        // Does the delete based on the incoming URI pattern.
        switch (sUriMatcher.match(uri)) {
        	case INDEXER:
        		// If the incoming pattern matches the general pattern for notes, does a delete
        		// based on the incoming "where" columns and arguments.
                count = db.delete(
                	Indexer.Index.TABLE_NAME,  // The database table name
                	selection,                 // The incoming where clause column names
                	selectionArgs              // The incoming where clause values
                );
                break;
        	case INDEXER_ID:
        		/*
        		 * Starts a final WHERE clause by restricting it to the
        		 * desired note ID.
        		 */
        		finalWhere =
                    Indexer.Index._ID +              	// The ID column name
                    " = " +                            	// test for equality
                    uri.getPathSegments().get(1);   	// the incoming note ID
                        

        		// If there were additional selection criteria, append them to the final
        		// WHERE clause
        		if (selection != null) {
        			finalWhere = finalWhere + " AND " + selection;
        		}

        		// Performs the delete.
        		count = db.delete(
        				Indexer.Index.TABLE_NAME,  // The database table name.
        				finalWhere,                // The final WHERE clause
        				selectionArgs                  // The incoming where clause values.
        		);
        		break;

        	default:
        		throw new IllegalArgumentException("Unknown URI " + uri);
        }
    	
		return count;
	}

    /**
     * This is called when a client calls {@link android.content.ContentResolver#getType(Uri)}.
     * Returns the MIME data type of the URI given as a parameter.
     *
     * @param uri The URI whose MIME type is desired.
     * @return The MIME type of the URI.
     * @throws IllegalArgumentException if the incoming URI pattern is invalid.
     */
	@Override
	public String getType(Uri uri) {
		/**
		 * Chooses the MIME type based on the incoming URI pattern
	     */
		switch (sUriMatcher.match(uri)) {
			// If the pattern is for notes or live folders, returns the general content type.
	    	case INDEXER:
	    		return Indexer.Index.CONTENT_TYPE;
	    		
	        // If the pattern is for note IDs, returns the note ID content type.
	        case INDEXER_ID:
	        	return Indexer.Index.CONTENT_ITEM_TYPE;

	        // If the URI pattern doesn't match any permitted patterns, throws an exception.
	        default:
	        	throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}

	
    /**
     * This is called when a client calls
     * {@link android.content.ContentResolver#insert(Uri, ContentValues)}.
     * Inserts a new row into the database. This method sets up default values for any
     * columns that are not included in the incoming map.
     * If rows were inserted, then listeners are notified of the change.
     * @return The row ID of the inserted row.
     * @throws SQLException if the insertion fails.
     */
	@Override
	public Uri insert(Uri uri, ContentValues initialValues) {
		// A map to hold the new record's values.
        ContentValues values;
				
		// If the incoming values map is not null, uses it for the new values.
        if (initialValues != null) {
            values = new ContentValues(initialValues);

        } else {
            // Otherwise, create a new value map
            values = new ContentValues();
        }
		
        // If the values map doesn't contain the path or ad identifier number.
        if ((values.containsKey(Indexer.Index.COLUMN_NAME_PATH) == false) || 
        	(values.containsKey(Indexer.Index.COLUMN_NAME_ID_AD) == false)){
        	throw new SQLException("Missed parameter. Failed to insert row into " + uri);
        }      
        
        // Opens the database object in "write" mode.
        // This will trigger its creation if it doesn't already exist.
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		
		// Performs the insert and returns the ID of the new index.
		values.put(Indexer.Index.COLUMN_NAME_TEXT, "Texto de prueba");
		values.put(Indexer.Index.COLUMN_NAME_URL, "http://gumartinm.name");
        long rowId = db.insert(
        	Indexer.Index.TABLE_NAME, // The table to insert into.
            null,  					  // A hack, SQLite sets this column value to null if values is empty.
            values                    // A map of column names, and the values to insert into the columns.
        );
		
        // If the insert succeeded, the row ID exists.
        if (rowId > 0) {
            // Creates a URI with the index ID pattern and the new row ID appended to it.
            Uri noteUri = ContentUris.withAppendedId(Indexer.Index.CONTENT_ID_URI_BASE, rowId);

            // Notifies observers registered against this provider that the data changed.
            getContext().getContentResolver().notifyChange(noteUri, null);
            return noteUri;
        }

        // If the insert didn't succeed, then the rowID is <= 0. Throws an exception.
        throw new SQLException("Failed to insert row into " + uri);
	}
	
	
	/**
	 * This method is called when a client calls
	 * {@link android.content.ContentResolver#query(Uri, String[], String, String[], String)}.
	 * Queries the database and returns a cursor containing the results.
	 *
	 * @return A cursor containing the results of the query. The cursor exists but is empty if
	 * the query returns no results or an exception occurs.
	 * @throws IllegalArgumentException if the incoming URI pattern is invalid.
	 */
	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		// Constructs a new query builder and sets its table name
	    SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
	    qb.setTables(Indexer.Index.TABLE_NAME);
		
	    /**
	     * Choose the projection and adjust the "where" clause based on URI pattern-matching.
	     */
	    switch (sUriMatcher.match(uri)) {
	    	// If the incoming URI is for notes, chooses the Indexer projection
	    	case INDEXER:
	    		qb.setProjectionMap(sIndexerProjectionMap);
	               
	    		break;

	        /* If the incoming URI is for a single note identified by its ID, chooses the
	         * note ID projection, and appends "_ID = <noteID>" to the where clause, so that
	         * it selects that single note
	         */
	        case INDEXER_ID:
	        	qb.setProjectionMap(sIndexerProjectionMap);
	        	qb.appendWhere(
	        	Indexer.Index._ID +		// the name of the ID column
	                   "=" +
	                   // the position of the note ID itself in the incoming URI
	                   uri.getPathSegments().get(1));
	            break;
	            
	        case INDEXER_IDAD:
	        	qb.setProjectionMap(sIndexerProjectionMap);
	        	qb.appendWhere(
	        	Indexer.Index.COLUMN_NAME_ID_AD + // the name of the ID column
	                   "=" +
	                   // the position of the Advertisement ID itself in the incoming URI
	                   uri.getPathSegments().get(2));
	        	break;
	        default:
	            // If the URI doesn't match any of the known patterns, throw an exception.
	            throw new IllegalArgumentException("Unknown URI " + uri);
	    }
	    
	    String orderBy;
	    // If no sort order is specified, uses the default
	    if (TextUtils.isEmpty(sortOrder)) {
	    	orderBy = Indexer.Index.DEFAULT_SORT_ORDER;
	    } else {
	        // otherwise, uses the incoming sort order
	        orderBy = sortOrder;
	    }

	    // Opens the database object in "read" mode, since no writes need to be done.
	    SQLiteDatabase db = mOpenHelper.getReadableDatabase();

	    /*
	     * Performs the query. If no problems occur trying to read the database, then a Cursor
	     * object is returned; otherwise, the cursor variable contains null. If no records were
	     * selected, then the Cursor object is empty, and Cursor.getCount() returns 0.
	     */
	    Cursor c = qb.query(
	    		db,            // The database to query
	    		projection,    // The columns to return from the query
	    		selection,     // The columns for the where clause
	    		selectionArgs, // The values for the where clause
	    		null,          // don't group the rows
	    		null,          // don't filter by row groups
	    		orderBy        // The sort order
	    );
	    
	    if (c == null) {
	    	// If the cursor is null, throw an exception
            throw new SQLiteReadOnlyDatabaseException("Unable to query " + uri);
	    }
	    // Tells the Cursor what URI to watch, so it knows when its source data changes
	    c.setNotificationUri(getContext().getContentResolver(), uri);
	    return c;
	}

    /**
     * This is called when a client calls
     * {@link android.content.ContentResolver#update(Uri,ContentValues,String,String[])}
     * Updates records in the database. The column names specified by the keys in the values map
     * are updated with new data specified by the values in the map. If the incoming URI matches the
     * note ID URI pattern, then the method updates the one record specified by the ID in the URI;
     * otherwise, it updates a set of records. The record or records must match the input
     * selection criteria specified by where and whereArgs.
     * If rows were updated, then listeners are notified of the change.
     *
     * @param uri The URI pattern to match and update.
     * @param values A map of column names (keys) and new values (values).
     * @param where An SQL "WHERE" clause that selects records based on their column values. If this
     * is null, then all records that match the URI pattern are selected.
     * @param whereArgs An array of selection criteria. If the "where" param contains value
     * placeholders ("?"), then each placeholder is replaced by the corresponding element in the
     * array.
     * @return The number of rows updated.
     * @throws IllegalArgumentException if the incoming URI pattern is invalid.
     */
	@Override
	public int update(Uri uri, ContentValues values, String where,
			String[] whereArgs) {
		// Opens the database object in "write" mode.
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count;
        String finalWhere;

        // Does the update based on the incoming URI pattern
        switch (sUriMatcher.match(uri)) {
        
        	// If the incoming URI matches the general notes pattern, does the update based on
        	// the incoming data.
        	case INDEXER:
        		
        		// Does the update and returns the number of rows updated.
                count = db.update(
                    Indexer.Index.TABLE_NAME, // The database table name.
                    values,                   // A map of column names and new values to use.
                    where,                    // The where clause column names.
                    whereArgs                 // The where clause column values to select on.
                );
                break;
            
            // If the incoming URI matches a single note ID, does the update based on the incoming
            // data, but modifies the where clause to restrict it to the particular note ID.
        	case INDEXER_ID:
        		
        		// From the incoming URI, get the note ID
                String noteId = uri.getPathSegments().get(1);

                /*
                 * Starts creating the final WHERE clause by restricting it to the incoming
                 * note ID.
                 */
                finalWhere =
                			Indexer.Index._ID +         // The ID column name
                			" = " +                    	// test for equality
                			noteId;                     // the incoming note ID

                // If there were additional selection criteria, append them to the final WHERE
                // clause
                if (where !=null) {
                    finalWhere = finalWhere + " AND " + where;
                }


                // Does the update and returns the number of rows updated.
                count = db.update(
                	Indexer.Index.TABLE_NAME, // The database table name.
                    values,                   // A map of column names and new values to use.
                    finalWhere,               // The final WHERE clause to use
                                              // placeholders for whereArgs
                    whereArgs                 // The where clause column values to select on, or
                                              // null if the values are in the where argument.
                );
                break;
        		
        	default:
        		throw new IllegalArgumentException("Unknown URI " + uri);
        }
		
        /*Gets a handle to the content resolver object for the current context, and notifies it
         * that the incoming URI changed. The object passes this along to the resolver framework,
         * and observers that have registered themselves for the provider are notified.
         */
        getContext().getContentResolver().notifyChange(uri, null);

        // Returns the number of rows updated.
        return count;
	}
	
	@Override
	public void shutdown() {
		mOpenHelper.close();
	}
}
