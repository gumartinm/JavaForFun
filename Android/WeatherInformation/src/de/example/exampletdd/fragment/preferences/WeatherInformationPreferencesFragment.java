package de.example.exampletdd.fragment.preferences;

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

        String keyPreference = this.getActivity().getString(
                R.string.weather_preferences_units_key);
        Preference connectionPref = this.findPreference(keyPreference);
        connectionPref.setSummary(this.getPreferenceManager()
                .getSharedPreferences().getString(keyPreference, ""));

        keyPreference = this.getActivity().getString(
                R.string.weather_preferences_language_key);
        connectionPref = this.findPreference(keyPreference);
        connectionPref.setSummary(this.getPreferenceManager()
                .getSharedPreferences().getString(keyPreference, ""));

        keyPreference = this.getActivity().getString(
                R.string.weather_preferences_day_forecast_key);
        connectionPref = this.findPreference(keyPreference);
        final String value = this.getPreferenceManager().getSharedPreferences().getString(keyPreference, "");
        String humanValue = "";
        if (value.equals("5")) {
            humanValue = "5-Day Forecast";
        } else if (value.equals("10")) {
            humanValue = "10-Day Forecast";
        } else if (value.equals("14")) {
            humanValue = "14-Day Forecast";
        }
        connectionPref.setSummary(humanValue);
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
        String keyValue = this.getActivity().getString(
                R.string.weather_preferences_units_key);

        if (key.equals(keyValue)) {
            final Preference connectionPref = this.findPreference(key);
            connectionPref.setSummary(sharedPreferences.getString(key, ""));
            return;
        }

        keyValue = this.getActivity().getString(
                R.string.weather_preferences_language_key);
        if (key.equals(keyValue)) {
            final Preference connectionPref = this.findPreference(key);
            connectionPref.setSummary(sharedPreferences.getString(key, ""));
            return;
        }

        keyValue = this.getActivity().getString(
                R.string.weather_preferences_day_forecast_key);
        if (key.equals(keyValue)) {
            final Preference connectionPref = this.findPreference(key);
            final String value = sharedPreferences.getString(key, "");
            String humanValue = "";
            if (value.equals("5")) {
                humanValue = "5-Day Forecast";
            } else if (value.equals("10")) {
                humanValue = "10-Day Forecast";
            } else if (value.equals("14")) {
                humanValue = "14-Day Forecast";
            }
            connectionPref.setSummary(humanValue);
            return;
        }

    }

}
