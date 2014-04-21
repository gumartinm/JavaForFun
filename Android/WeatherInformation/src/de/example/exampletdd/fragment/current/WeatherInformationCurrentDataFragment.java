package de.example.exampletdd.fragment.current;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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
import android.widget.ListView;

import com.fasterxml.jackson.core.JsonParseException;

import de.example.exampletdd.R;
import de.example.exampletdd.fragment.ErrorDialogFragment;
import de.example.exampletdd.fragment.ProgressDialogFragment;
import de.example.exampletdd.fragment.overview.IconsList;
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

        this.setEmptyText("No data available");

        this.setListShownNoAnimation(false);
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
            // 3. Try to update weather data on display with remote
            this.getRemoteCurrentWeatherInformation();
        }
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
        final DecimalFormat tempFormatter = (DecimalFormat) NumberFormat.getNumberInstance(Locale.US);
        tempFormatter.applyPattern("#####.#####");
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss Z", Locale.US);

        final double tempUnits = this.mIsFahrenheit ? 0 : 273.15;
        final String symbol = this.mIsFahrenheit ? "ºF" : "ºC";

        final int[] layouts = new int[3];
        layouts[0] = R.layout.weather_current_data_entry_first;
        layouts[1] = R.layout.weather_current_data_entry_second;
        layouts[2] = R.layout.weather_current_data_entry_fifth;
        final WeatherCurrentDataAdapter adapter = new WeatherCurrentDataAdapter(this.getActivity(),
                layouts);


        String tempMax = "";
        if (currentWeatherData.getMain().getTemp_max() != null) {
            double conversion = (Double) currentWeatherData.getMain().getTemp_max();
            conversion = conversion - tempUnits;
            tempMax = tempFormatter.format(conversion) + symbol;
        }
        String tempMin = "";
        if (currentWeatherData.getMain().getTemp_min() != null) {
            double conversion = (Double) currentWeatherData.getMain().getTemp_min();
            conversion = conversion - tempUnits;
            tempMin = tempFormatter.format(conversion) + symbol;
        }
        Bitmap picture;
        if ((currentWeatherData.getWeather().size() > 0)
                && (currentWeatherData.getWeather().get(0).getIcon() != null)
                && (IconsList.getIcon(currentWeatherData.getWeather().get(0).getIcon()) != null)) {
            final String icon = currentWeatherData.getWeather().get(0).getIcon();
            picture = BitmapFactory.decodeResource(this.getResources(), IconsList.getIcon(icon)
                    .getResourceDrawable());
        } else {
            picture = BitmapFactory.decodeResource(this.getResources(),
                    R.drawable.weather_severe_alert);
        }
        final WeatherCurrentDataEntryFirst entryFirst = new WeatherCurrentDataEntryFirst(tempMax,
                tempMin, picture);
        adapter.add(entryFirst);

        String description = "no description available";
        if (currentWeatherData.getWeather().size() > 0) {
            description = currentWeatherData.getWeather().get(0).getDescription();
        }
        final WeatherCurrentDataEntrySecond entrySecond = new WeatherCurrentDataEntrySecond(
                description);
        adapter.add(entrySecond);

        String humidityValue = "";
        if ((currentWeatherData.getMain() != null)
                && (currentWeatherData.getMain().getHumidity() != null)) {
            final double conversion = (Double) currentWeatherData.getMain().getHumidity();
            humidityValue = tempFormatter.format(conversion);
        }
        String pressureValue = "";
        if ((currentWeatherData.getMain() != null)
                && (currentWeatherData.getMain().getPressure() != null)) {
            final double conversion = (Double) currentWeatherData.getMain().getPressure();
            pressureValue = tempFormatter.format(conversion);
        }
        String windValue = "";
        if ((currentWeatherData.getWind() != null)
                && (currentWeatherData.getWind().getSpeed() != null)) {
            final double conversion = (Double) currentWeatherData.getWind().getSpeed();
            windValue = tempFormatter.format(conversion);
        }
        String rainValue = "";
        if ((currentWeatherData.getRain() != null)
                && (currentWeatherData.getRain().get3h() != null)) {
            final double conversion = (Double) currentWeatherData.getRain().get3h();
            rainValue = tempFormatter.format(conversion);
        }
        String cloudsValue = "";
        if ((currentWeatherData.getClouds() != null)
                && (currentWeatherData.getClouds().getAll() != null)) {
            final double conversion = (Double) currentWeatherData.getClouds().getAll();
            cloudsValue = tempFormatter.format(conversion);
        }
        String snowValue = "";
        if ((currentWeatherData.getSnow() != null)
                && (currentWeatherData.getSnow().get3h() != null)) {
            final double conversion = (Double) currentWeatherData.getSnow().get3h();
            snowValue = tempFormatter.format(conversion);
        }
        String feelsLike = "";
        if (currentWeatherData.getMain().getTemp() != null) {
            double conversion = (Double) currentWeatherData.getMain().getTemp();
            conversion = conversion - tempUnits;
            feelsLike = tempFormatter.format(conversion);
        }
        String sunRiseTime = "";
        if (currentWeatherData.getSys().getSunrise() != null) {
            final long unixTime = (Long) currentWeatherData.getSys().getSunrise();
            final Date unixDate = new Date(unixTime * 1000L);
            sunRiseTime = dateFormat.format(unixDate);
        }
        String sunSetTime = "";
        if (currentWeatherData.getSys().getSunset() != null) {
            final long unixTime = (Long) currentWeatherData.getSys().getSunset();
            final Date unixDate = new Date(unixTime * 1000L);
            sunSetTime = dateFormat.format(unixDate);
        }
        final WeatherCurrentDataEntryFifth entryFifth = new WeatherCurrentDataEntryFifth(
                sunRiseTime, sunSetTime, humidityValue, pressureValue, windValue, rainValue,
                feelsLike, symbol, snowValue, cloudsValue);
        adapter.add(entryFifth);


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
                    WeatherInformationCurrentDataFragment.this.setListShown(true);
                    Log.e(TAG, "WeatherTask onPostExecute exception: ", e);
                    final DialogFragment newFragment = ErrorDialogFragment
                            .newInstance(R.string.error_dialog_generic_error);
                    newFragment.show(
                            WeatherInformationCurrentDataFragment.this.getFragmentManager(),
                            "errorDialog");
                }
            } else {
                WeatherInformationCurrentDataFragment.this.setListShown(true);
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
}
