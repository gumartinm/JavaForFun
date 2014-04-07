package de.example.exampletdd.parser;

import org.json.JSONException;

import de.example.exampletdd.model.WeatherData;

public interface IJPOSWeatherParser {

    WeatherData retrieveWeatherFromJPOS(final String jsonData) throws JSONException;

}
