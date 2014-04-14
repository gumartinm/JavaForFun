package de.example.exampletdd.service;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Locale;

import com.fasterxml.jackson.core.JsonParseException;

import de.example.exampletdd.model.currentweather.CurrentWeatherData;
import de.example.exampletdd.model.forecastweather.ForecastWeatherData;
import de.example.exampletdd.parser.IJPOSWeatherParser;

public class WeatherServiceParser {
    private final IJPOSWeatherParser JPOSWeatherParser;

    public WeatherServiceParser(final IJPOSWeatherParser JPOSWeatherParser) {
        this.JPOSWeatherParser = JPOSWeatherParser;
    }

    public CurrentWeatherData retrieveCurrentWeatherDataFromJPOS(final String jsonData)
            throws JsonParseException, IOException {
        return this.JPOSWeatherParser.retrieveCurrentWeatherDataFromJPOS(jsonData);
    }

    public ForecastWeatherData retrieveForecastWeatherDataFromJPOS(final String jsonData)
            throws JsonParseException, IOException {
        return this.JPOSWeatherParser.retrieveForecastWeatherDataFromJPOS(jsonData);
    }

    public String createURIAPIForecastWeather(final String urlAPI, final String APIVersion,
            final double latitude, final double longitude, final String resultsNumber) {

        final MessageFormat formatURIAPI = new MessageFormat(urlAPI, Locale.ENGLISH);
        final Object[] values = new Object[4];
        values[0] = APIVersion;
        values[1] = latitude;
        values[2] = longitude;
        values[3] = resultsNumber;

        return formatURIAPI.format(values);
    }

    public String createURIAPITodayWeather(final String urlAPI, final String APIVersion,
            final double latitude, final double longitude) {

        final MessageFormat formatURIAPI = new MessageFormat(urlAPI, Locale.ENGLISH);
        final Object[] values = new Object[3];
        values[0] = APIVersion;
        values[1] = latitude;
        values[2] = longitude;

        return formatURIAPI.format(values);
    }

    public String createURIAPIicon(final String icon, final String urlAPI) {

        final MessageFormat formatURIAPI = new MessageFormat(urlAPI, Locale.ENGLISH);
        final Object[] values = new Object[1];
        values[0] = icon;

        return formatURIAPI.format(values);
    }

}
