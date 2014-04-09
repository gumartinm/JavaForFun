package de.example.exampletdd.fragment.overview;

import android.graphics.Bitmap;

public class WeatherOverviewEntry {
    private final String date;
    private final String temperature;
    private final Bitmap picture;

    public WeatherOverviewEntry(final String date, final String temperature,
            final Bitmap picture) {
        this.date = date;
        this.temperature = temperature;
        this.picture = picture;
    }

    public String getDate() {
        return this.date;
    }

    public String getTemperature() {
        return this.temperature;
    }

    public Bitmap getPicture() {
        return this.picture;
    }
}
