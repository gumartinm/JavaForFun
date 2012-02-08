package de.android.test3;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class IndexerOpenHelper extends SQLiteOpenHelper {
	// Used for debugging and logging
    private static final String TAG = "IndexerOpenHelper";
	
	private static final String DATABASE_NAME = "mobiads.db";
	private static final int DATABASE_VERSION = 1;


    IndexerOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    /**
     *
     * Creates the underlying database with table name and column names taken from the
     * NotePad class.
     */
    @Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE " + Indexer.Index.TABLE_NAME + " ("
				+ Indexer.Index._ID + "  INTEGER PRIMARY KEY, "
				+ Indexer.Index.COLUMN_NAME_ID_AD + " REAL" + " UNIQUE" + "NOT NULL"
				+ Indexer.Index.COLUMN_NAME_PATH + " TEXT(15)" + " UNIQUE" + " NOT NULL"
				+ ");");
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
        db.execSQL("DROP TABLE IF EXISTS " + Indexer.Index.TABLE_NAME);

        // Recreates the database with a new version
        onCreate(db);
	}
}
