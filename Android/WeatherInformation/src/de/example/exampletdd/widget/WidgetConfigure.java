package de.example.exampletdd.widget;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import de.example.exampletdd.R;
import de.example.exampletdd.fragment.map.MapProgressFragment;

public class WidgetConfigure extends Activity {
	private int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
	
    final View.OnClickListener mOnClickListener = new View.OnClickListener() {
        public void onClick(View v) {

        	
            // When the button is clicked, save the string in our prefs and return that they
            // clicked OK.
            // Push widget update to surface with newly set prefix
            final AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(
            		WidgetConfigure.this.getApplicationContext());
            WidgetProvider.updateAppWidget(
            		WidgetConfigure.this.getApplicationContext(),
            		appWidgetManager,
                    mAppWidgetId);

            // Make sure we pass back the original appWidgetId
            final Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
            WidgetConfigure.this.setResult(RESULT_OK, resultValue);
            finish();
        }
    };

    @Override
    public void onCreate(final Bundle icicle) {
        super.onCreate(icicle);
     
        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if they press the back button.
        this.setResult(RESULT_CANCELED);

        // Find the widget id from the intent. 
        final Intent intent = getIntent();
        final Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }
        
        // Set the view layout resource to use.
        this.setContentView(R.layout.appwidget_configure);
        
    	final Bundle args = new Bundle();
    	args.putInt("appWidgetId", mAppWidgetId);
    	final Fragment preferences = new WidgetPreferences();
        preferences.setRetainInstance(true);
    	preferences.setArguments(args);
        this.getFragmentManager()
        .beginTransaction()
        .replace(R.id.weather_appwidget_configure_preferences, preferences)
        .commit();
        
        // Bind the action for the save button.
        this.findViewById(R.id.weather_appwidget_configure_save_button).setOnClickListener(mOnClickListener);

   
        // If they gave us an intent without the widget id, just bail.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
        	this.finish();
        }
    }
    
    @Override
    public void onResume() {
        super.onResume();

        final ActionBar actionBar = this.getActionBar();
        actionBar.setTitle(this.getString(R.string.weather_preferences_actionbar_title));
    }
}
