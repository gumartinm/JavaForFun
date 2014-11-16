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
package name.gumartinm.weather.information.model.currentweather;

import java.io.Serializable;

public class Main implements Serializable {
    private static final long serialVersionUID = -7886213347552636533L;
    private Number grnd_level;
    private Number humidity;
    private Number pressure;
    private Number sea_level;
    private Number temp;
    private Number temp_max;
    private Number temp_min;

    public Number getGrnd_level() {
        return this.grnd_level;
    }

    public void setGrnd_level(final Number grnd_level) {
        this.grnd_level = grnd_level;
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

    public Number getSea_level() {
        return this.sea_level;
    }

    public void setSea_level(final Number sea_level) {
        this.sea_level = sea_level;
    }

    public Number getTemp(){
        return this.temp;
    }
    public void setTemp(final Number temp){
        this.temp = temp;
    }
    public Number getTemp_max(){
        return this.temp_max;
    }
    public void setTemp_max(final Number temp_max){
        this.temp_max = temp_max;
    }
    public Number getTemp_min(){
        return this.temp_min;
    }
    public void setTemp_min(final Number temp_min){
        this.temp_min = temp_min;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("Main [grnd_level=").append(this.grnd_level).append(", humidity=")
        .append(this.humidity).append(", pressure=").append(this.pressure)
        .append(", sea_level=").append(this.sea_level).append(", temp=").append(this.temp)
        .append(", temp_max=").append(this.temp_max).append(", temp_min=")
        .append(this.temp_min).append("]");
        return builder.toString();
    }
}
