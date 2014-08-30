package de.example.exampletdd.fragment.specific;

import android.graphics.Bitmap;

public class SpecificDataEntryFirst {
    private final Bitmap picture;
    private final String tempMax;
    private final String tempMin;

    public SpecificDataEntryFirst(final String tempMax, final String tempMin,
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
