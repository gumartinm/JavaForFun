package de.spring.webservices.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class Location {
    private Long id;
    private Long parentId;
    private String description;
    private Point point;

    public static class Point {
        private double longitude;
        private double latitude;

        protected Point() {

        }

        public Point(double longitude, double latitude) {
            this.longitude = longitude;
            this.latitude = latitude;
        }

        public double getLongitude() {
            return longitude;
        }

        public double getLatitude() {
            return latitude;
        }

        @Override
        public int hashCode() {
            final HashCodeBuilder hcb = new HashCodeBuilder();
            hcb.append(this.getLongitude());
            hcb.append(this.getLatitude());
            return hcb.toHashCode();
        }

        @Override
        public boolean equals(Object object) {
            boolean result = false;

            if (object == null) {
                return false;
            }
            if (object == this) {
                return true;
            }
            if (object.getClass() != getClass()) {
                return false;
            }

            final Point other = (Point) object;

            final EqualsBuilder eqb = new EqualsBuilder();
            eqb.append(this.getLongitude(), other.getLongitude());
            eqb.append(this.getLatitude(), other.getLatitude());
            result = eqb.isEquals();
            return result;
        }
    }

    /**
     * Required constructor for MyBatis
     */
    protected Location() {

    }

    public Location(Long id, Long parentId, Integer levelId, String description, Point point) {
        this.id = id;
        this.parentId = parentId;
        this.description = description;
        this.point = point;
    }

    public Long getId() {
        return id;
    }

    public Long getParentId() {
        return parentId;
    }

    public String getDescription() {
        return description;
    }

    public Point getPoint() {
        return point;
    }

    @Override
    public int hashCode() {
        final HashCodeBuilder hcb = new HashCodeBuilder();
        hcb.append(this.getId());
        return hcb.toHashCode();
    }

    @Override
    public boolean equals(Object object) {
        boolean result = false;

        if (object == null) { return false; }
        if (object == this) { return true; }
        if (object.getClass() != getClass()) {
          return false;
        }

        final Location other = (Location) object;

        final EqualsBuilder eqb = new EqualsBuilder();
        eqb.append(this.getId(), other.getId());
        result = eqb.isEquals();
        return result;
    }

}
