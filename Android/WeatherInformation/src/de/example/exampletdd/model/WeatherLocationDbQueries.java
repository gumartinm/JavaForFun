package de.example.exampletdd.model;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class WeatherLocationDbQueries {
	private final SQLiteOpenHelper mDbHelper;

	public interface DoQuery {
		
		public WeatherLocation doQuery(final Cursor cursor);
	}

	public WeatherLocationDbQueries(final SQLiteOpenHelper dbHelper) {
		this.mDbHelper = dbHelper;
	}
	
	// TODO: right now it can be used just once
	public WeatherLocation queryDataBase(String table,
			final String[] projection, final String[] selectionArgs,
			final String selection, final DoQuery doQuery) {
        // TODO: execute around idiom? I miss try/finally with resources
        final SQLiteDatabase db = this.mDbHelper.getReadableDatabase();
        try {
        	final Cursor cursor = db.query(table, projection, selection, selectionArgs, null, null, null);
        	try {
        		if (!cursor.moveToFirst()) {
        	        return null;
        		}
        		else {
        			return doQuery.doQuery(cursor);
        		}
        	} finally {
        		cursor.close();
        	}
        } finally {
        	db.close();
        }
    }
}
