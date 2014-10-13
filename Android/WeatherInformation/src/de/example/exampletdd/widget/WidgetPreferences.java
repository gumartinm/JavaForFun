package de.example.exampletdd.widget;

import de.example.exampletdd.R;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

public class WidgetPreferences extends PreferenceFragment {

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

     // Retain this fragment across configuration changes.
    	this.setRetainInstance(true);
    	
    	final Bundle bundle = this.getArguments();
    	int appWidgetId = bundle.getInt("appWidgetId");
    	
        // Load the preferences from an XML resource
        this.addPreferencesFromResource(R.xml.appwidget_preferences);
        
        // Temperature units
        String[] values = this.getResources().getStringArray(R.array.weather_preferences_units_value);
        String[] humanValues = this.getResources().getStringArray(R.array.weather_preferences_units_human_value);
        String keyPreference = this.getActivity().getApplicationContext().getString(
                R.string.weather_preferences_temperature_key);
        Preference connectionPref = this.findPreference(keyPreference);
        String value = this.getPreferenceManager().getSharedPreferences()
                .getString(keyPreference, "");
        String humanValue = "";
        if (value.equals(values[0])) {
            humanValue = humanValues[0];
        } else if (value.equals(values[1])) {
            humanValue = humanValues[1];
        } else if (value.equals(values[2])) {
            humanValue = humanValues[2];
        }
        connectionPref.setSummary(humanValue);
        
        
        // Refresh interval
        values = this.getResources().getStringArray(R.array.weather_preferences_refresh_interval);
        humanValues = this.getResources().getStringArray(R.array.weather_preferences_refresh_interval_human_value);
        keyPreference = this.getActivity().getApplicationContext().getString(
                R.string.weather_preferences_refresh_interval_key);
        connectionPref = this.findPreference(keyPreference);
        value = this.getPreferenceManager().getSharedPreferences().getString(keyPreference, "");
        humanValue = "";
        if (value.equals(values[0])) {
            humanValue = humanValues[0];
        } else if (value.equals(values[1])) {
            humanValue = humanValues[1];
        } else if (value.equals(values[2])) {
            humanValue = humanValues[2];
        } else if (value.equals(values[3])) {
            humanValue = humanValues[3];
        } else if (value.equals(values[4])) {
            humanValue = humanValues[4];
        } else if (value.equals(values[5])) {
            humanValue = humanValues[5];
        } else if (value.equals(values[6])) {
            humanValue = humanValues[6];
        }
        connectionPref.setSummary(humanValue);
    }

	public void onSharedPreferenceChanged(final SharedPreferences sharedPreferences, final String key) {
		
    	// Temperature units
    	String[] values = this.getResources().getStringArray(R.array.weather_preferences_units_value);
    	String[] humanValues = this.getResources().getStringArray(R.array.weather_preferences_units_human_value);
        String keyValue = this.getActivity().getApplicationContext().getString(
                R.string.weather_preferences_temperature_key);
        if (key.equals(keyValue)) {
        	final Preference connectionPref = this.findPreference(key);
            final String value = sharedPreferences.getString(key, "");
        	String humanValue = "";
        	if (value.equals(values[0])) {
        		humanValue = humanValues[0];
        	} else if (value.equals(values[1])) {
        		humanValue = humanValues[1];
        	} else if (value.equals(values[2])) {
        		humanValue = humanValues[2];
        	}

        	connectionPref.setSummary(humanValue);
        	return;
        }
		
        // Refresh interval
        values = this.getResources().getStringArray(R.array.weather_preferences_refresh_interval);
        humanValues = this.getResources().getStringArray(R.array.weather_preferences_refresh_interval_human_value);
        keyValue = this.getActivity().getApplicationContext().getString(
                R.string.weather_preferences_refresh_interval_key);
        if (key.equals(keyValue)) {
        	final Preference connectionPref = this.findPreference(key);
            final String value = sharedPreferences.getString(key, "");
            String humanValue = "";
            if (value.equals(values[0])) {
                humanValue = humanValues[0];
            } else if (value.equals(values[1])) {
                humanValue = humanValues[1];
            } else if (value.equals(values[2])) {
                humanValue = humanValues[2];
            } else if (value.equals(values[3])) {
                humanValue = humanValues[3];
            } else if (value.equals(values[4])) {
                humanValue = humanValues[4];
            } else if (value.equals(values[5])) {
                humanValue = humanValues[5];
            } else if (value.equals(values[6])) {
                humanValue = humanValues[6];
            }
            connectionPref.setSummary(humanValue);
            return;
        }
	}
}
