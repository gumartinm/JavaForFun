package de.example.exampletdd.parser;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;

import de.example.exampletdd.model.currentweather.CurrentWeatherData;
import de.example.exampletdd.model.forecastweather.ForecastWeatherData;

public interface IJPOSWeatherParser {

    public CurrentWeatherData retrieveCurrentWeatherDataFromJPOS(final String jsonData)
            throws JsonParseException, IOException;

    public ForecastWeatherData retrieveForecastWeatherDataFromJPOS(final String jsonData)
            throws JsonParseException, IOException;
}
