package de.example.exampletdd.fragment.preferences;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import de.example.exampletdd.R;
import de.example.exampletdd.WeatherInformationBatch;

public class WeatherInformationPreferencesFragment extends PreferenceFragment 
													implements OnSharedPreferenceChangeListener {

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        this.addPreferencesFromResource(R.xml.weather_preferences);
        
        
        // Units of Measurement
        String keyPreference = this.getActivity().getApplicationContext().getString(
                R.string.weather_preferences_units_key);
        Preference connectionPref = this.findPreference(keyPreference);
        connectionPref.setSummary(this.getPreferenceManager()
                .getSharedPreferences().getString(keyPreference, ""));
        
        // Update Time Rate
        keyPreference = this.getActivity().getApplicationContext().getString(
                R.string.weather_preferences_update_time_rate_key);
        connectionPref = this.findPreference(keyPreference);
        String value = this.getPreferenceManager().getSharedPreferences()
                .getString(keyPreference, "");
        String humanValue = "";
        if (value.equals("0")) {
            humanValue = "no updates";
        } else if (value.equals("900")) {
            humanValue = "fifteen minutes";
        } else if (value.equals("1800")) {
            humanValue = "half hour";
        } else if (value.equals("3600")) {
            humanValue = "one hour";
        } else if (value.equals("43200")) {
            humanValue = "half day";
        } else if (value.equals("86400")) {
            humanValue = "one day";
        }
        connectionPref.setSummary(humanValue);
        
        // Forecast days number
        keyPreference = this.getActivity().getApplicationContext().getString(
                R.string.weather_preferences_day_forecast_key);
        connectionPref = this.findPreference(keyPreference);
        value = this.getPreferenceManager().getSharedPreferences().getString(keyPreference, "");
        humanValue = "";
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
    	
    	// Units of Measurement
        String keyValue = this.getActivity().getApplicationContext().getString(
                R.string.weather_preferences_units_key);

        if (key.equals(keyValue)) {
            final Preference connectionPref = this.findPreference(key);
            connectionPref.setSummary(sharedPreferences.getString(key, ""));
            return;
        }

        // Update Time Rate
        keyValue = this.getActivity().getApplicationContext().getString(
        		R.string.weather_preferences_update_time_rate_key);
        if (key.equals(keyValue)) {
            final Preference connectionPref = this.findPreference(key);
            final String value = sharedPreferences.getString(key, "");
            String humanValue = "";
            if (value.equals("0")) {
                humanValue = "no updates";
            } else if (value.equals("900")) {
                humanValue = "fifteen minutes";
            } else if (value.equals("1800")) {
                humanValue = "half hour";
            } else if (value.equals("3600")) {
                humanValue = "one hour";
            } else if (value.equals("43200")) {
                humanValue = "half day";
            } else if (value.equals("86400")) {
                humanValue = "one day";
            }
            
            this.updateAlarm(value);
            connectionPref.setSummary(humanValue);
            return;
        }

        // Forecast days number
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

    private void updateAlarm(final String updateTimeRate) {
        long chosenInterval = 0;
        if (updateTimeRate.equals("900")) {
        	chosenInterval = AlarmManager.INTERVAL_FIFTEEN_MINUTES;
        } else if (updateTimeRate.equals("1800")) {
        	chosenInterval = AlarmManager.INTERVAL_HALF_HOUR;
        } else if (updateTimeRate.equals("3600")) {
        	chosenInterval = AlarmManager.INTERVAL_HOUR;
        } else if (updateTimeRate.equals("43200")) {
        	chosenInterval = AlarmManager.INTERVAL_HALF_DAY;
        } else if (updateTimeRate.equals("86400")) {
        	chosenInterval = AlarmManager.INTERVAL_DAY;
        }

        final AlarmManager alarmMgr =
        		(AlarmManager) this.getActivity().getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        // TODO: better use some string instead of .class? In case I change the service class
        // this could be a problem (I guess)
        final Intent serviceIntent =
        		new Intent(this.getActivity().getApplicationContext(), WeatherInformationBatch.class);
        final PendingIntent alarmIntent =
        		PendingIntent.getService(
        				this.getActivity().getApplicationContext(),
        				0,
        				serviceIntent,
        				PendingIntent.FLAG_UPDATE_CURRENT);
        if (chosenInterval != 0) {   
            alarmMgr.setInexactRepeating(
            		AlarmManager.ELAPSED_REALTIME,
            		SystemClock.elapsedRealtime(),
            		chosenInterval,
            		alarmIntent);
        } else {
        	alarmMgr.cancel(alarmIntent);
        }
    }
}
