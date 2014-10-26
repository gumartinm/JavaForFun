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

import android.appwidget.AppWidgetManager;
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
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fasterxml.jackson.core.JsonParseException;

import de.example.exampletdd.R;
import de.example.exampletdd.WidgetIntentService;
import de.example.exampletdd.httpclient.CustomHTTPClient;
import de.example.exampletdd.model.DatabaseQueries;
import de.example.exampletdd.model.WeatherLocation;
import de.example.exampletdd.model.currentweather.Current;
import de.example.exampletdd.parser.JPOSWeatherParser;
import de.example.exampletdd.service.IconsList;
import de.example.exampletdd.service.PermanentStorage;
import de.example.exampletdd.service.ServiceParser;
import de.example.exampletdd.widget.WidgetProvider;

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
            	final PermanentStorage store = new PermanentStorage(this.getActivity().getApplicationContext());
            	store.saveCurrent(current);
            }
        }     
        
        this.setHasOptionsMenu(false);

        this.getActivity().findViewById(R.id.weather_current_data_container).setVisibility(View.GONE);
        this.getActivity().findViewById(R.id.weather_current_progressbar).setVisibility(View.VISIBLE);
    	this.getActivity().findViewById(R.id.weather_current_error_message).setVisibility(View.GONE);  	
    }

    @Override
    public void onResume() {
        super.onResume();


        this.mReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(final Context context, final Intent intent) {
				final String action = intent.getAction();
				if (action.equals("de.example.exampletdd.UPDATECURRENT")) {
					final Current currentRemote = (Current) intent.getSerializableExtra("current");

					if (currentRemote != null) {

						// 1. Check conditions. They must be the same as the ones that triggered the AsyncTask.
						final DatabaseQueries query = new DatabaseQueries(context.getApplicationContext());
			            final WeatherLocation weatherLocation = query.queryDataBase();
			            final PermanentStorage store = new PermanentStorage(context.getApplicationContext());
			            final Current current = store.getCurrent();

			            if (current == null || !CurrentFragment.this.isDataFresh(weatherLocation.getLastCurrentUIUpdate())) {
			            	// 2. Update UI.
			            	CurrentFragment.this.updateUI(currentRemote);

			            	// 3. Update current data.
							store.saveCurrent(currentRemote);

                            // 4. If is new data (new location) update widgets.
                            if (weatherLocation.getIsNew()) {
                                WidgetProvider.updateAllAppWidgets(context);
                            }

                            // 5. Update location data.
                            weatherLocation.setIsNew(false);
                            weatherLocation.setLastCurrentUIUpdate(new Date());
                            query.updateDataBase(weatherLocation);
			            }

					} else {
						// Empty UI and show error message
						CurrentFragment.this.getActivity().findViewById(R.id.weather_current_data_container).setVisibility(View.GONE);
						CurrentFragment.this.getActivity().findViewById(R.id.weather_current_progressbar).setVisibility(View.GONE);
						CurrentFragment.this.getActivity().findViewById(R.id.weather_current_error_message).setVisibility(View.VISIBLE);
					}
				}
			}
        };

        // Register receiver
        final IntentFilter filter = new IntentFilter();
        filter.addAction("de.example.exampletdd.UPDATECURRENT");
        LocalBroadcastManager.getInstance(this.getActivity().getApplicationContext())
        						.registerReceiver(this.mReceiver, filter);

        // Empty UI
        this.getActivity().findViewById(R.id.weather_current_data_container).setVisibility(View.GONE);
        
        final DatabaseQueries query = new DatabaseQueries(this.getActivity().getApplicationContext());
        final WeatherLocation weatherLocation = query.queryDataBase();
        if (weatherLocation == null) {
            // Nothing to do.
        	// Show error message
        	final ProgressBar progress = (ProgressBar) getActivity().findViewById(R.id.weather_current_progressbar);
	        progress.setVisibility(View.GONE);
			final TextView errorMessage = (TextView) getActivity().findViewById(R.id.weather_current_error_message);
	        errorMessage.setVisibility(View.VISIBLE);
            return;
        }

        final PermanentStorage store = new PermanentStorage(this.getActivity().getApplicationContext());
        final Current current = store.getCurrent();

        if (current != null && this.isDataFresh(weatherLocation.getLastCurrentUIUpdate())) {
            this.updateUI(current);
        } else {
            // Load remote data (aynchronous)
            // Gets the data from the web.
        	this.getActivity().findViewById(R.id.weather_current_progressbar).setVisibility(View.VISIBLE);
        	this.getActivity().findViewById(R.id.weather_current_error_message).setVisibility(View.GONE);
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
    	final PermanentStorage store = new PermanentStorage(this.getActivity().getApplicationContext());
        final Current current = store.getCurrent();

        // TODO: Could it be better to save current data even if it is null value?
        //       So, perhaps do not check for null value.
        if (current != null) {
            savedInstanceState.putSerializable("Current", current);
        }

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(this.getActivity().getApplicationContext()).unregisterReceiver(this.mReceiver);

        super.onPause();
    }

    private interface UnitsConversor {
    	
    	public double doConversion(final double value);
    }

    private void updateUI(final Current current) {
    	
        final SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this.getActivity().getApplicationContext());

        // TODO: repeating the same code in Overview, Specific and Current!!!
        // 1. Update units of measurement.
        // 1.1 Temperature
        String tempSymbol;
        UnitsConversor tempUnitsConversor;
        String keyPreference = this.getResources().getString(R.string.weather_preferences_temperature_key);
        String[] values = this.getResources().getStringArray(R.array.weather_preferences_temperature);
        String unitsPreferenceValue = sharedPreferences.getString(
                keyPreference, this.getString(R.string.weather_preferences_temperature_celsius));
        if (unitsPreferenceValue.equals(values[0])) {
        	tempSymbol = values[0];
        	tempUnitsConversor = new UnitsConversor(){

				@Override
				public double doConversion(final double value) {
					return value - 273.15;
				}
        		
        	};
        } else if (unitsPreferenceValue.equals(values[1])) {
        	tempSymbol = values[1];
        	tempUnitsConversor = new UnitsConversor(){

				@Override
				public double doConversion(final double value) {
					return (value * 1.8) - 459.67;
				}
        		
        	};
        } else {
        	tempSymbol = values[2];
        	tempUnitsConversor = new UnitsConversor(){

				@Override
				public double doConversion(final double value) {
					return value;
				}
        		
        	};
        }

        // 1.2 Wind
        String windSymbol;
        UnitsConversor windUnitsConversor;
        keyPreference = this.getResources().getString(R.string.weather_preferences_wind_key);
        values = this.getResources().getStringArray(R.array.weather_preferences_wind);
        unitsPreferenceValue = sharedPreferences.getString(
                keyPreference, this.getString(R.string.weather_preferences_wind_meters));
        if (unitsPreferenceValue.equals(values[0])) {
        	windSymbol = values[0];
        	windUnitsConversor = new UnitsConversor(){

    			@Override
    			public double doConversion(double value) {
    				return value;
    			}	
        	};
        } else {
        	windSymbol = values[1];
        	windUnitsConversor = new UnitsConversor(){

    			@Override
    			public double doConversion(double value) {
    				return value * 2.237;
    			}	
        	};
        }

        // 1.3 Pressure
        String pressureSymbol;
        UnitsConversor pressureUnitsConversor;
        keyPreference = this.getResources().getString(R.string.weather_preferences_pressure_key);
        values = this.getResources().getStringArray(R.array.weather_preferences_pressure);
        unitsPreferenceValue = sharedPreferences.getString(
                keyPreference, this.getString(R.string.weather_preferences_pressure_pascal));
        if (unitsPreferenceValue.equals(values[0])) {
        	pressureSymbol = values[0];
        	pressureUnitsConversor = new UnitsConversor(){

    			@Override
    			public double doConversion(double value) {
    				return value;
    			}	
        	};
        } else {
        	pressureSymbol = values[1];
        	pressureUnitsConversor = new UnitsConversor(){

    			@Override
    			public double doConversion(double value) {
    				return value / 113.25d;
    			}	
        	};
        }


        // 2. Formatters
        final DecimalFormat tempFormatter = (DecimalFormat) NumberFormat.getNumberInstance(Locale.US);
        tempFormatter.applyPattern("#####.#####");
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.US);

        
        // 3. Prepare data for UI.
        String tempMax = "";
        if (current.getMain().getTemp_max() != null) {
            double conversion = (Double) current.getMain().getTemp_max();
            conversion = tempUnitsConversor.doConversion(conversion);
            tempMax = tempFormatter.format(conversion) + tempSymbol;
        }
        String tempMin = "";
        if (current.getMain().getTemp_min() != null) {
            double conversion = (Double) current.getMain().getTemp_min();
            conversion = tempUnitsConversor.doConversion(conversion);
            tempMin = tempFormatter.format(conversion) + tempSymbol;
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
            double conversion = (Double) current.getMain().getPressure();
            conversion = pressureUnitsConversor.doConversion(conversion);
            pressureValue = tempFormatter.format(conversion);
        }
        String windValue = "";
        if ((current.getWind() != null)
                && (current.getWind().getSpeed() != null)) {
            double conversion = (Double) current.getWind().getSpeed();
            conversion = windUnitsConversor.doConversion(conversion);
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
            conversion = tempUnitsConversor.doConversion(conversion);
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
        
        ((TextView) getActivity().findViewById(R.id.weather_current_humidity_value)).setText(humidityValue);
        ((TextView) getActivity().findViewById(R.id.weather_current_humidity_units)).setText(
        		this.getActivity().getApplicationContext().getString(R.string.text_units_percent));
        
        ((TextView) getActivity().findViewById(R.id.weather_current_pressure_value)).setText(pressureValue);
        ((TextView) getActivity().findViewById(R.id.weather_current_pressure_units)).setText(pressureSymbol);
        
        ((TextView) getActivity().findViewById(R.id.weather_current_wind_value)).setText(windValue);
        ((TextView) getActivity().findViewById(R.id.weather_current_wind_units)).setText(windSymbol);
        
        ((TextView) getActivity().findViewById(R.id.weather_current_rain_value)).setText(rainValue);
        ((TextView) getActivity().findViewById(R.id.weather_current_rain_units)).setText(
        		this.getActivity().getApplicationContext().getString(R.string.text_units_mm3h));
        
        ((TextView) getActivity().findViewById(R.id.weather_current_clouds_value)).setText(cloudsValue);
        ((TextView) getActivity().findViewById(R.id.weather_current_clouds_units)).setText(
        		this.getActivity().getApplicationContext().getString(R.string.text_units_percent));
        
        ((TextView) getActivity().findViewById(R.id.weather_current_snow_value)).setText(snowValue);
        ((TextView) getActivity().findViewById(R.id.weather_current_snow_units)).setText(
        		this.getActivity().getApplicationContext().getString(R.string.text_units_mm3h));
        
        ((TextView) getActivity().findViewById(R.id.weather_current_feelslike_value)).setText(feelsLike);
        ((TextView) getActivity().findViewById(R.id.weather_current_feelslike_units)).setText(tempSymbol);
        
        ((TextView) getActivity().findViewById(R.id.weather_current_sunrise_value)).setText(sunRiseTime);

        ((TextView) getActivity().findViewById(R.id.weather_current_sunset_value)).setText(sunSetTime);
        
        this.getActivity().findViewById(R.id.weather_current_data_container).setVisibility(View.VISIBLE);
        this.getActivity().findViewById(R.id.weather_current_progressbar).setVisibility(View.GONE);
        this.getActivity().findViewById(R.id.weather_current_error_message).setVisibility(View.GONE);       
    }
    
    private boolean isDataFresh(final Date lastUpdate) {
    	if (lastUpdate == null) {
    		return false;
    	}
    	
    	final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
    			this.getActivity().getApplicationContext());
        final String keyPreference = this.getString(R.string.weather_preferences_refresh_interval_key);
        final String refresh = sharedPreferences.getString(
        		keyPreference,
        		this.getResources().getStringArray(R.array.weather_preferences_refresh_interval)[0]);
        final Date currentTime = new Date();
    	if (((currentTime.getTime() - lastUpdate.getTime())) < Long.valueOf(refresh)) {
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
        final CustomHTTPClient HTTPClient;
        final ServiceParser weatherService;

        public CurrentTask(final Context context, final CustomHTTPClient HTTPClient,
        		final ServiceParser weatherService) {
        	this.localContext = context;
            this.HTTPClient = HTTPClient;
            this.weatherService = weatherService;
        }

        @Override
        protected Current doInBackground(final Object... params) {
        	final double latitude = (Double) params[0];
            final double longitude = (Double) params[1];
  
            Current current = null;
            try {
            	current = this.doInBackgroundThrowable(latitude, longitude);
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
            	HTTPClient.close();
            }

            return current;
        }

        private Current doInBackgroundThrowable(final double latitude, final double longitude)
                        throws URISyntaxException, ClientProtocolException, JsonParseException, IOException {

        	final String APIVersion = localContext.getResources().getString(R.string.api_version);
            final String urlAPI = localContext.getResources().getString(R.string.uri_api_weather_today);
            final String url = weatherService.createURIAPICurrent(urlAPI, APIVersion, latitude, longitude);
            final String urlWithoutCache = url.concat("&time=" + System.currentTimeMillis());
            final String jsonData = HTTPClient.retrieveDataAsString(new URL(urlWithoutCache));
            final Current current = weatherService.retrieveCurrentFromJPOS(jsonData);
            // TODO: what is this for? I guess I could skip it :/
            final Calendar now = Calendar.getInstance();
            current.setDate(now.getTime());

            return current;
        }

        @Override
        protected void onPostExecute(final Current current) {
        	// TODO: Is AsyncTask calling this method even when RunTimeException in doInBackground method?
        	// I hope so, otherwise I must catch(Throwable) in doInBackground method :(

            // Call updateUI on the UI thread.
            final Intent currentData = new Intent("de.example.exampletdd.UPDATECURRENT");
            currentData.putExtra("current", current);
            LocalBroadcastManager.getInstance(this.localContext).sendBroadcastSync(currentData);
        }
    }
}
