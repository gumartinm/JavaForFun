package name.gumartinm.weather.information.parser;

import com.fasterxml.jackson.core.JsonParseException;
import name.gumartinm.weather.information.model.currentweather.Current;
import name.gumartinm.weather.information.model.forecastweather.Forecast;

import java.io.IOException;


public interface IJPOSParser {

    public Current retrieveCurrenFromJPOS(final String jsonData)
            throws JsonParseException, IOException;

    public Forecast retrieveForecastFromJPOS(final String jsonData)
            throws JsonParseException, IOException;
}
