package de.example.jackson.auto;

import java.util.List;

public class WeatherData{
    private String base;
    private Clouds clouds;
    private Number cod;
    private Coord coord;
    private Number dt;
    private Number id;
    private Main main;
    private String name;
    private Sys sys;
    private List<Weather> weather;
    private Wind wind;

    public String getBase(){
        return this.base;
    }
    public void setBase(final String base){
        this.base = base;
    }
    public Clouds getClouds(){
        return this.clouds;
    }
    public void setClouds(final Clouds clouds){
        this.clouds = clouds;
    }
    public Number getCod(){
        return this.cod;
    }
    public void setCod(final Number cod){
        this.cod = cod;
    }
    public Coord getCoord(){
        return this.coord;
    }
    public void setCoord(final Coord coord){
        this.coord = coord;
    }
    public Number getDt(){
        return this.dt;
    }
    public void setDt(final Number dt){
        this.dt = dt;
    }
    public Number getId(){
        return this.id;
    }
    public void setId(final Number id){
        this.id = id;
    }
    public Main getMain(){
        return this.main;
    }
    public void setMain(final Main main){
        this.main = main;
    }
    public String getName(){
        return this.name;
    }
    public void setName(final String name){
        this.name = name;
    }
    public Sys getSys(){
        return this.sys;
    }
    public void setSys(final Sys sys){
        this.sys = sys;
    }
    public List<Weather> getWeather(){
        return this.weather;
    }
    public void setWeather(final List<Weather> weather){
        this.weather = weather;
    }
    public Wind getWind(){
        return this.wind;
    }
    public void setWind(final Wind wind){
        this.wind = wind;
    }
}
