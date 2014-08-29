package de.example.exampletdd;

import android.app.Application;
import de.example.exampletdd.model.GeocodingData;
import de.example.exampletdd.model.forecastweather.Forecast;

public class WeatherInformationApplication extends Application {
    private Forecast mForecast;
    private GeocodingData mGeocodingData;

    protected void setGeocodingData(final GeocodingData geocodingData) {
        this.mGeocodingData = geocodingData;
    }

    protected GeocodingData getGeocodingData() {
        return this.mGeocodingData;
    }

    public void setForecast(final Forecast forecast) {
        this.mForecast = forecast;
    }

    public Forecast getForecast() {
        return this.mForecast;
    }
}
