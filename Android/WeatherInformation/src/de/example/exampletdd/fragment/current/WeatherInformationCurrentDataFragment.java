package de.example.exampletdd.fragment.current;

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
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ListView;

import com.fasterxml.jackson.core.JsonParseException;

import de.example.exampletdd.R;
import de.example.exampletdd.fragment.ErrorDialogFragment;
import de.example.exampletdd.fragment.ProgressDialogFragment;
import de.example.exampletdd.fragment.specific.WeatherSpecificDataAdapter;
import de.example.exampletdd.fragment.specific.WeatherSpecificDataEntry;
import de.example.exampletdd.httpclient.CustomHTTPClient;
import de.example.exampletdd.model.GeocodingData;
import de.example.exampletdd.model.currentweather.CurrentWeatherData;
import de.example.exampletdd.parser.IJPOSWeatherParser;
import de.example.exampletdd.parser.JPOSWeatherParser;
import de.example.exampletdd.service.WeatherServiceParser;
import de.example.exampletdd.service.WeatherServicePersistenceFile;

public class WeatherInformationCurrentDataFragment extends ListFragment {
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
        this.mWeatherServicePersistenceFile.removeCurrentWeatherData();
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final ListView listWeatherView = this.getListView();
        listWeatherView.setChoiceMode(ListView.CHOICE_MODE_NONE);

        if (savedInstanceState != null) {
            // Restore state
            final CurrentWeatherData currentWeatherData = (CurrentWeatherData) savedInstanceState
                    .getSerializable("CurrentWeatherData");

            if (currentWeatherData != null) {
                try {
                    this.mWeatherServicePersistenceFile.storeCurrentWeatherData(currentWeatherData);
                } catch (final IOException e) {
                    final DialogFragment newFragment = ErrorDialogFragment
                            .newInstance(R.string.error_dialog_generic_error);
                    newFragment.show(this.getFragmentManager(), "errorDialog");
                }
            }
        }

        this.setHasOptionsMenu(false);

        final WeatherSpecificDataAdapter adapter = new WeatherSpecificDataAdapter(
                this.getActivity(), R.layout.weather_data_entry_list);


        this.setEmptyText("No data available");

        this.setListAdapter(adapter);
        this.setListShown(true);
        this.setListShownNoAnimation(true);

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

        // 2. Try to restore old information
        final CurrentWeatherData currentWeatherData = this.mWeatherServicePersistenceFile
                .getCurrentWeatherData();
        if (currentWeatherData != null) {
            this.updateCurrentWeatherData(currentWeatherData);
        } else {
            // 2.1 Empty list by default
            final WeatherSpecificDataAdapter adapter = new WeatherSpecificDataAdapter(
                    this.getActivity(), R.layout.weather_data_entry_list);
            this.setListAdapter(adapter);

            // 2.2. Try to update weather data on display with remote
            // information.
            this.getRemoteCurrentWeatherInformation();
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

    @Override
    public void onSaveInstanceState(final Bundle savedInstanceState) {

        // Save state
        final CurrentWeatherData currentWeatherData = this.mWeatherServicePersistenceFile
                .getCurrentWeatherData();

        if (currentWeatherData != null) {
            savedInstanceState.putSerializable("CurrentWeatherData", currentWeatherData);
        }

        super.onSaveInstanceState(savedInstanceState);
    }


    public void updateCurrentWeatherData(final CurrentWeatherData currentWeatherData) {
        final DecimalFormat tempFormatter = (DecimalFormat) NumberFormat.getNumberInstance(Locale
                .getDefault());
        tempFormatter.applyPattern("#####.#####");
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss Z",
                Locale.getDefault());

        final double tempUnits = this.mIsFahrenheit ? 0 : 273.15;

        final List<WeatherSpecificDataEntry> entries = this.createEmptyEntriesList();

        final WeatherSpecificDataAdapter adapter = new WeatherSpecificDataAdapter(
                this.getActivity(), R.layout.weather_data_entry_list);

        if (currentWeatherData.getWeather().size() > 0) {
            entries.set(0,
                    new WeatherSpecificDataEntry(this.getString(R.string.text_field_description),
                            currentWeatherData.getWeather().get(0).getDescription()));
        }

        if (currentWeatherData.getMain().getTemp() != null) {
            double conversion = (Double) currentWeatherData.getMain().getTemp();
            conversion = conversion - tempUnits;
            entries.set(1, new WeatherSpecificDataEntry(this.getString(R.string.text_field_tem),
                    tempFormatter.format(conversion)));
        }

        if (currentWeatherData.getMain().getTemp_max() != null) {
            double conversion = (Double) currentWeatherData.getMain().getTemp_max();
            conversion = conversion - tempUnits;
            entries.set(2, new WeatherSpecificDataEntry(
                    this.getString(R.string.text_field_tem_max), tempFormatter.format(conversion)));
        }

        if (currentWeatherData.getMain().getTemp_max() != null) {
            double conversion = (Double) currentWeatherData.getMain().getTemp_min();
            conversion = conversion - tempUnits;
            entries.set(3, new WeatherSpecificDataEntry(
                    this.getString(R.string.text_field_tem_min), tempFormatter.format(conversion)));
        }

        if (currentWeatherData.getSys().getSunrise() != null) {
            final long unixTime = (Long) currentWeatherData.getSys().getSunrise();
            final Date unixDate = new Date(unixTime * 1000L);
            final String dateFormatUnix = dateFormat.format(unixDate);
            entries.set(4,
                    new WeatherSpecificDataEntry(this.getString(R.string.text_field_sun_rise),
                            dateFormatUnix));
        }

        if (currentWeatherData.getSys().getSunset() != null) {
            final long unixTime = (Long) currentWeatherData.getSys().getSunset();
            final Date unixDate = new Date(unixTime * 1000L);
            final String dateFormatUnix = dateFormat.format(unixDate);
            entries.set(5, new WeatherSpecificDataEntry(
                    this.getString(R.string.text_field_sun_set), dateFormatUnix));
        }

        if (currentWeatherData.getClouds().getAll() != null) {
            final double cloudiness = (Double) currentWeatherData.getClouds().getAll();
            entries.set(6,
                    new WeatherSpecificDataEntry(this.getString(R.string.text_field_cloudiness),
                            tempFormatter.format(cloudiness)));
        }

        if (currentWeatherData.getIconData() != null) {
            final Bitmap icon = BitmapFactory.decodeByteArray(currentWeatherData.getIconData(), 0,
                    currentWeatherData.getIconData().length);
            final ImageView imageIcon = (ImageView) this.getActivity().findViewById(
                    R.id.weather_picture);
            imageIcon.setImageBitmap(icon);
        }

        adapter.addAll(entries);
        this.setListAdapter(adapter);
    }

    public class CurrentWeatherTask extends AsyncTask<Object, Void, CurrentWeatherData> {
        private static final String TAG = "WeatherTask";
        private final CustomHTTPClient weatherHTTPClient;
        private final WeatherServiceParser weatherService;
        private final DialogFragment newFragment;

        public CurrentWeatherTask(final CustomHTTPClient weatherHTTPClient,
                final WeatherServiceParser weatherService) {
            this.weatherHTTPClient = weatherHTTPClient;
            this.weatherService = weatherService;
            this.newFragment = ProgressDialogFragment.newInstance(
                    R.string.progress_dialog_get_remote_data,
                    WeatherInformationCurrentDataFragment.this
                    .getString(R.string.progress_dialog_generic_message));
        }

        @Override
        protected void onPreExecute() {
            this.newFragment.show(WeatherInformationCurrentDataFragment.this.getActivity()
                    .getFragmentManager(), "progressDialog");
        }

        @Override
        protected CurrentWeatherData doInBackground(final Object... params) {
            CurrentWeatherData currentWeatherData = null;

            try {
                currentWeatherData = this.doInBackgroundThrowable(params);
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

            return currentWeatherData;
        }

        @Override
        protected void onPostExecute(final CurrentWeatherData currentWeatherData) {
            this.weatherHTTPClient.close();

            this.newFragment.dismiss();

            if (currentWeatherData != null) {
                try {
                    this.onPostExecuteThrowable(currentWeatherData);
                } catch (final IOException e) {
                    Log.e(TAG, "WeatherTask onPostExecute exception: ", e);
                    final DialogFragment newFragment = ErrorDialogFragment
                            .newInstance(R.string.error_dialog_generic_error);
                    newFragment.show(
                            WeatherInformationCurrentDataFragment.this.getFragmentManager(),
                            "errorDialog");
                }
            } else {
                final DialogFragment newFragment = ErrorDialogFragment
                        .newInstance(R.string.error_dialog_generic_error);
                newFragment.show(WeatherInformationCurrentDataFragment.this.getFragmentManager(),
                        "errorDialog");
            }
        }

        @Override
        protected void onCancelled(final CurrentWeatherData currentWeatherData) {
            this.weatherHTTPClient.close();

            final DialogFragment newFragment = ErrorDialogFragment
                    .newInstance(R.string.error_dialog_connection_tiemout);
            newFragment.show(WeatherInformationCurrentDataFragment.this.getFragmentManager(),
                    "errorDialog");
        }

        private CurrentWeatherData doInBackgroundThrowable(final Object... params)
                throws ClientProtocolException, MalformedURLException, URISyntaxException,
                JsonParseException, IOException {
            // final SharedPreferences sharedPreferences = PreferenceManager
            // .getDefaultSharedPreferences(WeatherInformationCurrentDataFragment.this
            // .getActivity());
            //
            // final String keyPreference =
            // WeatherInformationCurrentDataFragment.this
            // .getActivity().getString(
            // R.string.weather_preferences_language_key);
            // final String languagePreferenceValue =
            // sharedPreferences.getString(keyPreference, "");

            // 1. Coordinates
            final GeocodingData geocodingData = (GeocodingData) params[0];

            final String APIVersion = WeatherInformationCurrentDataFragment.this.getResources()
                    .getString(R.string.api_version);

            // 2. Today
            final String urlAPI = WeatherInformationCurrentDataFragment.this.getResources()
                    .getString(R.string.uri_api_weather_today);
            final String url = this.weatherService.createURIAPITodayWeather(urlAPI, APIVersion,
                    geocodingData.getLatitude(), geocodingData.getLongitude());
            final String jsonData = this.weatherHTTPClient.retrieveDataAsString(new URL(url));
            final CurrentWeatherData currentWeatherData = this.weatherService
                    .retrieveCurrentWeatherDataFromJPOS(jsonData);
            final Calendar now = Calendar.getInstance();
            currentWeatherData.setDate(now.getTime());


            return currentWeatherData;
        }

        private void onPostExecuteThrowable(final CurrentWeatherData currentWeatherData)
                throws FileNotFoundException, IOException {

            WeatherInformationCurrentDataFragment.this.mWeatherServicePersistenceFile
            .storeCurrentWeatherData(currentWeatherData);

            WeatherInformationCurrentDataFragment.this.updateCurrentWeatherData(currentWeatherData);
        }
    }

    private void getRemoteCurrentWeatherInformation() {

        final GeocodingData geocodingData = this.mWeatherServicePersistenceFile.getGeocodingData();

        if (geocodingData != null) {
            final IJPOSWeatherParser JPOSWeatherParser = new JPOSWeatherParser();
            final WeatherServiceParser weatherService = new WeatherServiceParser(JPOSWeatherParser);
            final AndroidHttpClient httpClient = AndroidHttpClient
                    .newInstance("Android Weather Information Agent");
            final CustomHTTPClient HTTPweatherClient = new CustomHTTPClient(httpClient);

            final CurrentWeatherTask weatherTask = new CurrentWeatherTask(HTTPweatherClient,
                    weatherService);

            weatherTask.execute(geocodingData);
        }
    }

    private List<WeatherSpecificDataEntry> createEmptyEntriesList() {
        final List<WeatherSpecificDataEntry> entries = new ArrayList<WeatherSpecificDataEntry>();
        entries.add(new WeatherSpecificDataEntry(this.getString(R.string.text_field_description),
                null));
        entries.add(new WeatherSpecificDataEntry(this.getString(R.string.text_field_tem), null));
        entries.add(new WeatherSpecificDataEntry(this.getString(R.string.text_field_tem_max), null));
        entries.add(new WeatherSpecificDataEntry(this.getString(R.string.text_field_tem_min), null));
        entries.add(new WeatherSpecificDataEntry(this.getString(R.string.text_field_sun_rise), null));
        entries.add(new WeatherSpecificDataEntry(this.getString(R.string.text_field_sun_set), null));
        entries.add(new WeatherSpecificDataEntry(this.getString(R.string.text_field_cloudiness),
                null));
        entries.add(new WeatherSpecificDataEntry(this.getString(R.string.text_field_rain_time),
                null));
        entries.add(new WeatherSpecificDataEntry(this.getString(R.string.text_field_rain_amount),
                null));
        entries.add(new WeatherSpecificDataEntry(this.getString(R.string.text_field_wind_speed),
                null));
        entries.add(new WeatherSpecificDataEntry(this.getString(R.string.text_field_humidity), null));

        return entries;
    }
}
