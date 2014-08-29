package de.example.exampletdd.service;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Locale;

import com.fasterxml.jackson.core.JsonParseException;

import de.example.exampletdd.model.currentweather.Current;
import de.example.exampletdd.model.forecastweather.Forecast;
import de.example.exampletdd.parser.IJPOSParser;

public class ServiceParser {
    private final IJPOSParser JPOSParser;

    public ServiceParser(final IJPOSParser JPOSWeatherParser) {
        this.JPOSParser = JPOSWeatherParser;
    }

    public Current retrieveCurrentWeatherDataFromJPOS(final String jsonData)
            throws JsonParseException, IOException {
        return this.JPOSParser.retrieveCurrenFromJPOS(jsonData);
    }

    public Forecast retrieveForecastWeatherDataFromJPOS(final String jsonData)
            throws JsonParseException, IOException {
        return this.JPOSParser.retrieveForecastFromJPOS(jsonData);
    }

    public String createURIAPIForecastWeather(final String urlAPI, final String APIVersion,
            final double latitude, final double longitude, final String resultsNumber) {

        final MessageFormat formatURIAPI = new MessageFormat(urlAPI, Locale.US);
        final Object[] values = new Object[4];
        values[0] = APIVersion;
        values[1] = latitude;
        values[2] = longitude;
        values[3] = resultsNumber;

        return formatURIAPI.format(values);
    }

    public String createURIAPITodayWeather(final String urlAPI, final String APIVersion,
            final double latitude, final double longitude) {

        final MessageFormat formatURIAPI = new MessageFormat(urlAPI, Locale.US);
        final Object[] values = new Object[3];
        values[0] = APIVersion;
        values[1] = latitude;
        values[2] = longitude;

        return formatURIAPI.format(values);
    }

    public String createURIAPIicon(final String icon, final String urlAPI) {

        final MessageFormat formatURIAPI = new MessageFormat(urlAPI, Locale.US);
        final Object[] values = new Object[1];
        values[0] = icon;

        return formatURIAPI.format(values);
    }

}
