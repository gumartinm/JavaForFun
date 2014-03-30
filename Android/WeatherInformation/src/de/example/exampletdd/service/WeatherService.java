package de.example.exampletdd.service;

import java.text.MessageFormat;
import java.util.Locale;

import org.json.JSONException;

import de.example.exampletdd.model.WeatherData;
import de.example.exampletdd.parser.IJPOSWeatherParser;

public class WeatherService {
    private final IJPOSWeatherParser JPOSWeatherParser;

    public WeatherService(final IJPOSWeatherParser JPOSWeatherParser) {
        this.JPOSWeatherParser = JPOSWeatherParser;
    }

    public WeatherData retrieveWeather(final String jsonData) throws JSONException {
        return this.JPOSWeatherParser.retrieveWeatherFromJPOS(jsonData);
    }

    public String createURIAPICoord(final int latitude, final int longitude,
            final String urlAPI, final String APIVersion) {

        final MessageFormat formatURIAPI = new MessageFormat(urlAPI,
                Locale.ENGLISH);
        final Object[] values = new Object[3];
        values[0] = APIVersion;
        values[1] = latitude;
        values[2] = longitude;

        return formatURIAPI.format(values);
    }

    public String createURIAPICityCountry(final String cityCountry,
            final String urlAPI, final String APIVersion) {

        final MessageFormat formatURIAPI = new MessageFormat(urlAPI, Locale.ENGLISH);
        final Object[] values = new Object[2];
        values[0] = APIVersion;
        values[1] = cityCountry;

        return formatURIAPI.format(values);
    }

    public String createURIAPIicon(final String icon, final String urlAPI) {

        final MessageFormat formatURIAPI = new MessageFormat(urlAPI, Locale.ENGLISH);
        final Object[] values = new Object[1];
        values[0] = icon;

        return formatURIAPI.format(values);
    }

}
