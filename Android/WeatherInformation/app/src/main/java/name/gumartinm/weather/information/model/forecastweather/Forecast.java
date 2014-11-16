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
import java.util.List;

public class Forecast implements Serializable {
    private static final long serialVersionUID = 5095443678019686190L;
    private City city;
    private Number cnt;
    private Number cod;
    private List<name.gumartinm.weather.information.model.forecastweather.List> list;
    private Number message;

    public City getCity(){
        return this.city;
    }
    public void setCity(final City city){
        this.city = city;
    }
    public Number getCnt(){
        return this.cnt;
    }
    public void setCnt(final Number cnt){
        this.cnt = cnt;
    }

    public Number getCod() {
        return this.cod;
    }

    public void setCod(final Number cod) {
        this.cod = cod;
    }

    public List<name.gumartinm.weather.information.model.forecastweather.List> getList() {
        return this.list;
    }

    public void setList(final List<name.gumartinm.weather.information.model.forecastweather.List> list) {
        this.list = list;
    }
    public Number getMessage(){
        return this.message;
    }
    public void setMessage(final Number message){
        this.message = message;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("Forecast [city=").append(this.city).append(", cnt=")
        .append(this.cnt).append(", cod=").append(this.cod).append(", list=")
        .append(this.list).append(", message=").append(this.message).append("]");
        return builder.toString();
    }
}
