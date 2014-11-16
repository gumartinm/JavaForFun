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
