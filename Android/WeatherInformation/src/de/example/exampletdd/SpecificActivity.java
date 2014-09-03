package de.example.exampletdd;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import de.example.exampletdd.model.GeocodingData;

public class SpecificActivity extends FragmentActivity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.weather_specific);

        final ActionBar actionBar = this.getActionBar();

        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE, ActionBar.DISPLAY_SHOW_TITLE);
        actionBar.setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public void onResume() {
        super.onResume();

        // TODO: retrive data from data base (like I do on WindowsPhone 8)
        // 1. Update title.
        final GeocodingData geocodingData = new GeocodingData.Builder().build();
        if (geocodingData != null) {
        	final String city = (geocodingData.getCity() == null) ? this.getString(R.string.city_not_found)
                    : geocodingData.getCity();
            final String country = (geocodingData.getCountry() == null) ? this.getString(R.string.country_not_found)
                    : geocodingData.getCountry();
            final ActionBar actionBar = this.getActionBar();
            // TODO: I18N and comma :/
            actionBar.setTitle(city + "," + country);	
        }
    }
}
