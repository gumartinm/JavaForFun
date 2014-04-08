package de.example.exampletdd;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;

import android.app.ActionBar;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import de.example.exampletdd.activityinterface.ErrorMessage;
import de.example.exampletdd.activityinterface.GetWeather;
import de.example.exampletdd.fragment.ErrorDialogFragment;
import de.example.exampletdd.fragment.WeatherInformationDataFragment;
import de.example.exampletdd.model.GeocodingData;
import de.example.exampletdd.model.WeatherData;

public class WeatherInformationActivity extends Activity implements ErrorMessage {
    private static final String WEATHER_DATA_FILE = "weatherdata.file";
    private static final String WEATHER_GEOCODING_FILE = "weathergeocoding.file";
    private static final String TAG = "WeatherInformationActivity";
    private GetWeather mGetWeather;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_main);

        PreferenceManager.setDefaultValues(this, R.xml.weather_preferences, false);

        final ActionBar actionBar = this.getActionBar();

        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE, ActionBar.DISPLAY_SHOW_TITLE);
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Better using xml files? How to deal with savedInstanceState with xml files?
        // final WeatherDataFragment weatherDataFragment = new WeatherDataFragment();
        //
        // if (savedInstanceState == null) {
        //      this.getFragmentManager().beginTransaction()
        //      .add(R.id.container, weatherDataFragment).commit();
        // }
        final WeatherInformationDataFragment weatherDataFragment = (WeatherInformationDataFragment) this
                .getFragmentManager().findFragmentById(R.id.weather_data_frag);

        this.mGetWeather = weatherDataFragment;
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {

        this.getMenuInflater().inflate(R.menu.weather_main_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        super.onOptionsItemSelected(item);

        Intent intent;
        switch (item.getItemId()) {
        case R.id.weather_menu_settings:
            intent = new Intent("de.example.exampletdd.WEATHERINFO").
            setComponent(new ComponentName("de.example.exampletdd",
                    "de.example.exampletdd.WeatherInformationPreferencesActivity"));
            this.startActivity(intent);
            return true;
        case R.id.weather_menu_get:
            this.getWeather();
            return true;
        case R.id.weather_menu_map:
            intent = new Intent("de.example.exampletdd.WEATHERINFO")
            .setComponent(new ComponentName("de.example.exampletdd",
                    "de.example.exampletdd.WeatherInformationMapActivity"));
            this.startActivity(intent);
            return true;
        default:
            break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();

        final ActionBar actionBar = this.getActionBar();

        GeocodingData geocodingData = null;
        try {
            geocodingData = this.restoreGeocodingDataFromFile();
        } catch (final StreamCorruptedException e) {
            Log.e(TAG, "onCreate exception: ", e);
        } catch (final FileNotFoundException e) {
            Log.e(TAG, "onCreate exception: ", e);
        } catch (final IOException e) {
            Log.e(TAG, "onCreate exception: ", e);
        } catch (final ClassNotFoundException e) {
            Log.e(TAG, "onCreate exception: ", e);
        }
        if (geocodingData != null) {
            final String city = (geocodingData.getCity() == null) ? "city not found"
                    : geocodingData.getCity();
            final String country = (geocodingData.getCountry() == null) ? "country not found"
                    : geocodingData.getCountry();
            actionBar.setTitle(city + "," + country);
        }

    }

    @Override
    public void onRestoreInstanceState(final Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        final ActionBar actionBar = this.getActionBar();

        GeocodingData geocodingData = null;
        try {
            geocodingData = this.restoreGeocodingDataFromFile();
        } catch (final StreamCorruptedException e) {
            Log.e(TAG, "onCreate exception: ", e);
        } catch (final FileNotFoundException e) {
            Log.e(TAG, "onCreate exception: ", e);
        } catch (final IOException e) {
            Log.e(TAG, "onCreate exception: ", e);
        } catch (final ClassNotFoundException e) {
            Log.e(TAG, "onCreate exception: ", e);
        }
        if (geocodingData != null) {
            final String city = (geocodingData.getCity() == null) ? "city not found"
                    : geocodingData.getCity();
            final String country = (geocodingData.getCountry() == null) ? "country not found"
                    : geocodingData.getCountry();
            actionBar.setTitle(city + "," + country);
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
            this.mGetWeather.updateWeatherData(weatherData);
        }
    }

    @Override
    public void onSaveInstanceState(final Bundle savedInstanceState) {

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void createErrorDialog(final int title) {
        final DialogFragment newFragment = ErrorDialogFragment
                .newInstance(title);
        newFragment.show(this.getFragmentManager(), "errorDialog");
    }

    public void getWeather() {
        this.mGetWeather.getWeather();
    }

    private GeocodingData restoreGeocodingDataFromFile()
            throws StreamCorruptedException, FileNotFoundException,
            IOException, ClassNotFoundException {
        final InputStream persistenceFile = this.openFileInput(
                WEATHER_GEOCODING_FILE);

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

    private WeatherData restoreWeatherDataFromFile()
            throws StreamCorruptedException, FileNotFoundException,
            IOException, ClassNotFoundException {
        final InputStream persistenceFile = this.openFileInput(WEATHER_DATA_FILE);

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
}
