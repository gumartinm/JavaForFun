package de.example.exampletdd.parser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.example.exampletdd.model.WeatherData;

public class JPOSWeatherParser implements IJPOSWeatherParser {

    @Override
    public WeatherData retrieveWeatherFromJPOS(final String jsonData) throws JSONException {
        final JSONObject jsonWeatherData = new JSONObject(jsonData);

        JSONObject jsonObject = jsonWeatherData.getJSONObject("coord");
        final double longitude = jsonObject.getDouble("lon");
        final double latitude = jsonObject.getDouble("lat");
        final WeatherData.Coord coord = new WeatherData.Coord(longitude,
                latitude);

        jsonObject = jsonWeatherData.getJSONObject("sys");
        final long sunRiseTime = jsonObject.getLong("sunrise");
        final long sunSetTime = jsonObject.getLong("sunset");
        final double message = jsonObject.getDouble("message");
        final String country = jsonObject.getString("country");
        final WeatherData.System system = new WeatherData.System(country,
                sunRiseTime, sunSetTime, message);

        // TODO: array of WeatherData.Weather :(
        final JSONArray jsonArray = jsonWeatherData.getJSONArray("weather");
        jsonObject = jsonArray.getJSONObject(0);

        final int id = jsonObject.getInt("id");
        final String mainWeather = jsonObject.getString("main");
        final String description = jsonObject.getString("description");
        final String icon = jsonObject.getString("icon");
        final WeatherData.Weather weather = new WeatherData.Weather(id,
                mainWeather, description, icon);

        jsonObject = jsonWeatherData.getJSONObject("main");
        final double temp = jsonObject.getDouble("temp");
        final double minTemp = jsonObject.getDouble("temp_min");
        final double maxTemp = jsonObject.getDouble("temp_max");
        final double humidity = jsonObject.getDouble("humidity");
        final double pressure = jsonObject.getDouble("pressure");
        final WeatherData.Main main = new WeatherData.Main(temp, minTemp,
                maxTemp, humidity, pressure);

        jsonObject = jsonWeatherData.getJSONObject("wind");
        final double speed = jsonObject.getDouble("speed");
        final double deg = jsonObject.getDouble("deg");
        double gust = 0;
        try {
            gust = jsonObject.getDouble("gust");
        } catch (final JSONException e) {}
        double var_beg = 0;
        try {
            var_beg = jsonObject.getDouble("var_beg");
        } catch (final JSONException e) {}
        double var_end = 0;
        try {
            var_end = jsonObject.getDouble("var_end");
        } catch (final JSONException e) {}
        final WeatherData.Wind wind = new WeatherData.Wind(speed, deg, gust,
                var_beg, var_end);

        jsonObject = jsonWeatherData.getJSONObject("clouds");
        final double cloudiness = jsonObject.getDouble("all");
        final WeatherData.Clouds clouds = new WeatherData.Clouds(cloudiness);

        double time = 0;
        try {
            time = jsonObject.getDouble("time");
        } catch (final JSONException e) {}
        final WeatherData.DataReceivingTime dataReceivingTime =
                new WeatherData.DataReceivingTime(time);


        return new WeatherData.Builder().setCoord(coord).setSystem(system)
                .setWeather(weather).setMain(main).setWind(wind)
                .setClouds(clouds).setDataReceivingTime(dataReceivingTime)
                .build();
    }
}
