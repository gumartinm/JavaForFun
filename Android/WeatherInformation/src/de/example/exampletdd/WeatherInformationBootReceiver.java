package de.example.exampletdd;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.preference.PreferenceManager;

public class WeatherInformationBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {
        // TODO: there should be some option in the application if user does not want to set
        // alarm in boot time.
        
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            final SharedPreferences sharedPreferences = PreferenceManager
                    .getDefaultSharedPreferences(context);
            final String keyPreference = context
                    .getString(R.string.weather_preferences_update_time_rate_key);
            final String updateTimeRate = sharedPreferences.getString(keyPreference, "");
            final int timeRate = Integer.valueOf(updateTimeRate);

            final AlarmManager alarmMgr = (AlarmManager) context
                    .getSystemService(Context.ALARM_SERVICE);
            // TODO: better use some string instead of .class? In case I change the service class
            // this could be a problem (I guess)
            final Intent serviceIntent = new Intent(context, WeatherInformationBatch.class);
            final PendingIntent alarmIntent = PendingIntent.getService(context, 0, serviceIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            alarmMgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime()
                    + (timeRate * 1000), (timeRate * 1000), alarmIntent);
        }
    }

}
