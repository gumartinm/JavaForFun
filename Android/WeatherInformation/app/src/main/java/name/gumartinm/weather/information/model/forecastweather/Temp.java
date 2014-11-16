/**
 * Copyright 2014 Gustavo Martin Morcuende
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package name.gumartinm.weather.information.model.forecastweather;

import java.io.Serializable;

public class Temp implements Serializable {
    private static final long serialVersionUID = -2240491260916844781L;
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
