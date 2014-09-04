package de.example.exampletdd.model;

import android.content.Context;
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
	
	public WeatherLocation queryDataBase(final Context context) {
        final String selection = WeatherLocationContract.WeatherLocation.COLUMN_NAME_IS_SELECTED + " = ?";
        final String[] selectionArgs = { "1" };
        final String[] projection = {
        		WeatherLocationContract.WeatherLocation._ID,
        		WeatherLocationContract.WeatherLocation.COLUMN_NAME_CITY,
        		WeatherLocationContract.WeatherLocation.COLUMN_NAME_COUNTRY,
        		WeatherLocationContract.WeatherLocation.COLUMN_NAME_IS_SELECTED,
        		WeatherLocationContract.WeatherLocation.COLUMN_NAME_LAST_CURRENT_UI_UPDATE,
        		WeatherLocationContract.WeatherLocation.COLUMN_NAME_LAST_FORECAST_UI_UPDATE,
        		WeatherLocationContract.WeatherLocation.COLUMN_NAME_LATITUDE,
        		WeatherLocationContract.WeatherLocation.COLUMN_NAME_LONGITUDE
        	    };
        

        final WeatherLocationDbQueries.DoQuery doQuery = new WeatherLocationDbQueries.DoQuery() {

        	@Override
        	public WeatherLocation doQuery(final Cursor cursor) {
        		String city = cursor.getString(cursor.
        				getColumnIndexOrThrow(WeatherLocationContract.WeatherLocation.COLUMN_NAME_CITY));
        		String country = cursor.getString(cursor.
        				getColumnIndexOrThrow(WeatherLocationContract.WeatherLocation.COLUMN_NAME_COUNTRY));
        		double latitude = cursor.getDouble(cursor.
        				getColumnIndexOrThrow(WeatherLocationContract.WeatherLocation.COLUMN_NAME_LATITUDE));
        		double longitude = cursor.getDouble(cursor.
        				getColumnIndexOrThrow(WeatherLocationContract.WeatherLocation.COLUMN_NAME_LONGITUDE));
	        	
        		return new WeatherLocation.Builder().
        				setCity(city).setCountry(country).
        				setLatitude(latitude).setLongitude(longitude).
        				build();
        	}
        };

        return this.queryDataBase(
        		WeatherLocationContract.WeatherLocation.TABLE_NAME, projection,
        		selectionArgs, selection, doQuery);
    }
    
	// TODO: May I perform another query after this method (after closing almost everything but mDbHelper)
	private WeatherLocation queryDataBase(final String table,
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
