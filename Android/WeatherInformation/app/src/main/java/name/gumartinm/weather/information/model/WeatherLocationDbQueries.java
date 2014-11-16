/**
 * Copyright 2014 Gustavo Martin Morcuende
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package name.gumartinm.weather.information.model;

import java.util.Date;

import android.content.ContentValues;
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
	
	public WeatherLocation queryDataBase() {
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
        		WeatherLocationContract.WeatherLocation.COLUMN_NAME_LONGITUDE,
                WeatherLocationContract.WeatherLocation.COLUMN_NAME_IS_NEW
        	    };
        

        final WeatherLocationDbQueries.DoQuery doQuery = new WeatherLocationDbQueries.DoQuery() {

        	@Override
        	public WeatherLocation doQuery(final Cursor cursor) {        		
        		final int id = cursor.getInt(cursor.getColumnIndexOrThrow(WeatherLocationContract.WeatherLocation._ID));
        		final String city = cursor.getString(cursor.
        				getColumnIndexOrThrow(WeatherLocationContract.WeatherLocation.COLUMN_NAME_CITY));
        		final String country = cursor.getString(cursor.
        				getColumnIndexOrThrow(WeatherLocationContract.WeatherLocation.COLUMN_NAME_COUNTRY));
        		final boolean isSelected = (cursor.getInt(cursor
                        .getColumnIndexOrThrow(WeatherLocationContract.WeatherLocation.COLUMN_NAME_IS_SELECTED)) != 0);
        		Date lastCurrentUIUpdate = null;
        		if (!cursor.isNull(cursor
        				.getColumnIndexOrThrow(WeatherLocationContract.WeatherLocation.COLUMN_NAME_LAST_CURRENT_UI_UPDATE))) {
            		final long javaTime = cursor.getLong(cursor
            				.getColumnIndexOrThrow(WeatherLocationContract.WeatherLocation.COLUMN_NAME_LAST_CURRENT_UI_UPDATE));
            		lastCurrentUIUpdate = new Date(javaTime);
        		}
        		Date lasForecastUIUpdate = null;
        		if (!cursor.isNull(cursor
        				.getColumnIndexOrThrow(WeatherLocationContract.WeatherLocation.COLUMN_NAME_LAST_FORECAST_UI_UPDATE))) {
            		final long javaTime = cursor.getLong(cursor
            				.getColumnIndexOrThrow(WeatherLocationContract.WeatherLocation.COLUMN_NAME_LAST_FORECAST_UI_UPDATE));
            		lasForecastUIUpdate = new Date(javaTime);
        		}
        		final double latitude = cursor.getDouble(cursor.
        				getColumnIndexOrThrow(WeatherLocationContract.WeatherLocation.COLUMN_NAME_LATITUDE));
        		final double longitude = cursor.getDouble(cursor.
        				getColumnIndexOrThrow(WeatherLocationContract.WeatherLocation.COLUMN_NAME_LONGITUDE));
                final boolean isNew = (cursor.getInt(cursor
                        .getColumnIndexOrThrow(WeatherLocationContract.WeatherLocation.COLUMN_NAME_IS_NEW)) != 0);
        		
	        	
        		return new WeatherLocation()
        				.setId(id)
        				.setCity(city)
        				.setCountry(country)
        				.setIsSelected(isSelected)
        				.setLastCurrentUIUpdate(lastCurrentUIUpdate)
        				.setLastForecastUIUpdate(lasForecastUIUpdate)
        				.setLatitude(latitude)
        				.setLongitude(longitude)
                        .setIsNew(isNew);
        	}
        };

        return this.queryDataBase(
        		WeatherLocationContract.WeatherLocation.TABLE_NAME, projection,
        		selectionArgs, selection, doQuery);
    }
	
	public long insertIntoDataBase(final WeatherLocation weatherLocation) {
		// Create a new map of values, where column names are the keys
		final ContentValues values = new ContentValues();
		values.put(WeatherLocationContract.WeatherLocation.COLUMN_NAME_CITY, weatherLocation.getCity());
		values.put(WeatherLocationContract.WeatherLocation.COLUMN_NAME_COUNTRY, weatherLocation.getCountry());
		values.put(WeatherLocationContract.WeatherLocation.COLUMN_NAME_IS_SELECTED, weatherLocation.getIsSelected());
		Date javaTime = weatherLocation.getLastCurrentUIUpdate();
		if (javaTime != null) {
			values.put(WeatherLocationContract.WeatherLocation.COLUMN_NAME_LAST_CURRENT_UI_UPDATE, javaTime.getTime());
		}
		javaTime = weatherLocation.getLastForecastUIUpdate();
		if (javaTime != null) {
			values.put(WeatherLocationContract.WeatherLocation.COLUMN_NAME_LAST_FORECAST_UI_UPDATE, javaTime.getTime());
		}
		values.put(WeatherLocationContract.WeatherLocation.COLUMN_NAME_LATITUDE, weatherLocation.getLatitude());
		values.put(WeatherLocationContract.WeatherLocation.COLUMN_NAME_LONGITUDE, weatherLocation.getLongitude());
        values.put(WeatherLocationContract.WeatherLocation.COLUMN_NAME_IS_NEW, weatherLocation.getIsNew());
		
		return this.insertIntoDataBase(WeatherLocationContract.WeatherLocation.TABLE_NAME, values);
	}
	
	public void updateDataBase(final WeatherLocation weatherLocation) {
		final String selection = WeatherLocationContract.WeatherLocation.COLUMN_NAME_IS_SELECTED + " = ?";
        final String[] selectionArgs = { "1" };
		// Create a new map of values, where column names are the keys
		final ContentValues values = new ContentValues();
		values.put(WeatherLocationContract.WeatherLocation.COLUMN_NAME_CITY, weatherLocation.getCity());
		values.put(WeatherLocationContract.WeatherLocation.COLUMN_NAME_COUNTRY, weatherLocation.getCountry());
		values.put(WeatherLocationContract.WeatherLocation.COLUMN_NAME_IS_SELECTED, weatherLocation.getIsSelected());
		Date javaTime = weatherLocation.getLastCurrentUIUpdate();
		if (javaTime != null) {
			values.put(WeatherLocationContract.WeatherLocation.COLUMN_NAME_LAST_CURRENT_UI_UPDATE, javaTime.getTime());
		} else {
			values.putNull(WeatherLocationContract.WeatherLocation.COLUMN_NAME_LAST_CURRENT_UI_UPDATE);
		}
		javaTime = weatherLocation.getLastForecastUIUpdate();
		if (javaTime != null) {
			values.put(WeatherLocationContract.WeatherLocation.COLUMN_NAME_LAST_FORECAST_UI_UPDATE, javaTime.getTime());
		} else {
			values.putNull(WeatherLocationContract.WeatherLocation.COLUMN_NAME_LAST_FORECAST_UI_UPDATE);
		}
		values.put(WeatherLocationContract.WeatherLocation.COLUMN_NAME_LATITUDE, weatherLocation.getLatitude());
		values.put(WeatherLocationContract.WeatherLocation.COLUMN_NAME_LONGITUDE, weatherLocation.getLongitude());
        values.put(WeatherLocationContract.WeatherLocation.COLUMN_NAME_IS_NEW, weatherLocation.getIsNew());
		
		this.updateDataBase(WeatherLocationContract.WeatherLocation.TABLE_NAME, selectionArgs, selection, values);
	}
	
	// TODO: May I perform another query after this method (after closing almost everything but mDbHelper)
	private WeatherLocation queryDataBase(final String table,
			final String[] projection, final String[] selectionArgs,
			final String selection, final DoQuery doQuery) {
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
	
	// TODO: May I perform another query after this method (after closing almost everything but mDbHelper)
	private long insertIntoDataBase(final String table, final ContentValues values) {
        final SQLiteDatabase db = this.mDbHelper.getWritableDatabase();
        try {
        	return db.insert(table, null, values);
        } finally {
        	db.close();
        }
    }
	
	// TODO: May I perform another query after this method (after closing almost everything but mDbHelper)
	private long updateDataBase(final String table, final String[] selectionArgs,
			final String selection, final ContentValues values) {
        final SQLiteDatabase db = this.mDbHelper.getWritableDatabase();
        try {
        	return db.update(table, values, selection, selectionArgs);
        } finally {
        	db.close();
        }
    }
	
}
