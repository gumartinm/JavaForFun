package de.example.exampletdd;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StreamCorruptedException;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import de.example.exampletdd.fragment.specific.WeatherInformationSpecificDataFragment;
import de.example.exampletdd.model.GeocodingData;
import de.example.exampletdd.service.WeatherServicePersistenceFile;

public class WeatherInformationSpecificDataActivity extends Activity {
    private static final String TAG = "WeatherInformationSpecificDataActivity";
    private WeatherServicePersistenceFile mWeatherServicePersistenceFile;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.weather_specific_data);

        PreferenceManager.setDefaultValues(this, R.xml.weather_preferences, false);

        final ActionBar actionBar = this.getActionBar();

        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE, ActionBar.DISPLAY_SHOW_TITLE);
        actionBar.setDisplayHomeAsUpEnabled(true);

        final WeatherInformationSpecificDataFragment fragment =
                new WeatherInformationSpecificDataFragment();

        if (savedInstanceState == null) {
            this.getFragmentManager().beginTransaction()
            .add(R.id.container, fragment).commit();
        }

        this.mWeatherServicePersistenceFile = new WeatherServicePersistenceFile(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        final ActionBar actionBar = this.getActionBar();

        GeocodingData geocodingData = null;
        try {
            geocodingData = this.mWeatherServicePersistenceFile
                    .getGeocodingData();
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
            final String city = (geocodingData.getCity() == null) ? this.getString(R.string.city_not_found)
                    : geocodingData.getCity();
            final String country = (geocodingData.getCountry() == null) ? this.getString(R.string.country_not_found)
                    : geocodingData.getCountry();
            actionBar.setTitle(city + "," + country);
        }

    }
}
