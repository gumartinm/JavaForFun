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
import java.util.Date;
import java.util.List;

public class Current implements Serializable {
    private static final long serialVersionUID = -730690341739860818L;
    private String base;
    private Clouds clouds;
    private Number cod;
    private Coord coord;
    private Number dt;
    private Number id;
    private Main main;
    private String name;
    private Rain rain;
    private Snow snow;
    private Sys sys;
    private List<Weather> weather;
    private Wind wind;
    private byte[] iconData;
    private Date date;

    public String getBase(){
        return this.base;
    }
    public void setBase(final String base){
        this.base = base;
    }
    public Clouds getClouds(){
        return this.clouds;
    }
    public void setClouds(final Clouds clouds){
        this.clouds = clouds;
    }

    public Number getCod() {
        return this.cod;
    }

    public void setCod(final Number cod) {
        this.cod = cod;
    }
    public Coord getCoord(){
        return this.coord;
    }
    public void setCoord(final Coord coord){
        this.coord = coord;
    }
    public Number getDt(){
        return this.dt;
    }
    public void setDt(final Number dt){
        this.dt = dt;
    }
    public Number getId(){
        return this.id;
    }
    public void setId(final Number id){
        this.id = id;
    }
    public Main getMain(){
        return this.main;
    }
    public void setMain(final Main main){
        this.main = main;
    }
    public String getName(){
        return this.name;
    }
    public void setName(final String name){
        this.name = name;
    }
    public Rain getRain(){
        return this.rain;
    }
    public void setRain(final Rain rain){
        this.rain = rain;
    }
    public Snow getSnow() {
        return this.snow;
    }
    public void setSnow(final Snow snow) {
        this.snow = snow;
    }
    public Sys getSys(){
        return this.sys;
    }
    public void setSys(final Sys sys){
        this.sys = sys;
    }
    public List<Weather> getWeather(){
        return this.weather;
    }
    public void setWeather(final List<Weather> weather){
        this.weather = weather;
    }
    public Wind getWind(){
        return this.wind;
    }
    public void setWind(final Wind wind){
        this.wind = wind;
    }

    public byte[] getIconData() {
        return this.iconData;
    }

    public void setIconData(final byte[] iconData) {
        this.iconData = iconData;
    }

    public Date getDate() {
        return this.date;
    }

    public void setDate(final Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("Current [base=").append(this.base).append(", clouds=")
        .append(this.clouds).append(", cod=").append(this.cod).append(", coord=")
        .append(this.coord).append(", dt=").append(this.dt).append(", id=").append(this.id)
        .append(", main=").append(this.main).append(", name=").append(this.name)
        .append(", rain=").append(this.rain).append(", snow=").append(this.snow)
        .append(", sys=").append(this.sys).append(", weather=").append(this.weather)
        .append(", wind=").append(this.wind).append("]");
        return builder.toString();
    }
}
