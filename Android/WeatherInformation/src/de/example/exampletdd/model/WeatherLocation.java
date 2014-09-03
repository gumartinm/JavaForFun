package de.example.exampletdd.model;

import java.io.Serializable;
import java.util.Date;


public class WeatherLocation implements Serializable {
	private static final long serialVersionUID = 1379832318334553377L;
	private final String city;
    private final String country;
    private final double latitude;
    private final double longitude;
    private final Date lastCurrentUIUpdate;
    private final Date lastForecastUIUpdate;

    public static class Builder {
        private String mCity;
        private String mCountry;
        private double mLatitude;
        private double mLongitude;
        private Date mlastCurrentUIUpdate;
        private Date mlastForecastUIUpdate;

        public Builder setCity(final String city) {
            this.mCity = city;
            return this;
        }

        public Builder setCountry(final String country) {
            this.mCountry = country;
            return this;
        }

        public Builder setLatitude(final double latitude) {
            this.mLatitude = latitude;
            return this;
        }

        public Builder setLongitude(final double longitude) {
            this.mLongitude = longitude;
            return this;
        }
        
        public Builder setlastCurrentUIUpdate(final Date lastCurrentUIUpdate) {
        	this.mlastCurrentUIUpdate = lastCurrentUIUpdate;
        	return this;
        }
        
        public Builder setlastForecastUIUpdate(final Date lastForecastUIUpdate) {
        	this.mlastForecastUIUpdate = lastForecastUIUpdate;
        	return this;
        }

        public WeatherLocation build() {
            return new WeatherLocation(this);
        }
    }

    private WeatherLocation(final Builder builder) {
        this.city = builder.mCity;
        this.country = builder.mCountry;
        this.latitude = builder.mLatitude;
        this.longitude = builder.mLongitude;
        this.lastCurrentUIUpdate = builder.mlastCurrentUIUpdate;
        this.lastForecastUIUpdate = builder.mlastForecastUIUpdate;
    }

    public String getCity() {
        return this.city;
    }

    public String getCountry() {
        return this.country;
    }

    public double getLatitude() {
        return this.latitude;
    }

    public double getLongitude() {
        return this.longitude;
    }
    
    public Date getlastCurrentUIUpdate() {
    	return this.lastCurrentUIUpdate;
    }
    
    public Date getlastForecastUIUpdate() {
    	return this.lastForecastUIUpdate;
    }
}
