package de.example.exampletdd.fragment.overview;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.http.client.ClientProtocolException;

import android.app.DialogFragment;
import android.app.ListFragment;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.fasterxml.jackson.core.JsonParseException;

import de.example.exampletdd.R;
import de.example.exampletdd.activityinterface.GetWeather;
import de.example.exampletdd.fragment.ErrorDialogFragment;
import de.example.exampletdd.fragment.ProgressDialogFragment;
import de.example.exampletdd.fragment.specific.WeatherInformationSpecificDataFragment;
import de.example.exampletdd.httpclient.CustomHTTPClient;
import de.example.exampletdd.model.GeocodingData;
import de.example.exampletdd.model.forecastweather.ForecastWeatherData;
import de.example.exampletdd.parser.IJPOSWeatherParser;
import de.example.exampletdd.parser.JPOSWeatherParser;
import de.example.exampletdd.service.WeatherServiceParser;
import de.example.exampletdd.service.WeatherServicePersistenceFile;

public class WeatherInformationOverviewFragment extends ListFragment implements GetWeather {
    private boolean mIsFahrenheit;
    private WeatherServicePersistenceFile mWeatherServicePersistenceFile;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // final SharedPreferences sharedPreferences = PreferenceManager
        // .getDefaultSharedPreferences(this.getActivity());
        // final String keyPreference = this.getResources().getString(
        // R.string.weather_preferences_language_key);
        // this.mLanguage = sharedPreferences.getString(
        // keyPreference, "");

        this.mWeatherServicePersistenceFile = new WeatherServicePersistenceFile(this.getActivity());
        this.mWeatherServicePersistenceFile.removeForecastWeatherData();
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);



        final ListView listWeatherView = this.getListView();

        listWeatherView.setChoiceMode(ListView.CHOICE_MODE_NONE);

        if (savedInstanceState != null) {
            // Restore state
            final ForecastWeatherData forecastWeatherData = (ForecastWeatherData) savedInstanceState
                    .getSerializable("ForecastWeatherData");

            if (forecastWeatherData != null) {
                try {
                    this.mWeatherServicePersistenceFile
                    .storeForecastWeatherData(forecastWeatherData);
                } catch (final IOException e) {
                    final DialogFragment newFragment = ErrorDialogFragment
                            .newInstance(R.string.error_dialog_generic_error);
                    newFragment.show(this.getFragmentManager(), "errorDialog");
                }
            }
        }

        this.setHasOptionsMenu(false);

        final WeatherOverviewAdapter adapter = new WeatherOverviewAdapter(
                this.getActivity(), R.layout.weather_main_entry_list);


        this.setEmptyText("Press download to receive weather information");

        this.setListAdapter(adapter);
        this.setListShown(true);
        this.setListShownNoAnimation(true);
    }

    @Override
    public void onListItemClick(final ListView l, final View v, final int position, final long id) {
        final WeatherInformationSpecificDataFragment fragment = (WeatherInformationSpecificDataFragment) this.getFragmentManager()
                .findFragmentById(R.id.weather_specific_data__fragment);
        if (fragment == null) {
            // handset layout
            final Intent intent = new Intent("de.example.exampletdd.WEATHERINFO").
                    setComponent(new ComponentName("de.example.exampletdd",
                            "de.example.exampletdd.WeatherInformationSpecificDataActivity"));
            intent.putExtra("CHOSEN_DAY", (int) id);
            WeatherInformationOverviewFragment.this.getActivity().startActivity(intent);
        } else {
            // tablet layout
            fragment.getWeatherByDay((int) id);
        }
    }

    @Override
    public void onSaveInstanceState(final Bundle savedInstanceState) {

        // Save state
        final ForecastWeatherData forecastWeatherData = this.mWeatherServicePersistenceFile
                .getForecastWeatherData();

        if (forecastWeatherData != null) {
            savedInstanceState.putSerializable("ForecastWeatherData", forecastWeatherData);
        }

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void getRemoteWeatherInformation() {

        final GeocodingData geocodingData = this.mWeatherServicePersistenceFile.getGeocodingData();

        if (geocodingData != null) {
            final IJPOSWeatherParser JPOSWeatherParser = new JPOSWeatherParser();
            final WeatherServiceParser weatherService = new WeatherServiceParser(
                    JPOSWeatherParser);
            final AndroidHttpClient httpClient = AndroidHttpClient
                    .newInstance("Android Weather Information Agent");
            final CustomHTTPClient HTTPweatherClient = new CustomHTTPClient(
                    httpClient);

            final ForecastWeatherTask weatherTask = new ForecastWeatherTask(HTTPweatherClient,
                    weatherService);


            weatherTask.execute(geocodingData);
        }
    }

    @Override
    public void getWeatherByDay(final int chosenDay) {
        // Nothing to do.
    }

    public void updateForecastWeatherData(final ForecastWeatherData forecastWeatherData) {
        final List<WeatherOverviewEntry> entries = new ArrayList<WeatherOverviewEntry>();
        final WeatherOverviewAdapter adapter = new WeatherOverviewAdapter(this.getActivity(),
                R.layout.weather_main_entry_list);

        final Bitmap picture = BitmapFactory.decodeResource(
                this.getResources(), R.drawable.ic_02d);
        final DecimalFormat tempFormatter = (DecimalFormat) NumberFormat.getNumberInstance(Locale.getDefault());
        tempFormatter.applyPattern("#####.##");
        final SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM d", Locale.getDefault());
        final double tempUnits = this.mIsFahrenheit ? 0 : 273.15;
        final String symbol = this.mIsFahrenheit ? "ºF" : "ºC";


        final Calendar calendar = Calendar.getInstance();
        for (final de.example.exampletdd.model.forecastweather.List forecast : forecastWeatherData
                .getList()) {


            final Long forecastUNIXDate = (Long) forecast.getDt();
            calendar.setTimeInMillis(forecastUNIXDate * 1000L);
            final Date dayTime = calendar.getTime();
            final String dayText = dateFormat.format(dayTime);

            Double maxTemp = null;
            if (forecast.getTemp().getMax() != null) {
                maxTemp = (Double) forecast.getTemp().getMax();
                maxTemp = maxTemp - tempUnits;
            }

            Double minTemp = null;
            if (forecast.getTemp().getMin() != null) {
                minTemp = (Double) forecast.getTemp().getMin();
                minTemp = minTemp - tempUnits;
            }

            if ((maxTemp != null) && (minTemp != null)) {
                entries.add(new WeatherOverviewEntry(dayText, tempFormatter.format(maxTemp)
                        + symbol, tempFormatter.format(minTemp) + symbol, picture));
            }
        }

        this.setListAdapter(null);
        adapter.addAll(entries);
        this.setListAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();

        final SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this.getActivity());

        // 1. Update units of measurement.
        final String keyPreference = this.getResources().getString(
                R.string.weather_preferences_units_key);
        final String unitsPreferenceValue = sharedPreferences.getString(keyPreference, "");
        final String celsius = this.getResources().getString(
                R.string.weather_preferences_units_celsius);
        if (unitsPreferenceValue.equals(celsius)) {
            this.mIsFahrenheit = false;
        } else {
            this.mIsFahrenheit = true;
        }


        // 2. Update forecast weather data on display.
        final ForecastWeatherData forecastWeatherData = this.mWeatherServicePersistenceFile
                .getForecastWeatherData();
        if (forecastWeatherData != null) {
            this.updateForecastWeatherData(forecastWeatherData);
        }


        // 3. If language changed, try to retrieve new data for new language
        // (new strings with the chosen language)
        // keyPreference = this.getResources().getString(
        // R.string.weather_preferences_language_key);
        // final String languagePreferenceValue = sharedPreferences.getString(
        // keyPreference, "");
        // if (!languagePreferenceValue.equals(this.mLanguage)) {
        // this.mLanguage = languagePreferenceValue;
        // this.getWeather();
        // }
    }

    public class ForecastWeatherTask extends AsyncTask<Object, Void, ForecastWeatherData> {
        private static final String TAG = "ForecastWeatherTask";
        private final CustomHTTPClient weatherHTTPClient;
        private final WeatherServiceParser weatherService;
        private final DialogFragment newFragment;

        public ForecastWeatherTask(final CustomHTTPClient weatherHTTPClient,
                final WeatherServiceParser weatherService) {
            this.weatherHTTPClient = weatherHTTPClient;
            this.weatherService = weatherService;
            this.newFragment = ProgressDialogFragment.newInstance(
                    R.string.progress_dialog_get_remote_data,
                    WeatherInformationOverviewFragment.this
                    .getString(R.string.progress_dialog_generic_message));
        }

        @Override
        protected void onPreExecute() {
            this.newFragment.show(WeatherInformationOverviewFragment.this.getActivity()
                    .getFragmentManager(), "progressDialog");
        }

        @Override
        protected ForecastWeatherData doInBackground(final Object... params) {
            ForecastWeatherData forecastWeatherData = null;

            try {
                forecastWeatherData = this.doInBackgroundThrowable(params);
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

            return forecastWeatherData;
        }

        @Override
        protected void onPostExecute(final ForecastWeatherData weatherData) {
            this.weatherHTTPClient.close();

            this.newFragment.dismiss();

            if (weatherData != null) {
                try {
                    this.onPostExecuteThrowable(weatherData);
                } catch (final IOException e) {
                    Log.e(TAG, "WeatherTask onPostExecute exception: ", e);
                    final DialogFragment newFragment = ErrorDialogFragment
                            .newInstance(R.string.error_dialog_generic_error);
                    newFragment.show(WeatherInformationOverviewFragment.this.getFragmentManager(), "errorDialog");
                }
            } else {
                final DialogFragment newFragment = ErrorDialogFragment
                        .newInstance(R.string.error_dialog_generic_error);
                newFragment.show(WeatherInformationOverviewFragment.this.getFragmentManager(), "errorDialog");
            }
        }

        @Override
        protected void onCancelled(final ForecastWeatherData weatherData) {
            this.weatherHTTPClient.close();

            final DialogFragment newFragment = ErrorDialogFragment
                    .newInstance(R.string.error_dialog_connection_tiemout);
            newFragment.show(WeatherInformationOverviewFragment.this.getFragmentManager(), "errorDialog");
        }

        private ForecastWeatherData doInBackgroundThrowable(final Object... params)
                throws ClientProtocolException, MalformedURLException,
                URISyntaxException, JsonParseException, IOException {
            // final SharedPreferences sharedPreferences = PreferenceManager
            // .getDefaultSharedPreferences(WeatherInformationOverviewFragment.this
            // .getActivity());
            //
            // final String keyPreference =
            // WeatherInformationOverviewFragment.this
            // .getActivity().getString(
            // R.string.weather_preferences_language_key);
            // final String languagePreferenceValue =
            // sharedPreferences.getString(keyPreference, "");

            // 1. Coordinates
            final GeocodingData geocodingData = (GeocodingData) params[0];


            final String APIVersion = WeatherInformationOverviewFragment.this.getResources()
                    .getString(R.string.api_version);
            // 2. Forecast
            final String urlAPI = WeatherInformationOverviewFragment.this.getResources()
                    .getString(R.string.uri_api_weather_forecast);
            final String url = this.weatherService.createURIAPIForecastWeather(urlAPI, APIVersion,
                    geocodingData.getLatitude(), geocodingData.getLongitude(), "14");
            final String jsonData = this.weatherHTTPClient.retrieveDataAsString(new URL(url));
            final ForecastWeatherData forecastWeatherData = this.weatherService
                    .retrieveForecastWeatherDataFromJPOS(jsonData);

            return forecastWeatherData;
        }

        private void onPostExecuteThrowable(final ForecastWeatherData forecastWeatherData)
                throws FileNotFoundException, IOException {
            WeatherInformationOverviewFragment.this.mWeatherServicePersistenceFile
                    .storeForecastWeatherData(forecastWeatherData);

            WeatherInformationOverviewFragment.this.updateForecastWeatherData(forecastWeatherData);
        }
    }
}
