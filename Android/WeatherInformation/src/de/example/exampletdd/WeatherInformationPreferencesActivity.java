package de.example.exampletdd;

import android.app.Activity;
import android.os.Bundle;
import de.example.exampletdd.fragment.WeatherInformationPreferencesFragment;

public class WeatherInformationPreferencesActivity extends Activity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.getFragmentManager()
        .beginTransaction()
        .replace(android.R.id.content,
                new WeatherInformationPreferencesFragment()).commit();
    }

}
