package de.example.exampletdd.model.currentweather;

import java.io.Serializable;

public class Coord implements Serializable {
    private static final long serialVersionUID = 7151637605146377486L;
    private Number lat;
    private Number lon;

    public Number getLat(){
        return this.lat;
    }
    public void setLat(final Number lat){
        this.lat = lat;
    }
    public Number getLon(){
        return this.lon;
    }
    public void setLon(final Number lon){
        this.lon = lon;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("Coord [lat=").append(this.lat).append(", lon=").append(this.lon)
        .append("]");
        return builder.toString();
    }
}
