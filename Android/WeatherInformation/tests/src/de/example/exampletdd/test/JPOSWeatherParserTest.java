package de.example.exampletdd.test;

import junit.framework.TestCase;

import org.json.JSONException;

import de.example.exampletdd.model.WeatherData;
import de.example.exampletdd.parser.JPOSWeatherParser;

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
