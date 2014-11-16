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
package name.gumartinm.weather.information.test;

import name.gumartinm.weather.information.parser.JPOSWeatherParser;

import junit.framework.TestCase;

import org.json.JSONException;

public class JPOSWeatherParserTest extends TestCase {
    private JPOSWeatherParser jposWeatherParser;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        this.jposWeatherParser = new JPOSWeatherParser();
    }

    public void testRetrieveWeatherFromJPOS() throws JSONException {
        // Arrange
        final String jsonData = "{\"coord\":{\"lon\":139,\"lat\":35}}";
        final double longitude = 139;
        final double latitude = 35;
        final WeatherData.Coord coord = new WeatherData.Coord(longitude, latitude);
        final WeatherData expectedWeather = new WeatherData.Builder().setCoord(coord).build();

        // Act
        final WeatherData finalWeather = this.jposWeatherParser.retrieveWeatherFromJPOS(jsonData);

        // Assert
        assertEquals(expectedWeather.toString(), finalWeather.toString());
    }

}
