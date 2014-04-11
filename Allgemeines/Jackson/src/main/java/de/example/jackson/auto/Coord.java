package de.example.jackson.auto;


public class Coord{
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
}
