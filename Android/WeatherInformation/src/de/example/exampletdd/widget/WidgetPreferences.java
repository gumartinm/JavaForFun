package de.example.exampletdd.widget;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import de.example.exampletdd.R;

/**
 * TODO:
 * IT DOES NOT WORK IF USER IS WORKING WITH TWO OR MORE WIDGET PREFERENCE WINDOWS AT THE SAME TIME
 * (hopefully nobody will realize...)
 * How to implement custom preference activities (no extending from PreferenceActivity or PreferenceFragment)
 * without pain?
 */
public class WidgetPreferences extends PreferenceFragment implements OnSharedPreferenceChangeListener {
	private int appWidgetId;
	
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retain this fragment across configuration changes.
    	this.setRetainInstance(true);
    	
    	final Bundle bundle = this.getArguments();
    	appWidgetId = bundle.getInt("appWidgetId");
    	
        // Load the preferences from an XML resource
        this.addPreferencesFromResource(R.xml.appwidget_preferences);

        
        /******************* Show/hide country field *******************/

        /******************* Temperature units  *******************/
        String[] values = this.getResources().getStringArray(R.array.weather_preferences_temperature);
        String[] humanValues = this.getResources().getStringArray(R.array.weather_preferences_temperature_human_value);

        String keyPreference = this.getActivity().getApplicationContext().getString(
                R.string.widget_preferences_temperature_key);
        String realKeyPreference = keyPreference + "_" + appWidgetId;


        // What was saved to permanent storage (or default values if it is the first time)
        String value = this.getActivity().getSharedPreferences("WIDGET_PREFERENCES", Context.MODE_PRIVATE)
        		.getString(realKeyPreference, this.getString(R.string.weather_preferences_temperature_celsius));
        String humanValue = this.getString(R.string.weather_preferences_temperature_celsius_human_value);
        int index = 0;
        if (value.equals(values[0])) {
        	index = 0;
            humanValue = humanValues[0];
        } else if (value.equals(values[1])) {
        	index = 1;
            humanValue = humanValues[1];
        } else if (value.equals(values[2])) {
        	index = 2;
            humanValue = humanValues[2];
        }


        // What is shown on the screen
        final ListPreference listPref = (ListPreference) this.findPreference(keyPreference);
        listPref.setSummary(humanValue);
        listPref.setValueIndex(index);
        listPref.setValue(value);
    }

    @Override
	public void onSharedPreferenceChanged(final SharedPreferences sharedPreferences, final String key) {
		
    	// Temperature units
        String keyValue = this.getActivity().getApplicationContext().getString(
                R.string.widget_preferences_temperature_key);
        if (key.equals(keyValue)) {
        	final String[] values = this.getResources().getStringArray(
        			R.array.weather_preferences_temperature);
        	final String[] humanValues = this.getResources().getStringArray(
        			R.array.weather_preferences_temperature_human_value);
        	
        	final ListPreference listPref = (ListPreference) this.findPreference(key);
        	final String value = listPref.getValue();
        	// What was saved to permanent storage
            //final String value = sharedPreferences.getString(key, "");
        	String humanValue = "";
        	if (value.equals(values[0])) {
        		humanValue = humanValues[0];
        	} else if (value.equals(values[1])) {
        		humanValue = humanValues[1];
        	} else if (value.equals(values[2])) {
        		humanValue = humanValues[2];
        	}
        	// Update data on screen
        	listPref.setSummary(humanValue);
        	
        	
        	// Saving to permanent storage.
        	final String keyPreference = this.getActivity().getApplicationContext().getString(
                    R.string.widget_preferences_temperature_key);
            final String realKeyPreference = keyPreference + "_" + appWidgetId;
            
        	final SharedPreferences.Editor prefs =
        			this.getActivity().getSharedPreferences(
        					"WIDGET_PREFERENCES", 
        					Context.MODE_PRIVATE).edit();
            prefs.putString(realKeyPreference, value);
            prefs.commit();
        	return;
        }
        
        
        // Notification switch
        keyValue = this.getActivity().getApplicationContext().getString(
        		R.string.widget_preferences_country_switch_key);
        if (key.equals(keyValue)) {
        	final SwitchPreference preference = (SwitchPreference) this.findPreference(key);
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
    
    public static void deletePreference(final Context context, final int appWidgetId) {
    	final String keyPreference = context.getApplicationContext().getString(
                R.string.widget_preferences_temperature_key);
        final String realKeyPreference = keyPreference + "_" + appWidgetId;

    	final SharedPreferences.Editor prefs = context.getSharedPreferences("WIDGET_PREFERENCES", Context.MODE_PRIVATE).edit();
    	prefs.remove(realKeyPreference);
    	prefs.commit();
    }
}
