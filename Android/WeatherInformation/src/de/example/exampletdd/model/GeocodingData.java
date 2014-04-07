package de.example.exampletdd.model;

import java.io.Serializable;
import java.util.HashMap;


public class GeocodingData implements Serializable {
    private static final long serialVersionUID = -6774793966351174343L;
    private final String city;
    private final String country;
    private final GeocodingStatus status;
    private final double latitude;
    private final double longitude;

    public static class Builder {
        private String mCity;
        private String mCountry;
        private GeocodingStatus mStatus;
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

        public Builder setStatus(final GeocodingStatus status) {
            this.mStatus = status;
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
        super();
        this.city = builder.mCity;
        this.country = builder.mCountry;
        this.status = builder.mStatus;
        this.latitude = builder.mLatitude;
        this.longitude = builder.mLongitude;
    }

    public String getCity() {
        return this.city;
    }

    public String getCountry() {
        return this.country;
    }

    public GeocodingStatus getStatus() {
        return this.status;
    }

    public double getLatitude() {
        return this.latitude;
    }

    public double getLongitude() {
        return this.longitude;
    }

    public enum GeocodingStatus {
        // see: https://developers.google.com/maps/documentation/geocoding/
        OK("OK"),
        ZERO_RESULTS("ZERO_RESULTS"),
        OVER_QUERY_LIMIT("OVER_QUERY_LIMIT"),
        REQUEST_DENIED("REQUEST_DENIED"),
        INVALID_REQUEST("INVALID_REQUEST"),
        UNKNOWN_ERROR("UNKNOWN_ERROR");

        private final String codeValue;
        private static final HashMap<String, GeocodingStatus> mapStatus =
                new HashMap<String, GeocodingStatus>();

        static {
            for (final GeocodingStatus status : GeocodingStatus.values()) {
                mapStatus.put(status.codeValue, status);
            }
        }

        private GeocodingStatus(final String codeValue) {
            this.codeValue = codeValue;
        }

        public static GeocodingStatus getGeocodingStatus(final String value) {
            return mapStatus.get(value);
        }

    }
}
