package de.example.exampletdd;

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
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.http.AndroidHttpClient;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.RemoteViews;

import com.fasterxml.jackson.core.JsonParseException;

import de.example.exampletdd.httpclient.CustomHTTPClient;
import de.example.exampletdd.model.DatabaseQueries;
import de.example.exampletdd.model.WeatherLocation;
import de.example.exampletdd.model.currentweather.Current;
import de.example.exampletdd.parser.JPOSWeatherParser;
import de.example.exampletdd.service.IconsList;
import de.example.exampletdd.service.ServiceParser;

public class WeatherInformationBatch extends IntentService {
    private static final String TAG = "WeatherInformationBatch";


    public WeatherInformationBatch() {
        super("WIB-Thread");
    }

    @Override
    protected void onHandleIntent(final Intent intent) {
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
            	this.showNotification(current, weatherLocation);
            }
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
    
    private void showNotification(final Current current, final WeatherLocation weatherLocation) {
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
        final RemoteViews remoteView = new RemoteViews(this.getApplicationContext().getPackageName(), R.layout.notification);
        remoteView.setImageViewBitmap(R.id.weather_notification_image, picture);
        remoteView.setTextViewText(R.id.weather_notification_temperature_max, tempMax);
        remoteView.setTextViewText(R.id.weather_notification_temperature_min, tempMin);
        remoteView.setTextViewText(R.id.weather_notification_city, city);
        remoteView.setTextViewText(R.id.weather_notification_country, country);

        // 5. Activity launcher.
        final Intent resultIntent =  new Intent(this.getApplicationContext(), WeatherTabsActivity.class);
        // The PendingIntent to launch our activity if the user selects this notification
//        final PendingIntent contentIntent = PendingIntent.getActivity(
//        		this.getApplicationContext(), 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
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
        
    	final NotificationManagerCompat notificationManager =
    			NotificationManagerCompat.from(this.getApplicationContext());
    	

    	// 6. Create notification.
        final NotificationCompat.Builder notificationBuilder =
        		new NotificationCompat.Builder(this.getApplicationContext())
        		.setContent(remoteView)
                .setSmallIcon(R.drawable.ic_launcher)
                .setAutoCancel(true)
                .setLocalOnly(true)
                .setWhen(System.currentTimeMillis())
                .setContentIntent(resultPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        
        final Notification notification = notificationBuilder.build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        // Send the notification.
        // Sets an ID for the notification, so it can be updated (just in case)
        int notifyID = 1;
        notificationManager.notify(notifyID, notification);
    }
}
