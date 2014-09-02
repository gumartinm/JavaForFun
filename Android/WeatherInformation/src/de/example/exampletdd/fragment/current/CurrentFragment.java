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

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
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
import de.example.exampletdd.model.GeocodingData;
import de.example.exampletdd.model.currentweather.Current;
import de.example.exampletdd.parser.JPOSWeatherParser;
import de.example.exampletdd.service.IconsList;
import de.example.exampletdd.service.ServiceParser;

public class CurrentFragment extends Fragment {
    private static final String TAG = "CurrentFragment";

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
        final Current current = application.getCurrent();

        // TODO: Why don't I need mListState?

        // TODO: Also check whether data is fresh (like I do on WindowsPhone 8) using data base
        if (current != null /* && dataIsFresh() */) {
            this.updateUI(current);
        } else {
            // Load remote data (aynchronous)
            // Gets the data from the web.
            final CurrentTask task = new CurrentTask(
                    new CustomHTTPClient(AndroidHttpClient.newInstance("Android 4.3 WeatherInformation Agent")),
                    new ServiceParser(new JPOSWeatherParser()));

            task.execute(geocodingData);
            // TODO: make sure UI thread keeps running in parallel after that. I guess.
        }
        
        // TODO: Overview is doing things with mListState... Why not here?
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

        // TODO: Why don't I need mListState?
        
        super.onSaveInstanceState(savedInstanceState);
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
        
        final TextView descriptionView = (TextView) getActivity().findViewById(R.id.weather_specific_description);
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
    
    private class CurrentTask extends AsyncTask<GeocodingData, Void, Current> {
        final CustomHTTPClient weatherHTTPClient;
        final ServiceParser weatherService;

        public CurrentTask(final CustomHTTPClient weatherHTTPClient, final ServiceParser weatherService) {
            this.weatherHTTPClient = weatherHTTPClient;
            this.weatherService = weatherService;
        }

        @Override
        protected Current doInBackground(final GeocodingData... params) {
            Log.i(TAG, "CurrentTask doInBackground");
            Current current = null;

            try {
            	current = this.doInBackgroundThrowable(params[0], weatherHTTPClient, weatherService);
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

        private Current doInBackgroundThrowable(final GeocodingData geocodingData,
                final CustomHTTPClient HTTPClient, final ServiceParser serviceParser)
                        throws URISyntaxException, ClientProtocolException, JsonParseException, IOException {

        	final String APIVersion = getResources().getString(R.string.api_version);
            final String urlAPI = getResources().getString(R.string.uri_api_weather_today);
            final String url = weatherService.createURIAPICurrent(urlAPI, APIVersion,
                    geocodingData.getLatitude(), geocodingData.getLongitude());
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
        	 
        	if (current == null) {
        		// Nothing to do
        		// TODO: Should I show some error message? I am not doing it on WP8 Should I do it on WP8?
        		return;
        	}

            // Call updateUI on the UI thread.
        	updateUI(current);

            final WeatherInformationApplication application =
            		(WeatherInformationApplication) getActivity().getApplication();
            application.setCurrent(current);

            // TODO: update last time update using data base (like I do on Windows Phone 8)
        }
    }
}
