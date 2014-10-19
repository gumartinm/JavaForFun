package de.example.exampletdd;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import de.example.exampletdd.model.DatabaseQueries;
import de.example.exampletdd.model.WeatherLocation;

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

        // 1. Update title.
        final DatabaseQueries query = new DatabaseQueries(this);
        final WeatherLocation weatherLocation = query.queryDataBase();
        if (weatherLocation != null) {
        	final ActionBar actionBar = this.getActionBar();
            // TODO: I18N and comma :/
            actionBar.setTitle(weatherLocation.getCity() + "," + weatherLocation.getCountry());	
        }
    }
}
