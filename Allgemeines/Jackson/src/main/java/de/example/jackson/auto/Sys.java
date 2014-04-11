package de.example.jackson.auto;


public class Sys{
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
}
