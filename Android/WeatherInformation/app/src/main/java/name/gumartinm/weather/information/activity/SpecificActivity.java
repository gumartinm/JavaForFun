package name.gumartinm.weather.information.activity;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import name.gumartinm.weather.information.R;
import name.gumartinm.weather.information.model.DatabaseQueries;
import name.gumartinm.weather.information.model.WeatherLocation;

import java.text.MessageFormat;
import java.util.Locale;

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
            final String[] array = new String[2];
            array[0] = weatherLocation.getCity();
            array[1] = weatherLocation.getCountry();
            final MessageFormat message = new MessageFormat("{0},{1}", Locale.US);
            final String cityCountry = message.format(array);
            actionBar.setTitle(cityCountry);
        }
    }
}
