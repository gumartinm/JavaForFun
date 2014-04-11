package de.example.exampletdd.fragment.overview;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StreamCorruptedException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;

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
import de.example.exampletdd.R;
import de.example.exampletdd.activityinterface.GetWeather;
import de.example.exampletdd.fragment.ErrorDialogFragment;
import de.example.exampletdd.fragment.ProgressDialogFragment;
import de.example.exampletdd.fragment.specific.WeatherInformationSpecificDataFragment;
import de.example.exampletdd.httpclient.WeatherHTTPClient;
import de.example.exampletdd.model.GeocodingData;
import de.example.exampletdd.model.WeatherData;
import de.example.exampletdd.parser.IJPOSWeatherParser;
import de.example.exampletdd.parser.JPOSWeatherParser;
import de.example.exampletdd.service.WeatherService;
import de.example.exampletdd.service.WeatherServicePersistenceFile;

public class WeatherInformationOverviewFragment extends ListFragment implements GetWeather {
    private static final String TAG = "WeatherInformationOverviewFragment";
    private boolean mIsFahrenheit;
    private String mLanguage;
    private WeatherServicePersistenceFile mWeatherServicePersistenceFile;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this.getActivity());
        final String keyPreference = this.getResources().getString(
                R.string.weather_preferences_language_key);
        this.mLanguage = sharedPreferences.getString(
                keyPreference, "");

        this.mWeatherServicePersistenceFile = new WeatherServicePersistenceFile(
                this.getActivity());
        this.mWeatherServicePersistenceFile.removeWeatherData();
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final ListView listWeatherView = this.getListView();

        listWeatherView.setChoiceMode(ListView.CHOICE_MODE_NONE);

        if (savedInstanceState != null) {
            // Restore state
            final WeatherData weatherData = (WeatherData) savedInstanceState
                    .getSerializable("weatherData");
            try {
                this.mWeatherServicePersistenceFile
                .storeWeatherData(weatherData);
            } catch (final IOException e) {
                final DialogFragment newFragment = ErrorDialogFragment
                        .newInstance(R.string.error_dialog_generic_error);
                newFragment.show(this.getFragmentManager(), "errorDialog");
            }
        }

        this.setHasOptionsMenu(false);

        final WeatherOverviewAdapter adapter = new WeatherOverviewAdapter(
                this.getActivity(), R.layout.weather_main_entry_list);

        final Collection<WeatherOverviewEntry> entries = this
                .createEmptyEntriesList();

        this.setListAdapter(null);
        adapter.addAll(entries);
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
            WeatherInformationOverviewFragment.this.getActivity().startActivity(intent);
        } else {
            // tablet layout
            fragment.getWeather();
        }
    }

    @Override
    public void onSaveInstanceState(final Bundle savedInstanceState) {

        // Save state
        WeatherData weatherData = null;
        try {
            weatherData = this.mWeatherServicePersistenceFile.getWeatherData();
        } catch (final StreamCorruptedException e) {
            Log.e(TAG, "onResume exception: ", e);
        } catch (final FileNotFoundException e) {
            Log.e(TAG, "onResume exception: ", e);
        } catch (final IOException e) {
            Log.e(TAG, "onResume exception: ", e);
        } catch (final ClassNotFoundException e) {
            Log.e(TAG, "onResume exception: ", e);
        }

        if (weatherData != null) {
            savedInstanceState.putSerializable("weatherData", weatherData);
        }

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void getWeather() {

        GeocodingData geocodingData = null;
        try {
            geocodingData = this.mWeatherServicePersistenceFile
                    .getGeocodingData();
        } catch (final StreamCorruptedException e) {
            Log.e(TAG, "onResume exception: ", e);
        } catch (final FileNotFoundException e) {
            Log.e(TAG, "onResume exception: ", e);
        } catch (final IOException e) {
            Log.e(TAG, "onResume exception: ", e);
        } catch (final ClassNotFoundException e) {
            Log.e(TAG, "onResume exception: ", e);
        }

        if (geocodingData != null) {
            final IJPOSWeatherParser JPOSWeatherParser = new JPOSWeatherParser();
            final WeatherService weatherService = new WeatherService(
                    JPOSWeatherParser);
            final AndroidHttpClient httpClient = AndroidHttpClient
                    .newInstance("Android Weather Information Agent");
            final WeatherHTTPClient HTTPweatherClient = new WeatherHTTPClient(
                    httpClient);

            final WeatherTask weatherTask = new WeatherTask(HTTPweatherClient, weatherService);


            weatherTask.execute(geocodingData);
        }
    }

    public void updateWeatherData(final WeatherData weatherData) {
        final List<WeatherOverviewEntry> entries = this.createEmptyEntriesList();
        final WeatherOverviewAdapter adapter = new WeatherOverviewAdapter(this.getActivity(),
                R.layout.weather_main_entry_list);

        // Bitmap picture = null;
        //
        // if (weatherData.getWeather().getIcon() != null) {
        // picture= BitmapFactory.decodeByteArray(
        // weatherData.getIconData(), 0,
        // weatherData.getIconData().length);
        // }

        final Bitmap picture = BitmapFactory.decodeResource(
                this.getResources(), R.drawable.ic_02d);
        final DecimalFormat tempFormatter = (DecimalFormat) NumberFormat.getNumberInstance(Locale.getDefault());
        tempFormatter.applyPattern("#####.##");
        final SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM d", Locale.getDefault());
        final double tempUnits = this.mIsFahrenheit ? 0 : 273.15;
        double temp = weatherData.getMain().getTemp();
        temp = temp - tempUnits;
        double maxTemp = weatherData.getMain().getMaxTemp();
        maxTemp = maxTemp - tempUnits;
        double minTemp = weatherData.getMain().getMinTemp();
        minTemp = minTemp - tempUnits;

        final Calendar now = Calendar.getInstance();
        if (weatherData.getWeather() != null) {
            for (int i = 0; i<15; i++) {
                final Date day = now.getTime();
                entries.set(i, new WeatherOverviewEntry(dateFormat.format(day),
                        tempFormatter.format(temp), tempFormatter
                        .format(maxTemp), tempFormatter
                        .format(minTemp), picture));
                now.add(Calendar.DAY_OF_MONTH, 1);
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
        String keyPreference = this.getResources().getString(
                R.string.weather_preferences_units_key);
        final String unitsPreferenceValue = sharedPreferences.getString(keyPreference, "");
        final String celsius = this.getResources().getString(
                R.string.weather_preferences_units_celsius);
        if (unitsPreferenceValue.equals(celsius)) {
            this.mIsFahrenheit = false;
        } else {
            this.mIsFahrenheit = true;
        }


        // 2. Update current data on display.
        WeatherData weatherData = null;
        try {
            weatherData = this.mWeatherServicePersistenceFile.getWeatherData();
        } catch (final StreamCorruptedException e) {
            Log.e(TAG, "onResume exception: ", e);
        } catch (final FileNotFoundException e) {
            Log.e(TAG, "onResume exception: ", e);
        } catch (final IOException e) {
            Log.e(TAG, "onResume exception: ", e);
        } catch (final ClassNotFoundException e) {
            Log.e(TAG, "onResume exception: ", e);
        }
        if (weatherData != null) {
            this.updateWeatherData(weatherData);
        }


        // 3. If language changed, try to retrieve new data for new language
        // (new strings with the chosen language)
        keyPreference = this.getResources().getString(
                R.string.weather_preferences_language_key);
        final String languagePreferenceValue = sharedPreferences.getString(
                keyPreference, "");
        if (!languagePreferenceValue.equals(this.mLanguage)) {
            this.mLanguage = languagePreferenceValue;
            this.getWeather();
        }
    }

    public class WeatherTask extends AsyncTask<Object, Void, WeatherData> {
        private static final String TAG = "WeatherTask";
        private final WeatherHTTPClient weatherHTTPClient;
        private final WeatherService weatherService;
        private final DialogFragment newFragment;

        public WeatherTask(final WeatherHTTPClient weatherHTTPClient,
                final WeatherService weatherService) {
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
            } catch (final IOException e) {
                // logger infrastructure swallows UnknownHostException :/
                Log.e(TAG, "doInBackground exception: " + e.getMessage(), e);
            } catch (final JSONException e) {
                Log.e(TAG, "doInBackground exception: ", e);
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
                URISyntaxException, IOException, JSONException {
            final SharedPreferences sharedPreferences = PreferenceManager
                    .getDefaultSharedPreferences(WeatherInformationOverviewFragment.this
                            .getActivity());

            final String keyPreference = WeatherInformationOverviewFragment.this
                    .getActivity().getString(
                            R.string.weather_preferences_language_key);
            final String languagePreferenceValue = sharedPreferences.getString(keyPreference, "");

            final GeocodingData geocodingData = (GeocodingData) params[0];
            final String urlAPICoord = WeatherInformationOverviewFragment.this.getResources()
                    .getString(R.string.uri_api_coord);
            final String APIVersion = WeatherInformationOverviewFragment.this.getResources()
                    .getString(R.string.api_version);
            String url = this.weatherService.createURIAPICoord(geocodingData.getLatitude(),
                    geocodingData.getLongitude(), urlAPICoord, APIVersion, languagePreferenceValue);


            final String jsonData = this.weatherHTTPClient.retrieveJSONDataFromAPI(new URL(url));


            final WeatherData weatherData = this.weatherService.retrieveDataFromJPOS(jsonData);


            final String icon = weatherData.getWeather().getIcon();
            final String urlAPIicon = WeatherInformationOverviewFragment.this
                    .getResources().getString(R.string.uri_api_icon);
            url = this.weatherService.createURIAPIicon(icon, urlAPIicon);
            final byte[] iconData = this.weatherHTTPClient
                    .retrieveDataFromAPI(new URL(url)).toByteArray();
            weatherData.setIconData(iconData);


            return weatherData;
        }

        private void onPostExecuteThrowable(final WeatherData weatherData)
                throws FileNotFoundException, IOException {
            WeatherInformationOverviewFragment.this.mWeatherServicePersistenceFile
                    .storeWeatherData(weatherData);

            WeatherInformationOverviewFragment.this.updateWeatherData(weatherData);
        }
    }

    private List<WeatherOverviewEntry> createEmptyEntriesList() {
        final List<WeatherOverviewEntry> entries = new ArrayList<WeatherOverviewEntry>();
        final SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM d", Locale.getDefault());

        final Calendar now = Calendar.getInstance();
        for (int i = 0; i<15; i++) {
            final Date day = now.getTime();
            entries.add(i, new WeatherOverviewEntry(dateFormat.format(day),
                    null, null, null, null));
            now.add(Calendar.DAY_OF_MONTH, 1);
        }

        return entries;
    }
}
