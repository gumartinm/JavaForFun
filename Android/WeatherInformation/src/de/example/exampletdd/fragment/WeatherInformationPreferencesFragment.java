package de.example.exampletdd.fragment;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import de.example.exampletdd.R;

public class WeatherInformationPreferencesFragment extends PreferenceFragment
implements OnSharedPreferenceChangeListener {

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        this.addPreferencesFromResource(R.xml.weather_preferences);

        final String unitsKey = this.getResources().getString(
                R.string.weather_preferences_units_key);
        final Preference connectionPref = this.findPreference(unitsKey);
        this.getPreferenceManager().getSharedPreferences()
                .getString(unitsKey, "");
        connectionPref.setSummary(this.getPreferenceManager()
                .getSharedPreferences().getString(unitsKey, ""));
    }

    @Override
    public void onResume() {
        super.onResume();
        this.getPreferenceManager().getSharedPreferences()
        .registerOnSharedPreferenceChangeListener(this);

    }

    @Override
    public void onPause() {
        super.onPause();
        this.getPreferenceManager().getSharedPreferences()
        .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(
            final SharedPreferences sharedPreferences, final String key) {
        final String unitsKey = this.getResources().getString(
                R.string.weather_preferences_units_key);

        if (key.equals(unitsKey)) {
            final Preference connectionPref = this.findPreference(key);
            connectionPref.setSummary(sharedPreferences.getString(key, ""));
        }

    }

}
