package de.example.exampletdd.fragment.current;

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

import android.content.BroadcastReceiver;
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
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fasterxml.jackson.core.JsonParseException;

import de.example.exampletdd.R;
import de.example.exampletdd.WeatherInformationApplication;
import de.example.exampletdd.httpclient.CustomHTTPClient;
import de.example.exampletdd.model.DatabaseQueries;
import de.example.exampletdd.model.WeatherLocation;
import de.example.exampletdd.model.currentweather.Current;
import de.example.exampletdd.parser.JPOSWeatherParser;
import de.example.exampletdd.service.IconsList;
import de.example.exampletdd.service.ServiceParser;

public class CurrentFragment extends Fragment {
    private static final String TAG = "CurrentFragment";
    private BroadcastReceiver mReceiver;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
    
    	// Inflate the layout for this fragment
        return inflater.inflate(R.layout.weather_current_fragment, container, false);
    }
    
    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
        	// Restore UI state
            final Current current = (Current) savedInstanceState.getSerializable("Current");

            // TODO: Could it be better to store in global forecast data even if it is null value?
            //       So, perhaps do not check for null value and always store in global variable.
            if (current != null) {
            	final WeatherInformationApplication application =
            			(WeatherInformationApplication) getActivity().getApplication();
                application.setCurrent(current);
            }
        }
    }

    @Override
    public void onStart() {
    	super.onStart();

        this.mReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(final Context context, final Intent intent) {
				final String action = intent.getAction();
				if (action.equals("de.example.exampletdd.UPDATECURRENT")) {
					final Current current = (Current) intent.getSerializableExtra("current");

					if (current != null) {
						CurrentFragment.this.updateUI(current);

			            final WeatherInformationApplication application =
			            		(WeatherInformationApplication) getActivity().getApplication();
			            application.setCurrent(current);

			            final DatabaseQueries query = new DatabaseQueries(CurrentFragment.this.getActivity().getApplicationContext());
			            final WeatherLocation weatherLocation = query.queryDataBase();
			            weatherLocation.setLastCurrentUIUpdate(new Date());
			            query.updateDataBase(weatherLocation);
					}
				}
			}
        };

        // Register receiver
        final IntentFilter filter = new IntentFilter();
        filter.addAction("de.example.exampletdd.UPDATECURRENT");
        LocalBroadcastManager.getInstance(this.getActivity().getApplicationContext())
        						.registerReceiver(this.mReceiver, filter);
    }

    @Override
    public void onResume() {
        super.onResume();

        final DatabaseQueries query = new DatabaseQueries(this.getActivity().getApplicationContext());
        final WeatherLocation weatherLocation = query.queryDataBase();
        if (weatherLocation == null) {
            // Nothing to do.
            return;
        }
        
        final WeatherInformationApplication application =
        		(WeatherInformationApplication) getActivity().getApplication();
        final Current current = application.getCurrent();

        if (current != null && this.isDataFresh(weatherLocation.getLastCurrentUIUpdate())) {
            this.updateUI(current);
        } else {
            // Load remote data (aynchronous)
            // Gets the data from the web.
            final CurrentTask task = new CurrentTask(
            		this.getActivity().getApplicationContext(),
                    new CustomHTTPClient(AndroidHttpClient.newInstance("Android 4.3 WeatherInformation Agent")),
                    new ServiceParser(new JPOSWeatherParser()));

            task.execute(weatherLocation.getLatitude(), weatherLocation.getLongitude());
            // TODO: make sure UI thread keeps running in parallel after that. I guess.
        }
    }

    @Override
    public void onSaveInstanceState(final Bundle savedInstanceState) {

        // Save UI state
    	final WeatherInformationApplication application =
        		(WeatherInformationApplication) getActivity().getApplication();
        final Current current = application.getCurrent();

        // TODO: Could it be better to save current data even if it is null value?
        //       So, perhaps do not check for null value.
        if (current != null) {
            savedInstanceState.putSerializable("Current", current);
        }

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onStop() {
        LocalBroadcastManager.getInstance(this.getActivity().getApplicationContext()).unregisterReceiver(this.mReceiver);

        super.onStop();
    }

    private void updateUI(final Current current) {
    	
        final SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this.getActivity());

        // TODO: repeating the same code in Overview, Specific and Current!!!
        // 1. Update units of measurement.
        boolean mIsFahrenheit = false;
        final String keyPreference = this.getResources().getString(
                R.string.weather_preferences_units_key);
        final String unitsPreferenceValue = sharedPreferences.getString(keyPreference, "");
        final String celsius = this.getResources().getString(
                R.string.weather_preferences_units_celsius);
        if (unitsPreferenceValue.equals(celsius)) {
            mIsFahrenheit = false;
        } else {
            mIsFahrenheit = true;
        }
        final double tempUnits = mIsFahrenheit ? 0 : 273.15;
        final String symbol = mIsFahrenheit ? "ºF" : "ºC";


        // 2. Formatters
        final DecimalFormat tempFormatter = (DecimalFormat) NumberFormat.getNumberInstance(Locale.US);
        tempFormatter.applyPattern("#####.#####");
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.US);

        
        // 3. Prepare data for UI.
        String tempMax = "";
        if (current.getMain().getTemp_max() != null) {
            double conversion = (Double) current.getMain().getTemp_max();
            conversion = conversion - tempUnits;
            tempMax = tempFormatter.format(conversion) + symbol;
        }
        String tempMin = "";
        if (current.getMain().getTemp_min() != null) {
            double conversion = (Double) current.getMain().getTemp_min();
            conversion = conversion - tempUnits;
            tempMin = tempFormatter.format(conversion) + symbol;
        }
        Bitmap picture;
        if ((current.getWeather().size() > 0)
                && (current.getWeather().get(0).getIcon() != null)
                && (IconsList.getIcon(current.getWeather().get(0).getIcon()) != null)) {
            final String icon = current.getWeather().get(0).getIcon();
            picture = BitmapFactory.decodeResource(this.getResources(), IconsList.getIcon(icon)
                    .getResourceDrawable());
        } else {
            picture = BitmapFactory.decodeResource(this.getResources(),
                    R.drawable.weather_severe_alert);
        }

        // TODO: static resource
        String description = "no description available";
        if (current.getWeather().size() > 0) {
            description = current.getWeather().get(0).getDescription();
        }

        // TODO: units!!!!
        String humidityValue = "";
        if ((current.getMain() != null)
                && (current.getMain().getHumidity() != null)) {
            final double conversion = (Double) current.getMain().getHumidity();
            humidityValue = tempFormatter.format(conversion);
        }
        String pressureValue = "";
        if ((current.getMain() != null)
                && (current.getMain().getPressure() != null)) {
            final double conversion = (Double) current.getMain().getPressure();
            pressureValue = tempFormatter.format(conversion);
        }
        String windValue = "";
        if ((current.getWind() != null)
                && (current.getWind().getSpeed() != null)) {
            final double conversion = (Double) current.getWind().getSpeed();
            windValue = tempFormatter.format(conversion);
        }
        String rainValue = "";
        if ((current.getRain() != null)
                && (current.getRain().get3h() != null)) {
            final double conversion = (Double) current.getRain().get3h();
            rainValue = tempFormatter.format(conversion);
        }
        String cloudsValue = "";
        if ((current.getClouds() != null)
                && (current.getClouds().getAll() != null)) {
            final double conversion = (Double) current.getClouds().getAll();
            cloudsValue = tempFormatter.format(conversion);
        }
        String snowValue = "";
        if ((current.getSnow() != null)
                && (current.getSnow().get3h() != null)) {
            final double conversion = (Double) current.getSnow().get3h();
            snowValue = tempFormatter.format(conversion);
        }
        String feelsLike = "";
        if (current.getMain().getTemp() != null) {
            double conversion = (Double) current.getMain().getTemp();
            conversion = conversion - tempUnits;
            feelsLike = tempFormatter.format(conversion);
        }
        String sunRiseTime = "";
        if (current.getSys().getSunrise() != null) {
            final long unixTime = (Long) current.getSys().getSunrise();
            final Date unixDate = new Date(unixTime * 1000L);
            sunRiseTime = dateFormat.format(unixDate);
        }
        String sunSetTime = "";
        if (current.getSys().getSunset() != null) {
            final long unixTime = (Long) current.getSys().getSunset();
            final Date unixDate = new Date(unixTime * 1000L);
            sunSetTime = dateFormat.format(unixDate);
        }


        // 4. Update UI.
        final TextView tempMaxView = (TextView) getActivity().findViewById(R.id.weather_current_temp_max);
        tempMaxView.setText(tempMax);
        final TextView tempMinView = (TextView) getActivity().findViewById(R.id.weather_current_temp_min);
        tempMinView.setText(tempMin);
        final ImageView pictureView = (ImageView) getActivity().findViewById(R.id.weather_current_picture);
        pictureView.setImageBitmap(picture);    
        
        final TextView descriptionView = (TextView) getActivity().findViewById(R.id.weather_current_description);
        descriptionView.setText(description);
        
        final TextView humidityValueView = (TextView) getActivity().findViewById(R.id.weather_current_humidity_value);
        humidityValueView.setText(humidityValue);
        final TextView pressureValueView = (TextView) getActivity().findViewById(R.id.weather_current_pressure_value);
        pressureValueView.setText(pressureValue);
        final TextView windValueView = (TextView) getActivity().findViewById(R.id.weather_current_wind_value);
        windValueView.setText(windValue);
        final TextView rainValueView = (TextView) getActivity().findViewById(R.id.weather_current_rain_value);
        rainValueView.setText(rainValue);
        final TextView cloudsValueView = (TextView) getActivity().findViewById(R.id.weather_current_clouds_value);
        cloudsValueView.setText(cloudsValue);
        final TextView snowValueView = (TextView) getActivity().findViewById(R.id.weather_current_snow_value);
        snowValueView.setText(snowValue);
        final TextView feelsLikeView = (TextView) getActivity().findViewById(R.id.weather_current_feelslike_value);
        feelsLikeView.setText(feelsLike);
        
        final TextView sunRiseTimeView = (TextView) getActivity().findViewById(R.id.weather_current_sunrise_value);
        sunRiseTimeView.setText(sunRiseTime);
        final TextView sunSetTimeView = (TextView) getActivity().findViewById(R.id.weather_current_sunset_value);
        sunSetTimeView.setText(sunSetTime);
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
    private class CurrentTask extends AsyncTask<Object, Void, Current> {
    	// Store the context passed to the AsyncTask when the system instantiates it.
        private final Context localContext;
        final CustomHTTPClient weatherHTTPClient;
        final ServiceParser weatherService;

        public CurrentTask(final Context context, final CustomHTTPClient weatherHTTPClient,
        		final ServiceParser weatherService) {
        	this.localContext = context;
            this.weatherHTTPClient = weatherHTTPClient;
            this.weatherService = weatherService;
        }

        @Override
        protected Current doInBackground(final Object... params) {
        	Log.i(TAG, "CurrentTask doInBackground");
        	final double latitude = (Double) params[0];
            final double longitude = (Double) params[1];
  
            Current current = null;
            try {
            	current = this.doInBackgroundThrowable(latitude, longitude, weatherHTTPClient, weatherService);
            } catch (final JsonParseException e) {
                Log.e(TAG, "CurrentTask doInBackground exception: ", e);
            } catch (final ClientProtocolException e) {
                Log.e(TAG, "CurrentTask doInBackground exception: ", e);
            } catch (final MalformedURLException e) {
                Log.e(TAG, "CurrentTask doInBackground exception: ", e);
            } catch (final URISyntaxException e) {
                Log.e(TAG, "CurrentTask doInBackground exception: ", e);
            } catch (final IOException e) {
                // logger infrastructure swallows UnknownHostException :/
                Log.e(TAG, "CurrentTask doInBackground exception: " + e.getMessage(), e);
            } finally {
                weatherHTTPClient.close();
            }

            return current;
        }

        private Current doInBackgroundThrowable(final double latitude, final double longitude,
                final CustomHTTPClient HTTPClient, final ServiceParser serviceParser)
                        throws URISyntaxException, ClientProtocolException, JsonParseException, IOException {

        	final String APIVersion = localContext.getResources().getString(R.string.api_version);
            final String urlAPI = localContext.getResources().getString(R.string.uri_api_weather_today);
            final String url = weatherService.createURIAPICurrent(urlAPI, APIVersion, latitude, longitude);
            final String jsonData = weatherHTTPClient.retrieveDataAsString(new URL(url));
            final Current current = weatherService
                    .retrieveCurrentFromJPOS(jsonData);
            // TODO: what is this for? I guess I could skip it :/
            final Calendar now = Calendar.getInstance();
            current.setDate(now.getTime());

            return current;
        }

        @Override
        protected void onPostExecute(final Current current) {
        	// TODO: Is AsyncTask calling this method even when RunTimeException in doInBackground method?
        	// I hope so, otherwise I must catch(Throwable) in doInBackground method :(
        	if (current == null) {
        		// Nothing to do
        		// TODO: Should I show some error message? I am not doing it on WP8 Should I do it on WP8?
        		return;
        	}

            // Call updateUI on the UI thread.
            final Intent currentData = new Intent("de.example.exampletdd.UPDATECURRENT");
            currentData.putExtra("current", current);
            LocalBroadcastManager.getInstance(this.localContext).sendBroadcastSync(currentData);
        }
    }
}
