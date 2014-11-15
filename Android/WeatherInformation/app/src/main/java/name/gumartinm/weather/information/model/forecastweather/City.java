package name.gumartinm.weather.information.model.forecastweather;

import java.io.Serializable;


public class City implements Serializable {
    private static final long serialVersionUID = 3079687975077030704L;
    private Coord coord;
    private String country;
    private Number id;
    private String name;
    private Number population;

    public Coord getCoord(){
        return this.coord;
    }
    public void setCoord(final Coord coord){
        this.coord = coord;
    }
    public String getCountry(){
        return this.country;
    }
    public void setCountry(final String country){
        this.country = country;
    }
    public Number getId(){
        return this.id;
    }
    public void setId(final Number id){
        this.id = id;
    }
    public String getName(){
        return this.name;
    }
    public void setName(final String name){
        this.name = name;
    }
    public Number getPopulation(){
        return this.population;
    }
    public void setPopulation(final Number population){
        this.population = population;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("City [coord=").append(this.coord).append(", country=").append(this.country)
        .append(", id=").append(this.id).append(", name=").append(this.name)
        .append(", population=").append(this.population).append("]");
        return builder.toString();
    }
}
