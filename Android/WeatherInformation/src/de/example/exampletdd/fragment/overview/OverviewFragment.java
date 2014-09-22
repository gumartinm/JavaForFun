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

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ListFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.fasterxml.jackson.core.JsonParseException;

import de.example.exampletdd.R;
import de.example.exampletdd.WeatherInformationApplication;
import de.example.exampletdd.fragment.specific.SpecificFragment;
import de.example.exampletdd.httpclient.CustomHTTPClient;
import de.example.exampletdd.model.DatabaseQueries;
import de.example.exampletdd.model.WeatherLocation;
import de.example.exampletdd.model.forecastweather.Forecast;
import de.example.exampletdd.parser.JPOSWeatherParser;
import de.example.exampletdd.service.IconsList;
import de.example.exampletdd.service.ServiceParser;

public class OverviewFragment extends ListFragment {
    private static final String TAG = "OverviewFragment";
    private BroadcastReceiver mReceiver;

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
        }

        this.setHasOptionsMenu(false);

        // TODO: string static resource
        this.setEmptyText("No data available");
        this.setListShownNoAnimation(false);
    }

    @Override
    public void onResume() {
        super.onResume();

        this.mReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(final Context context, final Intent intent) {
				final String action = intent.getAction();
				if (action.equals("de.example.exampletdd.UPDATEFORECAST")) {
					final Forecast forecast = (Forecast) intent.getSerializableExtra("forecast");

					if (forecast != null) {
						OverviewFragment.this.updateUI(forecast);

			            final WeatherInformationApplication application =
			            		(WeatherInformationApplication) getActivity().getApplication();
			            application.setForecast(forecast);

			            final DatabaseQueries query = new DatabaseQueries(OverviewFragment.this.getActivity().getApplicationContext());
			            final WeatherLocation weatherLocation = query.queryDataBase();
			            weatherLocation.setLastForecastUIUpdate(new Date());
			            query.updateDataBase(weatherLocation);
					} else {
						OverviewFragment.this.setListShownNoAnimation(true);
					}
				}
			}
        };

        // Register receiver
        final IntentFilter filter = new IntentFilter();
        filter.addAction("de.example.exampletdd.UPDATEFORECAST");
        LocalBroadcastManager.getInstance(this.getActivity().getApplicationContext())
        						.registerReceiver(this.mReceiver, filter);

        final DatabaseQueries query = new DatabaseQueries(this.getActivity().getApplicationContext());
        final WeatherLocation weatherLocation = query.queryDataBase();
        if (weatherLocation == null) {
            // Nothing to do.
            return;
        }

        final WeatherInformationApplication application =
        		(WeatherInformationApplication) getActivity().getApplication();
        final Forecast forecast = application.getForecast();

        if (forecast != null && this.isDataFresh(weatherLocation.getLastForecastUIUpdate())) {
            this.updateUI(forecast);
        } else {
            // Load remote data (aynchronous)
            // Gets the data from the web.
            this.setListShownNoAnimation(false);
            final OverviewTask task = new OverviewTask(
            		this.getActivity().getApplicationContext(),
                    new CustomHTTPClient(AndroidHttpClient.newInstance("Android 4.3 WeatherInformation Agent")),
                    new ServiceParser(new JPOSWeatherParser()));

            task.execute(weatherLocation.getLatitude(), weatherLocation.getLongitude());
            // TODO: make sure thread UI keeps running in parallel after that. I guess.
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

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(this.getActivity().getApplicationContext()).unregisterReceiver(this.mReceiver);

        super.onPause();
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
        adapter.addAll(entries);
        this.setListAdapter(adapter);
    }

    private boolean isDataFresh(final Date lastUpdate) {
    	if (lastUpdate == null) {
    		return false;
    	}
    	
    	final Date currentTime = new Date();
    	if (((currentTime.getTime() - lastUpdate.getTime())) < 120000L) {
    		return true;
    	}
    	
    	return false;
    }

    // TODO: How could I show just one progress dialog when I have two fragments in tabs
    //       activity doing the same in background?
    //       I mean, if OverviewTask shows one progress dialog and CurrentTask does the same I will have
    //       have two progress dialogs... How may I solve this problem? I HATE ANDROID.
    private class OverviewTask extends AsyncTask<Object, Void, Forecast> {
    	// Store the context passed to the AsyncTask when the system instantiates it.
        private final Context localContext;
        private final CustomHTTPClient weatherHTTPClient;
        private final ServiceParser weatherService;

        public OverviewTask(final Context context, final CustomHTTPClient weatherHTTPClient,
        		final ServiceParser weatherService) {
        	this.localContext = context;
            this.weatherHTTPClient = weatherHTTPClient;
            this.weatherService = weatherService;
        }
        
        @Override
        protected Forecast doInBackground(final Object... params) {
            Log.i(TAG, "OverviewFragment doInBackground");
            final double latitude = (Double) params[0];
            final double longitude = (Double) params[1];

            Forecast forecast = null;

            try {
                forecast = this.doInBackgroundThrowable(latitude, longitude, weatherHTTPClient, weatherService);
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

        private Forecast doInBackgroundThrowable(final double latitude, final double longitude,
                final CustomHTTPClient HTTPClient, final ServiceParser serviceParser)
                        throws URISyntaxException, ClientProtocolException, JsonParseException, IOException {

            final String APIVersion = localContext.getResources().getString(R.string.api_version);
            final String urlAPI = localContext.getResources().getString(R.string.uri_api_weather_forecast);
            // TODO: number as resource
            final String url = serviceParser.createURIAPIForecast(urlAPI, APIVersion, latitude, longitude, "14");
            final String jsonData = HTTPClient.retrieveDataAsString(new URL(url));

            return serviceParser.retrieveForecastFromJPOS(jsonData);
        }

        @Override
        protected void onPostExecute(final Forecast forecast) {
        	// TODO: Is AsyncTask calling this method even when RunTimeException in doInBackground method?
        	// I hope so, otherwise I must catch(Throwable) in doInBackground method :(
        	
            // Call updateUI on the UI thread.
        	final Intent forecastData = new Intent("de.example.exampletdd.UPDATEFORECAST");
        	forecastData.putExtra("forecast", forecast);
            LocalBroadcastManager.getInstance(this.localContext).sendBroadcastSync(forecastData);
        }
    }
}
