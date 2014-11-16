/**
 * Copyright 2014 Gustavo Martin Morcuende
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package name.gumartinm.weather.information.widget;

import android.app.ActionBar;
import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;

import name.gumartinm.weather.information.R;

public class WidgetConfigure extends Activity {
	private int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Find the widget id from the intent. 
        final Intent intent = getIntent();
        final Bundle extras = intent.getExtras();
        boolean isActionFromUser = false;

        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);

            isActionFromUser = extras.getBoolean("actionFromUser", false);
        }
        
        // If they gave us an intent without the widget id, just bail.
    	if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
    		this.finish();
    	}

        if (!isActionFromUser) {
            // Set the result to CANCELED.  This will cause the widget host to cancel
            // out of the widget placement if they press the back button.
            this.setResult(RESULT_CANCELED);
        }
        
        // Set the view layout resource to use.
        this.setContentView(R.layout.appwidget_configure);

        /******************* Show/hide country field *******************/
        String keyPreference = this.getApplicationContext().getString(
                R.string.widget_preferences_country_switch_key);
        String realKeyPreference = keyPreference + "_" + mAppWidgetId;

        // What was saved to permanent storage (or default values if it is the first time)
        final boolean isShowCountry = this.getSharedPreferences("WIDGET_PREFERENCES", Context.MODE_PRIVATE)
                .getBoolean(realKeyPreference, false);

        // What is shown on the screen
        final Switch countrySwitch = (Switch) this.findViewById(R.id.weather_appwidget_configure_country);
        countrySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
                if (isChecked) {
                    buttonView.setText(WidgetConfigure.this.getString(R.string.widget_preferences_country_switch_on_summary));
                } else {
                    buttonView.setText(WidgetConfigure.this.getString(R.string.widget_preferences_country_switch_off_summary));
                }
            }
        });
        if (isShowCountry) {
            countrySwitch.setChecked(true);
            countrySwitch.setText(this.getString(R.string.widget_preferences_country_switch_on_summary));
        } else {
            countrySwitch.setChecked(false);
            countrySwitch.setText(this.getString(R.string.widget_preferences_country_switch_off_summary));
        }

        /********************* Temperature units  **********************/
        keyPreference = this.getApplicationContext().getString(
                R.string.widget_preferences_temperature_units_key);
        realKeyPreference = keyPreference + "_" + mAppWidgetId;

        // What was saved to permanent storage (or default values if it is the first time)
        final int tempValue = this.getSharedPreferences("WIDGET_PREFERENCES", Context.MODE_PRIVATE).getInt(realKeyPreference, 0);

        // What is shown on the screen
        final Spinner tempUnits = (Spinner) this.findViewById(R.id.weather_appwidget_configure_temperature_units);
        tempUnits.setSelection(tempValue);

        /**
         * android:saveEnabled
         * Controls whether the saving of this view's state is enabled (that is, whether its onSaveInstanceState() method will be called).
         *
         * After onStart the onSaveInstanceState method will be called for every widget, so
         * I do not need to do anything else to retrieve the UI's state after changing orientation.
         *
         * I do not know if this is a good pattern, it does not look like that. I guess, I should use
         * on Resume instead of onCreate/onStart and implement my own onSaveInstanceState method.
         * But I am tired...
         */
    }

    @Override
    public void onResume() {
        super.onResume();

        final ActionBar actionBar = this.getActionBar();
        actionBar.setTitle(this.getString(R.string.widget_preferences_action_settings));
    }


    public void onClickRefresh(final View view) {
        // Push widget update to surface
        WidgetProvider.refreshAppWidget(this.getApplicationContext(), mAppWidgetId);
    }

    public void onClickOk(final View view) {
        // Save to permanent storage
        final SharedPreferences.Editor prefs = this.getSharedPreferences(
                                        "WIDGET_PREFERENCES",
                                        Context.MODE_PRIVATE).edit();

        /******************* Show/hide country field *******************/
        // What is shown on the screen
        final Switch countrySwitch = (Switch) this.findViewById(R.id.weather_appwidget_configure_country);
        String keyPreference = this.getApplicationContext().getString(
                R.string.widget_preferences_country_switch_key);
        String realKeyPreference = keyPreference + "_" + mAppWidgetId;
        prefs.putBoolean(realKeyPreference, countrySwitch.isChecked());

        /********************* Temperature units  **********************/
        // What is shown on the screen
        final Spinner tempUnits = (Spinner) this.findViewById(R.id.weather_appwidget_configure_temperature_units);
        keyPreference = this.getApplicationContext().getString(
                R.string.widget_preferences_temperature_units_key);
        realKeyPreference = keyPreference + "_" + mAppWidgetId;
        prefs.putInt(realKeyPreference, tempUnits.getSelectedItemPosition());

        /****************** Saving to permanent storage  ***************/
        prefs.commit();

        // Push widget update to surface with newly set prefix
        WidgetProvider.updateAppWidget(this.getApplicationContext(), mAppWidgetId);

        // Make sure we pass back the original appWidgetId
        final Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        this.setResult(RESULT_OK, resultValue);
        finish();
    }

    public static void deletePreference(final Context context, final int appWidgetId) {
        final SharedPreferences.Editor prefs = context.getApplicationContext()
                .getSharedPreferences("WIDGET_PREFERENCES", Context.MODE_PRIVATE).edit();

        /******************* Show/hide country field *******************/
        String keyPreference = context.getApplicationContext().getString(
                R.string.widget_preferences_country_switch_key);
        String realKeyPreference = keyPreference + "_" + appWidgetId;
        prefs.remove(realKeyPreference);

        /********************* Temperature units  **********************/
        keyPreference = context.getApplicationContext().getString(
                R.string.widget_preferences_temperature_units_key);
        realKeyPreference = keyPreference + "_" + appWidgetId;
        prefs.remove(realKeyPreference);

        /****************** Updating permanent storage  ***************/
        prefs.commit();
    }
}
