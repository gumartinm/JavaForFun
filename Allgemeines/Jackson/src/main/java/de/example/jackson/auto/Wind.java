package de.example.jackson.auto;


public class Wind{
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
}
