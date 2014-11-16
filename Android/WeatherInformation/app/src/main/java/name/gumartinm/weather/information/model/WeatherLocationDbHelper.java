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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class WeatherLocationDbHelper extends SQLiteOpenHelper {
	private static final String TAG = "LocationDbHelper";
	public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Location.db";
    
    public WeatherLocationDbHelper(final Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    
	@Override
	public void onCreate(final SQLiteDatabase db) {
		db.execSQL("CREATE TABLE " + WeatherLocationContract.WeatherLocation.TABLE_NAME + " ("
				+ WeatherLocationContract.WeatherLocation._ID + " INTEGER PRIMARY KEY, "
				+ WeatherLocationContract.WeatherLocation.COLUMN_NAME_CITY + " TEXT" + " NOT NULL, "
				+ WeatherLocationContract.WeatherLocation.COLUMN_NAME_COUNTRY + " TEXT" + " NOT NULL, "
				+ WeatherLocationContract.WeatherLocation.COLUMN_NAME_IS_SELECTED + " INTEGER" + " NOT NULL, "
				+ WeatherLocationContract.WeatherLocation.COLUMN_NAME_LAST_CURRENT_UI_UPDATE + " INTEGER, "
				+ WeatherLocationContract.WeatherLocation.COLUMN_NAME_LAST_FORECAST_UI_UPDATE + " INTEGER, "
				+ WeatherLocationContract.WeatherLocation.COLUMN_NAME_LATITUDE + " REAL" + " NOT NULL, "
				+ WeatherLocationContract.WeatherLocation.COLUMN_NAME_LONGITUDE + " REAL" + " NOT NULL, "
                + WeatherLocationContract.WeatherLocation.COLUMN_NAME_IS_NEW + " INTEGER" + " NOT NULL "
				+ ");");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                + newVersion + ", which will destroy all old data");

        // Kills the table and existing data
        db.execSQL("DROP TABLE IF EXISTS " + WeatherLocationContract.WeatherLocation.TABLE_NAME);

        // Recreates the database with a new version
        onCreate(db);
	}

}
