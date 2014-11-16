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

import java.io.Serializable;
import java.util.Date;


public class WeatherLocation implements Serializable {
    private static final long serialVersionUID = -3781096380869053212L;
    private int id;
	private String city;
    private String country;
    private boolean isSelected;
    private double latitude;
    private double longitude;
    private Date lastCurrentUIUpdate;
    private Date lastForecastUIUpdate;
    private boolean isNew;

    public WeatherLocation setId(int id) {
		this.id = id;
		return this;
	}

	public WeatherLocation setCity(String city) {
		this.city = city;
		return this;
	}

	public WeatherLocation setCountry(String country) {
		this.country = country;
		return this;
	}

	public WeatherLocation setIsSelected(boolean isSelected) {
		this.isSelected = isSelected;
		return this;
	}

	public WeatherLocation setLatitude(double latitude) {
		this.latitude = latitude;
		return this;
	}

	public WeatherLocation setLongitude(double longitude) {
		this.longitude = longitude;
		return this;
	}

	public WeatherLocation setLastCurrentUIUpdate(Date lastCurrentUIUpdate) {
		this.lastCurrentUIUpdate = lastCurrentUIUpdate;
		return this;
	}

	public WeatherLocation setLastForecastUIUpdate(Date lastForecastUIUpdate) {
		this.lastForecastUIUpdate = lastForecastUIUpdate;
		return this;
	}

    public WeatherLocation setIsNew(final boolean isNew) {
        this.isNew = isNew;
        return this;
    }

	public int getId() {
    	return this.id;
    }
    
    public String getCity() {
        return this.city;
    }

    public String getCountry() {
        return this.country;
    }
    
    public boolean getIsSelected() {
    	return this.isSelected;
    }

    public double getLatitude() {
        return this.latitude;
    }

    public double getLongitude() {
        return this.longitude;
    }
    
    public Date getLastCurrentUIUpdate() {
    	return this.lastCurrentUIUpdate;
    }
    
    public Date getLastForecastUIUpdate() {
    	return this.lastForecastUIUpdate;
    }

    public boolean getIsNew() {
        return this.isNew;
    }
}
