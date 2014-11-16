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

public class Weather implements Serializable {
    private static final long serialVersionUID = -5066357704517363241L;
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
