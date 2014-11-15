package name.gumartinm.weather.information.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;

import name.gumartinm.weather.information.R;
import name.gumartinm.weather.information.fragment.preferences.WeatherInformationPreferencesFragment;

public class WeatherInformationPreferencesActivity extends Activity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.getFragmentManager()
        .beginTransaction()
        .replace(android.R.id.content,
                new WeatherInformationPreferencesFragment()).commit();
    }

    @Override
    public void onResume() {
        super.onResume();

        final ActionBar actionBar = this.getActionBar();
        actionBar.setTitle(this.getString(R.string.weather_preferences_action_settings));
    }
}
