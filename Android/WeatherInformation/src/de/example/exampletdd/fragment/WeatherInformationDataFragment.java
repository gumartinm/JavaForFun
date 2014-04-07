package de.example.exampletdd.fragment;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.StreamCorruptedException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import de.example.exampletdd.R;
import de.example.exampletdd.activityinterface.ErrorMessage;
import de.example.exampletdd.activityinterface.GetWeather;
import de.example.exampletdd.httpclient.WeatherHTTPClient;
import de.example.exampletdd.model.GeocodingData;
import de.example.exampletdd.model.WeatherData;
import de.example.exampletdd.parser.IJPOSWeatherParser;
import de.example.exampletdd.parser.JPOSWeatherParser;
import de.example.exampletdd.service.WeatherService;

public class WeatherInformationDataFragment extends Fragment implements GetWeather {
    private static final String WEATHER_DATA_FILE = "weatherdata.file";
    private static final String WEATHER_GEOCODING_FILE = "weathergeocoding.file";
    private static final String TAG = "WeatherInformationDataFragment";
    private boolean mIsFahrenheit;
    private String mLanguage;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.getActivity().deleteFile(WEATHER_DATA_FILE);

        final SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this.getActivity());
        final String keyPreference = this.getResources().getString(
                R.string.weather_preferences_language_key);
        this.mLanguage = sharedPreferences.getString(
                keyPreference, "");
    }

    @Override
    public View onCreateView(final LayoutInflater inflater,
            final ViewGroup container, final Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.weather_data_list,
                container, false);

        return rootView;
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final ListView listWeatherView = (ListView) this.getActivity().findViewById(
                R.id.weather_data_list_view);

        final WeatherDataAdapter adapter = new WeatherDataAdapter(this.getActivity(),
                R.layout.weather_data_entry_list);

        final Collection<WeatherDataEntry> entries = this.createEmptyEntriesList();

        adapter.addAll(entries);
        listWeatherView.setAdapter(adapter);
    }

    @Override
    public void getWeather() {

        GeocodingData geocodingData = null;
        try {
            geocodingData = this.restoreGeocodingDataFromFile();
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
        final DecimalFormat tempFormatter = new DecimalFormat("#####.#####");
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss Z");
        final double tempUnits = this.mIsFahrenheit ? 0 : 273.15;

        final List<WeatherDataEntry> entries = this.createEmptyEntriesList();

        final ListView listWeatherView = (ListView) this.getActivity().findViewById(
                R.id.weather_data_list_view);

        final WeatherDataAdapter adapter = new WeatherDataAdapter(this.getActivity(),
                R.layout.weather_data_entry_list);

        if (weatherData.getWeather() != null) {
            entries.set(0, new WeatherDataEntry(this.getString(R.string.text_field_description), weatherData.getWeather()
                    .getDescription()));
            double conversion = weatherData.getMain().getTemp();
            conversion = conversion - tempUnits;
            entries.set(1, new WeatherDataEntry(this.getString(R.string.text_field_tem), tempFormatter.format(conversion)));
            conversion = weatherData.getMain().getMaxTemp();
            conversion = conversion - tempUnits;
            entries.set(2, new WeatherDataEntry(this.getString(R.string.text_field_tem_max), tempFormatter.format(conversion)));
            conversion = weatherData.getMain().getMinTemp();
            conversion = conversion - tempUnits;
            entries.set(3, new WeatherDataEntry(this.getString(R.string.text_field_tem_min), tempFormatter.format(conversion)));
        }

        if (weatherData.getSystem() != null) {
            long unixTime = weatherData.getSystem().getSunRiseTime();
            Date unixDate = new Date(unixTime * 1000L);
            String dateFormatUnix = dateFormat.format(unixDate);
            entries.set(4, new WeatherDataEntry(this.getString(R.string.text_field_sun_rise), dateFormatUnix));

            unixTime = weatherData.getSystem().getSunSetTime();
            unixDate = new Date(unixTime * 1000L);
            dateFormatUnix = dateFormat.format(unixDate);
            entries.set(5, new WeatherDataEntry(this.getString(R.string.text_field_sun_set), dateFormatUnix));
        }

        if (weatherData.getClouds() != null) {
            final double cloudiness = weatherData.getClouds().getCloudiness();
            entries.set(6, new WeatherDataEntry(this.getString(R.string.text_field_cloudiness), tempFormatter.format(cloudiness)));
        }

        if (weatherData.getIconData() != null) {
            final Bitmap icon = BitmapFactory.decodeByteArray(
                    weatherData.getIconData(), 0,
                    weatherData.getIconData().length);
            final ImageView imageIcon = (ImageView) this.getActivity().findViewById(R.id.weather_picture);
            imageIcon.setImageBitmap(icon);
        }



        listWeatherView.setAdapter(null);
        adapter.addAll(entries);
        listWeatherView.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();

        final SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this.getActivity());

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

        keyPreference = this.getResources().getString(
                R.string.weather_preferences_language_key);
        final String languagePreferenceValue = sharedPreferences.getString(
                keyPreference, "");
        if (!languagePreferenceValue.equals(this.mLanguage)) {
            this.mLanguage = languagePreferenceValue;
            this.getWeather();

            return;
        }

        WeatherData weatherData = null;
        try {
            weatherData = this.restoreWeatherDataFromFile();
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
    }

    public class WeatherTask extends AsyncTask<Object, Void, WeatherData> {
        private static final String TAG = "WeatherTask";
        private final WeatherHTTPClient weatherHTTPClient;
        private final WeatherService weatherService;

        public WeatherTask(final WeatherHTTPClient weatherHTTPClient,
                final WeatherService weatherService) {
            this.weatherHTTPClient = weatherHTTPClient;
            this.weatherService = weatherService;
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
                Log.e(TAG, "doInBackground exception: ", e);
            } catch (final JSONException e) {
                Log.e(TAG, "doInBackground exception: ", e);
            } finally {
                this.weatherHTTPClient.close();
            }

            return weatherData;
        }

        @Override
        protected void onPostExecute(final WeatherData weatherData) {
            if (weatherData != null) {
                try {
                    this.onPostExecuteThrowable(weatherData);
                } catch (final IOException e) {
                    ((ErrorMessage) WeatherInformationDataFragment.this
                            .getActivity())
                            .createErrorDialog(R.string.error_dialog_generic_error);
                }
            } else {
                ((ErrorMessage) WeatherInformationDataFragment.this
                        .getActivity())
                        .createErrorDialog(R.string.error_dialog_generic_error);
            }

            this.weatherHTTPClient.close();
        }

        @Override
        protected void onCancelled(final WeatherData weatherData) {
            this.onCancelled();
            ((ErrorMessage) WeatherInformationDataFragment.this.getActivity())
            .createErrorDialog(R.string.error_dialog_connection_tiemout);

            this.weatherHTTPClient.close();
        }

        private WeatherData doInBackgroundThrowable(final Object... params)
                throws ClientProtocolException, MalformedURLException,
                URISyntaxException, IOException, JSONException {
            final SharedPreferences sharedPreferences = PreferenceManager
                    .getDefaultSharedPreferences(WeatherInformationDataFragment.this
                            .getActivity());

            final String keyPreference = WeatherInformationDataFragment.this
                    .getActivity().getString(
                            R.string.weather_preferences_language_key);
            final String languagePreferenceValue = sharedPreferences.getString(keyPreference, "");

            final GeocodingData geocodingData = (GeocodingData) params[0];
            final String urlAPICoord = WeatherInformationDataFragment.this.getResources()
                    .getString(R.string.uri_api_coord);
            final String APIVersion = WeatherInformationDataFragment.this.getResources()
                    .getString(R.string.api_version);
            String url = this.weatherService.createURIAPICoord(geocodingData.getLatitude(),
                    geocodingData.getLongitude(), urlAPICoord, APIVersion, languagePreferenceValue);


            final String jsonData = this.weatherHTTPClient.retrieveJSONDataFromAPI(new URL(url));


            final WeatherData weatherData = this.weatherService.retrieveDataFromJPOS(jsonData);


            final String icon = weatherData.getWeather().getIcon();
            final String urlAPIicon = WeatherInformationDataFragment.this
                    .getResources().getString(R.string.uri_api_icon);
            url = this.weatherService.createURIAPIicon(icon, urlAPIicon);
            final byte[] iconData = this.weatherHTTPClient
                    .retrieveDataFromAPI(new URL(url)).toByteArray();
            weatherData.setIconData(iconData);


            return weatherData;
        }

        private void onPostExecuteThrowable(final WeatherData weatherData)
                throws FileNotFoundException, IOException {
            WeatherInformationDataFragment.this.storeWeatherDataToFile(weatherData);

            WeatherInformationDataFragment.this.updateWeatherData(weatherData);
        }
    }

    private List<WeatherDataEntry> createEmptyEntriesList() {
        final List<WeatherDataEntry> entries = new ArrayList<WeatherDataEntry>();
        entries.add(new WeatherDataEntry(this.getString(R.string.text_field_description), null));
        entries.add(new WeatherDataEntry(this.getString(R.string.text_field_tem), null));
        entries.add(new WeatherDataEntry(this.getString(R.string.text_field_tem_max), null));
        entries.add(new WeatherDataEntry(this.getString(R.string.text_field_tem_min), null));
        entries.add(new WeatherDataEntry(this.getString(R.string.text_field_sun_rise), null));
        entries.add(new WeatherDataEntry(this.getString(R.string.text_field_sun_set), null));
        entries.add(new WeatherDataEntry(this.getString(R.string.text_field_cloudiness), null));
        entries.add(new WeatherDataEntry(this.getString(R.string.text_field_rain_time), null));
        entries.add(new WeatherDataEntry(this.getString(R.string.text_field_rain_amount), null));
        entries.add(new WeatherDataEntry(this.getString(R.string.text_field_wind_speed), null));
        entries.add(new WeatherDataEntry(this.getString(R.string.text_field_humidity), null));

        return entries;
    }

    private void storeWeatherDataToFile(final WeatherData weatherData)
            throws FileNotFoundException, IOException {
        final OutputStream persistenceFile = this.getActivity().openFileOutput(
                WEATHER_DATA_FILE, Context.MODE_PRIVATE);

        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(persistenceFile);

            oos.writeObject(weatherData);
        } finally {
            if (oos != null) {
                oos.close();
            }
        }
    }

    private WeatherData restoreWeatherDataFromFile() throws StreamCorruptedException,
    FileNotFoundException, IOException, ClassNotFoundException {
        final InputStream persistenceFile = this.getActivity().openFileInput(
                WEATHER_DATA_FILE);

        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(persistenceFile);

            return (WeatherData) ois.readObject();
        } finally {
            if (ois != null) {
                ois.close();
            }
        }
    }

    private GeocodingData restoreGeocodingDataFromFile()
            throws StreamCorruptedException, FileNotFoundException,
            IOException, ClassNotFoundException {
        final InputStream persistenceFile = this.getActivity()
                .openFileInput(WEATHER_GEOCODING_FILE);

        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(persistenceFile);

            return (GeocodingData) ois.readObject();
        } finally {
            if (ois != null) {
                ois.close();
            }
        }
    }
}
