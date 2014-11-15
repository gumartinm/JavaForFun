package com.weather.information.model.currentweather;

import java.io.Serializable;

public class Clouds implements Serializable {
    private static final long serialVersionUID = 3034435739326030899L;
    private Number all;

    public Number getAll(){
        return this.all;
    }
    public void setAll(final Number all){
        this.all = all;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("Clouds [all=").append(this.all).append("]");
        return builder.toString();
    }
}
