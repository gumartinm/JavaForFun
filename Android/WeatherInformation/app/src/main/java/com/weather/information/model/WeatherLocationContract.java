package com.weather.information.model;

import android.provider.BaseColumns;

public class WeatherLocationContract {

	// This class can't be instantiated
	private WeatherLocationContract() {}
	
	public static final class WeatherLocation implements BaseColumns {
		
		// This class can't be instantiated
		private WeatherLocation() {}
		
		public static final String TABLE_NAME = "locations";
		
		public static final String COLUMN_NAME_IS_SELECTED = "isSelected";
		
		public static final String COLUMN_NAME_LATITUDE = "latitude";
		
		public static final String COLUMN_NAME_LONGITUDE = "longitude";
		
		public static final String COLUMN_NAME_COUNTRY = "country";
		
		public static final String COLUMN_NAME_CITY = "city";
		
		public static final String COLUMN_NAME_LAST_FORECAST_UI_UPDATE = "lastForecastUpdate";
		
		public static final String COLUMN_NAME_LAST_CURRENT_UI_UPDATE = "lastCurrentUpdate";

        public static final String COLUMN_NAME_IS_NEW = "isNew";
	}

}
