package de.example.jackson.auto;


public class Weather{
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
}
