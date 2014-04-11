package de.example.jackson.databinding;

public class Coord {
    // City location
    private Double lon;
    private Double lat;


    public Double getLon() {
        return this.lon;
    }

    public Double getLat() {
        return this.lat;
    }

    public void setLon(final Double lon) {
        this.lon = lon;
    }

    public void setLat(final Double lat) {
        this.lat = lat;
    }
}
