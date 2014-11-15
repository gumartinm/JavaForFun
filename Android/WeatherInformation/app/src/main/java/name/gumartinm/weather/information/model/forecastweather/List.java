package name.gumartinm.weather.information.model.forecastweather;

import java.io.Serializable;


public class List implements Serializable {
    private static final long serialVersionUID = 838468273188666785L;
    private Number clouds;
    private Number deg;
    private Number dt;
    private Number humidity;
    private Number pressure;
    private Number rain;
    private Number snow;
    private Number speed;
    private Temp temp;
    private java.util.List<Weather> weather;

    public Number getClouds(){
        return this.clouds;
    }
    public void setClouds(final Number clouds){
        this.clouds = clouds;
    }
    public Number getDeg(){
        return this.deg;
    }
    public void setDeg(final Number deg){
        this.deg = deg;
    }
    public Number getDt(){
        return this.dt;
    }
    public void setDt(final Number dt){
        this.dt = dt;
    }
    public Number getHumidity(){
        return this.humidity;
    }
    public void setHumidity(final Number humidity){
        this.humidity = humidity;
    }
    public Number getPressure(){
        return this.pressure;
    }
    public void setPressure(final Number pressure){
        this.pressure = pressure;
    }
    public Number getRain(){
        return this.rain;
    }
    public void setRain(final Number rain){
        this.rain = rain;
    }
    public Number getSnow() {
        return this.snow;
    }
    public void setSnow(final Number snow) {
        this.snow = snow;
    }
    public Number getSpeed(){
        return this.speed;
    }
    public void setSpeed(final Number speed){
        this.speed = speed;
    }
    public Temp getTemp(){
        return this.temp;
    }
    public void setTemp(final Temp temp){
        this.temp = temp;
    }

    public java.util.List<Weather> getWeather() {
        return this.weather;
    }

    public void setWeather(final java.util.List<Weather> weather) {
        this.weather = weather;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("List [clouds=").append(this.clouds).append(", deg=").append(this.deg)
        .append(", dt=").append(this.dt).append(", humidity=").append(this.humidity)
        .append(", pressure=").append(this.pressure).append(", rain=").append(this.rain)
        .append(", snow=").append(this.snow).append(", speed=").append(this.speed)
        .append(", temp=").append(this.temp).append(", weather=").append(this.weather)
        .append("]");
        return builder.toString();
    }
}
