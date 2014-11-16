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
