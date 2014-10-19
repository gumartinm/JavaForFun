package de.example.exampletdd.parser;

import java.io.IOException;
import java.util.ArrayList;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import de.example.exampletdd.model.currentweather.Clouds;
import de.example.exampletdd.model.currentweather.Coord;
import de.example.exampletdd.model.currentweather.Current;
import de.example.exampletdd.model.currentweather.Main;
import de.example.exampletdd.model.currentweather.Rain;
import de.example.exampletdd.model.currentweather.Sys;
import de.example.exampletdd.model.currentweather.Wind;
import de.example.exampletdd.model.forecastweather.City;
import de.example.exampletdd.model.forecastweather.Forecast;
import de.example.exampletdd.model.forecastweather.Temp;

public class JPOSWeatherParser implements IJPOSParser {

    @Override
    public Current retrieveCurrenFromJPOS(final String jsonData)
            throws JsonParseException, IOException {
        final JsonFactory f = new JsonFactory();

        final Current currentWeatherData = new Current();
        currentWeatherData.setClouds(new Clouds());
        currentWeatherData.setCoord(new Coord());
        currentWeatherData.setMain(new Main());
        currentWeatherData.setRain(new Rain());
        currentWeatherData.setSys(new Sys());
        currentWeatherData
        .setWeather(new ArrayList<de.example.exampletdd.model.currentweather.Weather>());
        currentWeatherData.setWind(new Wind());
        final JsonParser jParser = f.createParser(jsonData);

        this.getCurrentWeatherData(currentWeatherData, jParser);

        return currentWeatherData;
    }

    @Override
    public Forecast retrieveForecastFromJPOS(final String jsonData)
            throws JsonParseException, IOException {
        final JsonFactory f = new JsonFactory();

        final Forecast forecastWeatherData = new Forecast();
        forecastWeatherData
        .setList(new ArrayList<de.example.exampletdd.model.forecastweather.List>(15));
        final City city = new City();
        city.setCoord(new de.example.exampletdd.model.forecastweather.Coord());
        forecastWeatherData.setCity(city);
        final JsonParser jParser = f.createParser(jsonData);

        this.getForecastWeatherData(forecastWeatherData, jParser);

        return forecastWeatherData;
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
        if (fieldname == "coord") {
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
        if (fieldname == "sys") {
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
        if (fieldname == "weather") {
            final de.example.exampletdd.model.currentweather.Weather weather = new de.example.exampletdd.model.currentweather.Weather();
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
        if (fieldname == "base") {
            currentWeatherData.setBase(jParser.getText());
        }
        if (fieldname == "main") {
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
        if (fieldname == "wind") {
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
        if (fieldname == "clouds") {
            while (jParser.nextToken() != JsonToken.END_OBJECT) {
                final String namefield = jParser.getCurrentName();
                jParser.nextToken(); // move to value
                if ("all".equals(namefield)) {
                    currentWeatherData.getClouds().setAll(jParser.getDoubleValue());
                }
            }
        }
        if (fieldname == "dt") {
            currentWeatherData.setDt(jParser.getLongValue());
        }
        if (fieldname == "rain") {
            while (jParser.nextToken() != JsonToken.END_OBJECT) {
                final String namefield = jParser.getCurrentName();
                jParser.nextToken(); // move to value
                if ("3h".equals(namefield)) {
                    currentWeatherData.getRain().set3h(jParser.getDoubleValue());
                }
            }
        }
        if (fieldname == "snow") {
            while (jParser.nextToken() != JsonToken.END_OBJECT) {
                final String namefield = jParser.getCurrentName();
                jParser.nextToken(); // move to value
                if ("3h".equals(namefield)) {
                    currentWeatherData.getSnow().set3h(jParser.getDoubleValue());
                }
            }
        }
        if (fieldname == "id") {
            currentWeatherData.setId(jParser.getLongValue());
        }
        if (fieldname == "name") {
            currentWeatherData.setName(jParser.getText());
        }
        if (fieldname == "cod") {
            currentWeatherData.setCod(jParser.getIntValue());
        }
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

        if (fieldname == "cod") {
            final String stringCod = jParser.getText();
            forecastWeatherData.setCod(Long.valueOf(stringCod));
        }
        if (fieldname == "message") {
            forecastWeatherData.setMessage(jParser.getDoubleValue());
        }
        if (fieldname == "city") {
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
        if (fieldname == "cnt") {
            forecastWeatherData.setCnt(jParser.getIntValue());
        }
        if (fieldname == "coord") {
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
        if (fieldname == "list") {
            final de.example.exampletdd.model.forecastweather.List list = new de.example.exampletdd.model.forecastweather.List();
            list.setTemp(new Temp());
            list.setWeather(new ArrayList<de.example.exampletdd.model.forecastweather.Weather>());
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
        if (fieldname == "temp") {
            final de.example.exampletdd.model.forecastweather.List list = forecastWeatherData
                    .getList().get(
                            (forecastWeatherData.getList().size() - 1));
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
        if (fieldname == "weather") {
            final de.example.exampletdd.model.forecastweather.List list = forecastWeatherData
                    .getList().get(
                            (forecastWeatherData.getList().size() - 1));
            final de.example.exampletdd.model.forecastweather.Weather weather = new de.example.exampletdd.model.forecastweather.Weather();
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
