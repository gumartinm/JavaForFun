package de.example.exampletdd;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Calendar;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.http.client.ClientProtocolException;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.fasterxml.jackson.core.JsonParseException;

import de.example.exampletdd.httpclient.CustomHTTPClient;
import de.example.exampletdd.model.GeocodingData;
import de.example.exampletdd.model.currentweather.CurrentWeatherData;
import de.example.exampletdd.model.forecastweather.ForecastWeatherData;
import de.example.exampletdd.parser.IJPOSWeatherParser;
import de.example.exampletdd.parser.JPOSWeatherParser;
import de.example.exampletdd.service.WeatherServiceParser;
import de.example.exampletdd.service.WeatherServicePersistenceFile;

public class WeatherInformationBatch extends Service {
    private static final String TAG = "WeatherInformationBatch";
    private static final String resultsNumber = "14";
    private WeatherServicePersistenceFile mWeatherServicePersistenceFile;
    private ScheduledExecutorService mUpdateWeatherTask;
    private int mUpdateTimeRate;

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(final Context context, final Intent intent) {
            // This method will be run in the main thread of this service.
            final String action = intent.getAction();
            if (action.equals("de.example.exampletdd.UPDATETIMERATEWEATHERBATCH")) {
                final Bundle extras = intent.getExtras();
                WeatherInformationBatch.this.mUpdateTimeRate = extras.getInt("UPDATE_RATE_TIME", 60);

                WeatherInformationBatch.this.updateWeather();
            }

            if (action.equals("de.example.exampletdd.UPDATEGEOCODINGWEATHERBATCH")) {
                WeatherInformationBatch.this.updateWeather();
            }
        }
    };

    @Override
    public IBinder onBind(final Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        final Bundle extras = intent.getExtras();
        this.mUpdateTimeRate = extras.getInt("UPDATE_RATE_TIME", 60);

        this.mWeatherServicePersistenceFile = new WeatherServicePersistenceFile(this);

        this.updateWeather();


        final IntentFilter filter = new IntentFilter();
        filter.addAction("de.example.exampletdd.UPDATETIMERATEWEATHERBATCH");
        filter.addAction("de.example.exampletdd.UPDATEGEOCODINGWEATHERBATCH");
        this.registerReceiver(this.mReceiver, filter);

        return Service.START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(this.mReceiver);
        if (this.mUpdateWeatherTask != null) {
            this.mUpdateWeatherTask.shutdownNow();
        }
    }

    private void updateWeather() {
        if (this.mUpdateWeatherTask != null) {
            this.mUpdateWeatherTask.shutdownNow();
        }

        this.mUpdateWeatherTask = Executors.newScheduledThreadPool(1);
        this.mUpdateWeatherTask.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    WeatherInformationBatch.this.updateWeatherTask();
                } catch (final Throwable e) {
                    Log.i(TAG, "updateWeather, unexpected exception: ", e);
                }
            }
        }, 0, this.mUpdateTimeRate, TimeUnit.SECONDS);
    }

    private void updateWeatherTask() {
        final GeocodingData geocodingData = this.mWeatherServicePersistenceFile.getGeocodingData();

        if (geocodingData != null) {
            final IJPOSWeatherParser JPOSWeatherParser = new JPOSWeatherParser();
            final WeatherServiceParser weatherService = new WeatherServiceParser(JPOSWeatherParser);
            final AndroidHttpClient httpClient = AndroidHttpClient.newInstance("Android Weather Information Agent");
            final CustomHTTPClient HTTPweatherClient = new CustomHTTPClient(httpClient);

            final ServiceWeatherTask weatherTask = new ServiceWeatherTask(HTTPweatherClient,
                    weatherService);

            weatherTask.execute(geocodingData);
        }
    }

    private class ServiceWeatherTask extends AsyncTask<Object, Void, WeatherData> {
        private static final String TAG = "ServiceWeatherTask";
        private final CustomHTTPClient weatherHTTPClient;
        private final WeatherServiceParser weatherService;

        private ServiceWeatherTask(final CustomHTTPClient weatherHTTPClient,
                final WeatherServiceParser weatherService) {
            this.weatherHTTPClient = weatherHTTPClient;
            this.weatherService = weatherService;
        }

        @Override
        protected WeatherData doInBackground(final Object... params) {
            WeatherData weatherData = null;

            Log.i(TAG, "ServiceWeatherTask Update Remote Data");
            try {
                weatherData = this.doInBackgroundThrowable(params);
            } catch (final ClientProtocolException e) {
                Log.e(TAG, "doInBackground exception: ", e);
            } catch (final MalformedURLException e) {
                Log.e(TAG, "doInBackground exception: ", e);
            } catch (final URISyntaxException e) {
                Log.e(TAG, "doInBackground exception: ", e);
            } catch (final JsonParseException e) {
                Log.e(TAG, "doInBackground exception: ", e);
            } catch (final IOException e) {
                // logger infrastructure swallows UnknownHostException :/
                Log.e(TAG, "doInBackground exception: " + e.getMessage(), e);
            } finally {
                this.weatherHTTPClient.close();
            }

            return weatherData;
        }

        @Override
        protected void onPostExecute(final WeatherData weatherData) {
            this.weatherHTTPClient.close();

            if (weatherData != null) {
                try {
                    this.onPostExecuteThrowable(weatherData);
                } catch (final IOException e) {
                    Log.e(TAG, "onPostExecute exception: ", e);
                }
            } else {
                Log.e(TAG, "onPostExecute WeatherData null value");
            }
        }

        @Override
        protected void onCancelled(final WeatherData weatherData) {
            this.weatherHTTPClient.close();
        }

        private WeatherData doInBackgroundThrowable(final Object... params)
                throws ClientProtocolException, MalformedURLException, URISyntaxException,
                JsonParseException, IOException {

            // 1. Coordinates
            final GeocodingData geocodingData = (GeocodingData) params[0];

            final String APIVersion = WeatherInformationBatch.this.getResources().getString(
                    R.string.api_version);

            // 2. Today
            String urlAPI = WeatherInformationBatch.this.getResources().getString(
                    R.string.uri_api_weather_today);
            String url = this.weatherService.createURIAPITodayWeather(urlAPI, APIVersion,
                    geocodingData.getLatitude(), geocodingData.getLongitude());
            String jsonData = this.weatherHTTPClient.retrieveDataAsString(new URL(url));
            final CurrentWeatherData currentWeatherData = this.weatherService
                    .retrieveCurrentWeatherDataFromJPOS(jsonData);
            final Calendar now = Calendar.getInstance();
            currentWeatherData.setDate(now.getTime());

            // 3. Forecast
            urlAPI = WeatherInformationBatch.this.getResources().getString(
                    R.string.uri_api_weather_forecast);
            url = this.weatherService.createURIAPIForecastWeather(urlAPI, APIVersion,
                    geocodingData.getLatitude(), geocodingData.getLongitude(), resultsNumber);
            jsonData = this.weatherHTTPClient.retrieveDataAsString(new URL(url));
            final ForecastWeatherData forecastWeatherData = this.weatherService
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
            WeatherInformationBatch.this.sendBroadcast(updateCurrentWeather);
            final Intent updateOverviewWeather = new Intent(
                    "de.example.exampletdd.UPDATEOVERVIEWWEATHER");
            WeatherInformationBatch.this.sendBroadcast(updateOverviewWeather);

        }
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
