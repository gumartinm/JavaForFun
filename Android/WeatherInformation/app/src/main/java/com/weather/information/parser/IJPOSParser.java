package com.weather.information.parser;

import com.fasterxml.jackson.core.JsonParseException;
import com.weather.information.model.currentweather.Current;
import com.weather.information.model.forecastweather.Forecast;

import java.io.IOException;


public interface IJPOSParser {

    public Current retrieveCurrenFromJPOS(final String jsonData)
            throws JsonParseException, IOException;

    public Forecast retrieveForecastFromJPOS(final String jsonData)
            throws JsonParseException, IOException;
}
