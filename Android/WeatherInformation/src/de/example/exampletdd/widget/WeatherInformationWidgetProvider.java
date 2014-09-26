package de.example.exampletdd.widget;


import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Locale;

import org.apache.http.client.ClientProtocolException;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.http.AndroidHttpClient;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.RemoteViews;

import com.fasterxml.jackson.core.JsonParseException;

import de.example.exampletdd.R;
import de.example.exampletdd.WeatherTabsActivity;
import de.example.exampletdd.httpclient.CustomHTTPClient;
import de.example.exampletdd.model.DatabaseQueries;
import de.example.exampletdd.model.WeatherLocation;
import de.example.exampletdd.model.currentweather.Current;
import de.example.exampletdd.parser.JPOSWeatherParser;
import de.example.exampletdd.service.IconsList;
import de.example.exampletdd.service.ServiceParser;

public class WeatherInformationWidgetProvider extends AppWidgetProvider {
    private static final String TAG = "WeatherInformationWidgetProvider";

    @Override
    public void onUpdate(final Context context, final AppWidgetManager appWidgetManager, final int[] appWidgetIds) {
        Log.d(TAG, "onUpdate");
        // For each widget that needs an update, get the text that we should display:
        //   - Create a RemoteViews object for it
        //   - Set the text in the RemoteViews object
        //   - Tell the AppWidgetManager to show that views object for the widget.
        final int N = appWidgetIds.length;
        for (int i=0; i<N; i++) {
            int appWidgetId = appWidgetIds[i];
            // To prevent any ANR timeouts, we perform the update in a service
        	final Intent intent = new Intent(context, UpdateService.class);
        	intent.putExtra("appWidgetId", appWidgetId);
            context.startService(intent);
            //updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }
    
    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        Log.d(TAG, "onDeleted");
        // When the user deletes the widget, delete the preference associated with it.
        final int N = appWidgetIds.length;
        for (int i=0; i<N; i++) {
            //ExampleAppWidgetConfigure.deleteTitlePref(context, appWidgetIds[i]);
        }
    }
    
    static void updateAppWidget(final Context context, final AppWidgetManager appWidgetManager, final int appWidgetId) {
        Log.i(TAG, "updateAppWidget appWidgetId=" + appWidgetId);

        int widgetId;
        Bundle myOptions = appWidgetManager.getAppWidgetOptions(appWidgetId);

        // Get the value of OPTION_APPWIDGET_HOST_CATEGORY
        int category = myOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_HOST_CATEGORY, -1);

        // If the value is WIDGET_CATEGORY_KEYGUARD, it's a lockscreen widget
        boolean isKeyguard = category == AppWidgetProviderInfo.WIDGET_CATEGORY_KEYGUARD;
        
        // Once you know the widget's category, you can optionally load a different base layout, set different 
        // properties, and so on. For example:
        //int baseLayout = isKeyguard ? R.layout.keyguard_widget_layout : R.layout.widget_layout;
        
        // Construct the RemoteViews object.  It takes the package name (in our case, it's our
        // package, but it needs this because on the other side it's the widget host inflating
        // the layout from our package).
        final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.appwidget);

        // Tell the widget manager
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }
    
    public static class UpdateService extends IntentService {
        private static final String TAG = "UpdateService";


        public UpdateService() {
            super("UpdateWidget-Thread");
        }

        @Override
        protected void onHandleIntent(final Intent intent) {
        	int appWidgetId = intent.getIntExtra("appWidgetId", -666);
        	
        	if (appWidgetId == -666) {
        		// Nothing to do. Something went wrong.
        		Log.e(TAG, "appWidgetId error value");
        		return;
        	}
        	
            final DatabaseQueries query = new DatabaseQueries(this.getApplicationContext());
            final WeatherLocation weatherLocation = query.queryDataBase();
            
            if (weatherLocation != null) {
                final ServiceParser weatherService = new ServiceParser(new JPOSWeatherParser());
                final CustomHTTPClient HTTPClient = new CustomHTTPClient(
                        AndroidHttpClient.newInstance("Android 4.3 WeatherInformation Agent"));

                Current current = null;
                try {
                	current = this.doInBackgroundThrowable(weatherLocation, HTTPClient, weatherService);
                    
                } catch (final JsonParseException e) {
                    Log.e(TAG, "doInBackground exception: ", e);
                } catch (final ClientProtocolException e) {
                    Log.e(TAG, "doInBackground exception: ", e);
                } catch (final MalformedURLException e) {
                    Log.e(TAG, "doInBackground exception: ", e);
                } catch (final URISyntaxException e) {
                    Log.e(TAG, "doInBackground exception: ", e);
                } catch (final IOException e) {
                    // logger infrastructure swallows UnknownHostException :/
                    Log.e(TAG, "doInBackground exception: " + e.getMessage(), e);
                } finally {
                    HTTPClient.close();
                }
                
                if (current != null) {
                	this.updateWidget(current, weatherLocation, appWidgetId);
                } else {
                	// TODO: show layout error in Widget
                }
                
            } else {
            	// TODO: show layout error in Widget
            }
        }

        private Current doInBackgroundThrowable(final WeatherLocation weatherLocation,
                final CustomHTTPClient HTTPClient, final ServiceParser weatherService)
                        throws ClientProtocolException, MalformedURLException, URISyntaxException,
                        JsonParseException, IOException {

            final String APIVersion = this.getResources().getString(R.string.api_version);

            final String urlAPI = this.getResources().getString(R.string.uri_api_weather_today);
            final String url = weatherService.createURIAPICurrent(urlAPI, APIVersion,
                    weatherLocation.getLatitude(), weatherLocation.getLongitude());
            final String jsonData = HTTPClient.retrieveDataAsString(new URL(url));
            final Current current = weatherService.retrieveCurrentFromJPOS(jsonData);
            // TODO: what is this for? I guess I could skip it :/
            final Calendar now = Calendar.getInstance();
            current.setDate(now.getTime());
            
            return current;
        }
        
        private void updateWidget(final Current current, final WeatherLocation weatherLocation, final int appWidgetId) {
            final SharedPreferences sharedPreferences = PreferenceManager
                    .getDefaultSharedPreferences(this.getApplicationContext());

            // TODO: repeating the same code in Overview, Specific and Current!!!
            // 1. Update units of measurement.
            boolean mIsFahrenheit = false;
            final String keyPreference = this.getResources().getString(
                    R.string.weather_preferences_units_key);
            final String unitsPreferenceValue = sharedPreferences.getString(keyPreference, "");
            final String celsius = this.getResources().getString(
                    R.string.weather_preferences_units_celsius);
            if (unitsPreferenceValue.equals(celsius)) {
                mIsFahrenheit = false;
            } else {
                mIsFahrenheit = true;
            }
            final double tempUnits = mIsFahrenheit ? 0 : 273.15;
            final String symbol = mIsFahrenheit ? "ºF" : "ºC";


            // 2. Formatters
            final DecimalFormat tempFormatter = (DecimalFormat) NumberFormat.getNumberInstance(Locale.US);
            tempFormatter.applyPattern("#####.#####");


            // 3. Prepare data for RemoteViews.
            String tempMax = "";
            if (current.getMain().getTemp_max() != null) {
                double conversion = (Double) current.getMain().getTemp_max();
                conversion = conversion - tempUnits;
                tempMax = tempFormatter.format(conversion) + symbol;
            }
            String tempMin = "";
            if (current.getMain().getTemp_min() != null) {
                double conversion = (Double) current.getMain().getTemp_min();
                conversion = conversion - tempUnits;
                tempMin = tempFormatter.format(conversion) + symbol;
            }
            Bitmap picture;
            if ((current.getWeather().size() > 0)
                    && (current.getWeather().get(0).getIcon() != null)
                    && (IconsList.getIcon(current.getWeather().get(0).getIcon()) != null)) {
                final String icon = current.getWeather().get(0).getIcon();
                picture = BitmapFactory.decodeResource(this.getResources(), IconsList.getIcon(icon)
                        .getResourceDrawable());
            } else {
                picture = BitmapFactory.decodeResource(this.getResources(),
                        R.drawable.weather_severe_alert);
            }
            final String city = weatherLocation.getCity();
            final String country = weatherLocation.getCountry();
            
            // 4. Insert data in RemoteViews.
            final RemoteViews remoteView = new RemoteViews(this.getApplicationContext().getPackageName(), R.layout.appwidget);
            remoteView.setImageViewBitmap(R.id.weather_appwidget_image, picture);
            remoteView.setTextViewText(R.id.weather_appwidget_temperature_max, tempMax);
            remoteView.setTextViewText(R.id.weather_appwidget_temperature_min, tempMin);
            remoteView.setTextViewText(R.id.weather_appwidget_city, city);
            remoteView.setTextViewText(R.id.weather_appwidget_country, country);

            // 5. Activity launcher.
            final Intent resultIntent =  new Intent(this.getApplicationContext(), WeatherTabsActivity.class);
            // The PendingIntent to launch our activity if the user selects this notification
//            final PendingIntent contentIntent = PendingIntent.getActivity(
//            		this.getApplicationContext(), 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            // The stack builder object will contain an artificial back stack for the started Activity.
            // This ensures that navigating backward from the Activity leads out of
            // your application to the Home screen.
            final TaskStackBuilder stackBuilder = TaskStackBuilder.create(this.getApplicationContext());
            // Adds the back stack for the Intent (but not the Intent itself)
            stackBuilder.addParentStack(WeatherTabsActivity.class);
            // Adds the Intent that starts the Activity to the top of the stack
            stackBuilder.addNextIntent(resultIntent);
            final PendingIntent resultPendingIntent =
            		stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                    );
            remoteView.setOnClickPendingIntent(R.id.weather_appwidget, resultPendingIntent);
            
            
            // Push update for this widget to the home screen
            final AppWidgetManager manager = AppWidgetManager.getInstance(this);
            manager.updateAppWidget(appWidgetId, remoteView);
            
            Log.i("WordWidget.UpdateService", "widget updated");
        }
    }
}
