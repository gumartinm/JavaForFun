package de.example.jackson.databinding;



public class WeatherDataPOJO {
    private String name;
    private Integer cod;
    private Coord coord;
    private Wind wind;


    public String getName() {
        return name;
    }

    public Integer getCod() {
        return cod;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public void setCod(final Integer cod) {
        this.cod = cod;
    }

    public Coord getCoord() {
        return coord;
    }

    public Wind getWind() {
        return wind;
    }
}
