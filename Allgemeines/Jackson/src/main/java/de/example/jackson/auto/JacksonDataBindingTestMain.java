package de.example.jackson.auto;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JacksonDataBindingTestMain {

    public static void main(final String[] args) {

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
        final ObjectMapper mapper = new ObjectMapper();

        // Unmarshalling:
        WeatherData weatherDataPOJO = null;
        try {
            weatherDataPOJO = mapper.readValue(dataB.getBytes("UTF-8"), WeatherData.class);
        } catch (final JsonParseException e) {
            e.printStackTrace();
        } catch (final JsonMappingException e) {
            e.printStackTrace();
        } catch (final UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (final IOException e) {
            e.printStackTrace();
        }

        if (weatherDataPOJO != null) {
            System.out.println("coord: " + weatherDataPOJO.getCoord().getLat() + " " + weatherDataPOJO.getCoord().getLon());
            System.out.println("wind: " + weatherDataPOJO.getWind().getSpeed() + " " + weatherDataPOJO.getWind().getDeg());
            System.out.println("name: " + weatherDataPOJO.getName());
            System.out.println("cod: " + weatherDataPOJO.getCod());
        }

        // Marshalling:
        try {
            final String dataBMarshalling = mapper.writeValueAsString(weatherDataPOJO);
            System.out.println(dataBMarshalling);
        } catch (final JsonGenerationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (final JsonMappingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (final IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
