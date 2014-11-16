/**
 * Copyright 2014 Gustavo Martin Morcuende
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
