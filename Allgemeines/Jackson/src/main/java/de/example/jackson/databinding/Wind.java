package de.example.jackson.databinding;

public class Wind {
    // Wind speed in mps
    private Double speed;
    // Wind direction in degrees (meteorological)
    private Double deg;
    // speed of wind gust
    private Double gust;
    // Wind direction
    private Double var_beg;
    // Wind direction
    private Double var_end;


    public Double getSpeed() {
        return this.speed;
    }

    public Double getDeg() {
        return this.deg;
    }

    public Double getGust() {
        return this.gust;
    }

    public Double getVar_beg() {
        return this.var_beg;
    }

    public Double getVar_end() {
        return this.var_end;
    }

    public void setSpeed(final Double speed) {
        this.speed = speed;
    }

    public void setDeg(final Double deg) {
        this.deg = deg;
    }

    public void setGust(final Double gust) {
        this.gust = gust;
    }

    public void setVar_beg(final Double var_beg) {
        this.var_beg = var_beg;
    }

    public void setVar_end(final Double var_end) {
        this.var_end = var_end;
    }
}
