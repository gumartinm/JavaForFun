package de.example.exampletdd.model.currentweather;

import java.io.Serializable;


public class Sys implements Serializable {
    private static final long serialVersionUID = 5333083785731053139L;
    private String country;
    private Number message;
    private Number sunrise;
    private Number sunset;

    public String getCountry(){
        return this.country;
    }
    public void setCountry(final String country){
        this.country = country;
    }
    public Number getMessage(){
        return this.message;
    }
    public void setMessage(final Number message){
        this.message = message;
    }
    public Number getSunrise(){
        return this.sunrise;
    }
    public void setSunrise(final Number sunrise){
        this.sunrise = sunrise;
    }
    public Number getSunset(){
        return this.sunset;
    }
    public void setSunset(final Number sunset){
        this.sunset = sunset;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("Sys [country=").append(this.country).append(", message=")
        .append(this.message).append(", sunrise=").append(this.sunrise).append(", sunset=")
        .append(this.sunset).append("]");
        return builder.toString();
    }
}
