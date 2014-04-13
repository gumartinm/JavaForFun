package de.example.jackson.databinding;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.example.jackson.auto.currentweather.CurrentWeatherData;
import de.example.jackson.auto.forecast.ForecastWeatherData;

public class JacksonDataBindingTestMain {

    public static void main(final String[] args) {

        // Searching by geographic coordinates:
        // http://api.openweathermap.org/data/2.5/weather?lat=57&lon=-2.15&cnt=1
        final String dataA =
                "{"
                        + "\"coord\":{\"lon\":139,\"lat\":35},"
                        + "\"sys\":{\"message\":5.2147,\"country\":\"JP\",\"sunrise\":1397161018,\"sunset\":1397207585},"
                        + "\"weather\":[{\"id\":800,\"main\":\"Clear\",\"description\":\"Sky is Clear\",\"icon\":\"01n\"}],"
                        + "\"base\":\"cmc stations\","
                        + "\"main\":{\"temp\":273.275,\"temp_min\":273.275,\"temp_max\":273.275,\"pressure\":988.56,\"sea_level\":1033.79,\"grnd_level\":988.56,\"humidity\":95},"
                        + "\"wind\":{\"speed\":1.11,\"deg\":64.5043},"
                        + "\"clouds\":{\"all\":0},"
                        + "\"dt\":1397227133,"
                        + "\"rain\":{\"3h\":0},"
                        + "\"id\":1851632,"
                        + "\"name\":\"Shuzenji\","
                        + "\"cod\":200"
                        + "}";

        final String dataB =
                "{"
                        + "\"coord\":{\"lon\":139,\"lat\":35},"
                        + "\"wind\":{\"speed\":1.11,\"deg\":64.5043},"
                        + "\"name\":\"Shuzenji\","
                        + "\"cod\":200"
                        + "}";

        // Getting daily forecast weather data: Searching 15 days forecast by geographic coordinates at JSON format
        // http://api.openweathermap.org/data/2.5/forecast/daily?lat=57&lon=-2.15&cnt=15&mode=json
        final String dataForeCast = "{\"cod\":\"200\","
                + "\"message\":0.0048,"
                + "\"city\":{\"id\":2641549,\"name\":\"Newtonhill\",\"coord\":{\"lon\":-2.15,\"lat\":57.033329},\"country\":\"GB\",\"population\":0},"
                + "\"cnt\":15,"
                + "\"list\":["
                + "{\"dt\":1397304000,\"temp\":{\"day\":286.15,\"min\":284.62,\"max\":286.15,\"night\":284.62,\"eve\":285.7,\"morn\":286.15},\"pressure\":1016.67,\"humidity\":84,\"weather\":[{\"id\":500,\"main\":\"Rain\",\"description\":\"light rain\",\"icon\":\"10d\"}],\"speed\":7.68,\"deg\":252,\"clouds\":0,\"rain\":0.25},"
                + "{\"dt\":1397390400,\"temp\":{\"day\":284.92,\"min\":282.3,\"max\":284.92,\"night\":282.3,\"eve\":283.79,\"morn\":284.24},\"pressure\":1021.62,\"humidity\":84,\"weather\":[{\"id\":804,\"main\":\"Clouds\",\"description\":\"overcast clouds\",\"icon\":\"04d\"}],\"speed\":7.91,\"deg\":259,\"clouds\":92},"
                + "{\"dt\":1397476800,\"temp\":{\"day\":282.1,\"min\":280.32,\"max\":282.1,\"night\":280.32,\"eve\":281.51,\"morn\":281.65},\"pressure\":1033.84,\"humidity\":92,\"weather\":[{\"id\":801,\"main\":\"Clouds\",\"description\":\"few clouds\",\"icon\":\"02d\"}],\"speed\":8.37,\"deg\":324,\"clouds\":20},"
                + "{\"dt\":1397563200,\"temp\":{\"day\":280.73,\"min\":280.11,\"max\":281.4,\"night\":281.4,\"eve\":280.75,\"morn\":280.11},\"pressure\":1039.27,\"humidity\":97,\"weather\":[{\"id\":801,\"main\":\"Clouds\",\"description\":\"few clouds\",\"icon\":\"02d\"}],\"speed\":7.31,\"deg\":184,\"clouds\":12},"
                + "{\"dt\":1397649600,\"temp\":{\"day\":281.73,\"min\":281.03,\"max\":282.22,\"night\":281.69,\"eve\":282.22,\"morn\":281.03},\"pressure\":1036.05,\"humidity\":90,\"weather\":[{\"id\":803,\"main\":\"Clouds\",\"description\":\"broken clouds\",\"icon\":\"04d\"}],\"speed\":7.61,\"deg\":205,\"clouds\":68},"
                + "{\"dt\":1397736000,\"temp\":{\"day\":282.9,\"min\":281.45,\"max\":283.21,\"night\":282.71,\"eve\":283.06,\"morn\":281.49},\"pressure\":1029.39,\"humidity\":83,\"weather\":[{\"id\":803,\"main\":\"Clouds\",\"description\":\"broken clouds\",\"icon\":\"04d\"}],\"speed\":6.17,\"deg\":268,\"clouds\":56},"
                + "{\"dt\":1397822400,\"temp\":{\"day\":285.26,\"min\":281.55,\"max\":285.26,\"night\":282.48,\"eve\":285.09,\"morn\":281.55},\"pressure\":1025.83,\"humidity\":0,\"weather\":[{\"id\":800,\"main\":\"Clear\",\"description\":\"sky is clear\",\"icon\":\"01d\"}],\"speed\":5.31,\"deg\":221,\"clouds\":10},"
                + "{\"dt\":1397908800,\"temp\":{\"day\":284.29,\"min\":281.5,\"max\":284.29,\"night\":282.53,\"eve\":283.58,\"morn\":281.5},\"pressure\":1024.55,\"humidity\":0,\"weather\":[{\"id\":500,\"main\":\"Rain\",\"description\":\"light rain\",\"icon\":\"10d\"}],\"speed\":5.51,\"deg\":192,\"clouds\":6},"
                + "{\"dt\":1397995200,\"temp\":{\"day\":283.36,\"min\":281.62,\"max\":284.34,\"night\":284.04,\"eve\":284.34,\"morn\":281.62},\"pressure\":1019.58,\"humidity\":0,\"weather\":[{\"id\":500,\"main\":\"Rain\",\"description\":\"light rain\",\"icon\":\"10d\"}],\"speed\":7.66,\"deg\":149,\"clouds\":0,\"rain\":0.48},"
                + "{\"dt\":1398081600,\"temp\":{\"day\":282.24,\"min\":280.51,\"max\":282.41,\"night\":280.51,\"eve\":282.41,\"morn\":280.9},\"pressure\":1027.35,\"humidity\":0,\"weather\":[{\"id\":500,\"main\":\"Rain\",\"description\":\"light rain\",\"icon\":\"10d\"}],\"speed\":8.17,\"deg\":221,\"clouds\":10,\"rain\":0.94},"
                + "{\"dt\":1398168000,\"temp\":{\"day\":282.28,\"min\":279.76,\"max\":282.28,\"night\":280.69,\"eve\":281.13,\"morn\":279.76},\"pressure\":1038.31,\"humidity\":0,\"weather\":[{\"id\":800,\"main\":\"Clear\",\"description\":\"sky is clear\",\"icon\":\"01d\"}],\"speed\":6.33,\"deg\":172,\"clouds\":1},"
                + "{\"dt\":1398254400,\"temp\":{\"day\":281.54,\"min\":280.52,\"max\":281.54,\"night\":281.44,\"eve\":281.23,\"morn\":280.52},\"pressure\":1022.4,\"humidity\":0,\"weather\":[{\"id\":500,\"main\":\"Rain\",\"description\":\"light rain\",\"icon\":\"10d\"}],\"speed\":7.84,\"deg\":140,\"clouds\":91,\"rain\":1.24},"
                + "{\"dt\":1398340800,\"temp\":{\"day\":282.1,\"min\":280.66,\"max\":282.78,\"night\":280.97,\"eve\":282.78,\"morn\":280.66},\"pressure\":1013.39,\"humidity\":0,\"weather\":[{\"id\":500,\"main\":\"Rain\",\"description\":\"light rain\",\"icon\":\"10d\"}],\"speed\":9.43,\"deg\":164,\"clouds\":98,\"rain\":1.03},"
                + "{\"dt\":1398427200,\"temp\":{\"day\":282.11,\"min\":280.72,\"max\":282.32,\"night\":282.32,\"eve\":280.99,\"morn\":280.72},\"pressure\":1018.65,\"humidity\":0,\"weather\":[{\"id\":502,\"main\":\"Rain\",\"description\":\"heavy intensity rain\",\"icon\":\"10d\"}],\"speed\":5.26,\"deg\":158,\"clouds\":83,\"rain\":14.4},"
                + "{\"dt\":1398513600,\"temp\":{\"day\":282.75,\"min\":280.61,\"max\":282.75,\"night\":280.61,\"eve\":281.75,\"morn\":281.96},\"pressure\":1007.4,\"humidity\":0,\"weather\":[{\"id\":500,\"main\":\"Rain\",\"description\":\"light rain\",\"icon\":\"10d\"}],\"speed\":9.18,\"deg\":198,\"clouds\":35,\"rain\":0.55}"
                + "]}";

        final ObjectMapper mapper = new ObjectMapper();

        CurrentWeatherData currentWeatherData = unmarshalling(dataA, mapper,
                CurrentWeatherData.class);
        if (currentWeatherData != null) {
            System.out.println("DATAA UNMARSHALLING (JSON TO JAVA)");
            System.out.println("coord: " + currentWeatherData.getCoord().getLat() + " "
                    + currentWeatherData.getCoord().getLon());
            System.out.println("wind: " + currentWeatherData.getWind().getSpeed() + " "
                    + currentWeatherData.getWind().getDeg());
            System.out.println("name: " + currentWeatherData.getName());
            System.out.println("cod: " + currentWeatherData.getCod());
        }

        String jsonMarshalling = marshalling(currentWeatherData, mapper);
        if (jsonMarshalling != null) {
            System.out.println("DATAA MARSHALLING (JAVA TO JSON)");
            System.out.println(jsonMarshalling);
        }

        System.out.println();
        System.out.println();

        currentWeatherData = unmarshalling(dataB, mapper,
                CurrentWeatherData.class);
        if (currentWeatherData != null) {
            System.out.println("DATAB UNMARSHALLING (JSON TO JAVA)");
            System.out.println("coord: " + currentWeatherData.getCoord().getLat() + " "
                    + currentWeatherData.getCoord().getLon());
            System.out.println("wind: " + currentWeatherData.getWind().getSpeed() + " "
                    + currentWeatherData.getWind().getDeg());
            System.out.println("name: " + currentWeatherData.getName());
            System.out.println("cod: " + currentWeatherData.getCod());
        }

        jsonMarshalling = marshalling(currentWeatherData, mapper);
        if (jsonMarshalling != null) {
            System.out.println("DATAB MARSHALLING (JAVA TO JSON)");
            System.out.println(jsonMarshalling);
        }

        System.out.println();
        System.out.println();


        final ForecastWeatherData forecastWeatherData = unmarshalling(
                dataForeCast, mapper, ForecastWeatherData.class);
        if (forecastWeatherData != null) {
            System.out.println("FORECASTWEATHER UNMARSHALLING (JSON TO JAVA)");
            System.out.println("cnt " + forecastWeatherData.getCnt());
            System.out.println("cod " + forecastWeatherData.getCod());
            System.out.println("city country " + forecastWeatherData.getCity().getCountry());
            System.out.println("city name " + forecastWeatherData.getCity().getName());
            System.out.println("city coord lat " + forecastWeatherData.getCity().getCoord().getLon());
            System.out.println("city population " + forecastWeatherData.getCity().getCoord().getLat());
            System.out.println("city id " + forecastWeatherData.getCity().getPopulation());
            System.out.println("cnt " + forecastWeatherData.getCity().getId());
            final int cnt = (Integer) forecastWeatherData.getCnt();
            for (int i = 0; i < cnt; i++) {
                System.out.println("FORECAST LIST NUMBER: " + i);
                if (forecastWeatherData.getList().get(i).getWeather().size() > 0) {
                    System.out.println("list weather description " + forecastWeatherData.getList().get(i).getWeather().get(0).getDescription());
                    System.out.println("list weather id " + forecastWeatherData.getList().get(i).getWeather().get(0).getId());
                    System.out.println("list weather main " + forecastWeatherData.getList().get(i).getWeather().get(0).getMain());
                    System.out.println("list weather icon " + forecastWeatherData.getList().get(i).getWeather().get(0).getIcon());
                }
                System.out.println("list clouds " + forecastWeatherData.getList().get(i).getClouds());
                System.out.println("list deg " + forecastWeatherData.getList().get(i).getDeg());
                System.out.println("list dt " + forecastWeatherData.getList().get(i).getDt());
                System.out.println("list humidity " + forecastWeatherData.getList().get(i).getHumidity());
                System.out.println("list pressure " + forecastWeatherData.getList().get(i).getPressure());
                System.out.println("list rain " + forecastWeatherData.getList().get(i).getRain());
                System.out.println("list snow " + forecastWeatherData.getList().get(i).getSnow());
                System.out.println("list speed " + forecastWeatherData.getList().get(i).getSpeed());
                System.out.println("list temp day " + forecastWeatherData.getList().get(i).getTemp().getDay());
                System.out.println("list temp eve " + forecastWeatherData.getList().get(i).getTemp().getEve());
                System.out.println("list temp max " + forecastWeatherData.getList().get(i).getTemp().getMax());
                System.out.println("list temp min " + forecastWeatherData.getList().get(i).getTemp().getMin());
                System.out.println("list temp morn " + forecastWeatherData.getList().get(i).getTemp().getMorn());
                System.out.println("list temp night " + forecastWeatherData.getList().get(i).getTemp().getNight());
            }
        }

        jsonMarshalling = marshalling(forecastWeatherData,
                mapper);
        if (jsonMarshalling != null) {
            System.out.println("FORECASTWEATHER MARSHALLING (JAVA TO JSON)");
            System.out.println(jsonMarshalling);
        }
    }

    /**
     * Unmarshalling: JSON -> JAVA
     * 
     * @param jsonData
     * @param mapper
     * @return WeatherData
     */
    public static <E> E unmarshalling(final String jsonData, final ObjectMapper mapper, final Class<E> typeClass) {
        E weatherData = null;

        try {
            weatherData = mapper.readValue(jsonData.getBytes("UTF-8"), typeClass);
        } catch (final JsonParseException e) {
            e.printStackTrace();
        } catch (final JsonMappingException e) {
            e.printStackTrace();
        } catch (final UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (final IOException e) {
            e.printStackTrace();
        }

        return weatherData;
    }

    /**
     * Marshalling: JAVA -> JSON
     * 
     * @param weatherData
     * @param mapper
     * @return String
     */
    public static String marshalling(final Object weatherData, final ObjectMapper mapper) {
        String marshalling = null;

        try {
            marshalling = mapper.writeValueAsString(weatherData);
        } catch (final JsonGenerationException e) {
            e.printStackTrace();
        } catch (final JsonMappingException e) {
            e.printStackTrace();
        } catch (final IOException e) {
            e.printStackTrace();
        }

        return marshalling;
    }
}
