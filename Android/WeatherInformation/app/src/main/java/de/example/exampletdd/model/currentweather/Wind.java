package de.example.exampletdd.model.currentweather;

import java.io.Serializable;

public class Wind implements Serializable {
    private static final long serialVersionUID = 5495842422633674631L;
    private Number deg;
    private Number speed;

    public Number getDeg(){
        return this.deg;
    }
    public void setDeg(final Number deg){
        this.deg = deg;
    }
    public Number getSpeed(){
        return this.speed;
    }
    public void setSpeed(final Number speed){
        this.speed = speed;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("Wind [deg=").append(this.deg).append(", speed=").append(this.speed)
        .append("]");
        return builder.toString();
    }
}
