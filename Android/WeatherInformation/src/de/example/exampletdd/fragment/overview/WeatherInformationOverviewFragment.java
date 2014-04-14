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
import java.util.Iterator;
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
import de.example.exampletdd.model.currentweather.CurrentWeatherData;
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
            final CurrentWeatherData currentWeatherData = (CurrentWeatherData) savedInstanceState
                    .getSerializable("CurrentWeatherData");

            if ((forecastWeatherData != null) && (currentWeatherData != null)) {
                try {
                    this.mWeatherServicePersistenceFile
                    .storeForecastWeatherData(forecastWeatherData);
                    this.mWeatherServicePersistenceFile.storeCurrentWeatherData(currentWeatherData);
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
            intent.putExtra("CHOSEN_DAY", id);
            WeatherInformationOverviewFragment.this.getActivity().startActivity(intent);
        } else {
            // tablet layout
            fragment.getWeatherByDay(position);
        }
    }

    @Override
    public void onSaveInstanceState(final Bundle savedInstanceState) {

        // Save state
        final ForecastWeatherData forecastWeatherData = this.mWeatherServicePersistenceFile
                .getForecastWeatherData();

        final CurrentWeatherData currentWeatherData = this.mWeatherServicePersistenceFile
                .getCurrentWeatherData();

        if ((forecastWeatherData != null) && (currentWeatherData != null)) {
            savedInstanceState.putSerializable("ForecastWeatherData", forecastWeatherData);
            savedInstanceState.putSerializable("CurrentWeatherData", currentWeatherData);
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

            final WeatherTask weatherTask = new WeatherTask(HTTPweatherClient, weatherService);


            weatherTask.execute(geocodingData);
        }
    }

    @Override
    public void getWeatherByDay(final int chosenDay) {
        // Nothing to do.
    }

    public void updateForecastWeatherData(final WeatherData weatherData) {
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

        final CurrentWeatherData currentWeatherData = weatherData.getCurrentWeatherData();

        String formatMaxTemp;
        String formatMinTemp;

        if (currentWeatherData.getMain().getTemp_max() != null) {
            double maxTemp = (Double) currentWeatherData.getMain().getTemp_max();
            maxTemp = maxTemp - tempUnits;
            formatMaxTemp = tempFormatter.format(maxTemp) + symbol;
        } else {
            formatMaxTemp = "no data";
        }
        if (currentWeatherData.getMain().getTemp_min() != null) {
            double minTemp = (Double) currentWeatherData.getMain().getTemp_min();
            minTemp = minTemp - tempUnits;
            formatMinTemp = tempFormatter.format(minTemp) + symbol;
        } else {
            formatMinTemp = "no data";
        }

        entries.add(new WeatherOverviewEntry(dateFormat.format(currentWeatherData.getDate()),
                formatMaxTemp, formatMinTemp, picture));


        final ForecastWeatherData forecastWeatherData = weatherData.getForecastWeatherData();

        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentWeatherData.getDate());

        final int forecastSize = forecastWeatherData.getList().size();
        final int chosenForecastDays = 14;
        final int index = forecastSize < chosenForecastDays ? forecastSize : chosenForecastDays;
        for (int i = 0; i < index; i++) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);

            final de.example.exampletdd.model.forecastweather.List forecast = forecastWeatherData
                    .getList().get(i);

            if (forecast.getTemp().getMax() != null) {
                double maxTemp = (Double) forecast.getTemp().getMax();
                maxTemp = maxTemp - tempUnits;
                formatMaxTemp = tempFormatter.format(maxTemp) + symbol;
            } else {
                formatMaxTemp = "no data";
            }

            if (forecast.getTemp().getMin() != null) {
                double minTemp = (Double) forecast.getTemp().getMin();
                minTemp = minTemp - tempUnits;
                formatMinTemp = tempFormatter.format(minTemp) + symbol;
            } else {
                formatMinTemp = "no data";
            }

            final Date day = calendar.getTime();
            entries.add(new WeatherOverviewEntry(dateFormat.format(day), formatMaxTemp,
                    formatMinTemp, picture));
        }

        final int leftDays = chosenForecastDays - index;
        for (int i = 0; i < leftDays; i++) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            final Date day = calendar.getTime();
            entries.add(new WeatherOverviewEntry(dateFormat.format(day), "no data", "no data", picture));
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
        final CurrentWeatherData currentWeatherData = this.mWeatherServicePersistenceFile
                .getCurrentWeatherData();
        if ((forecastWeatherData != null) && (currentWeatherData != null)) {
            final WeatherData weatherData = new WeatherData(forecastWeatherData, currentWeatherData);
            this.updateForecastWeatherData(weatherData);
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

    public class WeatherTask extends AsyncTask<Object, Void, WeatherData> {
        private static final String TAG = "WeatherTask";
        private final CustomHTTPClient weatherHTTPClient;
        private final WeatherServiceParser weatherService;
        private final DialogFragment newFragment;

        public WeatherTask(final CustomHTTPClient weatherHTTPClient,
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
        protected WeatherData doInBackground(final Object... params) {
            WeatherData weatherData = null;

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
        protected void onCancelled(final WeatherData weatherData) {
            this.weatherHTTPClient.close();

            final DialogFragment newFragment = ErrorDialogFragment
                    .newInstance(R.string.error_dialog_connection_tiemout);
            newFragment.show(WeatherInformationOverviewFragment.this.getFragmentManager(), "errorDialog");
        }

        private WeatherData doInBackgroundThrowable(final Object... params)
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


            final Calendar now = Calendar.getInstance();
            final String APIVersion = WeatherInformationOverviewFragment.this.getResources()
                    .getString(R.string.api_version);
            // 2. Forecast
            String urlAPI = WeatherInformationOverviewFragment.this.getResources()
                    .getString(R.string.uri_api_weather_forecast);
            String url = this.weatherService.createURIAPIForecastWeather(urlAPI, APIVersion,
                    geocodingData.getLatitude(), geocodingData.getLongitude(), "14");
            String jsonData = this.weatherHTTPClient.retrieveDataAsString(new URL(url));
            final ForecastWeatherData forecastWeatherData = this.weatherService
                    .retrieveForecastWeatherDataFromJPOS(jsonData);
            final Iterator<de.example.exampletdd.model.forecastweather.List> iterator =
                    forecastWeatherData.getList().iterator();
            while (iterator.hasNext()) {
                final de.example.exampletdd.model.forecastweather.List forecast = iterator.next();

                final Long forecastUNIXDate = (Long) forecast.getDt();
                final Calendar forecastCalendar = Calendar.getInstance();
                forecastCalendar.setTimeInMillis(forecastUNIXDate * 1000L);
                if (now.compareTo(forecastCalendar) == 1) {
                    iterator.remove();
                }
            }


            // 3. Today
            urlAPI = WeatherInformationOverviewFragment.this.getResources().getString(
                    R.string.uri_api_weather_today);
            url = this.weatherService.createURIAPITodayWeather(urlAPI, APIVersion,
                    geocodingData.getLatitude(), geocodingData.getLongitude());
            jsonData = this.weatherHTTPClient.retrieveDataAsString(new URL(url));
            final CurrentWeatherData currentWeatherData = this.weatherService
                    .retrieveCurrentWeatherDataFromJPOS(jsonData);
            currentWeatherData.setDate(now.getTime());

            final WeatherData weatherData = new WeatherData(forecastWeatherData, currentWeatherData);

            return weatherData;
        }

        private void onPostExecuteThrowable(final WeatherData weatherData)
                throws FileNotFoundException, IOException {
            WeatherInformationOverviewFragment.this.mWeatherServicePersistenceFile
            .storeForecastWeatherData(weatherData.getForecastWeatherData());
            WeatherInformationOverviewFragment.this.mWeatherServicePersistenceFile
            .storeCurrentWeatherData(weatherData.getCurrentWeatherData());

            WeatherInformationOverviewFragment.this.updateForecastWeatherData(weatherData);
        }
    }

    private class WeatherData {
        private final ForecastWeatherData forecastWeatherData;
        private final CurrentWeatherData currentWeatherData;

        private WeatherData(final ForecastWeatherData forecastWeatherData, final CurrentWeatherData currentWeatherData) {
            this.forecastWeatherData = forecastWeatherData;
            this.currentWeatherData = currentWeatherData;
        }

        private ForecastWeatherData getForecastWeatherData() {
            return this.forecastWeatherData;
        }

        private CurrentWeatherData getCurrentWeatherData() {
            return this.currentWeatherData;
        }
    }
}
