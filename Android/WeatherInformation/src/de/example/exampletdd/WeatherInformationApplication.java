package de.example.exampletdd;

import android.app.Application;
import de.example.exampletdd.model.currentweather.Current;
import de.example.exampletdd.model.forecastweather.Forecast;

public class WeatherInformationApplication extends Application {
    private Forecast mForecast;
    private Current mCurrent;
    private String mCity;
    private String mCountry;


    public void setForecast(final Forecast forecast) {
        this.mForecast = forecast;
    }

    public Forecast getForecast() {
        return this.mForecast;
    }
    
    public void setCurrent(final Current current) {
    	this.mCurrent = current;
    }
    
    public Current getCurrent() {
    	return this.mCurrent;
    }
    
    public void setCity(final String city) {
    	this.mCity = city;
    }
    
    public String getCity() {
    	return this.mCity;
    }
    
    public void setCountry(final String country) {
    	this.mCountry = country;
    }
    
    public String getCountry() {
    	return this.mCountry;
    }
} 
