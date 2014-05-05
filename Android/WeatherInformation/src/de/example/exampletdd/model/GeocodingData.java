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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = (prime * result) + ((this.city == null) ? 0 : this.city.hashCode());
        result = (prime * result) + ((this.country == null) ? 0 : this.country.hashCode());
        long temp;
        temp = Double.doubleToLongBits(this.latitude);
        result = (prime * result) + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(this.longitude);
        result = (prime * result) + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (this.getClass() != obj.getClass())
            return false;
        final GeocodingData other = (GeocodingData) obj;
        if (this.city == null) {
            if (other.city != null)
                return false;
        } else if (!this.city.equals(other.city))
            return false;
        if (this.country == null) {
            if (other.country != null)
                return false;
        } else if (!this.country.equals(other.country))
            return false;
        if (Double.doubleToLongBits(this.latitude) != Double.doubleToLongBits(other.latitude))
            return false;
        if (Double.doubleToLongBits(this.longitude) != Double.doubleToLongBits(other.longitude))
            return false;
        return true;
    }
}
