package de.android.test3;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class IndexerOpenHelper extends SQLiteOpenHelper {
	// Used for debugging and logging
    private static final String TAG = "IndexerOpenHelper";
	
	private static final String DBNAME = "mobiads";
	private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "indexer";
    private static final String TABLE_CREATE = "CREATE TABLE " + TABLE_NAME + 
    													"(_ID INTEGER NOT NULL, " +
    													"PATH TEXT(15) NOT NULL, " + 
    													"PRIMARY KEY (_ID), " +
    													"UNIQUE (PATH), " + ")";

    IndexerOpenHelper(Context context) {
        super(context, DBNAME, null, DATABASE_VERSION);
    }


    /**
     *
     * Creates the underlying database with table name and column names taken from the
     * NotePad class.
     */
    @Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(TABLE_CREATE);
	}


    /**
     *
     * Demonstrates that the provider must consider what happens when the
     * underlying datastore is changed. In this sample, the database is upgraded the database
     * by destroying the existing data.
     * A real application should upgrade the database in place.
     */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Logs that the database is being upgraded
        Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                + newVersion + ", which will destroy all old data");

        // Kills the table and existing data
        db.execSQL("DROP TABLE IF EXISTS indexer");

        // Recreates the database with a new version
        onCreate(db);
	}
}
