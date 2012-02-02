package de.android.test3;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class IndexerOpenHelper extends SQLiteOpenHelper {
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


    /*
     * Creates the data repository. This is called when the provider attempts to open the
     * repository and SQLite reports that it doesn't exist.
     */
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(TABLE_CREATE);
	}

	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}
}
