package de.example.exampletdd.fragment;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;

import android.app.Fragment;
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
import android.widget.EditText;
import android.widget.ImageView;
import de.example.exampletdd.R;
import de.example.exampletdd.activityinterface.ErrorMessage;
import de.example.exampletdd.activityinterface.OnClickButtons;
import de.example.exampletdd.httpclient.WeatherHTTPClient;
import de.example.exampletdd.model.WeatherData;
import de.example.exampletdd.parser.IJPOSWeatherParser;
import de.example.exampletdd.parser.JPOSWeatherParser;
import de.example.exampletdd.service.WeatherService;

public class WeatherInformationDataFragmentDeprecated extends Fragment implements OnClickButtons {
    private boolean isFahrenheit;
    private EditText weatherDescription;
    private EditText temperature;
    private EditText maxTemperature;
    private EditText minTemperature;
    private EditText sunRise;
    private EditText sunSet;
    private ImageView imageIcon;


    @Override
    public View onCreateView(final LayoutInflater inflater,
            final ViewGroup container, final Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_main,
                container, false);


        this.weatherDescription = (EditText) rootView.findViewById(R.id.editTextWeatherDescription);
        this.temperature = (EditText) rootView.findViewById(R.id.editTextTemperature);
        this.maxTemperature = (EditText) rootView.findViewById(R.id.editTextMaxTemperature);
        this.minTemperature = (EditText) rootView.findViewById(R.id.editTextMinTemperature);
        this.sunRise = (EditText) rootView.findViewById(R.id.editTextSunRise);
        this.sunSet = (EditText) rootView.findViewById(R.id.editTextSunSet);
        this.imageIcon = (ImageView) rootView.findViewById(R.id.imageIcon);

        return rootView;
    }

    @Override
    public void onClickGetWeather() {

        final IJPOSWeatherParser JPOSWeatherParser = new JPOSWeatherParser();
        final WeatherService weatherService = new WeatherService(
                JPOSWeatherParser);
        final AndroidHttpClient httpClient = AndroidHttpClient
                .newInstance("Android Weather Information Agent");
        final WeatherHTTPClient HTTPweatherClient = new WeatherHTTPClient(
                httpClient);

        final WeatherTask weatherTask = new WeatherTask(HTTPweatherClient, weatherService);

        final EditText cityCountry = (EditText) this.getActivity()
                .findViewById(R.id.editTextCity);

        weatherTask.execute(cityCountry.getText().toString());
    }

    public void updateWeatherData(final WeatherData weatherData) {
        final DecimalFormat tempFormatter = new DecimalFormat("#####.#####");
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss Z");
        final double tempUnits = this.isFahrenheit ? 0 : 273.15;

        if (weatherData.getWeather() != null) {
            this.weatherDescription.setText(weatherData.getWeather()
                    .getDescription());
            double conversion = weatherData.getMain().getTemp();
            conversion = conversion - tempUnits;
            this.temperature.setText(tempFormatter.format(conversion));
            conversion = weatherData.getMain().getMaxTemp();
            conversion = conversion - tempUnits;
            this.maxTemperature.setText(tempFormatter.format(conversion));
            conversion = weatherData.getMain().getMinTemp();
            conversion = conversion - tempUnits;
            this.minTemperature.setText(tempFormatter.format(conversion));
        }

        if (weatherData.getSystem() != null) {
            long unixTime = weatherData.getSystem().getSunRiseTime();
            Date unixDate = new Date(unixTime * 1000L);
            String dateFormatUnix = dateFormat.format(unixDate);
            this.sunRise.setText(dateFormatUnix);

            unixTime = weatherData.getSystem().getSunSetTime();
            unixDate = new Date(unixTime * 1000L);
            dateFormatUnix = dateFormat.format(unixDate);
            this.sunSet.setText(dateFormatUnix);
        }

        if (weatherData.getIconData() != null) {
            final Bitmap icon = BitmapFactory.decodeByteArray(
                    weatherData.getIconData(), 0,
                    weatherData.getIconData().length);
            this.imageIcon.setImageBitmap(icon);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        final SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this.getActivity());

        final String unitsKey = this.getResources().getString(
                R.string.weather_preferences_units_key);
        final String units = sharedPreferences.getString(unitsKey, "");
        final String celsius = this.getResources().getString(
                R.string.weather_preferences_units_celsius);
        if (units.equals(celsius)) {
            this.isFahrenheit = false;
        } else {
            this.isFahrenheit = true;
        }
    }

    public class WeatherTask extends AsyncTask<Object, Void, WeatherData> {
        private static final String TAG = "JSONWeatherTask";
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
                Log.e(TAG, "WeatherHTTPClient exception: ", e);
            } catch (final MalformedURLException e) {
                Log.e(TAG, "Syntax URL exception: ", e);
            } catch (final URISyntaxException e) {
                Log.e(TAG, "WeatherHTTPClient exception: ", e);
            } catch (final IOException e) {
                Log.e(TAG, "WeatherHTTPClient exception: ", e);
            } catch (final JSONException e) {
                Log.e(TAG, "WeatherService exception: ", e);
            } finally {
                this.weatherHTTPClient.close();
            }

            return weatherData;
        }

        @Override
        protected void onPostExecute(final WeatherData weatherData) {
            if (weatherData != null) {
                WeatherInformationDataFragmentDeprecated.this.updateWeatherData(weatherData);
            } else {
                ((ErrorMessage) WeatherInformationDataFragmentDeprecated.this.getActivity())
                .createErrorDialog(R.string.error_dialog_generic_error);
            }

            this.weatherHTTPClient.close();
        }

        @Override
        protected void onCancelled(final WeatherData weatherData) {
            this.onCancelled();
            ((ErrorMessage) WeatherInformationDataFragmentDeprecated.this.getActivity())
            .createErrorDialog(R.string.error_dialog_connection_tiemout);

            this.weatherHTTPClient.close();
        }

        private WeatherData doInBackgroundThrowable(final Object... params)
                throws ClientProtocolException, MalformedURLException,
                URISyntaxException, IOException, JSONException {
            final String cityCountry = (String) params[0];
            final String urlAPICity = WeatherInformationDataFragmentDeprecated.this.getResources()
                    .getString(R.string.uri_api_city);
            final String APIVersion = WeatherInformationDataFragmentDeprecated.this.getResources()
                    .getString(R.string.api_version);
            String url = this.weatherService.createURIAPICityCountry(
                    cityCountry, urlAPICity, APIVersion);


            final String jsonData = this.weatherHTTPClient.retrieveJSONDataFromAPI(new URL(url));


            final WeatherData weatherData = this.weatherService.retrieveWeather(jsonData);


            final String icon = weatherData.getWeather().getIcon();
            final String urlAPIicon = WeatherInformationDataFragmentDeprecated.this
                    .getResources().getString(R.string.uri_api_icon);
            url = this.weatherService.createURIAPIicon(icon, urlAPIicon);
            final byte[] iconData = this.weatherHTTPClient
                    .retrieveDataFromAPI(new URL(url)).toByteArray();
            weatherData.setIconData(iconData);


            return weatherData;
        }
    }
}
