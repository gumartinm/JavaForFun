package de.example.exampletdd.model;

import java.io.Serializable;


public class GeocodingData implements Serializable {
    private static final long serialVersionUID = 8607995242110846833L;
    private final String city;
    private final String country;
    private final double latitude;
    private final double longitude;

    public static class Builder {
        private String mCity;
        private String mCountry;
        private double mLatitude;
        private double mLongitude;

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

        public GeocodingData build() {
            return new GeocodingData(this);
        }
    }

    private GeocodingData(final Builder builder) {
        this.city = builder.mCity;
        this.country = builder.mCountry;
        this.latitude = builder.mLatitude;
        this.longitude = builder.mLongitude;
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
}
