package de.example.exampletdd.widget;

import android.appwidget.AppWidgetManager;
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
    private int mAppWidgetId;
    private boolean mIsCountry;
    private String mTempUnits;
	
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Do not retain this fragment across configuration changes because I am tired for following
        // the fragment lifecycle (I am going to loose the instance field values but I DON'T CARE!!!)
        this.setRetainInstance(false);
    	
    	final Bundle bundle = this.getArguments();
    	mAppWidgetId = bundle.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
    	
        // Load the preferences from an XML resource
        this.addPreferencesFromResource(R.xml.appwidget_preferences);


        /******************* Show/hide country field *******************/
        String keyPreference = this.getActivity().getApplicationContext().getString(
                R.string.widget_preferences_country_switch_key);
        String realKeyPreference = keyPreference + "_" + mAppWidgetId;
        
        // What was saved to permanent storage (or default values if it is the first time)
        boolean countryValue = this.getActivity().getSharedPreferences("WIDGET_PREFERENCES", Context.MODE_PRIVATE)
        		.getBoolean(realKeyPreference, false);
        
        // What is shown on the screen
        final SwitchPreference countryPref = (SwitchPreference) this.findPreference(keyPreference);
        countryPref.setChecked(countryValue);

        /********************* Temperature units  **********************/
        final String[] values = this.getResources().getStringArray(R.array.weather_preferences_temperature);
        final String[] humanValues = this.getResources().getStringArray(R.array.weather_preferences_temperature_human_value);

        keyPreference = this.getActivity().getApplicationContext().getString(
                R.string.widget_preferences_temperature_key);
        realKeyPreference = keyPreference + "_" + mAppWidgetId;


        // What was saved to permanent storage (or default values if it is the first time)
        final String tempValue = this.getActivity().getSharedPreferences("WIDGET_PREFERENCES", Context.MODE_PRIVATE)
        		.getString(realKeyPreference, this.getString(R.string.weather_preferences_temperature_celsius));
        String humanValue = this.getString(R.string.weather_preferences_temperature_celsius_human_value);
        int index = 0;
        if (tempValue.equals(values[0])) {
        	index = 0;
            humanValue = humanValues[0];
        } else if (tempValue.equals(values[1])) {
        	index = 1;
            humanValue = humanValues[1];
        } else if (tempValue.equals(values[2])) {
        	index = 2;
            humanValue = humanValues[2];
        }


        // What is shown on the screen
        final ListPreference listPref = (ListPreference) this.findPreference(keyPreference);
        listPref.setSummary(humanValue);
        listPref.setValueIndex(index);
        listPref.setValue(tempValue);
    }

    @Override
    public void onSharedPreferenceChanged(final SharedPreferences sharedPreferences, final String key) {

        /******************* Show/hide country field *******************/
        String keyPreference = this.getActivity().getApplicationContext().getString(
                R.string.widget_preferences_country_switch_key);
        if (key.equals(keyPreference)) {
            // What is shown on the screen
            final SwitchPreference preference = (SwitchPreference) this.findPreference(key);
            // Update temporal value
            mIsCountry = preference.isChecked();

            return;
        }

        /********************* Temperature units  **********************/
        keyPreference = this.getActivity().getApplicationContext().getString(
                R.string.widget_preferences_temperature_key);
        if (key.equals(keyPreference)) {
            final String[] values = this.getResources().getStringArray(
                    R.array.weather_preferences_temperature);
            final String[] humanValues = this.getResources().getStringArray(
                    R.array.weather_preferences_temperature_human_value);

            // What is shown on the screen
            final ListPreference listPref = (ListPreference) this.findPreference(key);
            final String value = listPref.getValue();
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

            // Update temporal value
            mTempUnits = value;

            return;
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

    public void onSavePreferences() {
        final SharedPreferences.Editor prefs =
                this.getActivity().getSharedPreferences(
                        "WIDGET_PREFERENCES",
                        Context.MODE_PRIVATE).edit();

        /******************* Show/hide country field *******************/
        String keyPreference = this.getActivity().getApplicationContext().getString(
                R.string.widget_preferences_country_switch_key);
        String realKeyPreference = keyPreference + "_" + mAppWidgetId;
        prefs.putBoolean(realKeyPreference, mIsCountry);


        /********************* Temperature units  **********************/
        keyPreference = this.getActivity().getApplicationContext().getString(
                R.string.widget_preferences_temperature_key);
        realKeyPreference = keyPreference + "_" + mAppWidgetId;
        prefs.putString(realKeyPreference, mTempUnits);

        // Saving to permanent storage.
        prefs.commit();
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
