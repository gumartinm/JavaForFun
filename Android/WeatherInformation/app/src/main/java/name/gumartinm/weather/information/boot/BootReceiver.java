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
package name.gumartinm.weather.information.boot;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.preference.PreferenceManager;

import name.gumartinm.weather.information.R;
import name.gumartinm.weather.information.notification.NotificationIntentService;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {

        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
        	
        	// Update Time Rate
            final SharedPreferences sharedPreferences = PreferenceManager
                    .getDefaultSharedPreferences(context);
            final String keyPreference = context
                    .getString(R.string.weather_preferences_update_time_rate_key);
            final String updateTimeRate = sharedPreferences.getString(keyPreference, "");            
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

            if (chosenInterval != 0) {
                final AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                // TODO: better use some string instead of .class? In case I change the service class
                // this could be a problem (I guess)
                final Intent serviceIntent = new Intent(context, NotificationIntentService.class);
                final PendingIntent alarmIntent = PendingIntent.getService(
                		context,
                		0,
                		serviceIntent,
                		PendingIntent.FLAG_UPDATE_CURRENT);
                alarmMgr.setInexactRepeating(
                		AlarmManager.ELAPSED_REALTIME,
                		SystemClock.elapsedRealtime() + chosenInterval,
                		chosenInterval,
                		alarmIntent);
            }
        }
    }

}
