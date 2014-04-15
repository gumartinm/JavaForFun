package de.example.exampletdd;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceManager;
import de.example.exampletdd.fragment.specific.WeatherInformationSpecificDataFragment;
import de.example.exampletdd.model.GeocodingData;
import de.example.exampletdd.service.WeatherServicePersistenceFile;

public class WeatherInformationCurrentDataActivity extends Activity {
    private WeatherServicePersistenceFile mWeatherServicePersistenceFile;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.weather_current_data);

        final ActionBar actionBar = this.getActionBar();

        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE, ActionBar.DISPLAY_SHOW_TITLE);
        actionBar.setDisplayHomeAsUpEnabled(true);

        final WeatherInformationSpecificDataFragment fragment = new WeatherInformationSpecificDataFragment();

        if (savedInstanceState == null) {
            this.getFragmentManager().beginTransaction().add(R.id.container, fragment).commit();
        }

        this.mWeatherServicePersistenceFile = new WeatherServicePersistenceFile(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        final ActionBar actionBar = this.getActionBar();

        actionBar.setTitle("Current weather information");

        final GeocodingData geocodingData = this.mWeatherServicePersistenceFile.getGeocodingData();

        if (geocodingData != null) {
            final String city = (geocodingData.getCity() == null) ? this
                    .getString(R.string.city_not_found) : geocodingData.getCity();
                    final String country = (geocodingData.getCountry() == null) ? this
                            .getString(R.string.country_not_found) : geocodingData.getCountry();
                            actionBar.setTitle("Current weather data information");
                            actionBar.setSubtitle(city + "," + country);
        }

    }
}
