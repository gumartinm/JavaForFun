package de.example.exampletdd;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Calendar;

import org.apache.http.client.ClientProtocolException;

import android.app.IntentService;
import android.content.Intent;
import android.net.http.AndroidHttpClient;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.fasterxml.jackson.core.JsonParseException;

import de.example.exampletdd.httpclient.CustomHTTPClient;
import de.example.exampletdd.model.GeocodingData;
import de.example.exampletdd.model.currentweather.CurrentWeatherData;
import de.example.exampletdd.model.forecastweather.ForecastWeatherData;
import de.example.exampletdd.parser.JPOSWeatherParser;
import de.example.exampletdd.service.WeatherServiceParser;
import de.example.exampletdd.service.WeatherServicePersistenceFile;

public class WeatherInformationBatch extends IntentService {
    private static final String TAG = "WeatherInformationBatch";
    private static final String resultsNumber = "14";
    private WeatherServicePersistenceFile mWeatherServicePersistenceFile;


    public WeatherInformationBatch() {
        super("WeatherInformationBatch");
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        Log.i(TAG, "WeatherInformationBatch onStartCommand");
        this.mWeatherServicePersistenceFile = new WeatherServicePersistenceFile(this);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(final Intent intent) {

        final GeocodingData geocodingData = this.mWeatherServicePersistenceFile.getGeocodingData();
        Log.i(TAG, "WeatherInformationBatch onHandleIntent");

        if (geocodingData != null) {
            Log.i(TAG, "WeatherInformationBatch onHandleIntent, geocodingData not null");
            final WeatherServiceParser weatherService = new WeatherServiceParser(new JPOSWeatherParser());
            final CustomHTTPClient weatherHTTPClient = new CustomHTTPClient(
                    AndroidHttpClient.newInstance("Android Weather Information Agent"));

            try {
                final WeatherData weatherData = this.doInBackgroundThrowable(geocodingData,
                        weatherHTTPClient, weatherService);
                this.onPostExecuteThrowable(weatherData);
            } catch (final JsonParseException e) {
                Log.e(TAG, "doInBackground exception: ", e);
            } catch (final ClientProtocolException e) {
                Log.e(TAG, "doInBackground exception: ", e);
            } catch (final MalformedURLException e) {
                Log.e(TAG, "doInBackground exception: ", e);
            } catch (final URISyntaxException e) {
                Log.e(TAG, "doInBackground exception: ", e);
            } catch (final IOException e) {
                // logger infrastructure swallows UnknownHostException :/
                Log.e(TAG, "doInBackground exception: " + e.getMessage(), e);
            } finally {
                weatherHTTPClient.close();
            }
        }
    }

    private WeatherData doInBackgroundThrowable(final GeocodingData geocodingData,
            final CustomHTTPClient weatherHTTPClient, final WeatherServiceParser weatherService)
                    throws ClientProtocolException, MalformedURLException, URISyntaxException,
                    JsonParseException, IOException {

        final String APIVersion = WeatherInformationBatch.this.getResources().getString(
                R.string.api_version);

        // 1. Today
        String urlAPI = WeatherInformationBatch.this.getResources().getString(
                R.string.uri_api_weather_today);
        String url = weatherService.createURIAPITodayWeather(urlAPI, APIVersion,
                geocodingData.getLatitude(), geocodingData.getLongitude());
        String jsonData = weatherHTTPClient.retrieveDataAsString(new URL(url));
        final CurrentWeatherData currentWeatherData = weatherService
                .retrieveCurrentWeatherDataFromJPOS(jsonData);
        final Calendar now = Calendar.getInstance();
        currentWeatherData.setDate(now.getTime());

        // 2. Forecast
        urlAPI = WeatherInformationBatch.this.getResources().getString(
                R.string.uri_api_weather_forecast);
        url = weatherService.createURIAPIForecastWeather(urlAPI, APIVersion,
                geocodingData.getLatitude(), geocodingData.getLongitude(), resultsNumber);
        jsonData = weatherHTTPClient.retrieveDataAsString(new URL(url));
        final ForecastWeatherData forecastWeatherData = weatherService
                .retrieveForecastWeatherDataFromJPOS(jsonData);

        return new WeatherData(forecastWeatherData, currentWeatherData);
    }

    private void onPostExecuteThrowable(final WeatherData weatherData)
            throws FileNotFoundException, IOException {

        WeatherInformationBatch.this.mWeatherServicePersistenceFile
        .storeCurrentWeatherData(weatherData.getCurrentWeatherData());
        WeatherInformationBatch.this.mWeatherServicePersistenceFile
        .storeForecastWeatherData(weatherData.getForecastWeatherData());

        // Update weather views.
        final Intent updateCurrentWeather = new Intent(
                "de.example.exampletdd.UPDATECURRENTWEATHER");
        LocalBroadcastManager.getInstance(WeatherInformationBatch.this).sendBroadcast(
                updateCurrentWeather);
        final Intent updateOverviewWeather = new Intent(
                "de.example.exampletdd.UPDATEOVERVIEWWEATHER");
        LocalBroadcastManager.getInstance(WeatherInformationBatch.this).sendBroadcast(
                updateOverviewWeather);

    }

    private class WeatherData {
        private final ForecastWeatherData mForecastWeatherData;
        private final CurrentWeatherData mCurrentWeatherData;

        public WeatherData(final ForecastWeatherData mForecastWeatherData,
                final CurrentWeatherData mCurrentWeatherData) {
            this.mForecastWeatherData = mForecastWeatherData;
            this.mCurrentWeatherData = mCurrentWeatherData;
        }

        public ForecastWeatherData getForecastWeatherData() {
            return this.mForecastWeatherData;
        }

        public CurrentWeatherData getCurrentWeatherData() {
            return this.mCurrentWeatherData;
        }
    }
}
