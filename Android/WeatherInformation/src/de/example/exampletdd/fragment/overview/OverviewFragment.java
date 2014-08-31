package de.example.exampletdd.fragment.overview;

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

import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.fasterxml.jackson.core.JsonParseException;

import de.example.exampletdd.R;
import de.example.exampletdd.WeatherInformationApplication;
import de.example.exampletdd.fragment.specific.SpecificFragment;
import de.example.exampletdd.httpclient.CustomHTTPClient;
import de.example.exampletdd.model.GeocodingData;
import de.example.exampletdd.model.forecastweather.Forecast;
import de.example.exampletdd.parser.JPOSWeatherParser;
import de.example.exampletdd.service.IconsList;
import de.example.exampletdd.service.ServiceParser;

public class OverviewFragment extends ListFragment {
    private static final String TAG = "OverviewFragment";
    private Parcelable mListState;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final ListView listWeatherView = this.getListView();
        listWeatherView.setChoiceMode(ListView.CHOICE_MODE_NONE);

        if (savedInstanceState != null) {
            // Restore UI state
            final Forecast forecast = (Forecast) savedInstanceState.getSerializable("Forecast");

            // TODO: Could it be better to store in global forecast data even if it is null value?
            //       So, perhaps do not check for null value and always store in global variable.
            if (forecast != null) {
                final WeatherInformationApplication application =
                		(WeatherInformationApplication) getActivity().getApplication();
                application.setForecast(forecast);
            }

            this.mListState = savedInstanceState.getParcelable("ListState");
        }

        this.setHasOptionsMenu(false);

        final OverviewAdapter adapter = new OverviewAdapter(
                this.getActivity(), R.layout.weather_main_entry_list);

        // TODO: string static resource
        this.setEmptyText("No data available");

        this.setListAdapter(adapter);
        this.setListShown(true);
        this.setListShownNoAnimation(true);
    }

    @Override
    public void onResume() {
        super.onResume();

        // TODO: retrive data from data base (like I do on WindowsPhone 8)
        final GeocodingData geocodingData = new GeocodingData.Builder().build();
        if (geocodingData == null) {
            // Nothing to do.
            return;
        }

        final WeatherInformationApplication application =
        		(WeatherInformationApplication) getActivity().getApplication();
        final Forecast forecast = application.getForecast();

        // TODO: Also check whether data is fresh (like I do on WindowsPhone 8) using data base
        if (forecast != null /* && dataIsFresh() */) {
            this.updateUI(forecast);
        } else {
            // Load remote data (aynchronous)
            // Gets the data from the web.
            final OverviewTask task = new OverviewTask(
                    new CustomHTTPClient(AndroidHttpClient.newInstance("Android 4.3 WeatherInformation Agent")),
                    new ServiceParser(new JPOSWeatherParser()));

            task.execute(geocodingData);
            // TODO: make sure thread UI keeps running in parallel after that. I guess.
        }

        // TODO: could mListState be an old value? It is just updated in onActivityCreated method... :/
        // What is this for? And be careful, it runs at the same time as updateUI!!! :(
        if (this.mListState != null) {
            this.getListView().onRestoreInstanceState(this.mListState);
        }
    }

    @Override
    public void onSaveInstanceState(final Bundle savedInstanceState) {

        // Save UI state
        final WeatherInformationApplication application =
        		(WeatherInformationApplication) getActivity().getApplication();
        final Forecast forecast = application.getForecast();

        // TODO: Could it be better to save forecast data even if it is null value?
        //       So, perhaps do not check for null value.
        if (forecast != null) {
            savedInstanceState.putSerializable("Forecast", forecast);
        }

        this.mListState = this.getListView().onSaveInstanceState();
        savedInstanceState.putParcelable("ListState", this.mListState);

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onListItemClick(final ListView l, final View v, final int position, final long id) {
        final SpecificFragment fragment = (SpecificFragment) this
                .getFragmentManager().findFragmentById(R.id.weather_specific_fragment);
        if (fragment == null) {
            // handset layout
            final Intent intent = new Intent("de.example.exampletdd.WEATHERINFO")
            .setComponent(new ComponentName("de.example.exampletdd",
                    "de.example.exampletdd.SpecificActivity"));
            intent.putExtra("CHOSEN_DAY", (int) id);
            OverviewFragment.this.getActivity().startActivity(intent);
        } else {
            // tablet layout
            fragment.updateUIByChosenDay((int) id);
        }
    }

    private void updateUI(final Forecast forecastWeatherData) {

        final SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this.getActivity());

        // TODO: repeating the same code in Overview, Specific and Current!!!
        // 1. Update units of measurement.
        boolean isFahrenheit = false;
        String keyPreference = this.getResources()
                .getString(R.string.weather_preferences_units_key);
        final String unitsPreferenceValue = sharedPreferences.getString(keyPreference, "Celsius");
        final String celsius = this.getResources().getString(
                R.string.weather_preferences_units_celsius);
        if (unitsPreferenceValue.equals(celsius)) {
            isFahrenheit = false;
        } else {
            isFahrenheit = true;
        }
        final double tempUnits = isFahrenheit ? 0 : 273.15;
        final String symbol = isFahrenheit ? "ºF" : "ºC";


        // 2. Update number day forecast.
        int mDayForecast;
        keyPreference = this.getResources()
                .getString(R.string.weather_preferences_day_forecast_key);
        final String dayForecast = sharedPreferences.getString(keyPreference, "5");
        mDayForecast = Integer.valueOf(dayForecast);


        // 3. Formatters
        final DecimalFormat tempFormatter = (DecimalFormat) NumberFormat.getNumberInstance(Locale.US);
        tempFormatter.applyPattern("#####.##");
        final SimpleDateFormat dayNameFormatter = new SimpleDateFormat("EEE", Locale.US);
        final SimpleDateFormat monthAndDayNumberormatter = new SimpleDateFormat("MMM d", Locale.US);


        // 4. Prepare data for UI.
        final List<OverviewEntry> entries = new ArrayList<OverviewEntry>();
        final OverviewAdapter adapter = new OverviewAdapter(this.getActivity(),
                R.layout.weather_main_entry_list);
        final Calendar calendar = Calendar.getInstance();
        int count = mDayForecast;
        for (final de.example.exampletdd.model.forecastweather.List forecast : forecastWeatherData
                .getList()) {

            Bitmap picture;

            if ((forecast.getWeather().size() > 0) &&
                    (forecast.getWeather().get(0).getIcon() != null) &&
                    (IconsList.getIcon(forecast.getWeather().get(0).getIcon()) != null)) {
                final String icon = forecast.getWeather().get(0).getIcon();
                picture = BitmapFactory.decodeResource(this.getResources(), IconsList.getIcon(icon)
                        .getResourceDrawable());
            } else {
                picture = BitmapFactory.decodeResource(this.getResources(),
                        R.drawable.weather_severe_alert);
            }

            final Long forecastUNIXDate = (Long) forecast.getDt();
            calendar.setTimeInMillis(forecastUNIXDate * 1000L);
            final Date dayTime = calendar.getTime();
            final String dayTextName = dayNameFormatter.format(dayTime);
            final String monthAndDayNumberText = monthAndDayNumberormatter.format(dayTime);

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
                entries.add(new OverviewEntry(dayTextName, monthAndDayNumberText,
                        tempFormatter.format(maxTemp) + symbol, tempFormatter.format(minTemp) + symbol,
                        picture));
            }

            count = count - 1;
            if (count == 0) {
                break;
            }
        }


        // 5. Update UI.
        this.setListAdapter(null);
        adapter.addAll(entries);
        this.setListAdapter(adapter);
    }


    private class OverviewTask extends AsyncTask<GeocodingData, Void, Forecast> {
        final CustomHTTPClient weatherHTTPClient;
        final ServiceParser weatherService;

        public OverviewTask(final CustomHTTPClient weatherHTTPClient, final ServiceParser weatherService) {
            this.weatherHTTPClient = weatherHTTPClient;
            this.weatherService = weatherService;
        }

        @Override
        protected Forecast doInBackground(final GeocodingData... params) {
            Log.i(TAG, "OverviewFragment doInBackground");
            Forecast forecast = null;

            try {
                forecast = this.doInBackgroundThrowable(params[0], weatherHTTPClient, weatherService);
            } catch (final JsonParseException e) {
                Log.e(TAG, "OverviewTask doInBackground exception: ", e);
            } catch (final ClientProtocolException e) {
                Log.e(TAG, "OverviewTask doInBackground exception: ", e);
            } catch (final MalformedURLException e) {
                Log.e(TAG, "OverviewTask doInBackground exception: ", e);
            } catch (final URISyntaxException e) {
                Log.e(TAG, "OverviewTask doInBackground exception: ", e);
            } catch (final IOException e) {
                // logger infrastructure swallows UnknownHostException :/
                Log.e(TAG, "OverviewTask doInBackground exception: " + e.getMessage(), e);
            } finally {
                weatherHTTPClient.close();
            }

            return forecast;
        }

        private Forecast doInBackgroundThrowable(final GeocodingData geocodingData,
                final CustomHTTPClient HTTPClient, final ServiceParser serviceParser)
                        throws URISyntaxException, ClientProtocolException, JsonParseException, IOException {

            final String APIVersion = getResources().getString(R.string.api_version);
            final String urlAPI = getResources().getString(R.string.uri_api_weather_forecast);
            // TODO: number as resource
            final String url = serviceParser.createURIAPIForecast(urlAPI, APIVersion,
                    geocodingData.getLatitude(), geocodingData.getLongitude(), "14");
            final String jsonData = HTTPClient.retrieveDataAsString(new URL(url));

            return serviceParser.retrieveForecastFromJPOS(jsonData);
        }

        @Override
        protected void onPostExecute(final Forecast forecast) {
 
        	if (forecast == null) {
        		// Nothing to do
        		return;
        	}

            // Call updateUI on the UI thread.
            updateUI(forecast);

            final WeatherInformationApplication application =
            		(WeatherInformationApplication) getActivity().getApplication();
            application.setForecast(forecast);

            // TODO: update last time update using data base (like I do on Windows Phone 8)
        }
    }
}
