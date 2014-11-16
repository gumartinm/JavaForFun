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

import name.gumartinm.weather.information.model.forecastweather.City;
import name.gumartinm.weather.information.model.forecastweather.Coord;
import name.gumartinm.weather.information.model.forecastweather.Forecast;
import name.gumartinm.weather.information.model.forecastweather.List;
import name.gumartinm.weather.information.model.forecastweather.Temp;
import name.gumartinm.weather.information.model.forecastweather.Weather;

public class JPOSForecastParser {

    public Forecast retrieveForecastFromJPOS(final String jsonData)
            throws JsonParseException, IOException {
        final JsonFactory f = new JsonFactory();

        final Forecast forecastWeatherData = new Forecast();
        forecastWeatherData
                .setList(new ArrayList<List>(15));
        final City city = new City();
        city.setCoord(new Coord());
        forecastWeatherData.setCity(city);
        final JsonParser jParser = f.createParser(jsonData);

        this.getForecastWeatherData(forecastWeatherData, jParser);

        return forecastWeatherData;
    }

    private void getForecastWeatherData(final Forecast forecastWeatherData,
                                        final JsonParser jParser) throws JsonParseException, IOException {
        if (jParser.nextToken() == JsonToken.START_OBJECT) {

            while (jParser.nextToken() != JsonToken.END_OBJECT) {
                final String fieldname = jParser.getCurrentName();
                final JsonToken nextToken = jParser.nextToken();
                if (nextToken == JsonToken.START_OBJECT) {
                    this.getForecastWeatherDataObjects(forecastWeatherData, jParser, fieldname);
                }
                if (nextToken == JsonToken.START_ARRAY) {
                    JsonToken tokenNext = jParser.nextToken();
                    while (tokenNext != JsonToken.END_ARRAY) {
                        if (tokenNext == JsonToken.START_OBJECT) {
                            this.getForecastWeatherDataObjects(forecastWeatherData, jParser, fieldname);
                        }
                        tokenNext = jParser.nextToken();
                    }
                }
                if ((nextToken == JsonToken.VALUE_NUMBER_INT)
                        || (nextToken == JsonToken.VALUE_STRING)) {
                    this.getForecastWeatherDataObjects(forecastWeatherData, jParser, fieldname);
                }
            }
        }
    }

    private void getForecastWeatherDataObjects(final Forecast forecastWeatherData,
                                               final JsonParser jParser, final String fieldname) throws JsonParseException,
            IOException {

        if ("cod".equals(fieldname)) {
            final String stringCod = jParser.getText();
            forecastWeatherData.setCod(Long.valueOf(stringCod));
        }
        if ("message".equals(fieldname)) {
            forecastWeatherData.setMessage(jParser.getDoubleValue());
        }
        if ("city".equals(fieldname)) {
            while (jParser.nextToken() != JsonToken.END_OBJECT) {
                final String namefield = jParser.getCurrentName();
                final JsonToken nextToken = jParser.nextToken(); // move to
                // value
                if ("id".equals(namefield)) {
                    forecastWeatherData.getCity().setId(jParser.getLongValue());
                }
                if ("name".equals(namefield)) {
                    forecastWeatherData.getCity().setName(jParser.getText());
                }
                if ("coord".equals(namefield)) {
                    if (nextToken == JsonToken.START_OBJECT) {
                        this.getForecastWeatherDataObjects(forecastWeatherData, jParser, namefield);
                    }
                }
                if ("country".equals(namefield)) {
                    forecastWeatherData.getCity().setCountry(jParser.getText());
                }
                if ("population".equals(namefield)) {
                    forecastWeatherData.getCity().setPopulation(jParser.getLongValue());
                }
            }
        }
        if ("cnt".equals(fieldname)) {
            forecastWeatherData.setCnt(jParser.getIntValue());
        }
        if ("coord".equals(fieldname)) {
            while (jParser.nextToken() != JsonToken.END_OBJECT) {
                final String namefield = jParser.getCurrentName();
                jParser.nextToken(); // move to value
                if ("lon".equals(namefield)) {
                    forecastWeatherData.getCity().getCoord().setLon(jParser.getDoubleValue());
                }
                if ("lat".equals(namefield)) {
                    forecastWeatherData.getCity().getCoord().setLat(jParser.getDoubleValue());
                }
            }
        }
        if ("list".equals(fieldname)) {
            final name.gumartinm.weather.information.model.forecastweather.List list = new name.gumartinm.weather.information.model.forecastweather.List();
            list.setTemp(new Temp());
            list.setWeather(new ArrayList<Weather>());
            forecastWeatherData.getList().add(list);
            while (jParser.nextToken() != JsonToken.END_OBJECT) {
                final String namefield = jParser.getCurrentName();
                final JsonToken nextToken = jParser.nextToken(); // move to
                // value
                if ("dt".equals(namefield)) {
                    list.setDt(jParser.getLongValue());
                }
                if ("temp".equals(namefield)) {
                    if (nextToken == JsonToken.START_OBJECT) {
                        this.getForecastWeatherDataObjects(forecastWeatherData, jParser, namefield);
                    }
                }
                if ("pressure".equals(namefield)) {
                    list.setPressure(jParser.getDoubleValue());
                }
                if ("humidity".equals(namefield)) {
                    list.setHumidity(jParser.getDoubleValue());
                }
                if ("weather".equals(namefield)) {
                    if (nextToken == JsonToken.START_ARRAY) {
                        JsonToken tokenNext = jParser.nextToken();
                        while (tokenNext != JsonToken.END_ARRAY) {
                            if (tokenNext == JsonToken.START_OBJECT) {
                                this.getForecastWeatherDataObjects(forecastWeatherData, jParser,
                                        namefield);
                            }
                            tokenNext = jParser.nextToken();
                        }
                    }
                }
                if ("speed".equals(namefield)) {
                    list.setSpeed(jParser.getDoubleValue());
                }
                if ("deg".equals(namefield)) {
                    list.setDeg(jParser.getDoubleValue());
                }
                if ("clouds".equals(namefield)) {
                    list.setClouds(jParser.getDoubleValue());
                }
                if ("rain".equals(namefield)) {
                    list.setRain(jParser.getDoubleValue());
                }
            }
        }
        if ("temp".equals(fieldname)) {
            final name.gumartinm.weather.information.model.forecastweather.List list =
                    forecastWeatherData.getList().get((forecastWeatherData.getList().size() - 1));
            while (jParser.nextToken() != JsonToken.END_OBJECT) {
                final String namefield = jParser.getCurrentName();
                jParser.nextToken(); // move to value
                if ("day".equals(namefield)) {
                    list.getTemp().setDay(jParser.getDoubleValue());
                }
                if ("min".equals(namefield)) {
                    list.getTemp().setMin(jParser.getDoubleValue());
                }
                if ("max".equals(namefield)) {
                    list.getTemp().setMax(jParser.getDoubleValue());
                }
                if ("night".equals(namefield)) {
                    list.getTemp().setNight(jParser.getDoubleValue());
                }
                if ("eve".equals(namefield)) {
                    list.getTemp().setEve(jParser.getDoubleValue());
                }
                if ("morn".equals(namefield)) {
                    list.getTemp().setMorn(jParser.getDoubleValue());
                }
            }
        }
        if ("weather".equals(fieldname)) {
            final name.gumartinm.weather.information.model.forecastweather.List list =
                    forecastWeatherData.getList().get((forecastWeatherData.getList().size() - 1));
            final Weather weather = new Weather();
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
            list.getWeather().add(weather);
        }
    }
}
