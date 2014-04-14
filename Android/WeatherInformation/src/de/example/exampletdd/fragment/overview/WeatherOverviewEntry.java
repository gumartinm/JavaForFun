package de.example.exampletdd.fragment.overview;

import android.graphics.Bitmap;

public class WeatherOverviewEntry {
    private final String date;
    private final String maxTemp;
    private final String minTemp;
    private final Bitmap picture;

    public WeatherOverviewEntry(final String date, final String maxTemp, final String minTemp,
            final Bitmap picture) {
        this.date = date;
        this.maxTemp = maxTemp;
        this.minTemp = minTemp;
        this.picture = picture;
    }

    public String getDate() {
        return this.date;
    }

    public String getMaxTemp() {
        return this.maxTemp;
    }

    public String getMinTemp() {
        return this.minTemp;
    }

    public Bitmap getPicture() {
        return this.picture;
    }
}
