package com.weather.information.fragment.overview;

import android.graphics.Bitmap;

public class OverviewEntry {
    private final String dateName;
    private final String dateNumber;
    private final String maxTemp;
    private final String minTemp;
    private final Bitmap picture;

    public OverviewEntry(final String dateName, final String dateNumber,
            final String maxTemp, final String minTemp,
            final Bitmap picture) {
        this.dateName = dateName;
        this.dateNumber = dateNumber;
        this.maxTemp = maxTemp;
        this.minTemp = minTemp;
        this.picture = picture;
    }

    public String getDateName() {
        return this.dateName;
    }

    public String getDateNumber() {
        return this.dateNumber;
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
