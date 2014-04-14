package de.example.exampletdd.model.currentweather;

import java.io.Serializable;

public class Weather implements Serializable {
    private static final long serialVersionUID = -34336548786316655L;
    private String description;
    private String icon;
    private Number id;
    private String main;

    public String getDescription(){
        return this.description;
    }
    public void setDescription(final String description){
        this.description = description;
    }
    public String getIcon(){
        return this.icon;
    }
    public void setIcon(final String icon){
        this.icon = icon;
    }
    public Number getId(){
        return this.id;
    }
    public void setId(final Number id){
        this.id = id;
    }
    public String getMain(){
        return this.main;
    }
    public void setMain(final String main){
        this.main = main;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("Weather [description=").append(this.description).append(", icon=")
        .append(this.icon).append(", id=").append(this.id).append(", main=")
        .append(this.main).append("]");
        return builder.toString();
    }
}
