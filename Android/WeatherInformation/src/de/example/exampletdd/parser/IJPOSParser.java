package de.example.exampletdd.parser;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;

import de.example.exampletdd.model.currentweather.Current;
import de.example.exampletdd.model.forecastweather.Forecast;

public interface IJPOSParser {

    public Current retrieveCurrenFromJPOS(final String jsonData)
            throws JsonParseException, IOException;

    public Forecast retrieveForecastFromJPOS(final String jsonData)
            throws JsonParseException, IOException;
}
