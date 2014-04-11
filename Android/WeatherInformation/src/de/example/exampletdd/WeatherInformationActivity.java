package de.example.exampletdd;

import android.app.ActionBar;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import de.example.exampletdd.activityinterface.GetWeather;
import de.example.exampletdd.fragment.overview.WeatherInformationOverviewFragment;
import de.example.exampletdd.model.GeocodingData;
import de.example.exampletdd.service.WeatherServicePersistenceFile;

public class WeatherInformationActivity extends Activity {
    private GetWeather mGetWeather;
    private WeatherServicePersistenceFile mWeatherServicePersistenceFile;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.weather_main);

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
        final WeatherInformationOverviewFragment weatherOverviewFragment = (WeatherInformationOverviewFragment) this
                .getFragmentManager().findFragmentById(R.id.weather_overview_fragment);

        this.mGetWeather = weatherOverviewFragment;

        this.mWeatherServicePersistenceFile = new WeatherServicePersistenceFile(
                this);
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

        final GeocodingData geocodingData =
                this.mWeatherServicePersistenceFile.getGeocodingData();

        if (geocodingData != null) {
            final String city = (geocodingData.getCity() == null) ? this.getString(R.string.city_not_found)
                    : geocodingData.getCity();
            final String country = (geocodingData.getCountry() == null) ? this.getString(R.string.country_not_found)
                    : geocodingData.getCountry();
            actionBar.setTitle(city + "," + country);
        }

    }


    public void getWeather() {
        this.mGetWeather.getWeather();
    }
}
