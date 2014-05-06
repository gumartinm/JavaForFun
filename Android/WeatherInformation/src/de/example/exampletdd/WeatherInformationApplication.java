package de.example.exampletdd;

import android.app.Application;
import de.example.exampletdd.model.GeocodingData;

public class WeatherInformationApplication extends Application {
    private GeocodingData mGeocodingData;

    protected void setGeocodingData(final GeocodingData geocodingData) {
        this.mGeocodingData = geocodingData;
    }

    protected GeocodingData getGeocodingData() {
        return this.mGeocodingData;
    }
}
