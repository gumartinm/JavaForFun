/**
 * 
 */
package de.android.test3;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/**
 *
 */
public class IndexerProvider extends ContentProvider {
    // Creates a UriMatcher object.
    private static final UriMatcher sUriMatcher;
    
    // Handle to a new DatabaseHelper.
    private IndexerOpenHelper mOpenHelper;
    
    // The incoming URI matches the Notes URI pattern
    private static final int INDEXER = 1;
    
    // The incoming URI matches the Note ID URI pattern
    private static final int INDEXER_ID = 2;
    
    static {
    	
    	/*
         * Creates and initializes the URI matcher
         */
        // Create a new instance
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        
        // Add a pattern that routes URIs terminated with "notes" to a NOTES operation
        sUriMatcher.addURI("de.android.test3.provider", "indexer", INDEXER);
        
        // Add a pattern that routes URIs terminated with "notes" plus an integer
        // to a note ID operation
        sUriMatcher.addURI("de.android.test3.provider", "indexer/#", INDEXER_ID);
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
        case INDEXER_ID:
        	/*
             * Starts a final WHERE clause by restricting it to the
             * desired note ID.
             */
            finalWhere =
                    NotePad.Notes._ID +                              // The ID column name
                    " = " +                                          // test for equality
                    uri.getPathSegments().                           // the incoming note ID
                        get(NotePad.Notes.NOTE_ID_PATH_POSITION)
            ;

            // If there were additional selection criteria, append them to the final
            // WHERE clause
            if (where != null) {
                finalWhere = finalWhere + " AND " + where;
            }

            // Performs the delete.
            count = db.delete(
                NotePad.Notes.TABLE_NAME,  // The database table name.
                finalWhere,                // The final WHERE clause
                whereArgs                  // The incoming where clause values.
            );
            break;

        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
    	
		return 0;
	}

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#getType(android.net.Uri)
	 */
	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#insert(android.net.Uri, android.content.ContentValues)
	 */
	@Override
	public Uri insert(Uri uri, ContentValues values) {
		/*
         * Gets a writeable database. This will trigger its creation if it doesn't already exist.
         *
         */
        db = mOpenHelper.getWritableDatabase();

		return null;
	}
	
	

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#query(android.net.Uri, java.lang.String[], java.lang.String, java.lang.String[], java.lang.String)
	 */
	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#update(android.net.Uri, android.content.ContentValues, java.lang.String, java.lang.String[])
	 */
	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

}
