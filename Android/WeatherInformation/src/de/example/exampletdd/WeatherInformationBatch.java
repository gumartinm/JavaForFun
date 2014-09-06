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
import de.example.exampletdd.model.WeatherLocation;
import de.example.exampletdd.model.WeatherLocationDbHelper;
import de.example.exampletdd.model.WeatherLocationDbQueries;
import de.example.exampletdd.model.currentweather.Current;
import de.example.exampletdd.model.forecastweather.Forecast;
import de.example.exampletdd.parser.JPOSWeatherParser;
import de.example.exampletdd.service.ServiceParser;

public class WeatherInformationBatch extends IntentService {
    private static final String TAG = "WeatherInformationBatch";
    private static final String resultsNumber = "14";


    public WeatherInformationBatch() {
        super("WeatherInformationBatch");
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        Log.i(TAG, "WeatherInformationBatch onStartCommand");

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(final Intent intent) {

        Log.i(TAG, "WeatherInformationBatch onHandleIntent");

        final WeatherLocation weatherLocation = this.queryDataBase();
        if (weatherLocation != null) {
            Log.i(TAG, "WeatherInformationBatch onHandleIntent, geocodingData not null");
            final ServiceParser weatherService = new ServiceParser(new JPOSWeatherParser());
            final CustomHTTPClient weatherHTTPClient = new CustomHTTPClient(
                    AndroidHttpClient.newInstance("Android Weather Information Agent"));

            try {
                this.doInBackgroundThrowable(weatherLocation,weatherHTTPClient, weatherService);
                this.onPostExecuteThrowable();
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

    private void doInBackgroundThrowable(final WeatherLocation weatherLocation,
            final CustomHTTPClient weatherHTTPClient, final ServiceParser weatherService)
                    throws ClientProtocolException, MalformedURLException, URISyntaxException,
                    JsonParseException, IOException {

        final String APIVersion = WeatherInformationBatch.this.getResources().getString(
                R.string.api_version);

        // 1. Today
        String urlAPI = WeatherInformationBatch.this.getResources().getString(
                R.string.uri_api_weather_today);
        String url = weatherService.createURIAPICurrent(urlAPI, APIVersion,
        		weatherLocation.getLatitude(), weatherLocation.getLongitude());
        String jsonData = weatherHTTPClient.retrieveDataAsString(new URL(url));
        final Current currentWeatherData = weatherService
                .retrieveCurrentFromJPOS(jsonData);
        final Calendar now = Calendar.getInstance();
        currentWeatherData.setDate(now.getTime());

        // 2. Forecast
        urlAPI = WeatherInformationBatch.this.getResources().getString(
                R.string.uri_api_weather_forecast);
        url = weatherService.createURIAPIForecast(urlAPI, APIVersion,
        		weatherLocation.getLatitude(), weatherLocation.getLongitude(), resultsNumber);
        jsonData = weatherHTTPClient.retrieveDataAsString(new URL(url));
        final Forecast forecastWeatherData = weatherService
                .retrieveForecastFromJPOS(jsonData);
    }

    private void onPostExecuteThrowable()
            throws FileNotFoundException, IOException {

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
    
    private WeatherLocation queryDataBase() {
        
    	// TODO: repeating the same code!!!
        final WeatherLocationDbHelper dbHelper = new WeatherLocationDbHelper(this);
        try {
        	final WeatherLocationDbQueries queryDb = new WeatherLocationDbQueries(dbHelper); 	
        	return queryDb.queryDataBase();
        } finally {
        	dbHelper.close();
        } 
    }
}
