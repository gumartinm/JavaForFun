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
import android.preference.SwitchPreference;
import de.example.exampletdd.R;
import de.example.exampletdd.NotificationIntentService;

public class WeatherInformationPreferencesFragment extends PreferenceFragment 
													implements OnSharedPreferenceChangeListener {

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        this.addPreferencesFromResource(R.xml.weather_preferences);
        
        
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
        
        // Wind
        values = this.getResources().getStringArray(R.array.weather_preferences_wind);
        humanValues = this.getResources().getStringArray(R.array.weather_preferences_wind_human_value);
        keyPreference = this.getString(R.string.weather_preferences_wind_key);
        connectionPref = this.findPreference(keyPreference);
        value = this.getPreferenceManager().getSharedPreferences().getString(keyPreference, "");
        humanValue = "";
        if (value.equals(values[0])) {
            humanValue = humanValues[0];
        } else if (value.equals(values[1])) {
            humanValue = humanValues[1];
        }
        connectionPref.setSummary(humanValue);

        // Pressure
        values = this.getResources().getStringArray(R.array.weather_preferences_pressure);
        humanValues = this.getResources().getStringArray(R.array.weather_preferences_pressure_human_value);
        keyPreference = this.getString(R.string.weather_preferences_pressure_key);
        connectionPref = this.findPreference(keyPreference);
        value = this.getPreferenceManager().getSharedPreferences().getString(keyPreference, "");
        humanValue = "";
        if (value.equals(values[0])) {
            humanValue = humanValues[0];
        } else if (value.equals(values[1])) {
            humanValue = humanValues[1];
        }
        connectionPref.setSummary(humanValue);

        // Forecast days number
        values = this.getResources().getStringArray(R.array.weather_preferences_day_forecast);
        humanValues = this.getResources().getStringArray(R.array.weather_preferences_day_forecast_human_value);
        keyPreference = this.getActivity().getApplicationContext().getString(
                R.string.weather_preferences_day_forecast_key);
        connectionPref = this.findPreference(keyPreference);
        value = this.getPreferenceManager().getSharedPreferences().getString(keyPreference, "");
        humanValue = "";
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

        // Update Time Rate
        values = this.getResources().getStringArray(R.array.weather_preferences_update_time_rate);
        humanValues = this.getResources().getStringArray(R.array.weather_preferences_update_time_rate_human_value);
        keyPreference = this.getActivity().getApplicationContext().getString(
                R.string.weather_preferences_update_time_rate_key);
        connectionPref = this.findPreference(keyPreference);
        value = this.getPreferenceManager().getSharedPreferences()
                .getString(keyPreference, "");
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

        // Wind
        values = this.getResources().getStringArray(R.array.weather_preferences_wind);
        humanValues = this.getResources().getStringArray(R.array.weather_preferences_wind_human_value);
        keyValue = this.getString(R.string.weather_preferences_wind_key);
        if (key.equals(keyValue)) {
            final Preference connectionPref = this.findPreference(key);
            final String value = sharedPreferences.getString(key, "");
            String humanValue = "";
            if (value.equals(values[0])) {
            	humanValue = humanValues[0];
            } else if (value.equals(values[1])) {
            	humanValue = humanValues[1];
            }
        
        	connectionPref.setSummary(humanValue);
        	return;
        }

        // Pressure
        values = this.getResources().getStringArray(R.array.weather_preferences_pressure);
        humanValues = this.getResources().getStringArray(R.array.weather_preferences_pressure_human_value);
        keyValue = this.getString(R.string.weather_preferences_pressure_key);
        if (key.equals(keyValue)) {
            final Preference connectionPref = this.findPreference(key);
            final String value = sharedPreferences.getString(key, "");
            String humanValue = "";
            if (value.equals(values[0])) {
            	humanValue = humanValues[0];
            } else if (value.equals(values[1])) {
            	humanValue = humanValues[1];
            }
        
        	connectionPref.setSummary(humanValue);
        	return;
        }

        // Forecast days number
        values = this.getResources().getStringArray(R.array.weather_preferences_day_forecast);
        humanValues = this.getResources().getStringArray(R.array.weather_preferences_day_forecast_human_value);
        keyValue = this.getActivity().getString(
                R.string.weather_preferences_day_forecast_key);
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

        // Notification switch
        keyValue = this.getActivity().getApplicationContext().getString(
        		R.string.weather_preferences_notifications_switch_key);
        if (key.equals(keyValue)) {
        	final SwitchPreference preference = (SwitchPreference)this.findPreference(key);
        	if (preference.isChecked())
        	{
        		keyValue = this.getActivity().getApplicationContext().getString(
                		R.string.weather_preferences_update_time_rate_key);
        		final String value = sharedPreferences.getString(keyValue, "");
        		this.updateNotification(value);
        	} else {
        		this.cancelNotification();
        	}
        }
        // Update Time Rate
        values = this.getResources().getStringArray(R.array.weather_preferences_update_time_rate);
        humanValues = this.getResources().getStringArray(R.array.weather_preferences_update_time_rate_human_value);
        keyValue = this.getActivity().getApplicationContext().getString(
        		R.string.weather_preferences_update_time_rate_key);
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
            }

            this.updateNotification(value);
            connectionPref.setSummary(humanValue);
            return;
        }
    }

    private void updateNotification(final String updateTimeRate) {
    	final String[] values = this.getResources().getStringArray(R.array.weather_preferences_update_time_rate);
        long chosenInterval = 0;
        if (updateTimeRate.equals(values[0])) {
        	chosenInterval = AlarmManager.INTERVAL_FIFTEEN_MINUTES;
        } else if (updateTimeRate.equals(values[1])) {
        	chosenInterval = AlarmManager.INTERVAL_HALF_HOUR;
        } else if (updateTimeRate.equals(values[2])) {
        	chosenInterval = AlarmManager.INTERVAL_HOUR;
        } else if (updateTimeRate.equals(values[3])) {
        	chosenInterval = AlarmManager.INTERVAL_HALF_DAY;
        } else if (updateTimeRate.equals(values[4])) {
        	chosenInterval = AlarmManager.INTERVAL_DAY;
        }

        final AlarmManager alarmMgr =
        		(AlarmManager) this.getActivity().getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        // TODO: better use some string instead of .class? In case I change the service class
        // this could be a problem (I guess)
        final Intent serviceIntent =
        		new Intent(this.getActivity().getApplicationContext(), NotificationIntentService.class);
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
        }
    }

    private void cancelNotification() {
    	final AlarmManager alarmMgr =
        		(AlarmManager) this.getActivity().getApplicationContext().getSystemService(Context.ALARM_SERVICE);
    	final Intent serviceIntent =
        		new Intent(this.getActivity().getApplicationContext(), NotificationIntentService.class);
    	final PendingIntent alarmIntent =
        		PendingIntent.getService(
        				this.getActivity().getApplicationContext(),
        				0,
        				serviceIntent,
        				PendingIntent.FLAG_UPDATE_CURRENT);
    	alarmMgr.cancel(alarmIntent);
    }
}
