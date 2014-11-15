package name.gumartinm.weather.information.model.forecastweather;

import java.io.Serializable;

public class Temp implements Serializable {
    private static final long serialVersionUID = -7614799035018271127L;
    private Number day;
    private Number eve;
    private Number max;
    private Number min;
    private Number morn;
    private Number night;

    public Number getDay(){
        return this.day;
    }
    public void setDay(final Number day){
        this.day = day;
    }
    public Number getEve(){
        return this.eve;
    }
    public void setEve(final Number eve){
        this.eve = eve;
    }
    public Number getMax(){
        return this.max;
    }
    public void setMax(final Number max){
        this.max = max;
    }
    public Number getMin(){
        return this.min;
    }
    public void setMin(final Number min){
        this.min = min;
    }
    public Number getMorn(){
        return this.morn;
    }
    public void setMorn(final Number morn){
        this.morn = morn;
    }
    public Number getNight(){
        return this.night;
    }
    public void setNight(final Number night){
        this.night = night;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("Temp [day=").append(this.day).append(", eve=").append(this.eve)
        .append(", max=").append(this.max).append(", min=").append(this.min)
        .append(", morn=").append(this.morn).append(", night=").append(this.night)
        .append("]");
        return builder.toString();
    }
}
