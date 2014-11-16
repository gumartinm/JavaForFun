package name.gumartinm.weather.information.service;

import com.fasterxml.jackson.core.JsonParseException;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Locale;

import name.gumartinm.weather.information.model.currentweather.Current;
import name.gumartinm.weather.information.parser.JPOSCurrentParser;

public class ServiceCurrentParser {
    private final JPOSCurrentParser JPOSParser;

    public ServiceCurrentParser(final JPOSCurrentParser jposParser) {
        this.JPOSParser = jposParser;
    }

    public Current retrieveCurrentFromJPOS(final String jsonData)
            throws JsonParseException, IOException {
        return this.JPOSParser.retrieveCurrenFromJPOS(jsonData);
    }

    public String createURIAPICurrent(final String urlAPI, final String APIVersion,
                                      final double latitude, final double longitude) {

        final MessageFormat formatURIAPI = new MessageFormat(urlAPI, Locale.US);
        final Object[] values = new Object[3];
        values[0] = APIVersion;
        values[1] = latitude;
        values[2] = longitude;

        return formatURIAPI.format(values);
    }
}
