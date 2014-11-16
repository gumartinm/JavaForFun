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
package name.gumartinm.weather.information.parser;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import java.io.IOException;
import java.util.ArrayList;

import name.gumartinm.weather.information.model.currentweather.Clouds;
import name.gumartinm.weather.information.model.currentweather.Coord;
import name.gumartinm.weather.information.model.currentweather.Current;
import name.gumartinm.weather.information.model.currentweather.Main;
import name.gumartinm.weather.information.model.currentweather.Rain;
import name.gumartinm.weather.information.model.currentweather.Snow;
import name.gumartinm.weather.information.model.currentweather.Sys;
import name.gumartinm.weather.information.model.currentweather.Weather;
import name.gumartinm.weather.information.model.currentweather.Wind;

public class JPOSCurrentParser {

    public Current retrieveCurrenFromJPOS(final String jsonData)
            throws JsonParseException, IOException {
        final JsonFactory f = new JsonFactory();

        final Current currentWeatherData = new Current();
        currentWeatherData.setClouds(new Clouds());
        currentWeatherData.setCoord(new Coord());
        currentWeatherData.setMain(new Main());
        currentWeatherData.setRain(new Rain());
        currentWeatherData.setSys(new Sys());
        currentWeatherData.setSnow(new Snow());
        currentWeatherData
                .setWeather(new ArrayList<Weather>());
        currentWeatherData.setWind(new Wind());
        final JsonParser jParser = f.createParser(jsonData);

        this.getCurrentWeatherData(currentWeatherData, jParser);

        return currentWeatherData;
    }

    private void getCurrentWeatherData(final Current currentWeatherData,
                                       final JsonParser jParser) throws JsonParseException, IOException {
        if (jParser.nextToken() == JsonToken.START_OBJECT) {

            while (jParser.nextToken() != JsonToken.END_OBJECT) {
                final String fieldname = jParser.getCurrentName();
                final JsonToken nextToken = jParser.nextToken();
                if (nextToken == JsonToken.START_OBJECT) {
                    this.getCurrentWeatherDataObjects(currentWeatherData, jParser, fieldname);
                }
                if (nextToken == JsonToken.START_ARRAY) {
                    JsonToken tokenNext = jParser.nextToken();
                    while (tokenNext != JsonToken.END_ARRAY) {
                        if (tokenNext == JsonToken.START_OBJECT) {
                            this.getCurrentWeatherDataObjects(currentWeatherData, jParser, fieldname);
                        }
                        tokenNext = jParser.nextToken();
                    }
                }
                if ((nextToken == JsonToken.VALUE_NUMBER_INT)
                        || (nextToken == JsonToken.VALUE_STRING)) {
                    this.getCurrentWeatherDataObjects(currentWeatherData, jParser, fieldname);
                }
            }
        }
    }

    private void getCurrentWeatherDataObjects(final Current currentWeatherData,
                                              final JsonParser jParser, final String fieldname) throws JsonParseException,
            IOException {
        if ("coord".equals(fieldname)) {
            while (jParser.nextToken() != JsonToken.END_OBJECT) {
                final String namefield = jParser.getCurrentName();
                jParser.nextToken(); // move to value
                if ("lon".equals(namefield)) {
                    currentWeatherData.getCoord().setLon(jParser.getDoubleValue());
                }
                if ("lat".equals(namefield)) {
                    currentWeatherData.getCoord().setLat(jParser.getDoubleValue());
                }
            }
        }
        if ("sys".equals(fieldname)) {
            while (jParser.nextToken() != JsonToken.END_OBJECT) {
                final String namefield = jParser.getCurrentName();
                jParser.nextToken(); // move to value
                if ("message".equals(namefield)) {
                    currentWeatherData.getSys().setMessage(jParser.getDoubleValue());
                }
                if ("country".equals(namefield)) {
                    currentWeatherData.getSys().setCountry(jParser.getValueAsString());
                }
                if ("sunrise".equals(namefield)) {
                    currentWeatherData.getSys().setSunrise(jParser.getValueAsLong());
                }
                if ("sunset".equals(namefield)) {
                    currentWeatherData.getSys().setSunset(jParser.getValueAsLong());
                }
            }
        }
        if ("weather".equals(fieldname)) {
            final Weather weather = new Weather();
            currentWeatherData.getWeather().add(weather);
            while (jParser.nextToken() != JsonToken.END_OBJECT) {
                final String namefield = jParser.getCurrentName();
                jParser.nextToken(); // move to value
                if ("id".equals(namefield)) {
                    weather.setId(jParser.getIntValue());
                }
                if ("main".equals(namefield)) {
                    weather.setMain(jParser.getText());
                }
                if ("description".equals(namefield)) {
                    weather.setDescription(jParser.getText());
                }
                if ("icon".equals(namefield)) {
                    weather.setIcon(jParser.getText());
                }

            }
        }
        if ("base".equals(fieldname)) {
            currentWeatherData.setBase(jParser.getText());
        }
        if ("main".equals(fieldname)) {
            while (jParser.nextToken() != JsonToken.END_OBJECT) {
                final String namefield = jParser.getCurrentName();
                jParser.nextToken(); // move to value
                if ("temp".equals(namefield)) {
                    currentWeatherData.getMain().setTemp(jParser.getDoubleValue());
                }
                if ("temp_min".equals(namefield)) {
                    currentWeatherData.getMain().setTemp_min(jParser.getDoubleValue());
                }
                if ("temp_max".equals(namefield)) {
                    currentWeatherData.getMain().setTemp_max(jParser.getDoubleValue());
                }
                if ("pressure".equals(namefield)) {
                    currentWeatherData.getMain().setPressure(jParser.getDoubleValue());
                }
                if ("sea_level".equals(namefield)) {
                    currentWeatherData.getMain().setSea_level(jParser.getDoubleValue());
                }
                if ("grnd_level".equals(namefield)) {
                    currentWeatherData.getMain().setGrnd_level(jParser.getDoubleValue());
                }
                if ("humidity".equals(namefield)) {
                    currentWeatherData.getMain().setHumidity(jParser.getDoubleValue());
                }
            }
        }
        if ("wind".equals(fieldname)) {
            while (jParser.nextToken() != JsonToken.END_OBJECT) {
                final String namefield = jParser.getCurrentName();
                jParser.nextToken(); // move to value
                if ("speed".equals(namefield)) {
                    currentWeatherData.getWind().setSpeed(jParser.getDoubleValue());
                }
                if ("deg".equals(namefield)) {
                    currentWeatherData.getWind().setDeg(jParser.getDoubleValue());
                }
            }
        }
        if ("clouds".equals(fieldname)) {
            while (jParser.nextToken() != JsonToken.END_OBJECT) {
                final String namefield = jParser.getCurrentName();
                jParser.nextToken(); // move to value
                if ("all".equals(namefield)) {
                    currentWeatherData.getClouds().setAll(jParser.getDoubleValue());
                }
            }
        }
        if ("dt".equals(fieldname)) {
            currentWeatherData.setDt(jParser.getLongValue());
        }
        if ("rain".equals(fieldname)) {
            while (jParser.nextToken() != JsonToken.END_OBJECT) {
                final String namefield = jParser.getCurrentName();
                jParser.nextToken(); // move to value
                if ("3h".equals(namefield)) {
                    currentWeatherData.getRain().set3h(jParser.getDoubleValue());
                }
            }
        }
        if ("snow".equals(fieldname)) {
            while (jParser.nextToken() != JsonToken.END_OBJECT) {
                final String namefield = jParser.getCurrentName();
                jParser.nextToken(); // move to value
                if ("3h".equals(namefield)) {
                    currentWeatherData.getSnow().set3h(jParser.getDoubleValue());
                }
            }
        }
        if ("id".equals(fieldname)) {
            currentWeatherData.setId(jParser.getLongValue());
        }
        if ("name".equals(fieldname)) {
            currentWeatherData.setName(jParser.getText());
        }
        if ("cod".equals(fieldname)) {
            currentWeatherData.setCod(jParser.getIntValue());
        }
    }
}
