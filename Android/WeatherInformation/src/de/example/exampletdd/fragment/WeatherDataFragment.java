package de.example.exampletdd.fragment;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;

import android.app.Activity;
import android.app.Fragment;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import de.example.exampletdd.R;
import de.example.exampletdd.activityinterface.ErrorMessage;
import de.example.exampletdd.activityinterface.UpdateWeatherData;
import de.example.exampletdd.httpclient.WeatherHTTPClient;
import de.example.exampletdd.model.WeatherData;
import de.example.exampletdd.parser.IJPOSWeatherParser;
import de.example.exampletdd.parser.JPOSWeatherParser;
import de.example.exampletdd.service.WeatherService;

public class WeatherDataFragment extends Fragment {
    private final Activity currentActivity;

    public WeatherDataFragment() {
        this.currentActivity = this.getActivity();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater,
            final ViewGroup container, final Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_main,
                container, false);
        return rootView;
    }

    public void onClickGetWeather(final View v) {

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
            final String cityCountry = (String) params[0];

            final String urlAPICity = WeatherDataFragment.this.getResources()
                    .getString(R.string.uri_api_city);
            final String APIVersion = WeatherDataFragment.this.getResources()
                    .getString(R.string.api_version);

            String url = this.weatherService.createURIAPICityCountry(
                    cityCountry, urlAPICity, APIVersion);

            String jsonData = null;
            try {
                jsonData = this.weatherHTTPClient
                        .retrieveJSONDataFromAPI(new URL(url));
            } catch (final ClientProtocolException e) {
                Log.e(TAG, "WeatherHTTPClient exception: ", e);
                ((ErrorMessage) WeatherDataFragment.this.currentActivity)
                .createErrorDialog(R.string.error_dialog_generic_error);
            } catch (final MalformedURLException e) {
                Log.e(TAG, "Syntax URL exception: ", e);
                ((ErrorMessage) WeatherDataFragment.this.currentActivity)
                .createErrorDialog(R.string.error_dialog_generic_error);
            } catch (final URISyntaxException e) {
                Log.e(TAG, "WeatherHTTPClient exception: ", e);
                ((ErrorMessage) WeatherDataFragment.this.currentActivity)
                .createErrorDialog(R.string.error_dialog_generic_error);
            } catch (final IOException e) {
                Log.e(TAG, "WeatherHTTPClient exception: ", e);
                ((ErrorMessage) WeatherDataFragment.this.currentActivity)
                .createErrorDialog(R.string.error_dialog_generic_error);
            } finally {
                this.weatherHTTPClient.close();
            }

            WeatherData weatherData = null;
            if (jsonData != null) {
                try {
                    weatherData = this.weatherService.retrieveWeather(jsonData);
                } catch (final JSONException e) {
                    Log.e(TAG, "WeatherService exception: ", e);
                    ((ErrorMessage) WeatherDataFragment.this.currentActivity)
                    .createErrorDialog(R.string.error_dialog_generic_error);
                }

            }

            if (weatherData != null) {
                final String icon = weatherData.getWeather().getIcon();
                final String urlAPIicon = WeatherDataFragment.this
                        .getResources().getString(R.string.uri_api_icon);

                url = this.weatherService.createURIAPIicon(icon, urlAPIicon);
                try {
                    final byte[] iconData = this.weatherHTTPClient
                            .retrieveDataFromAPI(new URL(url)).toByteArray();
                    weatherData.setIconData(iconData);
                } catch (final ClientProtocolException e) {
                    Log.e(TAG, "WeatherHTTPClient exception: ", e);
                    ((ErrorMessage) WeatherDataFragment.this.currentActivity)
                    .createErrorDialog(R.string.error_dialog_generic_error);
                } catch (final MalformedURLException e) {
                    Log.e(TAG, "Syntax URL exception: ", e);
                    ((ErrorMessage) WeatherDataFragment.this.currentActivity)
                    .createErrorDialog(R.string.error_dialog_generic_error);
                } catch (final URISyntaxException e) {
                    Log.e(TAG, "WeatherHTTPClient exception: ", e);
                    ((ErrorMessage) WeatherDataFragment.this.currentActivity)
                    .createErrorDialog(R.string.error_dialog_generic_error);
                } catch (final IOException e) {
                    Log.e(TAG, "WeatherHTTPClient exception: ", e);
                    ((ErrorMessage) WeatherDataFragment.this.currentActivity)
                    .createErrorDialog(R.string.error_dialog_generic_error);
                } finally {
                    this.weatherHTTPClient.close();
                }
            }

            return weatherData;
        }

        @Override
        protected void onPostExecute(final WeatherData weatherData) {
            if (weatherData != null) {
                ((UpdateWeatherData) WeatherDataFragment.this.currentActivity)
                .updateWeatherData(weatherData);
            } else {
                ((ErrorMessage) WeatherDataFragment.this.currentActivity)
                .createErrorDialog(R.string.error_dialog_generic_error);
            }

            this.weatherHTTPClient.close();
        }

        @Override
        protected void onCancelled(final WeatherData weatherData) {
            this.onCancelled();
            ((ErrorMessage) WeatherDataFragment.this.currentActivity)
            .createErrorDialog(R.string.error_dialog_connection_tiemout);

            this.weatherHTTPClient.close();
        }
    }
}
