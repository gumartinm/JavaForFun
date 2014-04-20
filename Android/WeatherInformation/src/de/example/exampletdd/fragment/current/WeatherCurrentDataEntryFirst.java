package de.example.exampletdd.fragment.current;

import android.graphics.Bitmap;

public class WeatherCurrentDataEntryFirst {
    private final Bitmap picture;
    private final String tempMax;
    private final String tempMin;

    public WeatherCurrentDataEntryFirst(final String tempMax, final String tempMin,
            final Bitmap picture) {
        this.tempMax = tempMax;
        this.tempMin = tempMin;
        this.picture = picture;
    }

    public Bitmap getPicture() {
        return this.picture;
    }

    public String getTempMax() {
        return this.tempMax;
    }

    public String getTempMin() {
        return this.tempMin;
    }
}
